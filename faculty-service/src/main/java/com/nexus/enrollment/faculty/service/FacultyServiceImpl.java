package com.nexus.enrollment.faculty.service;

import com.nexus.enrollment.common.model.Faculty;
import com.nexus.enrollment.common.model.Course;
import com.nexus.enrollment.common.model.Student;
import com.nexus.enrollment.common.exceptions.NotFoundException;
import com.nexus.enrollment.faculty.repository.FacultyRepository;
import java.util.List;
import java.util.ArrayList;

public class FacultyServiceImpl implements FacultyService {
    private final FacultyRepository facultyRepository;
    
    public FacultyServiceImpl(FacultyRepository facultyRepository) {
        this.facultyRepository = facultyRepository;
    }
    
    @Override
    public Faculty getFacultyById(Long id) {
        return facultyRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Faculty", id));
    }
    
    @Override
    public List<Course> getAssignedCourses(Long facultyId) {
        Faculty faculty = getFacultyById(facultyId);
        List<Long> assignedCourseIds = faculty.getAssignedCourseIds();
        
        // Create mock course data based on assigned course IDs
        // In a real implementation, this would call Course Service to get course details
        List<Course> courses = new ArrayList<>();
        for (Long courseId : assignedCourseIds) {
            Course course = createMockCourse(courseId);
            courses.add(course);
        }
        
        return courses;
    }
    
    private Course createMockCourse(Long courseId) {
        // Create mock course data based on course ID
        Course course;
        switch (courseId.intValue()) {
            case 1:
                course = new Course("CS101", "Introduction to Computer Science", 
                    "Basic programming concepts and problem solving", 1L, 
                    "Computer Science", 30, null);
                break;
            case 2:
                course = new Course("CS201", "Data Structures", 
                    "Arrays, linked lists, stacks, queues, trees", 1L, 
                    "Computer Science", 25, null);
                break;
            case 3:
                course = new Course("MATH101", "Calculus I", 
                    "Differential and integral calculus", 2L, 
                    "Mathematics", 35, null);
                break;
            case 4:
                course = new Course("PHYS101", "Physics I", 
                    "Classical mechanics and thermodynamics", 3L, 
                    "Physics", 20, null);
                break;
            case 5:
                course = new Course("PHYS201", "Physics II", 
                    "Electricity, magnetism, and optics", 3L, 
                    "Physics", 18, null);
                break;
            default:
                course = new Course("UNKNOWN", "Unknown Course", 
                    "Course details not available", 999L, 
                    "General", 0, null);
                break;
        }
        
        // Set the course ID to match the requested course ID
        course.setId(courseId);
        return course;
    }
    
    @Override
    public List<Student> getClassRoster(Long facultyId, Long courseId) {
        Faculty faculty = getFacultyById(facultyId);
        // Verify faculty is assigned to this course
        if (!faculty.getAssignedCourseIds().contains(courseId)) {
            throw new RuntimeException("Faculty is not assigned to this course");
        }
        
        // Create mock student roster based on course ID
        // In a real implementation, this would call Student Service to get enrolled students
        return createMockRoster(courseId);
    }
    
    private List<Student> createMockRoster(Long courseId) {
        List<Student> roster = new ArrayList<>();
        
        // Create different mock students for different courses
        switch (courseId.intValue()) {
            case 1: // CS101 - Introduction to Computer Science
                roster.add(createMockStudent(1L, "John Doe", "john.doe@student.edu", "Computer Science"));
                roster.add(createMockStudent(2L, "Jane Smith", "jane.smith@student.edu", "Computer Science"));
                roster.add(createMockStudent(3L, "Bob Wilson", "bob.wilson@student.edu", "Computer Science"));
                roster.add(createMockStudent(4L, "Alice Brown", "alice.brown@student.edu", "Computer Science"));
                break;
            case 2: // CS201 - Data Structures
                roster.add(createMockStudent(1L, "John Doe", "john.doe@student.edu", "Computer Science"));
                roster.add(createMockStudent(5L, "Charlie Davis", "charlie.davis@student.edu", "Computer Science"));
                roster.add(createMockStudent(6L, "Diana Miller", "diana.miller@student.edu", "Computer Science"));
                break;
            case 3: // MATH101 - Calculus I
                roster.add(createMockStudent(7L, "Eva Garcia", "eva.garcia@student.edu", "Mathematics"));
                roster.add(createMockStudent(8L, "Frank Johnson", "frank.johnson@student.edu", "Mathematics"));
                roster.add(createMockStudent(9L, "Grace Lee", "grace.lee@student.edu", "Engineering"));
                break;
            case 4: // PHYS101 - Physics I
                roster.add(createMockStudent(10L, "Henry Adams", "henry.adams@student.edu", "Physics"));
                roster.add(createMockStudent(11L, "Ivy Clark", "ivy.clark@student.edu", "Physics"));
                break;
            case 5: // PHYS201 - Physics II
                roster.add(createMockStudent(10L, "Henry Adams", "henry.adams@student.edu", "Physics"));
                roster.add(createMockStudent(12L, "Jack Taylor", "jack.taylor@student.edu", "Physics"));
                break;
            default:
                // Return empty roster for unknown courses
                break;
        }
        
        return roster;
    }
    
    private Student createMockStudent(Long studentId, String name, String email, String major) {
        Student student = new Student(name, email, major);
        student.setId(studentId);
        return student;
    }
    
    @Override
    public Faculty createFaculty(Faculty faculty) {
        return facultyRepository.save(faculty);
    }
    
    @Override
    public Faculty updateFaculty(Long id, Faculty faculty) {
        if (!facultyRepository.existsById(id)) {
            throw new NotFoundException("Faculty", id);
        }
        faculty.setId(id);
        return facultyRepository.save(faculty);
    }
    
    @Override
    public void deleteFaculty(Long id) {
        if (!facultyRepository.existsById(id)) {
            throw new NotFoundException("Faculty", id);
        }
        facultyRepository.deleteById(id);
    }
    
    @Override
    public List<Faculty> getAllFaculty() {
        return facultyRepository.findAll();
    }
}
