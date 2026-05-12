package com.rhythmicscholar.scholar_mvc.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(AuthInterceptor.class);

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler)
            throws Exception {
        String uri = request.getRequestURI();
        String contextPath = request.getContextPath();
        int port = request.getServerPort();
        String serverName = request.getServerName();
        String scheme = request.getScheme();

        // Normalize URI: remove context path and leading slash
        String path = uri;
        if (contextPath != null && !contextPath.isEmpty() && path.startsWith(contextPath)) {
            path = path.substring(contextPath.length());
        }
        while (path.startsWith("/")) {
            path = path.substring(1);
        }

        logger.debug("Request URI: {}, Normalized Path: {}, Port: {}", uri, path, port);

        // Base URLs for port switching
        String baseUrl8080 = scheme + "://" + serverName + ":8080" + contextPath;
        String baseUrl8081 = scheme + "://" + serverName + ":8081" + contextPath;

        // --- Logic Phân Cổng ---
        
        if (port == 8081) {
            // Cổng Admin: Chỉ cho phép admin path và static resources
            if (!path.startsWith("admin") && !isStaticResource(path)) {
                logger.info("Non-admin request on port 8081, redirecting to admin login");
                response.sendRedirect(baseUrl8081 + "/admin/login");
                return false;
            }
        } else {
            // Cổng User (thường là 8080): Không cho phép admin path
            if (path.startsWith("admin")) {
                logger.info("Admin request on port {}, redirecting to port 8081", port);
                response.sendRedirect(baseUrl8081 + "/admin/login");
                return false;
            }
            
            // Nếu không phải 8080 (ví dụ 80), chuyển hướng về 8080 cho trang user
            if (port != 8080 && !isStaticResource(path)) {
                logger.info("User request on port {}, redirecting to port 8080", port);
                response.sendRedirect(baseUrl8080 + "/login");
                return false;
            }
        }

        // --- Logic Xác Thực ---

        // Allowed paths that don't require authentication
        if (path.isEmpty() || // Allow root but we handle redirect below if not logged in
            path.equals("login") || path.equals("login.html") ||
            path.equals("register") || path.equals("register.html") ||
            path.equals("admin/login") || path.equals("admin/login.html") ||
            isStaticResource(path)) {
            return true;
        }

        HttpSession session = request.getSession();
        Object userId = session.getAttribute("userId");
        Object userRole = session.getAttribute("userRole");

        if (userId == null) {
            logger.info("Unauthorized access to {}, redirecting to login", path);
            if (path.startsWith("admin")) {
                response.sendRedirect(baseUrl8081 + "/admin/login");
            } else {
                // Sử dụng đường dẫn tương đối nếu đang ở đúng cổng
                if (port == 8080) {
                    response.sendRedirect(contextPath + "/login");
                } else {
                    response.sendRedirect(baseUrl8080 + "/login");
                }
            }
            return false;
        }

        // Kiểm tra quyền Admin
        if (path.startsWith("admin")) {
            if (!"ADMIN".equals(userRole)) {
                logger.warn("User {} tried to access admin path without ADMIN role", userId);
                response.sendRedirect(contextPath + "/?error=nopermission");
                return false;
            }
        }

        return true;
    }

    private boolean isStaticResource(String path) {
        return path.startsWith("css/") || path.startsWith("js/") ||
                path.startsWith("images/") || path.equals("favicon.ico") ||
                path.startsWith("webjars/") || path.startsWith("uploads/");
    }
}
