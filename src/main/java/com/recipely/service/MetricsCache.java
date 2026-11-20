package com.recipely.service;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

/** Thread-safe shared cache refreshed by the scheduled background task. */
public final class MetricsCache {
    private final Map<String, Long> values = new LinkedHashMap<>();
    private Instant refreshedAt = Instant.EPOCH;

    public synchronized void replace(Map<String, Long> nextValues) {
        values.clear();
        values.putAll(nextValues);
        refreshedAt = Instant.now();
    }

    public synchronized Map<String, Long> snapshot() { return Map.copyOf(values); }
    public synchronized Instant getRefreshedAt() { return refreshedAt; }
}
