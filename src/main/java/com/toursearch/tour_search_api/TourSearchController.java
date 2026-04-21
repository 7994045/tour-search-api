package com.toursearch.tour_search_api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/tours")
@RequiredArgsConstructor
public class TourSearchController {

    @GetMapping("/search")
    public ResponseEntity<List<TourDto>> search(
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String destination,
            @RequestParam(required = false, defaultValue = "") String dateFrom,
            @RequestParam(required = false, defaultValue = "7") int nights,
            @RequestParam(required = false, defaultValue = "2") int adults,
            @RequestParam(required = false, defaultValue = "0") int children
    ) {
        String searchCountry = (country != null && !country.isEmpty()) ? country : (destination != null ? destination : "Турция");
        return ResponseEntity.ok(getDemoTours(searchCountry, nights));
    }

    private List<TourDto> getDemoTours(String country, int nights) {
        Map<String, List<String>> resorts = Map.of(
            "Турция", List.of("Анталья", "Аланья", "Кемер", "Сиде"),
            "Египет", List.of("Шарм-эль-Шейх", "Хургада", "Марса-Алам"),
            "ОАЭ", List.of("Дубай", "Шарджа", "Абу-Даби", "Рас-эль-Хайма"),
            "Таиланд", List.of("Паттайя", "Пхукет", "Самуи", "Краби"),
            "Шри-Ланка", List.of("Бентота", "Унаватуна", "Тангалле")
        );

        Map<String, List<String>> hotels = Map.of(
            "Турция", List.of("Rixos Premium Belek", "Maxx Royal Kemer", "Titanic Deluxe", "Calista Luxury"),
            "Египет", List.of("Steigenberger Al Dau", "Sunrise Grand Select", "Cleopatra Luxury"),
            "ОАЭ", List.of("Atlantis The Palm", "Jumeirah Beach", "Rixos The Palm"),
            "Таиланд", List.of("Centara Grand", "Hilton Phuket", "Amari Pattaya"),
            "Шри-Ланка", List.of("Heritance Ahungalla", "Cinnamon Bey", "Taj Bentota")
        );

        Map<String, List<Double>> ratings = Map.of(
            "Турция", List.of(4.8, 4.9, 4.7, 4.5),
            "Египет", List.of(4.6, 4.8, 4.3),
            "ОАЭ", List.of(4.9, 4.7, 4.8),
            "Таиланд", List.of(4.7, 4.5, 4.4),
            "Шри-Ланка", List.of(4.6, 4.3, 4.4)
        );

        Map<String, int[]> priceRange = Map.of(
            "Турция", new int[]{85000, 250000},
            "Египет", new int[]{65000, 180000},
            "ОАЭ", new int[]{120000, 400000},
            "Таиланд", new int[]{90000, 220000},
            "Шри-Ланка", new int[]{75000, 190000}
        );

        List<String> countryResorts = resorts.getOrDefault(country, List.of("Курорт"));
        List<String> countryHotels = hotels.getOrDefault(country, List.of("Отель " + country));
        List<Double> countryRatings = ratings.getOrDefault(country, List.of(4.0));
        int[] range = priceRange.getOrDefault(country, new int[]{50000, 150000});

        Random rnd = new Random();
        int count = 3 + rnd.nextInt(4);
        List<TourDto> tours = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            int idx = i % countryHotels.size();
            int price = range[0] + rnd.nextInt(range[1] - range[0]);
            price = (price / 1000) * 1000;

            tours.add(TourDto.builder()
                .hotelName(countryHotels.get(idx))
                .rating(countryRatings.get(idx % countryRatings.size()))
                .country(country)
                .city(countryResorts.get(idx % countryResorts.size()))
                .price(price)
                .hotelImage("https://placehold.co/400x250?text=" + countryHotels.get(idx).replace(" ", "+"))
                .bookUrl("https://germes-travel.ru/book?hotel=" + idx)
                .build());
        }

        tours.sort(Comparator.comparingInt(TourDto::getPrice));
        return tours;
    }
}