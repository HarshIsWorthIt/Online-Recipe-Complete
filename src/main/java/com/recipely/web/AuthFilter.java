package com.recipely.web;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter({"/recipes/create", "/admin"})
public final class AuthFilter implements Filter {
    @Override public void doFilter(jakarta.servlet.ServletRequest request, jakarta.servlet.ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        if (httpRequest.getSession().getAttribute(AppKeys.CURRENT_USER) == null) {
            httpRequest.getSession().setAttribute("flash", "Please sign in to continue.");
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/auth?mode=login");
            return;
        }
        chain.doFilter(request, response);
    }
}
