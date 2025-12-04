package com.improved_enrollment_system.improved_enrollment_system.dto;


public class GPAResult {
    private boolean success;
    private double gpa;
    private String message;
    private String standing;


    public GPAResult(boolean success, double gpa, String message) {
        this.success = success;
        this.gpa = gpa;
        this.message = message;
    }


    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public double getGpa() {
        return gpa;
    }

    public void setGpa(double gpa) {
        this.gpa = gpa;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStanding() {
        return standing;
    }

    public void setStanding(String standing) {
        this.standing = standing;
    }
}