package com.nexus.enrollment.admin.service;

import com.nexus.enrollment.common.model.Course;
import com.nexus.enrollment.common.model.Student;
import com.nexus.enrollment.common.model.Faculty;
import java.util.List;
import java.util.ArrayList;

public class AdminServiceImpl implements AdminService {
    
    // In a real implementation, these would call other microservices
    // For now, returning mock data or placeholders
    
    @Override
    public Course createCourse(Course course) {
        // In real implementation, this would call Course Service
        course.setId(System.currentTimeMillis()); // Mock ID generation
        return course;
    }
    
    @Override
    public Course updateCourse(Long courseId, Course course) {
        // In real implementation, this would call Course Service
        course.setId(courseId);
        return course;
    }
    
    @Override
    public void deleteCourse(Long courseId) {
        // In real implementation, this would call Course Service
        System.out.println("Course " + courseId + " deleted by admin");
    }
    
    @Override
    public void forceEnrollStudent(Long studentId, Long courseId) {
        // In real implementation, this would call Student Service with admin privileges
        System.out.println("Student " + studentId + " force enrolled in course " + courseId);
    }
    
    @Override
    public List<Student> getAllStudents() {
        // In real implementation, this would call Student Service
        return new ArrayList<>();
    }
    
    @Override
    public List<Faculty> getAllFaculty() {
        // In real implementation, this would call Faculty Service
        return new ArrayList<>();
    }
    
    @Override
    public List<Course> getAllCourses() {
        // In real implementation, this would call Course Service
        return new ArrayList<>();
    }
}
