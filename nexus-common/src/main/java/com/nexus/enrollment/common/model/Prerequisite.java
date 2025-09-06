package com.nexus.enrollment.common.model;

import com.nexus.enrollment.common.util.JsonSerializable;

public class Prerequisite implements JsonSerializable {
    private Long id;
    private Long courseId;
    private Long prerequisiteCourseId;
    private String minimumGrade;
    
    public Prerequisite() {}
    
    public Prerequisite(Long courseId, Long prerequisiteCourseId, String minimumGrade) {
        this.courseId = courseId;
        this.prerequisiteCourseId = prerequisiteCourseId;
        this.minimumGrade = minimumGrade;
    }
    
    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }
    
    public Long getPrerequisiteCourseId() { return prerequisiteCourseId; }
    public void setPrerequisiteCourseId(Long prerequisiteCourseId) { this.prerequisiteCourseId = prerequisiteCourseId; }
    
    public String getMinimumGrade() { return minimumGrade; }
    public void setMinimumGrade(String minimumGrade) { this.minimumGrade = minimumGrade; }
    
    // JSON serialization method
    public String toJson() {
        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"id\":").append(id != null ? id : "null").append(",");
        json.append("\"courseId\":").append(courseId != null ? courseId : "null").append(",");
        json.append("\"prerequisiteCourseId\":").append(prerequisiteCourseId != null ? prerequisiteCourseId : "null").append(",");
        json.append("\"minimumGrade\":\"").append(minimumGrade != null ? minimumGrade.replace("\"", "\\\"") : "").append("\"");
        json.append("}");
        return json.toString();
    }
}
