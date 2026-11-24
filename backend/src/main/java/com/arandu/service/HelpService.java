package com.arandu.service;

import com.arandu.domain.Activity;
import com.arandu.domain.Course;
import com.arandu.domain.HelpTopic;
import com.arandu.domain.TutorQuestion;
import com.arandu.domain.UserAccount;
import com.arandu.dto.HelpDtos;
import com.arandu.repository.ActivityRepository;
import com.arandu.repository.CourseRepository;
import com.arandu.repository.HelpTopicRepository;
import com.arandu.repository.TutorQuestionRepository;
import com.arandu.repository.UserAccountRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class HelpService {

    private final HelpTopicRepository helpTopicRepository;
    private final TutorQuestionRepository tutorQuestionRepository;
    private final CourseRepository courseRepository;
    private final ActivityRepository activityRepository;
    private final UserAccountRepository userAccountRepository;

    public HelpService(
            HelpTopicRepository helpTopicRepository,
            TutorQuestionRepository tutorQuestionRepository,
            CourseRepository courseRepository,
            ActivityRepository activityRepository,
            UserAccountRepository userAccountRepository) {
        this.helpTopicRepository = helpTopicRepository;
        this.tutorQuestionRepository = tutorQuestionRepository;
        this.courseRepository = courseRepository;
        this.activityRepository = activityRepository;
        this.userAccountRepository = userAccountRepository;
    }

    @Transactional(readOnly = true)
    public HelpDtos.HelpSearchResponse search(Long courseId, String query) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Curso no encontrado"));
        List<HelpTopic> topics;
        if (query == null || query.isBlank()) {
            topics = helpTopicRepository.findByCourseId(courseId);
        } else {
            topics = helpTopicRepository.search(courseId, query.trim());
            if (topics.isEmpty()) {
                topics = helpTopicRepository.findByCourseId(courseId);
            }
        }
        String hint = "Estás en " + course.getTitle()
                + ". Buscá una explicación, revisá dudas similares o preguntá al tutor en privado.";
        return new HelpDtos.HelpSearchResponse(
                topics.stream().map(this::toTopic).toList(),
                hint);
    }

    @Transactional
    public HelpDtos.TutorQuestionResponse askTutor(Long userId, HelpDtos.TutorQuestionRequest request) {
        UserAccount user = userAccountRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        Course course = courseRepository.findById(request.courseId())
                .orElseThrow(() -> new IllegalArgumentException("Curso no encontrado"));
        Activity activity = null;
        if (request.activityId() != null) {
            activity = activityRepository.findById(request.activityId())
                    .orElseThrow(() -> new IllegalArgumentException("Actividad no encontrada"));
        }

        TutorQuestion question = new TutorQuestion();
        question.setUser(user);
        question.setCourse(course);
        question.setActivity(activity);
        question.setQuestion(request.question().trim());
        question.setPrivateQuestion(request.privateQuestion());
        question.setStatus("PENDIENTE");
        tutorQuestionRepository.save(question);
        return toTutor(question);
    }

    @Transactional(readOnly = true)
    public List<HelpDtos.TutorQuestionResponse> myQuestions(Long userId) {
        return tutorQuestionRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::toTutor)
                .toList();
    }

    private HelpDtos.HelpTopicResponse toTopic(HelpTopic topic) {
        return new HelpDtos.HelpTopicResponse(topic.getId(), topic.getQuestion(), topic.getAnswer(), topic.getTags());
    }

    private HelpDtos.TutorQuestionResponse toTutor(TutorQuestion question) {
        return new HelpDtos.TutorQuestionResponse(
                question.getId(),
                question.getQuestion(),
                question.isPrivateQuestion(),
                question.getStatus(),
                question.getCreatedAt());
    }
}
