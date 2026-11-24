package com.arandu.service;

import com.arandu.domain.Activity;
import com.arandu.domain.Enrollment;
import com.arandu.domain.StudyPlanItem;
import com.arandu.domain.UserAccount;
import com.arandu.domain.WeeklyAvailability;
import com.arandu.dto.PlanDtos;
import com.arandu.repository.ActivityProgressRepository;
import com.arandu.repository.ActivityRepository;
import com.arandu.repository.EnrollmentRepository;
import com.arandu.repository.StudyPlanItemRepository;
import com.arandu.repository.UserAccountRepository;
import com.arandu.repository.WeeklyAvailabilityRepository;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PlanService {

    private final WeeklyAvailabilityRepository availabilityRepository;
    private final StudyPlanItemRepository studyPlanItemRepository;
    private final UserAccountRepository userAccountRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final ActivityRepository activityRepository;
    private final ActivityProgressRepository activityProgressRepository;

    public PlanService(
            WeeklyAvailabilityRepository availabilityRepository,
            StudyPlanItemRepository studyPlanItemRepository,
            UserAccountRepository userAccountRepository,
            EnrollmentRepository enrollmentRepository,
            ActivityRepository activityRepository,
            ActivityProgressRepository activityProgressRepository) {
        this.availabilityRepository = availabilityRepository;
        this.studyPlanItemRepository = studyPlanItemRepository;
        this.userAccountRepository = userAccountRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.activityRepository = activityRepository;
        this.activityProgressRepository = activityProgressRepository;
    }

    @Transactional
    public PlanDtos.AvailabilityResponse saveAvailability(Long userId, PlanDtos.AvailabilityRequest request) {
        UserAccount user = userAccountRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        WeeklyAvailability availability = availabilityRepository.findByUserId(userId).orElseGet(WeeklyAvailability::new);
        availability.setUser(user);
        availability.setMondayMinutes(request.mondayMinutes());
        availability.setTuesdayMinutes(request.tuesdayMinutes());
        availability.setWednesdayMinutes(request.wednesdayMinutes());
        availability.setThursdayMinutes(request.thursdayMinutes());
        availability.setFridayMinutes(request.fridayMinutes());
        availability.setSaturdayMinutes(request.saturdayMinutes());
        availability.setSundayMinutes(request.sundayMinutes());
        availability.setUpdatedAt(Instant.now());
        availabilityRepository.save(availability);
        return toAvailability(availability);
    }

    @Transactional(readOnly = true)
    public PlanDtos.AvailabilityResponse getAvailability(Long userId) {
        return availabilityRepository.findByUserId(userId)
                .map(this::toAvailability)
                .orElse(new PlanDtos.AvailabilityResponse(0, 0, 0, 0, 0, 0, 0, 0));
    }

    @Transactional
    public PlanDtos.PlanResponse generatePlan(Long userId) {
        WeeklyAvailability availability = availabilityRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Primero organizá tu semana con minutos disponibles"));
        if (availability.totalMinutes() <= 0) {
            throw new IllegalArgumentException("Indicá al menos un bloque de tiempo disponible");
        }

        List<Enrollment> enrollments = enrollmentRepository.findByUserIdOrderByLastAccessedAtDesc(userId);
        if (enrollments.isEmpty()) {
            throw new IllegalArgumentException("Necesitás estar inscripto en un curso para adaptar el plan");
        }

        Enrollment enrollment = enrollments.getFirst();
        List<Activity> pending = activityRepository.findByCourseIdOrderByOrderIndexAsc(enrollment.getCourse().getId())
                .stream()
                .filter(activity -> activityProgressRepository
                        .findByEnrollmentIdAndActivityId(enrollment.getId(), activity.getId())
                        .map(p -> !p.isCompleted())
                        .orElse(true))
                .toList();

        studyPlanItemRepository.deleteByUserId(userId);

        List<DaySlot> slots = List.of(
                new DaySlot("Lunes", availability.getMondayMinutes()),
                new DaySlot("Martes", availability.getTuesdayMinutes()),
                new DaySlot("Miércoles", availability.getWednesdayMinutes()),
                new DaySlot("Jueves", availability.getThursdayMinutes()),
                new DaySlot("Viernes", availability.getFridayMinutes()),
                new DaySlot("Sábado", availability.getSaturdayMinutes()),
                new DaySlot("Domingo", availability.getSundayMinutes()));

        List<StudyPlanItem> items = new ArrayList<>();
        int activityCursor = 0;
        int order = 1;
        for (DaySlot slot : slots) {
            int remaining = slot.minutes();
            while (remaining >= 10 && activityCursor < pending.size()) {
                Activity activity = pending.get(activityCursor);
                int chunk = Math.min(remaining, Math.min(activity.getDurationMinutes(), pickChunk(remaining)));
                StudyPlanItem item = new StudyPlanItem();
                item.setUser(enrollment.getUser());
                item.setActivity(activity);
                item.setDayLabel(slot.label());
                item.setDurationMinutes(chunk);
                item.setOrderIndex(order++);
                items.add(item);
                remaining -= chunk;
                if (chunk >= activity.getDurationMinutes()) {
                    activityCursor++;
                } else {
                    break;
                }
            }
        }

        studyPlanItemRepository.saveAll(items);
        return new PlanDtos.PlanResponse(
                availability.totalMinutes(),
                items.stream().map(item -> toPlanItem(item, enrollment.getId())).toList());
    }

    @Transactional(readOnly = true)
    public PlanDtos.PlanResponse currentPlan(Long userId) {
        List<StudyPlanItem> items = studyPlanItemRepository.findByUserIdOrderByOrderIndexAsc(userId);
        List<Enrollment> enrollments = enrollmentRepository.findByUserIdOrderByLastAccessedAtDesc(userId);
        Long enrollmentId = enrollments.isEmpty() ? null : enrollments.getFirst().getId();
        int total = availabilityRepository.findByUserId(userId).map(WeeklyAvailability::totalMinutes).orElse(0);
        return new PlanDtos.PlanResponse(
                total,
                items.stream().map(item -> toPlanItem(item, enrollmentId)).toList());
    }

    private int pickChunk(int remaining) {
        if (remaining >= 30) {
            return 30;
        }
        if (remaining >= 15) {
            return 15;
        }
        return 10;
    }

    private PlanDtos.AvailabilityResponse toAvailability(WeeklyAvailability availability) {
        return new PlanDtos.AvailabilityResponse(
                availability.getMondayMinutes(),
                availability.getTuesdayMinutes(),
                availability.getWednesdayMinutes(),
                availability.getThursdayMinutes(),
                availability.getFridayMinutes(),
                availability.getSaturdayMinutes(),
                availability.getSundayMinutes(),
                availability.totalMinutes());
    }

    private PlanDtos.PlanItemResponse toPlanItem(StudyPlanItem item, Long enrollmentId) {
        return new PlanDtos.PlanItemResponse(
                item.getId(),
                item.getDayLabel(),
                item.getActivity().getId(),
                enrollmentId,
                item.getActivity().getTitle(),
                item.getActivity().getUnitNumber(),
                item.getActivity().getType(),
                item.getDurationMinutes());
    }

    private record DaySlot(String label, int minutes) {
    }
}
