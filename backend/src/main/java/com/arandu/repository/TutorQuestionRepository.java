package com.arandu.repository;

import com.arandu.domain.TutorQuestion;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TutorQuestionRepository extends JpaRepository<TutorQuestion, Long> {
    List<TutorQuestion> findByUserIdOrderByCreatedAtDesc(Long userId);
}
