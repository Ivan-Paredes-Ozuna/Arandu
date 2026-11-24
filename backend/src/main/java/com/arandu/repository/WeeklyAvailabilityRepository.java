package com.arandu.repository;

import com.arandu.domain.WeeklyAvailability;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WeeklyAvailabilityRepository extends JpaRepository<WeeklyAvailability, Long> {
    Optional<WeeklyAvailability> findByUserId(Long userId);
}
