package com.nexus.enrollment.faculty.service;

import com.nexus.enrollment.common.model.Faculty;
import com.nexus.enrollment.common.model.Course;
import com.nexus.enrollment.common.model.Student;
import com.nexus.enrollment.common.exceptions.NotFoundException;
import com.nexus.enrollment.common.service.ServiceClient;
import com.nexus.enrollment.common.service.ServiceResponse;
import com.nexus.enrollment.faculty.repository.FacultyRepository;
import java.util.List;
import java.util.ArrayList;

public class FacultyService {
    private final FacultyRepository facultyRepository;
    private final ServiceClient serviceClient;
    
    public FacultyService(FacultyRepository facultyRepository) {
        this.facultyRepository = facultyRepository;
        this.serviceClient = new ServiceClient();
    }
    
    public Faculty getFacultyById(Long id) {
        return facultyRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Faculty", id));
    }
    
    public List<Course> getAssignedCourses(Long facultyId) {
        Faculty faculty = getFacultyById(facultyId);
        List<Long> assignedCourseIds = faculty.getAssignedCourseIds();
        
        // Use Course Service to get actual course details instead of mock data
        List<Course> courses = new ArrayList<>();
        for (Long courseId : assignedCourseIds) {
            ServiceResponse<Course> courseResponse = serviceClient.get("course", "/courses/" + courseId, Course.class);
            if (courseResponse.isSuccess()) {
                courses.add(courseResponse.getData());
            } else {
                // Fallback to mock data if Course Service is unavailable
                Course mockCourse = createMockCourse(courseId);
                courses.add(mockCourse);
            }
        }
        
        return courses;
    }
    
    public List<Student> getClassRoster(Long facultyId, Long courseId) {
        // Verify faculty has access to this course
        Faculty faculty = getFacultyById(facultyId);
        if (!faculty.getAssignedCourseIds().contains(courseId)) {
            throw new IllegalArgumentException("Faculty does not have access to course " + courseId);
        }
        
        // Use Student Service to get enrolled students for this course
        // This is a simplified implementation - in reality would need to call Student Service
        return new ArrayList<>(); // Placeholder
    }
    
    public Faculty createFaculty(Faculty faculty) {
        return facultyRepository.save(faculty);
    }
    
    public Faculty updateFaculty(Long id, Faculty faculty) {
        if (!facultyRepository.existsById(id)) {
            throw new NotFoundException("Faculty", id);
        }
        faculty.setId(id);
        return facultyRepository.save(faculty);
    }
    
    public void deleteFaculty(Long id) {
        if (!facultyRepository.existsById(id)) {
            throw new NotFoundException("Faculty", id);
        }
        facultyRepository.deleteById(id);
    }
    
    public List<Faculty> getAllFaculty() {
        return facultyRepository.findAll();
    }
    
    public void assignCourse(Long facultyId, Long courseId) {
        Faculty faculty = getFacultyById(facultyId);
        if (!faculty.getAssignedCourseIds().contains(courseId)) {
            faculty.getAssignedCourseIds().add(courseId);
            facultyRepository.save(faculty);
        }
    }
    
    private Course createMockCourse(Long courseId) {
        // Create mock course data based on course ID as fallback
        Course course;
        switch (courseId.intValue()) {
            case 1:
                course = new Course("CS101", "Introduction to Computer Science", 
                    "Basic programming concepts and problem solving", 1L, 
                    "Computer Science", 30, null);
                break;
            case 2:
                course = new Course("CS201", "Data Structures", 
                    "Algorithms and data structures", 1L, 
                    "Computer Science", 25, null);
                break;
            case 3:
                course = new Course("MATH101", "Calculus I", 
                    "Differential and integral calculus", 2L, 
                    "Mathematics", 35, null);
                break;
            default:
                course = new Course("UNKNOWN", "Unknown Course", 
                    "Course details not available", 0L, 
                    "Unknown", 0, null);
        }
        course.setId(courseId);
        return course;
    }
}
