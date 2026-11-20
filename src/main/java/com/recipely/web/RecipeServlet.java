package com.recipely.web;

import com.recipely.exception.ValidationException;
import com.recipely.model.User;
import com.recipely.service.RecipeService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/recipes/create")
public final class RecipeServlet extends BaseServlet {
    @Override protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, jakarta.servlet.ServletException {
        User user = currentUser(request);
        if (!user.canSubmitRecipes()) { flash(request, "Your role can explore recipes but cannot submit one."); response.sendRedirect(request.getContextPath() + "/"); return; }
        request.setAttribute("user", user);
        view(request, response, "recipe-form");
    }

    @Override protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, jakarta.servlet.ServletException {
        User user = currentUser(request);
        RecipeService recipes = (RecipeService) getServletContext().getAttribute(AppKeys.RECIPE_SERVICE);
        try {
            recipes.submit(user, request.getParameter("title"), request.getParameter("description"), request.getParameter("ingredients"),
                request.getParameter("instructions"), request.getParameter("category"), request.getParameter("cuisine"),
                request.getParameter("nation"), request.getParameter("taste"), parseInt(request.getParameter("prepMinutes")), parseInt(request.getParameter("cookMinutes")));
            flash(request, user.canModerateRecipes() ? "Recipe published." : "Recipe submitted for administrator approval.");
            response.sendRedirect(request.getContextPath() + "/");
        } catch (ValidationException exception) {
            request.setAttribute("error", exception.getMessage());
            request.setAttribute("user", user);
            view(request, response, "recipe-form");
        }
    }

    private int parseInt(String value) { try { return Integer.parseInt(value); } catch (NumberFormatException exception) { return 0; } }
}
