package com.toursearch.tour_search_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.toursearch")
public class TourSearchApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(TourSearchApiApplication.class, args);
    }
}