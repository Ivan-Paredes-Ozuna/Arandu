package com.arandu.repository;

import com.arandu.domain.ActivityProgress;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivityProgressRepository extends JpaRepository<ActivityProgress, Long> {
    List<ActivityProgress> findByEnrollmentId(Long enrollmentId);
    Optional<ActivityProgress> findByEnrollmentIdAndActivityId(Long enrollmentId, Long activityId);
}
