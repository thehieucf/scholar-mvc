package com.rhythmicscholar.scholar_mvc.config;

import com.rhythmicscholar.scholar_mvc.interceptor.AuthInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.util.Locale;

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
     * Cấu hình LocaleResolver dùng Session để lưu ngôn ngữ người dùng đã chọn.
     * Mặc định là tiếng Anh (en).
     */
    @Bean
    public LocaleResolver localeResolver() {
        SessionLocaleResolver resolver = new SessionLocaleResolver();
        resolver.setDefaultLocale(Locale.ENGLISH);
        return resolver;
    }

    /**
     * Interceptor lắng nghe tham số ?lang=en hoặc ?lang=vi trên mọi request
     * để thay đổi ngôn ngữ hiển thị.
     */
    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor interceptor = new LocaleChangeInterceptor();
        interceptor.setParamName("lang");
        return interceptor;
    }

    /**
     * Đăng ký AuthInterceptor và LocaleChangeInterceptor vào chuỗi xử lý request.
     *
     * @param registry Đối tượng registry để thêm interceptor
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // LocaleChangeInterceptor phải đăng ký trước AuthInterceptor
        registry.addInterceptor(localeChangeInterceptor());
        // Áp dụng AuthInterceptor cho tất cả các đường dẫn (/**) theo mặc định
        // Loại trừ /error để Spring Boot có thể render trang lỗi mặc định
        registry.addInterceptor(authInterceptor)
                .excludePathPatterns("/error", "/error/**");
    }
}
