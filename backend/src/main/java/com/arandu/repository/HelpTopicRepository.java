package com.arandu.repository;

import com.arandu.domain.HelpTopic;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface HelpTopicRepository extends JpaRepository<HelpTopic, Long> {
    List<HelpTopic> findByCourseId(Long courseId);

    @Query("""
            select h from HelpTopic h
            where h.course.id = :courseId
              and (lower(h.question) like lower(concat('%', :q, '%'))
                or lower(h.tags) like lower(concat('%', :q, '%'))
                or lower(h.answer) like lower(concat('%', :q, '%')))
            """)
    List<HelpTopic> search(@Param("courseId") Long courseId, @Param("q") String q);
}
