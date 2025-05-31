package com.app.playbooker.dto;

import lombok.*;

import java.util.List;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ResultPageData<T> {
    private PaginationData paginationData;
    List<T> results;
}
