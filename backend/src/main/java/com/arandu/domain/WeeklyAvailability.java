package com.arandu.domain;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "weekly_availability")
public class WeeklyAvailability {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false, fetch = FetchType.LAZY)
    private UserAccount user;

    @Column(nullable = false)
    private Integer mondayMinutes = 0;

    @Column(nullable = false)
    private Integer tuesdayMinutes = 0;

    @Column(nullable = false)
    private Integer wednesdayMinutes = 0;

    @Column(nullable = false)
    private Integer thursdayMinutes = 0;

    @Column(nullable = false)
    private Integer fridayMinutes = 0;

    @Column(nullable = false)
    private Integer saturdayMinutes = 0;

    @Column(nullable = false)
    private Integer sundayMinutes = 0;

    @Column(nullable = false)
    private Instant updatedAt = Instant.now();

    public int totalMinutes() {
        return mondayMinutes + tuesdayMinutes + wednesdayMinutes + thursdayMinutes
                + fridayMinutes + saturdayMinutes + sundayMinutes;
    }

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

    public Integer getMondayMinutes() {
        return mondayMinutes;
    }

    public void setMondayMinutes(Integer mondayMinutes) {
        this.mondayMinutes = mondayMinutes;
    }

    public Integer getTuesdayMinutes() {
        return tuesdayMinutes;
    }

    public void setTuesdayMinutes(Integer tuesdayMinutes) {
        this.tuesdayMinutes = tuesdayMinutes;
    }

    public Integer getWednesdayMinutes() {
        return wednesdayMinutes;
    }

    public void setWednesdayMinutes(Integer wednesdayMinutes) {
        this.wednesdayMinutes = wednesdayMinutes;
    }

    public Integer getThursdayMinutes() {
        return thursdayMinutes;
    }

    public void setThursdayMinutes(Integer thursdayMinutes) {
        this.thursdayMinutes = thursdayMinutes;
    }

    public Integer getFridayMinutes() {
        return fridayMinutes;
    }

    public void setFridayMinutes(Integer fridayMinutes) {
        this.fridayMinutes = fridayMinutes;
    }

    public Integer getSaturdayMinutes() {
        return saturdayMinutes;
    }

    public void setSaturdayMinutes(Integer saturdayMinutes) {
        this.saturdayMinutes = saturdayMinutes;
    }

    public Integer getSundayMinutes() {
        return sundayMinutes;
    }

    public void setSundayMinutes(Integer sundayMinutes) {
        this.sundayMinutes = sundayMinutes;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
