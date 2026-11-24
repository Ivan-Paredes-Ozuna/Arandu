package com.arandu.repository;

import com.arandu.domain.Activity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivityRepository extends JpaRepository<Activity, Long> {
    List<Activity> findByCourseIdOrderByOrderIndexAsc(Long courseId);
}
