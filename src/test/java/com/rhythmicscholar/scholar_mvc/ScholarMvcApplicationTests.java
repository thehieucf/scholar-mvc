package com.rhythmicscholar.scholar_mvc;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Integration test cơ bản cho ứng dụng Scholar MVC.
 *
 * <p>Annotation {@code @SpringBootTest} yêu cầu Spring khởi động toàn bộ
 * ApplicationContext (load tất cả Bean, kết nối database, v.v.).</p>
 *
 * <p>Test {@code contextLoads()} chỉ kiểm tra rằng Spring Boot có thể
 * khởi động thành công mà không có lỗi cấu hình nào.
 * Đây là bước kiểm tra tối thiểu nhưng quan trọng khi làm việc nhóm.</p>
 *
 * <p><b>Lưu ý cho người mới:</b> Để chạy test này, bạn cần có database
 * MySQL đang chạy với cấu hình đúng trong application.properties.</p>
 */
@SpringBootTest
class ScholarMvcApplicationTests {

	@Test
	void contextLoads() {
		// Test này không có assert — nếu chạy được mà không ném exception
		// thì nghĩa là toàn bộ cấu hình Spring Boot đã đúng.
	}

}
