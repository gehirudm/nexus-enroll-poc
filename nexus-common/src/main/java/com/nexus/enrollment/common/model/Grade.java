package com.nexus.enrollment.common.model;

import com.nexus.enrollment.common.enums.GradeStatus;
import com.nexus.enrollment.common.util.JsonSerializable;

public class Grade implements JsonSerializable {
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
    
    // JSON serialization method
    @Override
    public String toJson() {
        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"id\":").append(id != null ? id : "null").append(",");
        json.append("\"studentId\":").append(studentId != null ? studentId : "null").append(",");
        json.append("\"courseId\":").append(courseId != null ? courseId : "null").append(",");
        json.append("\"gradeValue\":\"").append(gradeValue != null ? gradeValue.replace("\"", "\\\"") : "").append("\",");
        json.append("\"status\":\"").append(status != null ? status.toString() : "").append("\",");
        json.append("\"facultyId\":").append(facultyId != null ? facultyId : "null");
        json.append("}");
        return json.toString();
    }
}
