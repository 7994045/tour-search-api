package com.toursearch.tour_search_api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/tours")
@RequiredArgsConstructor
public class TourSearchController {

    @GetMapping("/search")
    public ResponseEntity<List<TourDto>> search(
            @RequestParam(required = false) String destination,
            @RequestParam(required = false, defaultValue = "7") int nights) {
        return ResponseEntity.ok(getMockTours());
    }

    private List<TourDto> getMockTours() {
        return Arrays.asList(
            TourDto.builder()
                .hotelName("Royal Grand Hotel")
                .rating(4.5)
                .country("Турция")
                .city("Анталья")
                .price(85000)
                .hotelImage("https://example.com/hotel1.jpg")
                .bookUrl("https://example.com/book/1")
                .build(),
            TourDto.builder()
                .hotelName("Sea View Resort")
                .rating(4.8)
                .country("Египет")
                .city("Шарм-эль-Шейх")
                .price(72000)
                .hotelImage("https://example.com/hotel2.jpg")
                .bookUrl("https://example.com/book/2")
                .build()
        );
    }
}
