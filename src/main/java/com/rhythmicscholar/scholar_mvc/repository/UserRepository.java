package com.rhythmicscholar.scholar_mvc.repository;

import com.rhythmicscholar.scholar_mvc.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    /** Đếm số user theo role ("USER" hoặc "ADMIN") */
    long countByRole(String role);

    /** Lấy 6 user mới nhất để hiển thị trên admin dashboard */
    List<User> findTop6ByOrderByIdDesc();

    /** Đếm số user theo từng trình độ (currentLevel) — chỉ tính USER, không tính ADMIN. Trả về Object[]{level, count}. */
    @Query("SELECT u.currentLevel, COUNT(u) FROM User u WHERE u.role = 'USER' GROUP BY u.currentLevel ORDER BY COUNT(u) DESC")
    List<Object[]> countByLevel();

    /** Tìm kiếm user theo tên hoặc email (có phân trang). */
    @Query("SELECT u FROM User u WHERE LOWER(u.fullName) LIKE LOWER(CONCAT('%', :q, '%')) OR LOWER(u.email) LIKE LOWER(CONCAT('%', :q, '%'))")
    Page<User> searchByNameOrEmail(@Param("q") String q, Pageable pageable);

    /** Đếm kết quả tìm kiếm. */
    @Query("SELECT COUNT(u) FROM User u WHERE LOWER(u.fullName) LIKE LOWER(CONCAT('%', :q, '%')) OR LOWER(u.email) LIKE LOWER(CONCAT('%', :q, '%'))")
    long countByNameOrEmail(@Param("q") String q);
}
