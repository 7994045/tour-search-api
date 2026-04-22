package com.toursearch.service;

import org.springframework.stereotype.Service;
import java.util.concurrent.atomic.AtomicLong;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

@Service
public class StatsService {
    private final AtomicLong totalSearches = new AtomicLong(0);
    private final AtomicLong totalErrors = new AtomicLong(0);
    private final List<SearchRecord> recentSearches = new ArrayList<>();
    private final List<ErrorRecord> recentErrors = new ArrayList<>();
    private static final int MAX_RECORDS = 50;

    public void recordSearch(String country, String city) {
        totalSearches.incrementAndGet();
        synchronized (recentSearches) {
            recentSearches.add(new SearchRecord(country, city, LocalDateTime.now()));
            if (recentSearches.size() > MAX_RECORDS) recentSearches.remove(0);
        }
    }

    public void recordError(String message) {
        totalErrors.incrementAndGet();
        synchronized (recentErrors) {
            recentErrors.add(new ErrorRecord(message, LocalDateTime.now()));
            if (recentErrors.size() > MAX_RECORDS) recentErrors.remove(0);
        }
    }

    public long getTotalSearches() { return totalSearches.get(); }
    public long getTotalErrors() { return totalErrors.get(); }
    public List<SearchRecord> getRecentSearches() {
        synchronized (recentSearches) { return new ArrayList<>(recentSearches); }
    }
    public List<ErrorRecord> getRecentErrors() {
        synchronized (recentErrors) { return new ArrayList<>(recentErrors); }
    }

    public static class SearchRecord {
        public final String country;
        public final String city;
        public final LocalDateTime time;
        public SearchRecord(String country, String city, LocalDateTime time) {
            this.country = country; this.city = city; this.time = time;
        }
    }

    public static class ErrorRecord {
        public final String message;
        public final LocalDateTime time;
        public ErrorRecord(String message, LocalDateTime time) {
            this.message = message; this.time = time;
        }
    }
}