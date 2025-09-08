package com.nexus.enrollment.common.model;

import java.util.ArrayList;
import java.util.List;

public class Faculty {
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
}
