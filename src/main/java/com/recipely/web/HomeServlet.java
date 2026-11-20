package com.recipely.web;

import com.recipely.service.MetricsCache;
import com.recipely.service.RecipeFilter;
import com.recipely.service.RecipeService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet({"", "/home"})
public final class HomeServlet extends BaseServlet {
    @Override protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, jakarta.servlet.ServletException {
        RecipeService recipes = (RecipeService) getServletContext().getAttribute(AppKeys.RECIPE_SERVICE);
        MetricsCache metrics = (MetricsCache) getServletContext().getAttribute(AppKeys.METRICS_CACHE);
        int maxMinutes = parseInt(request.getParameter("time"));
        request.setAttribute("recipes", recipes.discover(new RecipeFilter(request.getParameter("q"), request.getParameter("nation"), request.getParameter("taste"), maxMinutes)));
        request.setAttribute("metrics", metrics.snapshot());
        request.setAttribute("user", currentUser(request));
        request.setAttribute("flash", request.getSession().getAttribute("flash"));
        request.getSession().removeAttribute("flash");
        view(request, response, "home");
    }

    private int parseInt(String value) { try { return value == null || value.isBlank() ? 0 : Integer.parseInt(value); } catch (NumberFormatException exception) { return 0; } }
}
