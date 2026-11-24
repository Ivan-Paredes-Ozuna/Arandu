package com.arandu.web;

import com.arandu.dto.CourseDtos;
import com.arandu.service.CourseService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping
    public List<CourseDtos.CourseSummary> list(
            @RequestParam(required = false) String level,
            @RequestParam(required = false) String category) {
        return courseService.list(level, category);
    }

    @GetMapping("/{id}")
    public CourseDtos.CourseDetail get(@PathVariable Long id) {
        return courseService.get(id);
    }
}
