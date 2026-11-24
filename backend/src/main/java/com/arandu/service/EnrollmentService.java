package com.arandu.service;

import com.arandu.domain.Activity;
import com.arandu.domain.ActivityProgress;
import com.arandu.domain.Course;
import com.arandu.domain.Enrollment;
import com.arandu.domain.UserAccount;
import com.arandu.dto.CourseDtos;
import com.arandu.dto.EnrollmentDtos;
import com.arandu.repository.ActivityProgressRepository;
import com.arandu.repository.ActivityRepository;
import com.arandu.repository.CourseRepository;
import com.arandu.repository.EnrollmentRepository;
import com.arandu.repository.UserAccountRepository;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;
    private final UserAccountRepository userAccountRepository;
    private final ActivityRepository activityRepository;
    private final ActivityProgressRepository activityProgressRepository;
    private final CourseService courseService;

    public EnrollmentService(
            EnrollmentRepository enrollmentRepository,
            CourseRepository courseRepository,
            UserAccountRepository userAccountRepository,
            ActivityRepository activityRepository,
            ActivityProgressRepository activityProgressRepository,
            CourseService courseService) {
        this.enrollmentRepository = enrollmentRepository;
        this.courseRepository = courseRepository;
        this.userAccountRepository = userAccountRepository;
        this.activityRepository = activityRepository;
        this.activityProgressRepository = activityProgressRepository;
        this.courseService = courseService;
    }

    @Transactional
    public EnrollmentDtos.EnrollmentResponse enroll(Long userId, EnrollmentDtos.EnrollRequest request) {
        if (enrollmentRepository.existsByUserIdAndCourseId(userId, request.courseId())) {
            throw new IllegalStateException("Ya estás inscripto en este curso");
        }
        UserAccount user = userAccountRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        Course course = courseRepository.findById(request.courseId())
                .orElseThrow(() -> new IllegalArgumentException("Curso no encontrado"));

        List<Activity> activities = activityRepository.findByCourseIdOrderByOrderIndexAsc(course.getId());
        if (activities.isEmpty()) {
            throw new IllegalStateException("El curso no tiene actividades");
        }

        Enrollment enrollment = new Enrollment();
        enrollment.setUser(user);
        enrollment.setCourse(course);
        enrollment.setStatus("INSCRITO");
        enrollment.setProgressPercent(0);
        enrollment.setLastActivity(activities.getFirst());
        enrollment.setEnrolledAt(Instant.now());
        enrollment.setLastAccessedAt(Instant.now());
        enrollmentRepository.save(enrollment);

        for (Activity activity : activities) {
            ActivityProgress progress = new ActivityProgress();
            progress.setEnrollment(enrollment);
            progress.setActivity(activity);
            progress.setPercentWatched(0);
            progress.setCompleted(false);
            activityProgressRepository.save(progress);
        }

        return toResponse(enrollment, activities.getFirst(), true);
    }

    @Transactional(readOnly = true)
    public List<EnrollmentDtos.EnrollmentResponse> mine(Long userId) {
        return enrollmentRepository.findByUserIdOrderByLastAccessedAtDesc(userId).stream()
                .map(e -> {
                    Activity next = resolveNextActivity(e);
                    return toResponse(e, next, false);
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public EnrollmentDtos.EnrollmentResponse get(Long userId, Long enrollmentId) {
        Enrollment enrollment = requireEnrollment(userId, enrollmentId);
        return toResponse(enrollment, resolveNextActivity(enrollment), false);
    }

    @Transactional(readOnly = true)
    public EnrollmentDtos.ActivitySessionResponse resume(Long userId, Long enrollmentId) {
        Enrollment enrollment = requireEnrollment(userId, enrollmentId);
        Activity next = resolveNextActivity(enrollment);
        boolean firstAccess = enrollment.getProgressPercent() == 0;
        return openSession(enrollment, next, firstAccess);
    }

    @Transactional(readOnly = true)
    public EnrollmentDtos.ActivitySessionResponse openActivity(Long userId, Long enrollmentId, Long activityId) {
        Enrollment enrollment = requireEnrollment(userId, enrollmentId);
        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new IllegalArgumentException("Actividad no encontrada"));
        if (!activity.getCourse().getId().equals(enrollment.getCourse().getId())) {
            throw new IllegalArgumentException("La actividad no pertenece a este curso");
        }
        boolean firstAccess = enrollment.getProgressPercent() == 0 && activity.getOrderIndex() == 1;
        return openSession(enrollment, activity, firstAccess);
    }

    @Transactional
    public EnrollmentDtos.ActivitySessionResponse updateProgress(
            Long userId, Long enrollmentId, Long activityId, EnrollmentDtos.ProgressUpdateRequest request) {
        Enrollment enrollment = requireEnrollment(userId, enrollmentId);
        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new IllegalArgumentException("Actividad no encontrada"));

        int percent = Math.max(0, Math.min(100, request.percentWatched()));
        ActivityProgress progress = activityProgressRepository
                .findByEnrollmentIdAndActivityId(enrollmentId, activityId)
                .orElseGet(() -> {
                    ActivityProgress created = new ActivityProgress();
                    created.setEnrollment(enrollment);
                    created.setActivity(activity);
                    return created;
                });
        progress.setPercentWatched(percent);
        progress.setCompleted(percent >= 100);
        progress.setUpdatedAt(Instant.now());
        activityProgressRepository.save(progress);

        enrollment.setLastActivity(activity);
        enrollment.setLastAccessedAt(Instant.now());
        recalculateCourseProgress(enrollment);
        enrollmentRepository.save(enrollment);

        return openSession(enrollment, activity, false);
    }

    private EnrollmentDtos.ActivitySessionResponse openSession(Enrollment enrollment, Activity activity, boolean firstAccess) {
        ActivityProgress progress = activityProgressRepository
                .findByEnrollmentIdAndActivityId(enrollment.getId(), activity.getId())
                .orElse(null);
        int percent = progress == null ? 0 : progress.getPercentWatched();
        int minutesRemaining = Math.max(1, (int) Math.ceil(activity.getDurationMinutes() * (100 - percent) / 100.0));
        List<CourseDtos.ActivityDto> syllabus = activityRepository
                .findByCourseIdOrderByOrderIndexAsc(enrollment.getCourse().getId())
                .stream()
                .map(courseService::toActivity)
                .toList();

        return new EnrollmentDtos.ActivitySessionResponse(
                enrollment.getId(),
                activity.getId(),
                enrollment.getCourse().getId(),
                enrollment.getCourse().getTitle(),
                activity.getUnitNumber(),
                activity.getTitle(),
                activity.getType(),
                activity.getDurationMinutes(),
                percent,
                minutesRemaining,
                activity.getSummary(),
                firstAccess,
                syllabus);
    }

    private void recalculateCourseProgress(Enrollment enrollment) {
        List<ActivityProgress> progresses = activityProgressRepository.findByEnrollmentId(enrollment.getId());
        if (progresses.isEmpty()) {
            enrollment.setProgressPercent(0);
            return;
        }
        int avg = (int) Math.round(progresses.stream()
                .mapToInt(ActivityProgress::getPercentWatched)
                .average()
                .orElse(0));
        enrollment.setProgressPercent(avg);
    }

    private Activity resolveNextActivity(Enrollment enrollment) {
        List<Activity> activities = activityRepository.findByCourseIdOrderByOrderIndexAsc(enrollment.getCourse().getId());
        List<ActivityProgress> progresses = activityProgressRepository.findByEnrollmentId(enrollment.getId());

        for (Activity activity : activities) {
            ActivityProgress progress = progresses.stream()
                    .filter(p -> p.getActivity().getId().equals(activity.getId()))
                    .findFirst()
                    .orElse(null);
            if (progress == null || !progress.isCompleted()) {
                return activity;
            }
        }
        return activities.isEmpty() ? enrollment.getLastActivity() : activities.getLast();
    }

    private Enrollment requireEnrollment(Long userId, Long enrollmentId) {
        return enrollmentRepository.findByIdAndUserId(enrollmentId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Inscripción no encontrada"));
    }

    private EnrollmentDtos.EnrollmentResponse toResponse(Enrollment enrollment, Activity next, boolean freshlyEnrolled) {
        int daysInactive = freshlyEnrolled
                ? 0
                : (int) Duration.between(enrollment.getLastAccessedAt(), Instant.now()).toDays();
        Activity last = enrollment.getLastActivity();
        return new EnrollmentDtos.EnrollmentResponse(
                enrollment.getId(),
                enrollment.getCourse().getId(),
                enrollment.getCourse().getTitle(),
                enrollment.getStatus(),
                enrollment.getProgressPercent(),
                next == null ? null : next.getTitle(),
                next == null ? null : next.getDurationMinutes(),
                next == null ? null : next.getId(),
                daysInactive,
                last == null ? null : last.getTitle(),
                last == null ? null : last.getUnitNumber());
    }
}
