package com.nexus.enrollment.student.service;

import com.nexus.enrollment.common.model.Student;
import com.nexus.enrollment.common.model.Schedule;
import com.nexus.enrollment.common.model.Enrollment;
import java.util.List;

public interface StudentService {
    Student getStudentById(Long id);
    List<Student> getAllStudents();
    Schedule getStudentSchedule(Long studentId);
    List<Enrollment> getStudentEnrollments(Long studentId);
    Student createStudent(Student student);
    Student updateStudent(Long id, Student student);
    void deleteStudent(Long id);
}
