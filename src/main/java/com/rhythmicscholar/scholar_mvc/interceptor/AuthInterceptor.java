package com.rhythmicscholar.scholar_mvc.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();
        String contextPath = request.getContextPath();
        if (contextPath != null && !contextPath.isEmpty() && uri.startsWith(contextPath)) {
            uri = uri.substring(contextPath.length());
        }
        if (uri.startsWith("/")) {
            uri = uri.substring(1);
        }

        // Các path không cần xác thực
        if (uri.equals("login") || uri.equals("login.html") ||
            uri.equals("register") || uri.equals("register.html") ||
            uri.startsWith("css/") || uri.startsWith("js/") ||
            uri.startsWith("images/") || uri.equals("favicon.ico") ||
            uri.startsWith("webjars/")) {
            return true;
        }

        HttpSession session = request.getSession();
        Long userId = (Long) session.getAttribute("userId");

        // Chưa đăng nhập → redirect về login
        if (userId == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return false;
        }

        // Kiểm tra quyền truy cập khu vực admin
        if (uri.startsWith("admin/")) {
            String role = (String) session.getAttribute("userRole");
            if (!"ADMIN".equals(role)) {
                // User thường cố truy cập admin → redirect về trang chủ user
                response.sendRedirect(request.getContextPath() + "/");
                return false;
            }
        }

        return true;
    }
}
