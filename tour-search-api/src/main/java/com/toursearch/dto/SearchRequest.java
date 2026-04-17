package com.toursearch.dto;

import lombok.Data;

@Data
public class SearchRequest {
    private String from;
    private String to;
    private String dateFrom;
    private String dateTo;
    private Integer nights;
    private Integer adults;
}
