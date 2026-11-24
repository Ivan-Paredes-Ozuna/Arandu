package com.arandu.domain;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "tutor_questions")
public class TutorQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private UserAccount user;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    private Activity activity;

    @Column(nullable = false, length = 2000)
    private String question;

    @Column(nullable = false)
    private boolean privateQuestion = true;

    @Column(nullable = false)
    private String status = "PENDIENTE";

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserAccount getUser() {
        return user;
    }

    public void setUser(UserAccount user) {
        this.user = user;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public boolean isPrivateQuestion() {
        return privateQuestion;
    }

    public void setPrivateQuestion(boolean privateQuestion) {
        this.privateQuestion = privateQuestion;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
