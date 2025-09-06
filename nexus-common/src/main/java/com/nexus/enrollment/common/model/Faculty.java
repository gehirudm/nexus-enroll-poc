package com.nexus.enrollment.common.model;

import com.nexus.enrollment.common.util.JsonSerializable;
import java.util.ArrayList;
import java.util.List;

public class Faculty implements JsonSerializable {
    private Long id;
    private String name;
    private String email;
    private String department;
    private List<Long> assignedCourseIds;
    
    public Faculty() {
        this.assignedCourseIds = new ArrayList<>();
    }
    
    public Faculty(String name, String email, String department) {
        this();
        this.name = name;
        this.email = email;
        this.department = department;
    }
    
    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    
    public List<Long> getAssignedCourseIds() { return assignedCourseIds; }
    public void setAssignedCourseIds(List<Long> assignedCourseIds) { this.assignedCourseIds = assignedCourseIds; }
    
    // JSON serialization method
    @Override
    public String toJson() {
        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"id\":").append(id).append(",");
        json.append("\"name\":\"").append(name != null ? name.replace("\"", "\\\"") : "").append("\",");
        json.append("\"email\":\"").append(email != null ? email.replace("\"", "\\\"") : "").append("\",");
        json.append("\"department\":\"").append(department != null ? department.replace("\"", "\\\"") : "").append("\",");
        json.append("\"assignedCourseIds\":[");
        if (assignedCourseIds != null) {
            for (int i = 0; i < assignedCourseIds.size(); i++) {
                if (i > 0) json.append(",");
                json.append(assignedCourseIds.get(i));
            }
        }
        json.append("]");
        json.append("}");
        return json.toString();
    }
}
