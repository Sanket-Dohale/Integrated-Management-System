package com.qapla.ERP.Society.interceptor;

import com.qapla.ERP.Society.model.Member;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();
        Member loggedInUser = (Member) session.getAttribute("loggedInUser");

        // Allow login page and static resources
        if (request.getRequestURI().equals("/login") ||
                request.getRequestURI().startsWith("/css/") ||
                request.getRequestURI().startsWith("/js/") ||
                request.getRequestURI().startsWith("/images/")) {
            return true;
        }

        // Redirect to login if not authenticated
        if (loggedInUser == null) {
            response.sendRedirect("/login");
            return false;
        }

        return true;
    }
}

