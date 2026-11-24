package com.arandu.domain;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "activity_progress", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"enrollment_id", "activity_id"})
})
public class ActivityProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Enrollment enrollment;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Activity activity;

    @Column(nullable = false)
    private Integer percentWatched = 0;

    @Column(nullable = false)
    private boolean completed = false;

    @Column(nullable = false)
    private Instant updatedAt = Instant.now();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Enrollment getEnrollment() {
        return enrollment;
    }

    public void setEnrollment(Enrollment enrollment) {
        this.enrollment = enrollment;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public Integer getPercentWatched() {
        return percentWatched;
    }

    public void setPercentWatched(Integer percentWatched) {
        this.percentWatched = percentWatched;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
