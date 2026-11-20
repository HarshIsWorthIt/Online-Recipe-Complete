package com.recipely.service;

import com.recipely.model.RecipeStatus;
import com.recipely.repository.RecipeRepository;
import com.recipely.repository.UserRepository;
import java.util.LinkedHashMap;
import java.util.Map;

/** Runnable scheduled by the application listener to refresh dashboard counters. */
public final class MetricsRefreshTask implements Runnable {
    private final UserRepository users;
    private final RecipeRepository recipes;
    private final MetricsCache cache;

    public MetricsRefreshTask(UserRepository users, RecipeRepository recipes, MetricsCache cache) {
        this.users = users;
        this.recipes = recipes;
        this.cache = cache;
    }

    @Override public void run() {
        Map<String, Long> next = new LinkedHashMap<>();
        next.put("users", (long) users.findAll().size());
        next.put("approved", recipes.countByStatus(RecipeStatus.APPROVED));
        next.put("pending", recipes.countByStatus(RecipeStatus.PENDING));
        cache.replace(next);
    }
}
