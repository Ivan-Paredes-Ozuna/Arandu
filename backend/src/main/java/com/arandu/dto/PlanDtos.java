package com.arandu.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public final class PlanDtos {

    private PlanDtos() {
    }

    public record AvailabilityRequest(
            @NotNull @Min(0) @Max(240) Integer mondayMinutes,
            @NotNull @Min(0) @Max(240) Integer tuesdayMinutes,
            @NotNull @Min(0) @Max(240) Integer wednesdayMinutes,
            @NotNull @Min(0) @Max(240) Integer thursdayMinutes,
            @NotNull @Min(0) @Max(240) Integer fridayMinutes,
            @NotNull @Min(0) @Max(240) Integer saturdayMinutes,
            @NotNull @Min(0) @Max(240) Integer sundayMinutes) {
    }

    public record AvailabilityResponse(
            Integer mondayMinutes,
            Integer tuesdayMinutes,
            Integer wednesdayMinutes,
            Integer thursdayMinutes,
            Integer fridayMinutes,
            Integer saturdayMinutes,
            Integer sundayMinutes,
            Integer totalMinutes) {
    }

    public record PlanItemResponse(
            Long id,
            String dayLabel,
            Long activityId,
            Long enrollmentId,
            String activityTitle,
            Integer unitNumber,
            String type,
            Integer durationMinutes) {
    }

    public record PlanResponse(Integer totalMinutes, List<PlanItemResponse> items) {
    }
}
