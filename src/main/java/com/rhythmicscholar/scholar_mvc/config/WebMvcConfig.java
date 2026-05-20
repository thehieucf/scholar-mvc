package com.rhythmicscholar.scholar_mvc.config;

import com.rhythmicscholar.scholar_mvc.interceptor.AuthInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Lớp cấu hình Spring MVC cho ứng dụng Rhythmic Scholar.
 * Đăng ký các interceptor và tùy chỉnh hành vi của DispatcherServlet.
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    /** Interceptor kiểm tra xác thực người dùng trước mỗi request. */
    @Autowired
    private AuthInterceptor authInterceptor;

    /**
     * Đăng ký AuthInterceptor vào chuỗi xử lý request của Spring MVC.
     * Interceptor này sẽ được gọi trước khi request đến controller.
     *
     * @param registry Đối tượng registry để thêm interceptor
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Áp dụng AuthInterceptor cho tất cả các đường dẫn (/**) theo mặc định
        registry.addInterceptor(authInterceptor);
    }
}
