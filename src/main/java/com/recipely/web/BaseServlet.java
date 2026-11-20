package com.recipely.web;

import com.recipely.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public abstract class BaseServlet extends HttpServlet {
    protected void view(HttpServletRequest request, HttpServletResponse response, String page) throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/" + page + ".jsp").forward(request, response);
    }

    protected User currentUser(HttpServletRequest request) {
        Object value = request.getSession().getAttribute(AppKeys.CURRENT_USER);
        return value instanceof User user ? user : null;
    }

    protected void flash(HttpServletRequest request, String message) { request.getSession().setAttribute("flash", message); }
}
