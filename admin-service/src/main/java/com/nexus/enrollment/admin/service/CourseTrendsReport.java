package com.nexus.enrollment.admin.service;

import java.util.Map;
import java.util.List;

public class CourseTrendsReport {
    private Map<String, Integer> popularCourses;
    private Map<String, Double> enrollmentTrends;
    private List<Map<String, Object>> departmentAnalysis;
    
    public CourseTrendsReport() {}
    
    public CourseTrendsReport(Map<String, Integer> popularCourses, 
                             Map<String, Double> enrollmentTrends,
                             List<Map<String, Object>> departmentAnalysis) {
        this.popularCourses = popularCourses;
        this.enrollmentTrends = enrollmentTrends;
        this.departmentAnalysis = departmentAnalysis;
    }
    
    // Getters and setters
    public Map<String, Integer> getPopularCourses() { return popularCourses; }
    public void setPopularCourses(Map<String, Integer> popularCourses) { this.popularCourses = popularCourses; }
    
    public Map<String, Double> getEnrollmentTrends() { return enrollmentTrends; }
    public void setEnrollmentTrends(Map<String, Double> enrollmentTrends) { this.enrollmentTrends = enrollmentTrends; }
    
    public List<Map<String, Object>> getDepartmentAnalysis() { return departmentAnalysis; }
    public void setDepartmentAnalysis(List<Map<String, Object>> departmentAnalysis) { this.departmentAnalysis = departmentAnalysis; }
}
