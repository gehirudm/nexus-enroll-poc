package com.nexus.enrollment.common.model;

import com.nexus.enrollment.common.enums.GradeStatus;

public class Grade {
    private Long id;
    private Long studentId;
    private Long courseId;
    private String gradeValue;
    private GradeStatus status;
    private Long facultyId;
    
    public Grade() {}
    
    public Grade(Long studentId, Long courseId, String gradeValue, Long facultyId) {
        this.studentId = studentId;
        this.courseId = courseId;
        this.gradeValue = gradeValue;
        this.facultyId = facultyId;
        this.status = GradeStatus.PENDING;
    }
    
    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }
    
    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }
    
    public String getGradeValue() { return gradeValue; }
    public void setGradeValue(String gradeValue) { this.gradeValue = gradeValue; }
    
    public GradeStatus getStatus() { return status; }
    public void setStatus(GradeStatus status) { this.status = status; }
    
    public Long getFacultyId() { return facultyId; }
    public void setFacultyId(Long facultyId) { this.facultyId = facultyId; }
    
    // Business method
    public boolean isPassing() { 
        return !gradeValue.equals("F") && !gradeValue.equals("W");
    }
}
