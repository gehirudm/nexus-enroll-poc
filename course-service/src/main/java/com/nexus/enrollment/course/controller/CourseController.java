package com.nexus.enrollment.course.controller;

import com.nexus.enrollment.common.model.Course;
import com.nexus.enrollment.common.util.ResponseBuilder;
import com.nexus.enrollment.course.service.CourseService;
import java.util.List;

public class CourseController {
    private final CourseService courseService;
    
    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }
    
    public ResponseBuilder.Response getCourse(Long id) {
        try {
            Course course = courseService.getCourseById(id);
            return ResponseBuilder.success("Course retrieved successfully", course);
        } catch (Exception e) {
            return ResponseBuilder.error(e.getMessage());
        }
    }
    
    public ResponseBuilder.Response getAllCourses() {
        try {
            List<Course> courses = courseService.getAllCourses();
            return ResponseBuilder.success("Courses retrieved successfully", courses);
        } catch (Exception e) {
            return ResponseBuilder.error(e.getMessage());
        }
    }
    
    public ResponseBuilder.Response getCoursesByDepartment(String department) {
        try {
            List<Course> courses = courseService.getCoursesByDepartment(department);
            return ResponseBuilder.success("Courses retrieved successfully", courses);
        } catch (Exception e) {
            return ResponseBuilder.error(e.getMessage());
        }
    }
    
    public ResponseBuilder.Response getCoursesByInstructor(Long facultyId) {
        try {
            List<Course> courses = courseService.getCoursesByInstructor(facultyId);
            return ResponseBuilder.success("Courses retrieved successfully", courses);
        } catch (Exception e) {
            return ResponseBuilder.error(e.getMessage());
        }
    }
    
    public ResponseBuilder.Response searchCourses(String department, String keyword) {
        try {
            List<Course> courses = courseService.searchCourses(department, keyword);
            return ResponseBuilder.success("Courses retrieved successfully", courses);
        } catch (Exception e) {
            return ResponseBuilder.error(e.getMessage());
        }
    }
    
    public ResponseBuilder.Response getAvailableCourses() {
        try {
            List<Course> courses = courseService.getAvailableCourses();
            return ResponseBuilder.success("Available courses retrieved successfully", courses);
        } catch (Exception e) {
            return ResponseBuilder.error(e.getMessage());
        }
    }
    
    public ResponseBuilder.Response getEnrollmentCount(Long courseId) {
        try {
            int count = courseService.getEnrollmentCount(courseId);
            return ResponseBuilder.success("Enrollment count retrieved successfully", count);
        } catch (Exception e) {
            return ResponseBuilder.error(e.getMessage());
        }
    }
    
    public ResponseBuilder.Response createCourse(Course course) {
        try {
            Course createdCourse = courseService.createCourse(course);
            return ResponseBuilder.success("Course created successfully", createdCourse);
        } catch (Exception e) {
            return ResponseBuilder.error(e.getMessage());
        }
    }
    
    public ResponseBuilder.Response updateCourse(Long id, Course course) {
        try {
            Course updatedCourse = courseService.updateCourse(id, course);
            return ResponseBuilder.success("Course updated successfully", updatedCourse);
        } catch (Exception e) {
            return ResponseBuilder.error(e.getMessage());
        }
    }
    
    public ResponseBuilder.Response deleteCourse(Long id) {
        try {
            courseService.deleteCourse(id);
            return ResponseBuilder.success("Course deleted successfully", null);
        } catch (Exception e) {
            return ResponseBuilder.error(e.getMessage());
        }
    }
}
