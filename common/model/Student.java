package common.model;

import java.util.ArrayList;
import java.util.List;

public class Student {
    private Long id;
    private String name;
    private String email;
    private String department;
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
    
    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    
    public List<Enrollment> getEnrollments() { return enrollments; }
    public void setEnrollments(List<Enrollment> enrollments) { this.enrollments = enrollments; }
    
    public List<Grade> getGrades() { return grades; }
    public void setGrades(List<Grade> grades) { this.grades = grades; }
    
    // Business method
    public boolean hasCompletedCourse(Long courseId) {
        return grades.stream().anyMatch(grade -> 
            grade.getCourseId().equals(courseId) && grade.isPassing());
    }
}
