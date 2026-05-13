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
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
            @NonNull Object handler)
            throws Exception {
        String uri = request.getRequestURI();
        String contextPath = request.getContextPath();
        int port = request.getServerPort();
        String serverName = request.getServerName();
        String scheme = request.getScheme();

        // 1. Chuẩn hóa đường dẫn
        String path = uri;
        if (contextPath != null && !contextPath.isEmpty() && path.startsWith(contextPath)) {
            path = path.substring(contextPath.length());
        }
        while (path.startsWith("/")) {
            path = path.substring(1);
        }

        logger.info(">>> Interceptor: Request URI: {}, Path: {}, Port: {}", uri, path, port);

        String cleanContextPath = (contextPath == null || contextPath.equals("/")) ? "" : contextPath;
        String baseUrl8080 = scheme + "://" + serverName + ":8080" + cleanContextPath;
        String baseUrl8081 = scheme + "://" + serverName + ":8081" + cleanContextPath;

        // 2. Tài nguyên tĩnh
        if (isStaticResource(path) || path.startsWith("error")) {
            return true;
        }

        // 3. Phân luồng Cổng
        if (port == 8081) {
            if (!path.startsWith("admin")) {
                logger.info("Redirecting non-admin request on 8081 to /admin/login");
                response.sendRedirect(baseUrl8081 + "/admin/login");
                return false;
            }
        } else {
            if (path.startsWith("admin")) {
                logger.info("Redirecting admin request on user port to 8081");
                response.sendRedirect(baseUrl8081 + "/admin/login");
                return false;
            }
        }

        // 4. Các trang công khai
        boolean isPublicPath = path.equals("login") || path.equals("login.html") ||
                path.equals("register") || path.equals("register.html") ||
                path.equals("admin/login") || path.equals("admin/login.html") ||
                path.equals("admin/logout") ||
                path.isEmpty(); // Thêm trang chủ trống

        if (isPublicPath) {
            return true;
        }

        // 5. Kiểm tra đăng nhập
        HttpSession session = request.getSession();
        Object userId = session.getAttribute("userId");

        if (userId == null) {
            if (path.startsWith("admin")) {
                response.sendRedirect(baseUrl8081 + "/admin/login");
            } else {
                logger.info("User not logged in, redirecting to /login");
                response.sendRedirect(baseUrl8080 + "/login");
            }
            return false;
        }

        return true;
    }

    private boolean isStaticResource(String path) {
        return path.startsWith("css/") || path.startsWith("js/") ||
                path.startsWith("images/") || path.equals("favicon.ico") ||
                path.startsWith("webjars/") || path.startsWith("uploads/");
    }
}
