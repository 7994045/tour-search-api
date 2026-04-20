package com.toursearch.controller;

import com.toursearch.dto.SearchRequest;
import com.toursearch.dto.Tour;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tours")
public class TourController {


    @PostMapping("/search")
    public List<Tour> searchTours(@RequestBody SearchRequest request) {
        List<Tour> allTours = getAllTours();
        
        // Filter by destination (to)
        if (request.getTo() != null && !request.getTo().isEmpty()) {
            String dest = request.getTo().toLowerCase();
            allTours = allTours.stream()
                .filter(t -> t.getCountry().toLowerCase().contains(dest) 
                          || t.getCity().toLowerCase().contains(dest)
                          || t.getHotelName().toLowerCase().contains(dest))
                .collect(Collectors.toList());
        }
        
        // Filter by max price
        if (request.getMaxPrice() != null && request.getMaxPrice() > 0) {
            allTours = allTours.stream()
                .filter(t -> t.getPrice() <= request.getMaxPrice())
                .collect(Collectors.toList());
        }
        
        return allTours;
    }
    
    private List<Tour> getAllTours() {
        List<Tour> tours = new ArrayList<>();
        
        // Turkey tours
        Tour t1 = new Tour();
        t1.setHotelName("Royal Grand Resort");
        t1.setRating(4.5);
        t1.setCountry("Турция");
        t1.setCity("Анталья");
        t1.setHotelImage("https://example.com/hotel.jpg");
        t1.setPrice(85000.0);
        t1.setBookUrl("https://example.com/book");
        tours.add(t1);
        
        Tour t2 = new Tour();
        t2.setHotelName("Sun Beach Hotel");
        t2.setRating(4.0);
        t2.setCountry("Турция");
        t2.setCity("Мармарис");
        t2.setHotelImage("https://example.com/hotel2.jpg");
        t2.setPrice(65000.0);
        t2.setBookUrl("https://example.com/book");
        tours.add(t2);
        
        // Egypt tours
        Tour e1 = new Tour();
        e1.setHotelName("Pyramid View Resort");
        e1.setRating(4.8);
        e1.setCountry("Египет");
        e1.setCity("Хургада");
        e1.setHotelImage("https://example.com/egypt1.jpg");
        e1.setPrice(72000.0);
        e1.setBookUrl("https://example.com/book");
        tours.add(e1);
        
        Tour e2 = new Tour();
        e2.setHotelName("Red Sea Paradise");
        e2.setRating(4.3);
        e2.setCountry("Египет");
        e2.setCity("Шарм-эль-Шейх");
        e2.setHotelImage("https://example.com/egypt2.jpg");
        e2.setPrice(68000.0);
        e2.setBookUrl("https://example.com/book");
        tours.add(e2);
        
        // UAE tours
        Tour u1 = new Tour();
        u1.setHotelName("Dubai Luxury Palace");
        u1.setRating(5.0);
        u1.setCountry("ОАЭ");
        u1.setCity("Дубай");
        u1.setHotelImage("https://example.com/dubai1.jpg");
        u1.setPrice(150000.0);
        u1.setBookUrl("https://example.com/book");
        tours.add(u1);
        
        Tour u2 = new Tour();
        u2.setHotelName("Abu Dhabi Royal Hotel");
        u2.setRating(4.7);
        u2.setCountry("ОАЭ");
        u2.setCity("Абу-Даби");
        u2.setHotelImage("https://example.com/dubai2.jpg");
        u2.setPrice(120000.0);
        u2.setBookUrl("https://example.com/book");
        tours.add(u2);
        
        return tours;
    }
}
