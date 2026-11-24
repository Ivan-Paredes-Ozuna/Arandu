package com.arandu.service;

import com.arandu.domain.Activity;
import com.arandu.domain.Course;
import com.arandu.dto.CourseDtos;
import com.arandu.repository.CourseRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CourseService {

    private final CourseRepository courseRepository;

    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    @Transactional(readOnly = true)
    public List<CourseDtos.CourseSummary> list(String level, String category) {
        List<Course> courses = courseRepository.findAll();
        return courses.stream()
                .filter(c -> level == null || level.isBlank() || c.getLevel().equalsIgnoreCase(level))
                .filter(c -> category == null || category.isBlank() || c.getCategory().equalsIgnoreCase(category))
                .map(this::toSummary)
                .toList();
    }

    @Transactional(readOnly = true)
    public CourseDtos.CourseDetail get(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Curso no encontrado"));
        return toDetail(course);
    }

    private CourseDtos.CourseSummary toSummary(Course course) {
        return new CourseDtos.CourseSummary(
                course.getId(),
                course.getTitle(),
                course.getDescription(),
                course.getLevel(),
                course.getDurationHours(),
                course.getModality(),
                course.getRating(),
                course.getUnitCount(),
                course.getCategory());
    }

    private CourseDtos.CourseDetail toDetail(Course course) {
        List<CourseDtos.ActivityDto> activities = course.getActivities().stream()
                .map(this::toActivity)
                .toList();
        return new CourseDtos.CourseDetail(
                course.getId(),
                course.getTitle(),
                course.getDescription(),
                course.getLevel(),
                course.getDurationHours(),
                course.getModality(),
                course.getRating(),
                course.getUnitCount(),
                course.getCategory(),
                activities);
    }

    CourseDtos.ActivityDto toActivity(Activity activity) {
        return new CourseDtos.ActivityDto(
                activity.getId(),
                activity.getUnitNumber(),
                activity.getTitle(),
                activity.getType(),
                activity.getDurationMinutes(),
                activity.getOrderIndex(),
                activity.getSummary());
    }
}
