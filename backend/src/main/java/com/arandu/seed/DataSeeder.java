package com.arandu.seed;

import com.arandu.domain.Activity;
import com.arandu.domain.ActivityProgress;
import com.arandu.domain.Course;
import com.arandu.domain.Enrollment;
import com.arandu.domain.HelpTopic;
import com.arandu.domain.UserAccount;
import com.arandu.repository.ActivityProgressRepository;
import com.arandu.repository.ActivityRepository;
import com.arandu.repository.CourseRepository;
import com.arandu.repository.EnrollmentRepository;
import com.arandu.repository.HelpTopicRepository;
import com.arandu.repository.UserAccountRepository;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DataSeeder implements CommandLineRunner {

    private final CourseRepository courseRepository;
    private final ActivityRepository activityRepository;
    private final HelpTopicRepository helpTopicRepository;
    private final UserAccountRepository userAccountRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final ActivityProgressRepository activityProgressRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(
            CourseRepository courseRepository,
            ActivityRepository activityRepository,
            HelpTopicRepository helpTopicRepository,
            UserAccountRepository userAccountRepository,
            EnrollmentRepository enrollmentRepository,
            ActivityProgressRepository activityProgressRepository,
            PasswordEncoder passwordEncoder) {
        this.courseRepository = courseRepository;
        this.activityRepository = activityRepository;
        this.helpTopicRepository = helpTopicRepository;
        this.userAccountRepository = userAccountRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.activityProgressRepository = activityProgressRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (courseRepository.count() > 0) {
            return;
        }

        Course databases = course(
                "Bases de Datos",
                "Aprendé modelado relacional, SQL y consultas con JOIN en bloques cortos que se adaptan a tu semana.",
                "Intermedio",
                12,
                "Autónomo + tutorías",
                4.7,
                6,
                "Datos");
        Course python = course(
                "Python para automatizar",
                "Scripts prácticos para organizar tareas académicas y laborales en sesiones de 10 a 30 minutos.",
                "Principiante",
                8,
                "Autónomo",
                4.5,
                5,
                "Programación");
        Course ux = course(
                "Diseño de interfaces",
                "Principios de usabilidad, heurísticas y prototipado aplicados a productos educativos.",
                "Intermedio",
                10,
                "Autónomo + mentoría",
                4.6,
                5,
                "Diseño");

        courseRepository.saveAll(List.of(databases, python, ux));

        List<Activity> dbActivities = List.of(
                activity(databases, 1, "Modelo relacional", "video", 12, 1,
                        "Conceptos de entidades, atributos y relaciones para empezar el curso."),
                activity(databases, 1, "Ejercicio: claves y relaciones", "ejercicio", 15, 2,
                        "Práctica breve para identificar claves primarias y foráneas."),
                activity(databases, 2, "Consultas SELECT", "video", 10, 3,
                        "Filtrar y proyectar datos con SELECT y WHERE."),
                activity(databases, 3, "Agregaciones", "practica", 20, 4,
                        "GROUP BY y funciones de agregación en casos reales."),
                activity(databases, 4, "JOIN en SQL", "video", 12, 5,
                        "Reanudación de INNER JOIN y LEFT JOIN con ejemplos."),
                activity(databases, 4, "Práctica de JOIN", "practica", 30, 6,
                        "Resolver consultas que combinan varias tablas."));
        List<Activity> pyActivities = List.of(
                activity(python, 1, "Variables y flujos", "video", 10, 1, "Fundamentos para automatizar tareas."),
                activity(python, 2, "Archivos y carpetas", "practica", 20, 2, "Organizar materiales de estudio con scripts."),
                activity(python, 3, "APIs simples", "ejercicio", 15, 3, "Consumir datos externos en bloques cortos."));
        List<Activity> uxActivities = List.of(
                activity(ux, 1, "Heurísticas de Nielsen", "video", 12, 1, "Visibilidad del estado y prevención de errores."),
                activity(ux, 2, "Evaluación heurística", "practica", 25, 2, "Inspeccionar una interfaz y priorizar hallazgos."));

        activityRepository.saveAll(dbActivities);
        activityRepository.saveAll(pyActivities);
        activityRepository.saveAll(uxActivities);

        helpTopicRepository.saveAll(List.of(
                help(databases, "¿Cuándo usar INNER JOIN?",
                        "Usá INNER JOIN cuando solo necesitás filas que existen en ambas tablas. Si una fila no tiene pareja, no aparece.",
                        "join,sql,inner"),
                help(databases, "¿Qué diferencia hay con LEFT JOIN?",
                        "LEFT JOIN conserva todas las filas de la tabla izquierda y completa con NULL cuando no hay coincidencia a la derecha.",
                        "join,sql,left"),
                help(databases, "Me confundo con las claves foráneas",
                        "Una clave foránea apunta a la clave primaria de otra tabla y expresa la relación entre entidades.",
                        "modelo,claves"),
                help(python, "¿Por dónde retomo si abandoné una semana?",
                        "Abrí Mi progreso y usá Retomar ahora: te lleva a la última actividad incompleta sin buscar manualmente.",
                        "ritmo,progreso")));

        UserAccount demo = new UserAccount();
        demo.setFullName("Ana Demo");
        demo.setEmail("ana@arandu.edu.py");
        demo.setPasswordHash(passwordEncoder.encode("arandu123"));
        userAccountRepository.save(demo);

        Enrollment advanced = new Enrollment();
        advanced.setUser(demo);
        advanced.setCourse(databases);
        advanced.setStatus("INSCRITO");
        advanced.setProgressPercent(42);
        advanced.setLastActivity(dbActivities.get(4));
        advanced.setEnrolledAt(Instant.now().minus(20, ChronoUnit.DAYS));
        advanced.setLastAccessedAt(Instant.now().minus(5, ChronoUnit.DAYS));
        enrollmentRepository.save(advanced);

        for (int i = 0; i < dbActivities.size(); i++) {
            ActivityProgress progress = new ActivityProgress();
            progress.setEnrollment(advanced);
            progress.setActivity(dbActivities.get(i));
            if (i < 4) {
                progress.setPercentWatched(100);
                progress.setCompleted(true);
            } else if (i == 4) {
                progress.setPercentWatched(35);
                progress.setCompleted(false);
            } else {
                progress.setPercentWatched(0);
                progress.setCompleted(false);
            }
            activityProgressRepository.save(progress);
        }
    }

    private Course course(
            String title,
            String description,
            String level,
            int hours,
            String modality,
            double rating,
            int units,
            String category) {
        Course course = new Course();
        course.setTitle(title);
        course.setDescription(description);
        course.setLevel(level);
        course.setDurationHours(hours);
        course.setModality(modality);
        course.setRating(rating);
        course.setUnitCount(units);
        course.setCategory(category);
        return course;
    }

    private Activity activity(
            Course course,
            int unit,
            String title,
            String type,
            int minutes,
            int order,
            String summary) {
        Activity activity = new Activity();
        activity.setCourse(course);
        activity.setUnitNumber(unit);
        activity.setTitle(title);
        activity.setType(type);
        activity.setDurationMinutes(minutes);
        activity.setOrderIndex(order);
        activity.setSummary(summary);
        return activity;
    }

    private HelpTopic help(Course course, String question, String answer, String tags) {
        HelpTopic topic = new HelpTopic();
        topic.setCourse(course);
        topic.setQuestion(question);
        topic.setAnswer(answer);
        topic.setTags(tags);
        return topic;
    }
}
