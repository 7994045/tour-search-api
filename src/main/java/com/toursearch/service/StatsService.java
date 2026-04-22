package com.toursearch.service;

import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.ConcurrentLinkedDeque;

@Service
public class StatsService {

    private final AtomicLong totalSearches = new AtomicLong(0);
    private final AtomicLong totalUsers = new AtomicLong(0);
    private final AtomicLong totalErrors = new AtomicLong(0);
    private final ConcurrentLinkedDeque<LogEntry> recentSearches = new ConcurrentLinkedDeque<>();
    private final ConcurrentLinkedDeque<LogEntry> recentErrors = new ConcurrentLinkedDeque<>();

    private static final int MAX_LOGS = 50;
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd.MM HH:mm");

    public void recordSearch(String country, int resultsCount) {
        totalSearches.incrementAndGet();
        recentSearches.addFirst(new LogEntry(country + " — " + resultsCount + " рез.", LocalDateTime.now()));
        while (recentSearches.size() > MAX_LOGS) recentSearches.removeLast();
    }

    public void recordUser(long chatId, String username) {
        totalUsers.incrementAndGet();
    }

    public void recordError(String error) {
        totalErrors.incrementAndGet();
        recentErrors.addFirst(new LogEntry(error, LocalDateTime.now()));
        while (recentErrors.size() > MAX_LOGS) recentErrors.removeLast();
    }

    public long getTotalSearches() { return totalSearches.get(); }
    public long getTotalUsers() { return totalUsers.get(); }
    public long getTotalErrors() { return totalErrors.get(); }

    public String getFullStats() {
        StringBuilder sb = new StringBuilder();
        sb.append("\u1f4ca Статистика Germes Travel:\n\n");
        sb.append("\u1f50d Поисков: ").append(totalSearches.get()).append("\n");
        sb.append("\u1f465 Пользователей: ").append(totalUsers.get()).append("\n");
        sb.append("\u274c Ошибок: ").append(totalErrors.get()).append("\n\n");
        sb.append("Последние поиски:\n");
        int count = 0;
        for (LogEntry log : recentSearches) {
            if (count++ >= 5) break;
            sb.append("\u2022 ").append(log.message).append(" (").append(log.time.format(FMT)).append(")\n");
        }
        return sb.toString();
    }

    public String getUsersStats() {
        return "\u1f465 Всего пользователей: " + totalUsers.get();
    }

    public String getSearchesStats() {
        StringBuilder sb = new StringBuilder();
        sb.append("\u1f50d Всего поисков: ").append(totalSearches.get()).append("\n\nПоследние:\n");
        for (LogEntry log : recentSearches) {
            sb.append("\u2022 ").append(log.message).append(" (").append(log.time.format(FMT)).append(")\n");
        }
        return sb.toString();
    }

    public String getErrorsStats() {
        if (recentErrors.isEmpty()) return "\u2705 Ошибок нет!";
        StringBuilder sb = new StringBuilder();
        sb.append("\u274c Всего ошибок: ").append(totalErrors.get()).append("\n\nПоследние:\n");
        for (LogEntry log : recentErrors) {
            sb.append("\u2022 ").append(log.message).append(" (").append(log.time.format(FMT)).append(")\n");
        }
        return sb.toString();
    }

    private static class LogEntry {
        final String message;
        final LocalDateTime time;
        LogEntry(String message, LocalDateTime time) {
            this.message = message;
            this.time = time;
        }
    }
}