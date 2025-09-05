package com.nexus.enrollment.admin.service;

import com.nexus.enrollment.common.model.Course;
import com.nexus.enrollment.common.model.Student;
import com.nexus.enrollment.common.model.Faculty;
import java.util.List;

public interface AdminService {
    Course createCourse(Course course);
    Course updateCourse(Long courseId, Course course);
    void deleteCourse(Long courseId);
    void forceEnrollStudent(Long studentId, Long courseId);
    List<Student> getAllStudents();
    List<Faculty> getAllFaculty();
    List<Course> getAllCourses();
}
