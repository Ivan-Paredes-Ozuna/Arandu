package com.arandu.repository;

import com.arandu.domain.StudyPlanItem;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

public interface StudyPlanItemRepository extends JpaRepository<StudyPlanItem, Long> {
    List<StudyPlanItem> findByUserIdOrderByOrderIndexAsc(Long userId);

    @Modifying
    @Transactional
    void deleteByUserId(Long userId);
}
