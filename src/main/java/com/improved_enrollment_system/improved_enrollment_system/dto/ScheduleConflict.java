package com.improved_enrollment_system.improved_enrollment_system.dto;

public class ScheduleConflict {
    private String course1Code;
    private String course1Title;
    private String course1Schedule;
    private String course2Code;
    private String course2Title;
    private String course2Schedule;
    private String reason;

    // Constructor
    public ScheduleConflict(String course1Code, String course1Title, String course1Schedule,
                            String course2Code, String course2Title, String course2Schedule,
                            String reason) {
        this.course1Code = course1Code;
        this.course1Title = course1Title;
        this.course1Schedule = course1Schedule;
        this.course2Code = course2Code;
        this.course2Title = course2Title;
        this.course2Schedule = course2Schedule;
        this.reason = reason;
    }

    // Getters
    public String getCourse1Code() { return course1Code; }
    public String getCourse1Title() { return course1Title; }
    public String getCourse1Schedule() { return course1Schedule; }
    public String getCourse2Code() { return course2Code; }
    public String getCourse2Title() { return course2Title; }
    public String getCourse2Schedule() { return course2Schedule; }
    public String getReason() { return reason; }

    @Override
    public String toString() {
        return "CONFLICT: " + course1Code + " (" + course1Schedule + ") conflicts with " +
                course2Code + " (" + course2Schedule + ") - " + reason;
    }
}