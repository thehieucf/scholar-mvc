package com.rhythmicscholar.scholar_mvc;

import com.rhythmicscholar.scholar_mvc.service.BadgeService;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * Lớp khởi động chính của ứng dụng Rhythmic Scholar.
 */
@SpringBootApplication
public class ScholarMvcApplication {

	public static void main(String[] args) {
		SpringApplication.run(ScholarMvcApplication.class, args);
	}

	/**
	 * Seed dữ liệu badge mặc định khi ứng dụng khởi động lần đầu.
	 */
	@Bean
	public ApplicationRunner seedBadges(BadgeService badgeService) {
		return args -> badgeService.seedDefaultBadges();
	}
}
