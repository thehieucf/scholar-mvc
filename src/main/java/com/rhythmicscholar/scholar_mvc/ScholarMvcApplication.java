package com.rhythmicscholar.scholar_mvc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Lớp khởi động chính của ứng dụng Rhythmic Scholar.
 * Sử dụng @SpringBootApplication để kích hoạt tự động cấu hình Spring Boot,
 * quét component và cấu hình JPA.
 */
@SpringBootApplication
public class ScholarMvcApplication {

	/**
	 * Điểm vào (entry point) của ứng dụng.
	 * Khởi chạy Spring Boot với cấu hình mặc định.
	 *
	 * @param args Tham số dòng lệnh (không bắt buộc)
	 */
	public static void main(String[] args) {
		SpringApplication.run(ScholarMvcApplication.class, args);
	}

}
