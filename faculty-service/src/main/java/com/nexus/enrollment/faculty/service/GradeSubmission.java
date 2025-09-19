package com.nexus.enrollment.faculty.service;

public class GradeSubmission {
    private Long studentId;
    private Long courseId;
    private String grade;  // Changed from gradeValue to match Postman
    private String semester;  // Added to match Postman
    
    public GradeSubmission() {}
    
    public GradeSubmission(Long studentId, Long courseId, String grade, String semester) {
        this.studentId = studentId;
        this.courseId = courseId;
        this.grade = grade;
        this.semester = semester;
    }
    
    // Getters and setters
    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }
    
    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }
    
    public String getGrade() { return grade; }  // Changed method name
    public void setGrade(String grade) { this.grade = grade; }  // Changed method name
    
    public String getSemester() { return semester; }  // Added
    public void setSemester(String semester) { this.semester = semester; }  // Added
}
