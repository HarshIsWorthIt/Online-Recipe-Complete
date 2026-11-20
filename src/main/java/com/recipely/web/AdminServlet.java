package com.recipely.web;

import com.recipely.exception.ValidationException;
import com.recipely.model.RecipeStatus;
import com.recipely.model.User;
import com.recipely.service.MetricsCache;
import com.recipely.service.RecipeService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/admin")
public final class AdminServlet extends BaseServlet {
    @Override protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, jakarta.servlet.ServletException {
        User user = currentUser(request);
        if (!user.canModerateRecipes()) { flash(request, "Administrator access is required."); response.sendRedirect(request.getContextPath() + "/"); return; }
        RecipeService recipes = (RecipeService) getServletContext().getAttribute(AppKeys.RECIPE_SERVICE);
        request.setAttribute("pendingRecipes", recipes.pendingRecipes());
        request.setAttribute("metrics", ((MetricsCache) getServletContext().getAttribute(AppKeys.METRICS_CACHE)).snapshot());
        request.setAttribute("user", user);
        view(request, response, "admin");
    }

    @Override protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        User user = currentUser(request);
        RecipeService recipes = (RecipeService) getServletContext().getAttribute(AppKeys.RECIPE_SERVICE);
        try {
            long recipeId = Long.parseLong(request.getParameter("recipeId"));
            RecipeStatus status = RecipeStatus.valueOf(request.getParameter("status"));
            recipes.moderate(user, recipeId, status);
            flash(request, "Recipe status updated to " + status.name().toLowerCase() + ".");
        } catch (IllegalArgumentException | ValidationException exception) {
            flash(request, exception.getMessage());
        }
        response.sendRedirect(request.getContextPath() + "/admin");
    }
}
