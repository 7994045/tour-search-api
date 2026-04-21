package com.toursearch.controller;

import com.toursearch.model.TourResponse;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/tours")
public class TourController {

    @GetMapping("/search")
    public List<TourResponse> search(
            @RequestParam String country,
            @RequestParam(required = false, defaultValue = "") String dateFrom,
            @RequestParam(required = false, defaultValue = "7") int nights,
            @RequestParam(required = false, defaultValue = "2") int adults,
            @RequestParam(required = false, defaultValue = "0") int children
    ) {
        // TODO: Подключить реальный API туроператоров
        // Пока возвращаем демо-данные
        return getDemoTours(country, nights, adults);
    }

    private List<TourResponse> getDemoTours(String country, int nights, int adults) {
        List<TourResponse> tours = new ArrayList<>();

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

        Map<String, List<Integer>> stars = Map.of(
            "Турция", List.of(5, 5, 5, 4),
            "Египет", List.of(5, 5, 4),
            "ОАЭ", List.of(5, 5, 5),
            "Таиланд", List.of(5, 4, 4),
            "Шри-Ланка", List.of(5, 4, 4)
        );

        Map<String, List<String>> meals = Map.of(
            "Турция", List.of("UAI", "AI", "AI", "AI"),
            "Египет", List.of("AI", "AI", "HB"),
            "ОАЭ", List.of("HB", "BB", "HB"),
            "Таиланд", List.of("BB", "BB", "HB"),
            "Шри-Ланка", List.of("HB", "BB", "HB")
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
        List<Integer> countryStars = stars.getOrDefault(country, List.of(4));
        List<String> countryMeals = meals.getOrDefault(country, List.of("BB"));
        int[] range = priceRange.getOrDefault(country, new int[]{50000, 150000});

        Random rnd = new Random();
        int count = 3 + rnd.nextInt(4); // 3-6 результатов

        for (int i = 0; i < count; i++) {
            int idx = i % countryHotels.size();
            int price = range[0] + rnd.nextInt(range[1] - range[0]);
            price = (price / 1000) * 1000; // округляем

            tours.add(new TourResponse(
                countryHotels.get(idx),
                country,
                countryResorts.get(idx % countryResorts.size()),
                countryStars.get(idx % countryStars.size()),
                nights,
                adults,
                countryMeals.get(idx % countryMeals.size()),
                price
            ));
        }

        tours.sort(Comparator.comparingInt(TourResponse::getPrice));
        return tours;
    }
}