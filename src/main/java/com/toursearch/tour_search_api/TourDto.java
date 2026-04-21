package com.toursearch.tour_search_api;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TourDto {
    private String hotelName;
    private Double rating;
    private String country;
    private String city;
    private Integer price;
    private String hotelImage;
    private String bookUrl;
}