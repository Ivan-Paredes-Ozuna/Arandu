package com.arandu.repository;

import com.arandu.domain.Course;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findByCategoryIgnoreCase(String category);
    List<Course> findByLevelIgnoreCase(String level);
}
