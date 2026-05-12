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

        // 1. Chuẩn hóa URI: Xóa contextPath và dấu '/' ở đầu
        String path = uri;
        if (contextPath != null && !contextPath.isEmpty() && path.startsWith(contextPath)) {
            path = path.substring(contextPath.length());
        }
        while (path.startsWith("/")) {
            path = path.substring(1);
        }

        logger.debug("Request URI: {}, Normalized Path: {}, Port: {}", uri, path, port);

        // Các URL chuẩn để chuyển hướng
        String baseUrl8080 = scheme + "://" + serverName + ":8080" + contextPath;
        String baseUrl8081 = scheme + "://" + serverName + ":8081" + contextPath;

        // 2. Luôn cho phép các file tĩnh (CSS, JS, Images...) đi qua
        if (isStaticResource(path)) {
            return true;
        }

        // 3. Khai báo các đường dẫn công khai không cần đăng nhập
        boolean isPublicPath = path.equals("login") || path.equals("login.html") ||
                path.equals("register") || path.equals("register.html") ||
                path.equals("admin/login") || path.equals("admin/login.html");

        // 4. Kiểm tra luồng Cổng (Port Routing)
        if (port == 8081) {
            // Nếu ở cổng 8081 mà không phải trang admin thì đẩy về trang đăng nhập admin
            if (!path.startsWith("admin")) {
                response.sendRedirect(baseUrl8081 + "/admin/login");
                return false;
            }
        } else {
            // Nếu ở cổng 8080 (hoặc cổng khác) mà vào đường dẫn admin thì đẩy sang cổng 8081
            if (path.startsWith("admin")) {
                response.sendRedirect(baseUrl8081 + "/admin/login");
                return false;
            }
        }

        // --- Chặn lỗi lặp vô hạn (Infinite Loop) ---
        // Nếu là URL công khai thì cho qua luôn tại đây, không kiểm tra session nữa
        if (isPublicPath) {
            return true;
        }

        // 5. Kiểm tra Xác Thực (Authentication)
        HttpSession session = request.getSession();
        Object userId = session.getAttribute("userId");
        Object userRole = session.getAttribute("userRole");

        if (userId == null) {
            logger.info("Người dùng chưa đăng nhập, truy cập path: {}. Chuyển hướng về trang đăng nhập.", path);
            if (path.startsWith("admin")) {
                response.sendRedirect(baseUrl8081 + "/admin/login");
            } else {
                response.sendRedirect(baseUrl8080 + "/login");
            }
            return false;
        }

        // 6. Kiểm tra Phân Quyền (Authorization) cho Admin
        if (path.startsWith("admin")) {
            // Kiểm tra role: Đảm bảo trong Database cột role của bạn được lưu là "ADMIN"
            if (!"ADMIN".equals(userRole)) {
                logger.warn("Tài khoản ID {} cố gắng truy cập trang Admin nhưng không có quyền", userId);
                response.sendRedirect(baseUrl8080 + "/?error=nopermission");
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