package com.app.playbooker.controller;

import com.app.playbooker.bo.PlaySpaceBO;
import com.app.playbooker.dto.*;
import com.app.playbooker.entity.PlaySpace;
import com.app.playbooker.repository.PlaySpaceRepository;
import com.app.playbooker.service.PlaySpaceService;
import com.app.playbooker.utils.PlaySpaceUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.app.playbooker.utils.AppConstants.ROLE_ADMIN;
import static com.app.playbooker.utils.AppConstants.ROLE_USER;

@RestController
@RequestMapping("/api/v1/playspace")
public class PlaySpaceController {

    @Autowired
    private PlaySpaceService playSpaceService;

    @Autowired
    private PlaySpaceRepository playSpaceRepository;


    @Secured({ROLE_ADMIN, ROLE_USER})
    @GetMapping("/get/{id}")
    public ResponseEntity<PlaySpaceResponse> getPlaySpaceById(@PathVariable String id) {
        return ResponseEntity.ok(playSpaceService.getPlaySpaceById(id));
    }

    @Secured({ROLE_ADMIN})
    @GetMapping("/getAll")
    public ResponseEntity<List<PlaySpaceBO>> getAllPlaySpace() {
        return ResponseEntity.ok(playSpaceService.getAllPlaySpace());
    }

    @Secured({ROLE_ADMIN})
    @PostMapping("/create")
    public ResponseEntity<PlaySpace> createPlaySpace(@RequestBody PlaySpaceDTO playSpaceDTO) {
        return new ResponseEntity<>(playSpaceService.createPlaySpace(playSpaceDTO), HttpStatus.CREATED);
    }


    @Secured({ROLE_ADMIN, ROLE_USER})
    @GetMapping("/search")
    public ResponseEntity<ResultPageData<PlaySpaceBO>> searchPlaySpace(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String filter,
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<PlaySpaceBO> playSpaceBOPage = playSpaceService.searchPlaySpaces(PlaySpaceUtility.parseRequest(query, filter, sort), page, size);
        PaginationData paginationData = PaginationData.builder()
                .currentPage(playSpaceBOPage.getNumber())
                .totalCount(playSpaceBOPage.getTotalElements())
                .totalPages(playSpaceBOPage.getTotalPages())
                .count(playSpaceBOPage.getNumberOfElements())
                .build();

        ResultPageData<PlaySpaceBO> playSpaceResultPageData = new ResultPageData<>();
        playSpaceResultPageData.setPaginationData(paginationData);
        playSpaceResultPageData.setResults(playSpaceBOPage.getContent());

        return ResponseEntity.ok(playSpaceResultPageData);
    }


    @Secured({ROLE_ADMIN})
    @PatchMapping("/update/{id}")
    public ResponseEntity<PlaySpace> updatePlaySpace(
            @PathVariable String id,
            @RequestBody UpdatePlaySpaceDTO updatePlaySpaceDTO
    ) {
        return ResponseEntity.ok(playSpaceService.updatePlaySpace(id, updatePlaySpaceDTO));
    }


    @Secured({ROLE_ADMIN})
    @PutMapping("/update_playspace/{id}")
    public ResponseEntity<PlaySpace> updateFullPlaySpace(
            @PathVariable String id,
            @RequestBody PlaySpaceDTO updatePlaySpaceDTO
    ) {
        return ResponseEntity.ok(playSpaceService.updateFullPlaySpace(id, updatePlaySpaceDTO));
    }


    @Secured({ROLE_ADMIN})
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deletePlaySpace(@PathVariable String id) {
        playSpaceService.deletePlaySpace(id);
        return ResponseEntity.noContent().build();
    }
}
