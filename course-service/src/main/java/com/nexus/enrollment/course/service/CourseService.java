package com.nexus.enrollment.course.service;

import com.nexus.enrollment.common.model.Course;
import com.nexus.enrollment.common.exceptions.NotFoundException;
import com.nexus.enrollment.course.repository.CourseRepository;
import java.util.List;
import java.util.stream.Collectors;

public class CourseService {
    private final CourseRepository courseRepository;
    
    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }
    
    public Course getCourseById(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Course", id));
    }
    
    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }
    
    public List<Course> getCoursesByDepartment(String department) {
        return courseRepository.findByDepartment(department);
    }
    
    public List<Course> getCoursesByInstructor(Long facultyId) {
        return courseRepository.findByInstructor(facultyId);
    }
    
    public List<Course> searchCourses(String department, String keyword) {
        List<Course> courses = department != null && !department.isEmpty() 
            ? getCoursesByDepartment(department) 
            : getAllCourses();
            
        if (keyword != null && !keyword.isEmpty()) {
            String lowerKeyword = keyword.toLowerCase();
            return courses.stream()
                    .filter(course -> 
                        course.getName().toLowerCase().contains(lowerKeyword) ||
                        course.getDescription().toLowerCase().contains(lowerKeyword) ||
                        course.getCourseCode().toLowerCase().contains(lowerKeyword))
                    .collect(Collectors.toList());
        }
        
        return courses;
    }
    
    public List<Course> getAvailableCourses() {
        return courseRepository.findAvailableCourses();
    }
    
    public int getEnrollmentCount(Long courseId) {
        Course course = getCourseById(courseId);
        return course.getTotalCapacity() - course.getAvailableSeats();
    }
    
    public Course createCourse(Course course) {
        return courseRepository.save(course);
    }
    
    public Course updateCourse(Long id, Course course) {
        if (!courseRepository.existsById(id)) {
            throw new NotFoundException("Course", id);
        }
        course.setId(id);
        return courseRepository.save(course);
    }
    
    public void deleteCourse(Long id) {
        if (!courseRepository.existsById(id)) {
            throw new NotFoundException("Course", id);
        }
        courseRepository.deleteById(id);
    }
}
