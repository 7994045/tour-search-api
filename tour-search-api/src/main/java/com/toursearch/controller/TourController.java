package com.toursearch.controller;

import com.toursearch.dto.SearchRequest;
import com.toursearch.dto.Tour;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/tours")
public class TourController {

    @PostMapping("/search")
    public List<Tour> searchTours(@RequestBody SearchRequest request) {
        List<Tour> tours = new ArrayList<>();
        
        Tour tour = new Tour();
        tour.setHotelName("Royal Grand Resort");
        tour.setRating(4.5);
        tour.setCountry("Турция");
        tour.setCity("Анталья");
        tour.setHotelImage("https://example.com/hotel.jpg");
        tour.setPrice(85000.0);
        tour.setBookUrl("https://example.com/book");
        
        tours.add(tour);
        
        return tours;
    }
}
