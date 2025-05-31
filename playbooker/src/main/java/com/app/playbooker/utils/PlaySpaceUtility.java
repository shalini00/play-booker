package com.app.playbooker.utils;

import com.app.playbooker.dto.PlaySpaceSearchCriteria;
import com.app.playbooker.enums.Sport;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;

public class PlaySpaceUtility {

    public static PlaySpaceSearchCriteria parseRequest(String query, String filter, String sort) {
        // Filter - attribute1:value1;attribute1:value2;attribute2:value1;attribute2:value2...
        // Sort - sort=attribute1:value1
        List<Sport> sports = new ArrayList<>();
        PlaySpaceSearchCriteria playSpaceSearchCriteria = new PlaySpaceSearchCriteria();
        playSpaceSearchCriteria.setSports(sports);
        playSpaceSearchCriteria.setName(query);

        if (filter != null && !filter.isEmpty()) {
            String[] atrributes = filter.split(";");
            for (String val: atrributes) {
                String[] keyValue = val.split(":");
                String key = keyValue[0];
                String value = keyValue[1];
                evaluateAttribute(playSpaceSearchCriteria, key, value);
            }
        }

        if (sort != null && !sort.isEmpty()) {
            String[] atrributes = sort.split(":");
            String key = atrributes[0];
            String value = atrributes[1];
            String field = mapSortField(key);
            Sort.Direction direction = value.equalsIgnoreCase("desc")
                    ? Sort.Direction.DESC : Sort.Direction.ASC;
            playSpaceSearchCriteria.setSortOrders(Sort.by(new Sort.Order(direction, field)));
        }

        return playSpaceSearchCriteria;
    }

    private static void evaluateAttribute(PlaySpaceSearchCriteria playSpaceSearchCriteria, String key, String value) {
        switch (key.toUpperCase()) {
            case "SPORT":
                playSpaceSearchCriteria.getSports().add(Sport.valueOf(value));
                break;
            case "CITY":
                playSpaceSearchCriteria.setCity(value);
                break;
            case "RATING":
                playSpaceSearchCriteria.setAverageRating(Double.valueOf(value));
                break;
            default:
                throw new RuntimeException("Invalid Filter key");

        }
    }

    private static String mapSortField(String key) {
        return switch (key.toUpperCase()) {
            case "PRICE" -> "pricePerHour";
            case "NAME" -> "name";
            case "RATING" -> "averageRating";
            default -> throw new RuntimeException("Invalid sort field");
        };
    }
}
