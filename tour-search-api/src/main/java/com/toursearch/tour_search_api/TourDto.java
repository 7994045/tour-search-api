        package com.toursearch.tour_search_api;



import lombok.Data;
import lombok.Builder;

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


