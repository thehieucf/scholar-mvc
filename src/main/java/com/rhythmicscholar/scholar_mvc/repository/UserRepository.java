package com.rhythmicscholar.scholar_mvc.repository;

import com.rhythmicscholar.scholar_mvc.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    /** Đếm số user theo role ("USER" hoặc "ADMIN") */
    long countByRole(String role);

    /** Lấy 5 user mới nhất để hiển thị trên admin dashboard */
    List<User> findTop5ByOrderByIdDesc();
}
