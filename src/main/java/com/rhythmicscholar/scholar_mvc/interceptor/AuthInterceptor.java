package com.rhythmicscholar.scholar_mvc.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
            @NonNull Object handler)
            throws Exception {
        String uri = request.getRequestURI();
        String contextPath = request.getContextPath();
        int port = request.getServerPort();
        String serverName = request.getServerName();
        String scheme = request.getScheme();

        // Chuẩn hóa đường dẫn
        String path = uri;
        if (contextPath != null && !contextPath.isEmpty() && path.startsWith(contextPath)) {
            path = path.substring(contextPath.length());
        }
        while (path.startsWith("/")) {
            path = path.substring(1);
        }

        String baseUrl8080 = scheme + "://" + serverName + ":8080" + contextPath;
        String baseUrl8081 = scheme + "://" + serverName + ":8081" + contextPath;

        // 1. MỞ KHÓA TÀI NGUYÊN VÀ TRANG BÁO LỖI (Sửa lỗi quay đều tại đây)
        if (isStaticResource(path) || path.startsWith("error")) {
            return true;
        }

        // 2. Khai báo các đường dẫn công khai
        boolean isPublicPath = path.equals("login") || path.equals("login.html") ||
                path.equals("register") || path.equals("register.html") ||
                path.equals("admin/login") || path.equals("admin/login.html");

        // 3. Xử lý phân luồng Cổng (Port)
        if (port == 8081) {
            if (!path.startsWith("admin")) {
                response.sendRedirect(baseUrl8081 + "/admin/login");
                return false;
            }
        } else {
            if (path.startsWith("admin")) {
                response.sendRedirect(baseUrl8081 + "/admin/login");
                return false;
            }
        }

        // Cho phép đi qua nếu là trang công khai
        if (isPublicPath) {
            return true;
        }

        // 4. Kiểm tra đăng nhập
        HttpSession session = request.getSession();
        Object userId = session.getAttribute("userId");
        Object userRole = session.getAttribute("userRole");

        if (userId == null) {
            if (path.startsWith("admin")) {
                response.sendRedirect(baseUrl8081 + "/admin/login");
            } else {
                response.sendRedirect(baseUrl8080 + "/login");
            }
            return false;
        }

        // 5. Phân quyền Admin
        if (path.startsWith("admin")) {
            if (!"ADMIN".equals(userRole)) {
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