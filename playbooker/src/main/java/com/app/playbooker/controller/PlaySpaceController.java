package com.app.playbooker.controller;

import com.app.playbooker.bo.PlaySpaceBO;
import com.app.playbooker.dto.PlaySpaceDTO;
import com.app.playbooker.dto.PlaySpaceResponse;
import com.app.playbooker.dto.PlaySpaceSearchCriteria;
import com.app.playbooker.entity.PlaySpace;
import com.app.playbooker.repository.PlaySpaceRepository;
import com.app.playbooker.service.PlaySpaceService;
import com.app.playbooker.utils.PlaySpaceSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/playspace")
public class PlaySpaceController {

    @Autowired
    private PlaySpaceService playSpaceService;

    @Autowired
    private PlaySpaceRepository playSpaceRepository;

    @GetMapping("/get/{id}")
    public ResponseEntity<PlaySpaceResponse> getPlaySpaceById(@PathVariable String id) {
        return ResponseEntity.ok(playSpaceService.getPlaySpaceById(id));
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<PlaySpaceBO>> getAllPlaySpace() {
        return ResponseEntity.ok(playSpaceService.getAllPlaySpace());
    }

    @PostMapping("/create")
//    @Secured({"ROLE_ADMIN"})
    public ResponseEntity<PlaySpace> createPlaySpace(@RequestBody PlaySpaceDTO playSpaceDTO) {
        return new ResponseEntity<>(playSpaceService.createPlaySpace(playSpaceDTO), HttpStatus.CREATED);
    }

    @PostMapping("/search")
    public ResponseEntity<List<PlaySpace>> searchPlaySpace(@RequestBody PlaySpaceSearchCriteria criteria) {
        Specification<PlaySpace> specification = PlaySpaceSpecification.build(criteria);
        return ResponseEntity.ok(playSpaceRepository.findAll(specification));
    }
}
