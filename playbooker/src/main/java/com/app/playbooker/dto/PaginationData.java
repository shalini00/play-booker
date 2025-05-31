package com.app.playbooker.dto;

import lombok.*;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PaginationData {
    private int currentPage;
    private int count;
    private long totalCount;
    private int totalPages;
}
