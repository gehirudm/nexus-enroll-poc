package com.nexus.enrollment.common.model;

import com.nexus.enrollment.common.util.JsonSerializable;
import java.util.ArrayList;
import java.util.List;

public class Student implements JsonSerializable {
    private Long id;
    private String name;
    private String email;
    private String department;
    private String major;
    private List<Enrollment> enrollments;
    private List<Grade> grades;
    
    public Student() {
        this.enrollments = new ArrayList<>();
        this.grades = new ArrayList<>();
    }
    
    public Student(String name, String email, String department) {
        this();
        this.name = name;
        this.email = email;
        this.department = department;
    }
    
    public Student(String name, String email, String department, String major) {
        this();
        this.name = name;
        this.email = email;
        this.department = department;
        this.major = major;
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
    
    public String getMajor() { return major; }
    public void setMajor(String major) { this.major = major; }
    
    public List<Enrollment> getEnrollments() { return enrollments; }
    public void setEnrollments(List<Enrollment> enrollments) { this.enrollments = enrollments; }
    
    public List<Grade> getGrades() { return grades; }
    public void setGrades(List<Grade> grades) { this.grades = grades; }
    
    // Business method
    public boolean hasCompletedCourse(Long courseId) {
        return grades.stream().anyMatch(grade -> 
            grade.getCourseId().equals(courseId) && grade.isPassing());
    }
    
    // JSON serialization method
    @Override
    public String toJson() {
        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"id\":").append(id).append(",");
        json.append("\"name\":\"").append(name != null ? name.replace("\"", "\\\"") : "").append("\",");
        json.append("\"email\":\"").append(email != null ? email.replace("\"", "\\\"") : "").append("\",");
        json.append("\"department\":\"").append(department != null ? department.replace("\"", "\\\"") : "").append("\",");
        json.append("\"major\":\"").append(major != null ? major.replace("\"", "\\\"") : "").append("\",");
        json.append("\"enrollments\":[");
        if (enrollments != null) {
            for (int i = 0; i < enrollments.size(); i++) {
                if (i > 0) json.append(",");
                if (enrollments.get(i) instanceof JsonSerializable) {
                    json.append(((JsonSerializable) enrollments.get(i)).toJson());
                } else {
                    json.append("\"").append(enrollments.get(i).toString()).append("\"");
                }
            }
        }
        json.append("],");
        json.append("\"grades\":[");
        if (grades != null) {
            for (int i = 0; i < grades.size(); i++) {
                if (i > 0) json.append(",");
                if (grades.get(i) instanceof JsonSerializable) {
                    json.append(((JsonSerializable) grades.get(i)).toJson());
                } else {
                    json.append("\"").append(grades.get(i).toString()).append("\"");
                }
            }
        }
        json.append("]");
        json.append("}");
        return json.toString();
    }
}
