package com.rhythmicscholar.scholar_mvc.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

/**
 * Xử lý ngoại lệ toàn cục cho ứng dụng.
 * Ngăn chặn lỗi "Cannot render error page ... response has already been committed"
 * bằng cách kiểm tra trạng thái response trước khi cố gắng render trang lỗi.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public Object handleException(Exception ex,
                                  HttpServletRequest request,
                                  HttpServletResponse response) {
        log.error("=== EXCEPTION for [{}] ===", request.getRequestURI(), ex);

        // Nếu response đã committed (ví dụ: Thymeleaf đã bắt đầu render),
        // không thể redirect hoặc forward → chỉ log lỗi
        if (response.isCommitted()) {
            log.warn("Response already committed for [{}], cannot redirect to error page.",
                     request.getRequestURI());
            return null;
        }

        // Nếu là request từ khu vực admin, redirect về dashboard
        String uri = request.getRequestURI();
        if (uri != null && uri.startsWith("/admin")) {
            return new ModelAndView("redirect:/admin/dashboard");
        }

        // Các request khác, redirect về trang chủ
        return new ModelAndView("redirect:/");
    }
}
