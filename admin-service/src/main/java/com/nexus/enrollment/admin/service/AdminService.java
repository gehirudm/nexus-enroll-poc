package com.nexus.enrollment.admin.service;

import com.nexus.enrollment.common.model.Course;
import com.nexus.enrollment.common.model.Student;
import com.nexus.enrollment.common.model.Faculty;
import com.nexus.enrollment.common.registries.CourseServiceRegistry;
import com.nexus.enrollment.common.registries.StudentServiceRegistry;
import com.nexus.enrollment.common.registries.FacultyServiceRegistry;
import com.nexus.enrollment.common.registries.AdminServiceRegistry;
import com.nexus.enrollment.common.service.ServiceResponse;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.List;
import java.util.ArrayList;
import java.lang.reflect.Type;

public class AdminService {
    private final Gson gson = new Gson();
    
    public Course createCourse(Course course) {
        // Use CourseServiceRegistry to create course
        ServiceResponse<Course> response = CourseServiceRegistry.createCourse(course);
        if (response.isSuccess()) {
            return response.getData();
        } else {
            throw new RuntimeException("Failed to create course: " + response.getMessage());
        }
    }
    
    public Course updateCourse(Long courseId, Course course) {
        // Use CourseServiceRegistry to get existing course first
        ServiceResponse<Course> getResponse = CourseServiceRegistry.getCourse(courseId);
        if (!getResponse.isSuccess()) {
            throw new RuntimeException("Course not found: " + getResponse.getMessage());
        }
        
        // Update course details but preserve ID
        course.setId(courseId);
        
        // Use AdminServiceRegistry for privileged update operation
        ServiceResponse<Course> updateResponse = AdminServiceRegistry.updateCourse(courseId, course);
        if (updateResponse.isSuccess()) {
            return updateResponse.getData();
        } else {
            throw new RuntimeException("Failed to update course: " + updateResponse.getMessage());
        }
    }
    
    public void deleteCourse(Long courseId) {
        // Check if course exists first
        ServiceResponse<Course> response = CourseServiceRegistry.getCourse(courseId);
        if (!response.isSuccess()) {
            throw new RuntimeException("Course not found: " + response.getMessage());
        }
        
        // Use AdminServiceRegistry for privileged delete operation
        ServiceResponse<String> deleteResponse = AdminServiceRegistry.deleteCourse(courseId);
        if (!deleteResponse.isSuccess()) {
            throw new RuntimeException("Failed to delete course: " + deleteResponse.getMessage());
        }
        
        System.out.println("Course " + courseId + " deleted by admin");
    }
    
    public void forceEnrollStudent(Long studentId, Long courseId) {
        // Check if student exists
        ServiceResponse<Student> studentResponse = StudentServiceRegistry.getStudent(studentId);
        if (!studentResponse.isSuccess()) {
            throw new RuntimeException("Student not found: " + studentResponse.getMessage());
        }
        
        // Check if course exists
        ServiceResponse<Course> courseResponse = CourseServiceRegistry.getCourse(courseId);
        if (!courseResponse.isSuccess()) {
            throw new RuntimeException("Course not found: " + courseResponse.getMessage());
        }
        
        // Force enroll student (using regular enrollment for now)
        ServiceResponse<String> enrollResponse = StudentServiceRegistry.enrollStudent(studentId, courseId);
        if (!enrollResponse.isSuccess()) {
            throw new RuntimeException("Failed to force enroll student: " + enrollResponse.getMessage());
        }
        
        System.out.println("Student " + studentId + " force enrolled in course " + courseId);
    }
    
    public List<Student> getAllStudents() {
        ServiceResponse<String> response = AdminServiceRegistry.getAllStudents();
        if (response.isSuccess()) {
            try {
                Type listType = new TypeToken<List<Student>>(){}.getType();
                return gson.fromJson(response.getData(), listType);
            } catch (Exception e) {
                // If JSON parsing fails, return empty list
                System.err.println("Failed to parse students JSON: " + e.getMessage());
                return new ArrayList<>();
            }
        } else {
            throw new RuntimeException("Failed to get students: " + response.getMessage());
        }
    }
    
    public List<Faculty> getAllFaculty() {
        ServiceResponse<String> response = AdminServiceRegistry.getAllFaculty();
        if (response.isSuccess()) {
            try {
                Type listType = new TypeToken<List<Faculty>>(){}.getType();
                return gson.fromJson(response.getData(), listType);
            } catch (Exception e) {
                // If JSON parsing fails, return empty list
                System.err.println("Failed to parse faculty JSON: " + e.getMessage());
                return new ArrayList<>();
            }
        } else {
            throw new RuntimeException("Failed to get faculty: " + response.getMessage());
        }
    }
    
    public List<Course> getAllCourses() {
        ServiceResponse<String> response = CourseServiceRegistry.getAllCourses();
        if (response.isSuccess()) {
            try {
                Type listType = new TypeToken<List<Course>>(){}.getType();
                return gson.fromJson(response.getData(), listType);
            } catch (Exception e) {
                // If JSON parsing fails, return empty list
                System.err.println("Failed to parse courses JSON: " + e.getMessage());
                return new ArrayList<>();
            }
        } else {
            throw new RuntimeException("Failed to get courses: " + response.getMessage());
        }
    }
}
