package com.nexus.enrollment.common.model;

import com.nexus.enrollment.common.enums.EnrollmentStatus;
import com.nexus.enrollment.common.util.JsonSerializable;
import java.util.Date;
import java.text.SimpleDateFormat;

public class Enrollment implements JsonSerializable {
    private Long id;
    private Long studentId;
    private Long courseId;
    private EnrollmentStatus status;
    private Date enrollmentDate;
    
    public Enrollment() {}
    
    public Enrollment(Long studentId, Long courseId, EnrollmentStatus status) {
        this.studentId = studentId;
        this.courseId = courseId;
        this.status = status;
        this.enrollmentDate = new Date();
    }
    
    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }
    
    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }
    
    public EnrollmentStatus getStatus() { return status; }
    public void setStatus(EnrollmentStatus status) { this.status = status; }
    
    public Date getEnrollmentDate() { return enrollmentDate; }
    public void setEnrollmentDate(Date enrollmentDate) { this.enrollmentDate = enrollmentDate; }
    
    // JSON serialization method
    @Override
    public String toJson() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"id\":").append(id != null ? id : "null").append(",");
        json.append("\"studentId\":").append(studentId != null ? studentId : "null").append(",");
        json.append("\"courseId\":").append(courseId != null ? courseId : "null").append(",");
        json.append("\"status\":\"").append(status != null ? status.toString() : "").append("\",");
        json.append("\"enrollmentDate\":\"").append(enrollmentDate != null ? dateFormat.format(enrollmentDate) : "").append("\"");
        json.append("}");
        return json.toString();
    }
}
