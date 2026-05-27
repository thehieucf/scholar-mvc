package com.rhythmicscholar.scholar_mvc.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Interceptor kiểm tra xác thực và phân quyền người dùng.
 * Được gọi trước mỗi request để đảm bảo:
 *   - Người dùng đã đăng nhập trước khi truy cập các trang bảo vệ.
 *   - Chỉ ADMIN mới được truy cập khu vực /admin/*.
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {

    /**
     * Xử lý trước khi request được chuyển đến controller.
     * Trả về true để tiếp tục xử lý, false để dừng và redirect.
     *
     * @param request  HTTP request hiện tại
     * @param response HTTP response để ghi redirect nếu cần
     * @param handler  Handler (controller) sẽ xử lý request
     * @return true nếu cho phép tiếp tục, false nếu chặn lại
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // Nếu response đã được commit, không thể redirect → bỏ qua
        if (response.isCommitted()) {
            return false;
        }

        // Lấy URI của request và loại bỏ context path nếu có
        String uri = request.getRequestURI();
        String contextPath = request.getContextPath();
        if (contextPath != null && !contextPath.isEmpty() && uri.startsWith(contextPath)) {
            uri = uri.substring(contextPath.length());
        }
        // Loại bỏ dấu "/" đầu tiên để dễ so sánh
        if (uri.startsWith("/")) {
            uri = uri.substring(1);
        }

        // Các path không cần xác thực: trang login, register và tài nguyên tĩnh
        if (uri.equals("login") || uri.equals("login.html") ||
            uri.equals("register") || uri.equals("register.html") ||
            uri.startsWith("css/") || uri.startsWith("js/") ||
            uri.startsWith("images/") || uri.equals("favicon.ico") ||
            uri.startsWith("webjars/") || uri.startsWith("error")) {
            return true; // Cho phép truy cập tự do
        }

        // Lấy userId từ session để kiểm tra trạng thái đăng nhập
        HttpSession session = request.getSession();
        Long userId = (Long) session.getAttribute("userId");

        // Chưa đăng nhập → redirect về trang login
        if (userId == null) {
            if (!response.isCommitted()) {
                response.sendRedirect(request.getContextPath() + "/login");
            }
            return false;
        }

        // Kiểm tra quyền truy cập khu vực admin (/admin/*)
        if (uri.startsWith("admin/")) {
            String role = (String) session.getAttribute("userRole");
            if (!"ADMIN".equals(role)) {
                // User thường cố truy cập admin → redirect về trang chủ user
                if (!response.isCommitted()) {
                    response.sendRedirect(request.getContextPath() + "/");
                }
                return false;
            }
        }

        // Đã xác thực và có đủ quyền → cho phép tiếp tục
        return true;
    }
}
