package com.toursearch.tour_search_api;

import com.toursearch.service.StatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/tours")
public class TourSearchController {

    @Autowired
    private StatsService statsService;

    @GetMapping("/search")
    public ResponseEntity<List<TourDto>> search(
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String destination,
            @RequestParam(required = false, defaultValue = "") String dateFrom,
            @RequestParam(required = false, defaultValue = "7") int nights,
            @RequestParam(required = false, defaultValue = "2") int adults,
            @RequestParam(required = false, defaultValue = "0") int children
    ) {
        String searchCountry = (country != null && !country.isEmpty()) ? country : (destination != null ? destination : "\u0422\u0443\u0440\u0446\u0438\u044f");
        try {
            statsService.recordSearch(searchCountry, destination);
            return ResponseEntity.ok(getDemoTours(searchCountry, nights));
        } catch (Exception e) {
            statsService.recordError("Search error: " + e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    private List<TourDto> getDemoTours(String country, int nights) {
        Map<String, List<String>> resorts = Map.of(
                "\u0422\u0443\u0440\u0446\u0438\u044f", List.of("\u0410\u043d\u0442\u0430\u043b\u044c\u044f", "\u0410\u043b\u0430\u043d\u044c\u044f", "\u041a\u0435\u043c\u0435\u0440", "\u0421\u0438\u0434\u0435"),
                "\u0415\u0433\u0438\u043f\u0435\u0442", List.of("\u0428\u0430\u0440\u043c-\u044d\u043b\u044c-\u0428\u0435\u0439\u0445", "\u0425\u0443\u0440\u0433\u0430\u0434\u0430", "\u041a\u043b\u0435\u043e\u043f\u0430\u0442\u0440\u0430"),
                "\u041e\u0410\u042d", List.of("\u0414\u0443\u0431\u0430\u0438", "\u0410\u0431\u0443-\u0414\u0430\u0431\u0438", "\u0428\u0430\u0440\u0434\u0436\u0430"),
                "\u0422\u0430\u0438\u043b\u0430\u043d\u0434", List.of("\u041f\u0430\u0442\u0442\u0430\u044f", "\u041f\u0443\u043a\u0435\u0442", "\u041a\u0440\u0430\u0431\u0438", "\u0411\u0430\u043d\u0433\u043a\u043e\u043a"),
                "\u0413\u0440\u0443\u0437\u0438\u044f", List.of("\u0411\u0430\u0442\u0443\u043c\u0438", "\u041a\u0443\u0442\u0430\u0438\u0441\u0438", "\u0411\u0430\u043a\u0443\u0440\u0438\u0430\u043d\u0438"),
                "\u0420\u043e\u0441.\u041a\u0430\u0432\u043a\u0430\u0437", List.of("\u0421\u043e\u0447\u0438", "\u0414\u0430\u0433\u043e\u043c\u044b\u0441", "\u041a\u0438\u0441\u043b\u043e\u0432\u043e\u0434\u0441\u043a", "\u0415\u0441\u0441\u0435\u043d\u0442\u0443\u043a\u0438")
        );

        Map<String, List<String>> hotels = Map.of(
                "\u0422\u0443\u0440\u0446\u0438\u044f", List.of("Rixos Premium Belek", "Maxx Royal Kemer", "Titanic Deluxe", "Calista Luxury"),
                "\u0415\u0433\u0438\u043f\u0435\u0442", List.of("Steigenberger Al Dau", "Sunrise Grand Select", "Cleopatra Luxury"),
                "\u041e\u0410\u042d", List.of("Atlantis The Palm", "Jumeirah Beach", "Rixos The Palm"),
                "\u0422\u0430\u0438\u043b\u0430\u043d\u0434", List.of("Centara Grand", "Hilton Phuket", "Amari Pattaya"),
                "\u0413\u0440\u0443\u0437\u0438\u044f", List.of("Hilton Batumi", "Courtyard by Marriott", "Le Port Batumi"),
                "\u0420\u043e\u0441.\u041a\u0430\u0432\u043a\u0430\u0437", List.of("Heritage Ahtungalla", "Cinnamon Bey", "Taj Bentota")
        );

        Map<String, List<Double>> ratings = Map.of(
                "\u0422\u0443\u0440\u0446\u0438\u044f", List.of(4.8, 4.9, 4.7, 4.5),
                "\u0415\u0433\u0438\u043f\u0435\u0442", List.of(4.6, 4.8, 4.3),
                "\u041e\u0410\u042d", List.of(4.9, 4.7, 4.8),
                "\u0422\u0430\u0438\u043b\u0430\u043d\u0434", List.of(4.7, 4.5, 4.4),
                "\u0413\u0440\u0443\u0437\u0438\u044f", List.of(4.7, 4.5, 4.4),
                "\u0420\u043e\u0441.\u041a\u0430\u0432\u043a\u0430\u0437", List.of(4.6, 4.3, 4.4)
        );

        Map<String, int[]> priceRange = Map.of(
                "\u0422\u0443\u0440\u0446\u0438\u044f", new int[]{85000, 250000},
                "\u0415\u0433\u0438\u043f\u0435\u0442", new int[]{65000, 180000},
                "\u041e\u0410\u042d", new int[]{120000, 400000},
                "\u0422\u0430\u0438\u043b\u0430\u043d\u0434", new int[]{90000, 220000},
                "\u0413\u0440\u0443\u0437\u0438\u044f", new int[]{70000, 180000},
                "\u0420\u043e\u0441.\u041a\u0430\u0432\u043a\u0430\u0437", new int[]{75000, 190000}
        );

        List<String> countryResorts = resorts.getOrDefault(country, List.of("\u041c\u0430\u043b\u0430\u0433\u0430"));
        List<String> countryHotels = hotels.getOrDefault(country, List.of("\u041e\u0442\u0435\u043b\u044c " + country));
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
                    .hotelImage("https://placeholder.co/400x250?text=" + countryHotels.get(idx).replace(" ", "+"))
                    .bookUrl("https://germes-travel.ru/book?hotel=" + idx)
                    .build());
        }

        tours.sort(Comparator.comparingInt(TourDto::getPrice));
        return tours;
    }
}