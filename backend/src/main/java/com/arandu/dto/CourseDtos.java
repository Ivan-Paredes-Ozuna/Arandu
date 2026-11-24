package com.arandu.dto;

import java.util.List;

public final class CourseDtos {

    private CourseDtos() {
    }

    public record CourseSummary(
            Long id,
            String title,
            String description,
            String level,
            Integer durationHours,
            String modality,
            Double rating,
            Integer unitCount,
            String category) {
    }

    public record ActivityDto(
            Long id,
            Integer unitNumber,
            String title,
            String type,
            Integer durationMinutes,
            Integer orderIndex,
            String summary) {
    }

    public record CourseDetail(
            Long id,
            String title,
            String description,
            String level,
            Integer durationHours,
            String modality,
            Double rating,
            Integer unitCount,
            String category,
            List<ActivityDto> activities) {
    }
}
