package com.recipely.web;

import com.recipely.exception.ValidationException;
import com.recipely.model.Role;
import com.recipely.model.User;
import com.recipely.service.AuthService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/auth")
public final class AuthServlet extends BaseServlet {
    @Override protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, jakarta.servlet.ServletException {
        request.setAttribute("mode", "register".equals(request.getParameter("mode")) ? "register" : "login");
        request.setAttribute("flash", request.getSession().getAttribute("flash"));
        request.getSession().removeAttribute("flash");
        view(request, response, "auth");
    }

    @Override protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, jakarta.servlet.ServletException {
        AuthService auth = (AuthService) getServletContext().getAttribute(AppKeys.AUTH_SERVICE);
        String action = request.getParameter("action");
        try {
            User user;
            if ("register".equals(action)) {
                Role role = Role.valueOf(request.getParameter("role"));
                user = auth.register(request.getParameter("name"), request.getParameter("email"), request.getParameter("password"), role);
                flash(request, "Account created. Welcome to Recipely.");
            } else {
                user = auth.login(request.getParameter("email"), request.getParameter("password"))
                    .orElseThrow(() -> new ValidationException("Incorrect email or password."));
                flash(request, "Welcome back, " + user.getName() + ".");
            }
            request.getSession().setAttribute(AppKeys.CURRENT_USER, user);
            response.sendRedirect(request.getContextPath() + "/");
        } catch (IllegalArgumentException | ValidationException exception) {
            request.setAttribute("error", exception.getMessage());
            request.setAttribute("mode", "register".equals(action) ? "register" : "login");
            view(request, response, "auth");
        }
    }
}
