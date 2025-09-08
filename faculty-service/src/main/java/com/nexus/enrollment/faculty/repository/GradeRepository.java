package com.nexus.enrollment.faculty.repository;

import com.nexus.enrollment.common.model.Grade;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing Grade entities
 */
public interface GradeRepository {
    
    /**
     * Save a grade
     */
    Grade save(Grade grade);
    
    /**
     * Find grade by ID
     */
    Optional<Grade> findById(Long id);
    
    /**
     * Find all grades
     */
    List<Grade> findAll();
    
    /**
     * Find grades by student ID
     */
    List<Grade> findByStudentId(Long studentId);
    
    /**
     * Find grades by course ID
     */
    List<Grade> findByCourseId(Long courseId);
    
    /**
     * Find grades by faculty ID
     */
    List<Grade> findByFacultyId(Long facultyId);
    
    /**
     * Find grade by student ID and course ID
     */
    Optional<Grade> findByStudentIdAndCourseId(Long studentId, Long courseId);
    
    /**
     * Delete grade by ID
     */
    boolean deleteById(Long id);
    
    /**
     * Check if grade exists
     */
    boolean existsById(Long id);
}
