package com.app.playbooker.repository;

import com.app.playbooker.entity.PlaySpace;
import com.app.playbooker.enums.Sport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface PlaySpaceRepository extends JpaRepository<PlaySpace, String>, JpaSpecificationExecutor<PlaySpace> {
    Optional<PlaySpace> findByName(String name);
    Optional<List<PlaySpace>> findBySportsContaining(Sport sport);
}
