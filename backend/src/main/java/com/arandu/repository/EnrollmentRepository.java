package com.arandu.repository;

import com.arandu.domain.Enrollment;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    List<Enrollment> findByUserIdOrderByLastAccessedAtDesc(Long userId);
    Optional<Enrollment> findByIdAndUserId(Long id, Long userId);
    Optional<Enrollment> findByUserIdAndCourseId(Long userId, Long courseId);
    boolean existsByUserIdAndCourseId(Long userId, Long courseId);
}
