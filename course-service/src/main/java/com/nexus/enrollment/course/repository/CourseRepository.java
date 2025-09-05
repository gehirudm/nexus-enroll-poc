package com.nexus.enrollment.course.repository;

import com.nexus.enrollment.common.model.Course;
import com.nexus.enrollment.common.repository.CrudRepository;
import java.util.List;

public interface CourseRepository extends CrudRepository<Course, Long> {
    List<Course> findByDepartment(String department);
    List<Course> findByInstructor(Long facultyId);
    List<Course> findAvailableCourses();
}
