package com.rhythmicscholar.scholar_mvc.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import org.springframework.stereotype.Component;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Filter bọc response bằng một buffer lớn hơn để đảm bảo response không bị commit
 * trước khi Thymeleaf hoàn thành render toàn bộ template.
 * 
 * Khi buffer size mặc định của Tomcat (~8KB) bị vượt quá, Tomcat flush response
 * ra client. Nếu sau đó Thymeleaf ném exception, Spring Boot không thể render
 * error page vì response đã committed → gây lỗi "Cannot render error page".
 *
 * Filter này tăng buffer size lên 256KB để đủ chứa các template lớn.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ResponseBufferFilter implements Filter {

    private static final int BUFFER_SIZE = 256 * 1024; // 256 KB

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (response instanceof HttpServletResponse httpResponse) {
            // Tăng buffer size để tránh commit response quá sớm
            httpResponse.setBufferSize(BUFFER_SIZE);
        }
        chain.doFilter(request, response);
    }
}
