package com.app.playbooker.service;

import com.app.playbooker.bo.AvailabilitySlotBO;
import com.app.playbooker.bo.PlaySpaceBO;
import com.app.playbooker.dto.PlaySpaceDTO;
import com.app.playbooker.dto.PlaySpaceResponse;
import com.app.playbooker.entity.PlaySpace;
import com.app.playbooker.entity.AvailabilitySlot;
import com.app.playbooker.repository.PlaySpaceRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PlaySpaceService {
    @Autowired
    private PlaySpaceRepository playSpaceRepository;

    public PlaySpaceResponse getPlaySpaceById(String id) {
        Optional<PlaySpace> playSpace = playSpaceRepository.findById(id);
        return playSpace.isPresent()
                ? getPlaySpaceData(playSpace.get(), HttpStatus.OK, "")
                : getPlaySpaceData(null, HttpStatus.NOT_FOUND, "Play Space does not exist with given id");
    }

    public List<PlaySpaceBO> getAllPlaySpace() {
        return playSpaceRepository.findAll().stream().map(this::getPlaySpaceBOFromPlayspace).toList();
    }

    public PlaySpace createPlaySpace(PlaySpaceDTO playSpaceDTO) {
        PlaySpace playSpace = new PlaySpace();

        // 1. Set direct fields
        BeanUtils.copyProperties(playSpaceDTO, playSpace);
        // 3. Set availability slots — THIS is where playSpace_id was missing!
        if (playSpaceDTO.getAvailabilitySlots() != null) {
            playSpace.setAvailabilitySlots(
                    playSpaceDTO.getAvailabilitySlots().stream().map(dto -> {
                        AvailabilitySlot slot = new AvailabilitySlot();
                        slot.setStartTime(dto.getStartTime());
                        slot.setEndTime(dto.getEndTime());
                        slot.setBooked(dto.isBooked());
                        slot.setPlaySpace(playSpace); // 🔥 set parent
                        return slot;
                    }).toList()
            );
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

    /**
     * <pre>This method converts {@link PlaySpace} object to {@link PlaySpaceBO} object.</pre>
     *
     * <pre>Why {@link PlaySpaceBO} is required?</pre>
     * <p>If we return {@link PlaySpace} object from our API then there will be one issue. {@link PlaySpace} object has association
     * with {@link AvailabilitySlot} object and {@link AvailabilitySlot} object also has association with {@link PlaySpace} object
     * So when the API returns {@link PlaySpace} object, we get {@link AvailabilitySlot} in response and
     * inside {@link AvailabilitySlot} there is again {@link PlaySpace}, this causes circular reference issue.</p>
     * <p>To solve this we have introduced {@link PlaySpaceBO} and {@link AvailabilitySlotBO} such that {@link AvailabilitySlotBO}
     * does not have reference to the {@link PlaySpaceBO} but {@link PlaySpaceBO} has reference of {@link AvailabilitySlotBO}
     * Hence this resolves the circular reference issue.</p>
     *
     * <p>This method fetches availability slots from the {@link PlaySpace} object and prepare list of availability sort BOs,
     * then it creates {@link PlaySpaceBO} object, copies all the properties of {@link PlaySpace} object except availability
     * slots. Then it explicitly sets newly prepared availability slot BOs and finally return the BO.</p>
     *
     * @param playSpace Play Space Object
     * @return {@link PlaySpaceBO}
     */
    public PlaySpaceBO getPlaySpaceBOFromPlayspace(PlaySpace playSpace) {
        List<AvailabilitySlot> slots = playSpace.getAvailabilitySlots();
        List<AvailabilitySlotBO> slotBOs = slots.stream().map(slot -> {
            AvailabilitySlotBO slotBO = new AvailabilitySlotBO();
            slotBO.setId(slot.getId());
            slotBO.setBooked(slot.isBooked());
            slotBO.setStartTime(slot.getStartTime());
            slotBO.setEndTime(slot.getEndTime());
            return slotBO;
        }).toList();

        PlaySpaceBO playSpaceBO = new PlaySpaceBO();
        BeanUtils.copyProperties(playSpace, playSpaceBO, "availabilitySlots");
        playSpaceBO.setAvailabilitySlots(slotBOs);
        return playSpaceBO;
    }


}

