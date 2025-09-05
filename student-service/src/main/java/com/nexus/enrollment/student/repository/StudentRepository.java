package com.nexus.enrollment.student.repository;

import com.nexus.enrollment.common.model.Student;
import com.nexus.enrollment.common.repository.CrudRepository;
import java.util.List;
import java.util.Optional;

public interface StudentRepository extends CrudRepository<Student, Long> {
    Optional<Student> findByEmail(String email);
    List<Student> findByCourseEnrolled(Long courseId);
}
