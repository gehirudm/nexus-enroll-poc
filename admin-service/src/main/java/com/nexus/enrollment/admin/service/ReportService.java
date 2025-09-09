package com.nexus.enrollment.admin.service;

import java.util.*;

public class ReportService {
    
    public EnrollmentReport generateEnrollmentReport(String department, String semester) {
        // Mock data for demonstration
        Map<String, Integer> courseEnrollments = new HashMap<>();
        courseEnrollments.put("CS101", 25);
        courseEnrollments.put("CS201", 20);
        courseEnrollments.put("MATH101", 30);
        
        List<Map<String, Object>> studentData = new ArrayList<>();
        Map<String, Object> studentInfo = new HashMap<>();
        studentInfo.put("totalStudents", 75);
        studentInfo.put("newEnrollments", 15);
        studentData.add(studentInfo);
        
        return new EnrollmentReport(department, semester, 75, courseEnrollments, studentData);
    }
    
    public FacultyWorkloadReport generateFacultyWorkloadReport() {
        // Mock data for demonstration
        Map<String, Integer> facultyCourseCount = new HashMap<>();
        facultyCourseCount.put("Dr. Alice Johnson", 3);
        facultyCourseCount.put("Prof. Bob Smith", 2);
        facultyCourseCount.put("Dr. Carol Davis", 4);
        
        Map<String, Integer> facultyStudentCount = new HashMap<>();
        facultyStudentCount.put("Dr. Alice Johnson", 75);
        facultyStudentCount.put("Prof. Bob Smith", 50);
        facultyStudentCount.put("Dr. Carol Davis", 100);
        
        List<Map<String, Object>> workloadDistribution = new ArrayList<>();
        Map<String, Object> distribution = new HashMap<>();
        distribution.put("averageCoursesPerFaculty", 3.0);
        distribution.put("averageStudentsPerFaculty", 75.0);
        workloadDistribution.add(distribution);
        
        return new FacultyWorkloadReport(facultyCourseCount, facultyStudentCount, workloadDistribution);
    }
    
    public CourseTrendsReport generateCourseTrendsReport() {
        // Mock data for demonstration
        Map<String, Integer> popularCourses = new HashMap<>();
        popularCourses.put("CS101", 95);
        popularCourses.put("MATH201", 88);
        popularCourses.put("PHYS101", 76);
        
        Map<String, Double> enrollmentTrends = new HashMap<>();
        enrollmentTrends.put("Computer Science", 15.5);
        enrollmentTrends.put("Mathematics", 8.2);
        enrollmentTrends.put("Physics", 5.8);
        
        List<Map<String, Object>> departmentAnalysis = new ArrayList<>();
        Map<String, Object> analysis = new HashMap<>();
        analysis.put("fastestGrowing", "Computer Science");
        analysis.put("mostPopular", "CS101");
        departmentAnalysis.add(analysis);
        
        return new CourseTrendsReport(popularCourses, enrollmentTrends, departmentAnalysis);
    }
}
