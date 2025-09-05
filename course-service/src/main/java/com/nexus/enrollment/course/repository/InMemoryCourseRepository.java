package com.nexus.enrollment.course.repository;

import com.nexus.enrollment.common.model.Course;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryCourseRepository implements CourseRepository {
    private final Map<Long, Course> courses = new HashMap<>();
    private Long nextId = 1L;
    
    @Override
    public Course save(Course course) {
        if (course.getId() == null) {
            course.setId(nextId++);
        }
        courses.put(course.getId(), course);
        return course;
    }
    
    @Override
    public Optional<Course> findById(Long id) {
        return Optional.ofNullable(courses.get(id));
    }
    
    @Override
    public List<Course> findAll() {
        return new ArrayList<>(courses.values());
    }
    
    @Override
    public void deleteById(Long id) {
        courses.remove(id);
    }
    
    @Override
    public boolean existsById(Long id) {
        return courses.containsKey(id);
    }
    
    @Override
    public List<Course> findByDepartment(String department) {
        return courses.values().stream()
                .filter(course -> course.getDepartment().equalsIgnoreCase(department))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Course> findByInstructor(Long facultyId) {
        return courses.values().stream()
                .filter(course -> course.getInstructorId().equals(facultyId))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Course> findAvailableCourses() {
        return courses.values().stream()
                .filter(course -> !course.isFull())
                .collect(Collectors.toList());
    }
}
