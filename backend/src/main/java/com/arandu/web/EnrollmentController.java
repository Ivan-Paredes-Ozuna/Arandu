package com.arandu.web;

import com.arandu.dto.EnrollmentDtos;
import com.arandu.security.UserPrincipal;
import com.arandu.service.EnrollmentService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/enrollments")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    public EnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    @PostMapping
    public EnrollmentDtos.EnrollmentResponse enroll(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody EnrollmentDtos.EnrollRequest request) {
        return enrollmentService.enroll(principal.getId(), request);
    }

    @GetMapping("/mine")
    public List<EnrollmentDtos.EnrollmentResponse> mine(@AuthenticationPrincipal UserPrincipal principal) {
        return enrollmentService.mine(principal.getId());
    }

    @GetMapping("/{id}")
    public EnrollmentDtos.EnrollmentResponse get(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id) {
        return enrollmentService.get(principal.getId(), id);
    }

    @GetMapping("/{id}/resume")
    public EnrollmentDtos.ActivitySessionResponse resume(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id) {
        return enrollmentService.resume(principal.getId(), id);
    }

    @GetMapping("/{id}/activities/{activityId}")
    public EnrollmentDtos.ActivitySessionResponse openActivity(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id,
            @PathVariable Long activityId) {
        return enrollmentService.openActivity(principal.getId(), id, activityId);
    }

    @PatchMapping("/{id}/activities/{activityId}/progress")
    public EnrollmentDtos.ActivitySessionResponse updateProgress(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id,
            @PathVariable Long activityId,
            @Valid @RequestBody EnrollmentDtos.ProgressUpdateRequest request) {
        return enrollmentService.updateProgress(principal.getId(), id, activityId, request);
    }
}
