package com.nexus.enrollment.common.model;

import com.nexus.enrollment.common.util.JsonSerializable;
import java.util.ArrayList;
import java.util.List;

public class Course implements JsonSerializable {
    private Long id;
    private String courseCode;
    private String name;
    private String description;
    private Long instructorId;
    private String department;
    private int totalCapacity;
    private int availableSeats;
    private Schedule schedule;
    private List<Prerequisite> prerequisites;
    
    public Course() {
        this.prerequisites = new ArrayList<>();
    }
    
    public Course(String courseCode, String name, String description, Long instructorId, 
                  String department, int totalCapacity, Schedule schedule) {
        this();
        this.courseCode = courseCode;
        this.name = name;
        this.description = description;
        this.instructorId = instructorId;
        this.department = department;
        this.totalCapacity = totalCapacity;
        this.availableSeats = totalCapacity;
        this.schedule = schedule;
    }
    
    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Long getInstructorId() { return instructorId; }
    public void setInstructorId(Long instructorId) { this.instructorId = instructorId; }
    
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    
    public int getTotalCapacity() { return totalCapacity; }
    public void setTotalCapacity(int totalCapacity) { this.totalCapacity = totalCapacity; }
    
    public int getAvailableSeats() { return availableSeats; }
    public void setAvailableSeats(int availableSeats) { this.availableSeats = availableSeats; }
    
    public Schedule getSchedule() { return schedule; }
    public void setSchedule(Schedule schedule) { this.schedule = schedule; }
    
    public List<Prerequisite> getPrerequisites() { return prerequisites; }
    public void setPrerequisites(List<Prerequisite> prerequisites) { this.prerequisites = prerequisites; }
    
    // Business methods
    public boolean isFull() { 
        return availableSeats <= 0; 
    }
    
    public void decrementSeats() { 
        if (availableSeats > 0) {
            availableSeats--; 
        }
    }
    
    public void incrementSeats() { 
        if (availableSeats < totalCapacity) {
            availableSeats++; 
        }
    }
    
    // JSON serialization method
    public String toJson() {
        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"id\":").append(id != null ? id : "null").append(",");
        json.append("\"courseCode\":\"").append(courseCode != null ? courseCode.replace("\"", "\\\"") : "").append("\",");
        json.append("\"name\":\"").append(name != null ? name.replace("\"", "\\\"") : "").append("\",");
        json.append("\"description\":\"").append(description != null ? description.replace("\"", "\\\"") : "").append("\",");
        json.append("\"department\":\"").append(department != null ? department.replace("\"", "\\\"") : "").append("\",");
        json.append("\"instructorId\":").append(instructorId != null ? instructorId : "null").append(",");
        json.append("\"totalCapacity\":").append(totalCapacity).append(",");
        json.append("\"availableSeats\":").append(availableSeats);
        
        // Add schedule if present
        if (schedule != null) {
            json.append(",\"schedule\":").append(schedule.toJson());
        }
        
        // Add prerequisites if present
        if (prerequisites != null && !prerequisites.isEmpty()) {
            json.append(",\"prerequisites\":[");
            for (int i = 0; i < prerequisites.size(); i++) {
                if (i > 0) json.append(",");
                json.append(prerequisites.get(i).toJson());
            }
            json.append("]");
        }
        
        json.append("}");
        return json.toString();
    }
}
