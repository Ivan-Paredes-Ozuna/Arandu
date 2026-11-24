package com.arandu.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public final class EnrollmentDtos {

    private EnrollmentDtos() {
    }

    public record EnrollRequest(
            @NotNull(message = "Seleccioná un curso") Long courseId,
            @NotBlank(message = "Ingresá tu nombre completo") String fullName,
            @NotBlank(message = "Ingresá un correo electrónico válido")
            @Email(message = "Ingresá un correo electrónico válido") String email,
            @AssertTrue(message = "Debés aceptar las condiciones para continuar") boolean acceptTerms) {
    }

    public record EnrollmentResponse(
            Long id,
            Long courseId,
            String courseTitle,
            String status,
            Integer progressPercent,
            String nextActivityTitle,
            Integer nextActivityDurationMinutes,
            Long nextActivityId,
            Integer daysInactive,
            String lastActivityTitle,
            Integer lastActivityUnit) {
    }

    public record ProgressUpdateRequest(@NotNull Integer percentWatched) {
    }

    public record ActivitySessionResponse(
            Long enrollmentId,
            Long activityId,
            Long courseId,
            String courseTitle,
            Integer unitNumber,
            String title,
            String type,
            Integer durationMinutes,
            Integer percentWatched,
            Integer minutesRemaining,
            String summary,
            boolean firstAccess,
            java.util.List<CourseDtos.ActivityDto> syllabus) {
    }
}
