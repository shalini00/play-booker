package com.app.playbooker.utils;

import com.app.playbooker.dto.PlaySpaceSearchCriteria;
import com.app.playbooker.entity.PlaySpace;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class PlaySpaceSpecification {
    public static Specification<PlaySpace> build(PlaySpaceSearchCriteria criteria) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (criteria.getName() != null && !criteria.getName().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("name")), "%" + criteria.getName().toLowerCase() + "%"));
            }

            if (criteria.getSport() != null) {
                predicates.add(cb.equal((root.get("sport")), criteria.getSport()));
            }

            if (criteria.getCity() != null && !criteria.getCity().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("address").get("city")), "%" + criteria.getCity().toLowerCase() + "%"));
            }

            if (criteria.getAverageRating() != null) {
                predicates.add(cb.greaterThanOrEqualTo((root.get("averageRating")), criteria.getAverageRating()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
