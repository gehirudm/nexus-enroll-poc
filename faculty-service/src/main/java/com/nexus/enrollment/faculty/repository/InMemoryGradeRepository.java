package com.nexus.enrollment.faculty.repository;

import com.nexus.enrollment.common.model.Grade;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * In-memory implementation of GradeRepository
 */
public class InMemoryGradeRepository implements GradeRepository {
    private final Map<Long, Grade> grades = new HashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);
    
    @Override
    public Grade save(Grade grade) {
        if (grade.getId() == null) {
            grade.setId(idGenerator.getAndIncrement());
        }
        grades.put(grade.getId(), grade);
        return grade;
    }
    
    @Override
    public Optional<Grade> findById(Long id) {
        return Optional.ofNullable(grades.get(id));
    }
    
    @Override
    public List<Grade> findAll() {
        return grades.values().stream().collect(Collectors.toList());
    }
    
    @Override
    public List<Grade> findByStudentId(Long studentId) {
        return grades.values().stream()
                .filter(grade -> grade.getStudentId().equals(studentId))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Grade> findByCourseId(Long courseId) {
        return grades.values().stream()
                .filter(grade -> grade.getCourseId().equals(courseId))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Grade> findByFacultyId(Long facultyId) {
        return grades.values().stream()
                .filter(grade -> grade.getFacultyId().equals(facultyId))
                .collect(Collectors.toList());
    }
    
    @Override
    public Optional<Grade> findByStudentIdAndCourseId(Long studentId, Long courseId) {
        return grades.values().stream()
                .filter(grade -> grade.getStudentId().equals(studentId) && 
                               grade.getCourseId().equals(courseId))
                .findFirst();
    }
    
    @Override
    public boolean deleteById(Long id) {
        return grades.remove(id) != null;
    }
    
    @Override
    public boolean existsById(Long id) {
        return grades.containsKey(id);
    }
}
