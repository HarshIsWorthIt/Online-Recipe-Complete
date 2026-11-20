package com.recipely.web;

import com.recipely.repository.DatabaseInitializer;
import com.recipely.repository.DatabaseManager;
import com.recipely.repository.RecipeRepository;
import com.recipely.repository.UserRepository;
import com.recipely.service.AuthService;
import com.recipely.service.MetricsCache;
import com.recipely.service.MetricsRefreshTask;
import com.recipely.service.RecipeService;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@WebListener
public final class AppContextListener implements ServletContextListener {
    @Override public void contextInitialized(ServletContextEvent event) {
        ServletContext context = event.getServletContext();
        DatabaseManager database = new DatabaseManager();
        new DatabaseInitializer(database).initialize();
        UserRepository users = new UserRepository(database);
        RecipeRepository recipes = new RecipeRepository(database);
        context.setAttribute(AppKeys.AUTH_SERVICE, new AuthService(users));
        context.setAttribute(AppKeys.RECIPE_SERVICE, new RecipeService(recipes));

        MetricsCache cache = new MetricsCache();
        MetricsRefreshTask refreshTask = new MetricsRefreshTask(users, recipes, cache);
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(refreshTask, 0, 60, TimeUnit.SECONDS);
        context.setAttribute(AppKeys.METRICS_CACHE, cache);
        context.setAttribute(AppKeys.EXECUTOR, executor);
    }

    @Override public void contextDestroyed(ServletContextEvent event) {
        Object service = event.getServletContext().getAttribute(AppKeys.EXECUTOR);
        if (service instanceof ScheduledExecutorService executor) executor.shutdownNow();
    }
}
