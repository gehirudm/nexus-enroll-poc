package com.nexus.enrollment.faculty.service;

import com.nexus.enrollment.common.model.Faculty;
import com.nexus.enrollment.common.model.Course;
import com.nexus.enrollment.common.model.Student;
import java.util.List;

public interface FacultyService {
    Faculty getFacultyById(Long id);
    List<Course> getAssignedCourses(Long facultyId);
    List<Student> getClassRoster(Long facultyId, Long courseId);
    Faculty createFaculty(Faculty faculty);
    Faculty updateFaculty(Long id, Faculty faculty);
    void deleteFaculty(Long id);
    List<Faculty> getAllFaculty();
}
