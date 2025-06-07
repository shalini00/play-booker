package com.app.playbooker.service;

import com.app.playbooker.bo.PlaySpaceBO;
import com.app.playbooker.dto.PlaySpaceDTO;
import com.app.playbooker.dto.PlaySpaceResponse;
import com.app.playbooker.dto.PlaySpaceSearchCriteria;
import com.app.playbooker.dto.UpdatePlaySpaceDTO;
import com.app.playbooker.entity.PlaySpace;
import com.app.playbooker.entity.Review;
import com.app.playbooker.enums.PlaySpaceVisibility;
import com.app.playbooker.exceptions.PlaySpaceException;
import com.app.playbooker.exceptions.PlaySpaceNotFoundException;
import com.app.playbooker.repository.PlaySpaceRepository;
import com.app.playbooker.repository.ReviewRepository;
import com.app.playbooker.repository.UserRepository;
import com.app.playbooker.utils.JwtUtil;
import com.app.playbooker.spec.PlaySpaceSpecification;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PlaySpaceService {

    public static final double DEFAULT_AVG_RATING = 4.0;

    @Autowired
    private PlaySpaceRepository playSpaceRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private HttpServletRequest httpServletRequest;

    public PlaySpaceResponse getPlaySpaceById(String id) {
        Optional<PlaySpace> playSpace = playSpaceRepository.findById(id);
        return playSpace.isPresent()
                ? getPlaySpaceData(playSpace.get(), HttpStatus.OK, "")
                : getPlaySpaceData(null, HttpStatus.NOT_FOUND, "Play Space does not exist with given id");
    }

    public PlaySpace getPlaySpaceObjectById(String id) {
        return playSpaceRepository.findById(id).orElseThrow(() -> new PlaySpaceNotFoundException(id));
    }

    public List<PlaySpaceBO> getAllPlaySpace() {
        return playSpaceRepository.findAllByPlaySpaceVisibility(PlaySpaceVisibility.ACTIVE)
                .stream().map(this::getPlaySpaceBOFromPlayspace).toList();
    }

    public PlaySpace createPlaySpace(PlaySpaceDTO playSpaceDTO) {
        String playSpaceName = playSpaceDTO.getName();
        Double latitude = playSpaceDTO.getAddress().getLatitude();
        Double longitude = playSpaceDTO.getAddress().getLongitude();
        Optional<PlaySpace> playSpaceOptional = playSpaceRepository.findByNameAndPlaySpaceVisibilityAndAddress_LatitudeAndAddress_Longitude(playSpaceName, PlaySpaceVisibility.ACTIVE, latitude, longitude);
        if (playSpaceOptional.isPresent()) {
            throw new PlaySpaceException("Playspace already present.");
        }
        PlaySpace playSpace = new PlaySpace();
        BeanUtils.copyProperties(playSpaceDTO, playSpace);
        if (playSpace.getPlaySpaceVisibility() == null) {
            playSpace.setPlaySpaceVisibility(PlaySpaceVisibility.ACTIVE);
        }
        return playSpaceRepository.save(playSpace);
    }

    public PlaySpaceResponse getPlaySpaceData(PlaySpace playSpace, HttpStatus status, String msg) {
        PlaySpaceResponse playSpaceResponse = new PlaySpaceResponse();
        playSpaceResponse.setStatus(status);
        playSpaceResponse.setMsg(msg);
        playSpaceResponse.setData(playSpace);

        return playSpaceResponse;
    }

    public PlaySpace updatePlaySpace(String playSpaceId, UpdatePlaySpaceDTO updatePlaySpaceDTO) {
        PlaySpace playSpace = playSpaceRepository.findById(playSpaceId).orElseThrow(() -> new PlaySpaceNotFoundException(playSpaceId));
        if (updatePlaySpaceDTO.getName() != null) playSpace.setName(updatePlaySpaceDTO.getName());
        if (updatePlaySpaceDTO.getDescription() != null) playSpace.setDescription(updatePlaySpaceDTO.getDescription());
        if (updatePlaySpaceDTO.getSports() != null) playSpace.setSports(updatePlaySpaceDTO.getSports());
        if (updatePlaySpaceDTO.getPricePerHour() != null) playSpace.setPricePerHour(updatePlaySpaceDTO.getPricePerHour());
        if (updatePlaySpaceDTO.getAmenities() != null) playSpace.setAmenities(updatePlaySpaceDTO.getAmenities());
        if (updatePlaySpaceDTO.getImageUrls() != null) playSpace.setImageUrls(updatePlaySpaceDTO.getImageUrls());
        if (updatePlaySpaceDTO.getWeeklyOpeningHours() != null) playSpace.setWeeklyOpeningHours(updatePlaySpaceDTO.getWeeklyOpeningHours());
        if (updatePlaySpaceDTO.getAddress() != null) playSpace.setAddress(updatePlaySpaceDTO.getAddress());

        return playSpaceRepository.save(playSpace);
    }

    /**
     * <pre>This method converts {@link PlaySpace} object to {@link PlaySpaceBO} object.</pre>
     *
     * @param playSpace Play Space Object
     * @return {@link PlaySpaceBO}
     */
    public PlaySpaceBO getPlaySpaceBOFromPlayspace(PlaySpace playSpace) {
        PlaySpaceBO playSpaceBO = new PlaySpaceBO();
        BeanUtils.copyProperties(playSpace, playSpaceBO);
        return playSpaceBO;
    }

    public void updatePlaySpaceRating(String playSpaceId) {
        PlaySpace playSpace = playSpaceRepository.findById(playSpaceId).orElseThrow(() -> new PlaySpaceNotFoundException(playSpaceId));
        List<Review> reviews = reviewRepository.findByPlaySpaceId(playSpaceId);

        if (reviews.isEmpty()) {
            playSpace.setAverageRating(DEFAULT_AVG_RATING);
            playSpace.setNumberOfReviews(0);
        } else {
            double avgRating = reviews.stream()
                    .mapToDouble(Review::getRating)
                    .average()
                    .orElse(0.0);

            playSpace.setAverageRating(avgRating);
            playSpace.setNumberOfReviews(reviews.size());
        }

        playSpaceRepository.save(playSpace);
    }

    public PlaySpace updateFullPlaySpace(String playSpaceId, PlaySpaceDTO updatedPlaySpaceDTO) {
        PlaySpace existingPlaySpace = playSpaceRepository.findById(playSpaceId).orElseThrow(() -> new PlaySpaceNotFoundException(playSpaceId));
        PlaySpace updatedPlaySpace = new PlaySpace();
        BeanUtils.copyProperties(updatedPlaySpaceDTO, updatedPlaySpace);
        updatedPlaySpace.setId(existingPlaySpace.getId());

        return playSpaceRepository.save(updatedPlaySpace);
    }

    public Page<PlaySpaceBO> searchPlaySpaces(PlaySpaceSearchCriteria criteria, int page, int size) {
        Specification<PlaySpace> specification = PlaySpaceSpecification.build(criteria);
        Sort sort = criteria.getSortOrders() != null ? criteria.getSortOrders() : Sort.by("createdAt").descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<PlaySpace> playSpacePage = playSpaceRepository.findAll(specification, pageable);
        return playSpacePage.map(this::getPlaySpaceBOFromPlayspace);
    }

    @Transactional
    public void deletePlaySpace(String playSpaceId) {
        playSpaceRepository.deleteById(playSpaceId);
    }

    public void togglePlaySpaceVisibility(String id) {
        PlaySpace playSpace = playSpaceRepository.findById(id).orElseThrow(() -> new PlaySpaceNotFoundException(id));
        if (playSpace.getPlaySpaceVisibility().equals(PlaySpaceVisibility.ACTIVE)) {
            playSpace.setPlaySpaceVisibility(PlaySpaceVisibility.INACTIVE);
        } else {
            playSpace.setPlaySpaceVisibility(PlaySpaceVisibility.ACTIVE);
        }
        playSpaceRepository.save(playSpace);
    }

    public long getActivePlaySpaceCount() {
        return playSpaceRepository.countByPlaySpaceVisibility(PlaySpaceVisibility.ACTIVE); // assuming a flag
    }

}

