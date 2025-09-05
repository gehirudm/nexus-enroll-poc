package com.nexus.enrollment.student.controller;

import com.nexus.enrollment.common.model.Student;
import com.nexus.enrollment.common.model.Enrollment;
import com.nexus.enrollment.common.model.EnrollmentResult;
import com.nexus.enrollment.common.model.Schedule;
import com.nexus.enrollment.common.util.ResponseBuilder;
import com.nexus.enrollment.student.service.StudentService;
import com.nexus.enrollment.student.service.EnrollmentService;
import java.util.List;

public class StudentController {
    private final StudentService studentService;
    private final EnrollmentService enrollmentService;
    
    public StudentController(StudentService studentService, EnrollmentService enrollmentService) {
        this.studentService = studentService;
        this.enrollmentService = enrollmentService;
    }
    
    // HTTP endpoint handlers
    public ResponseBuilder.Response getStudent(Long id) {
        try {
            Student student = studentService.getStudentById(id);
            return ResponseBuilder.success("Student retrieved successfully", student);
        } catch (Exception e) {
            return ResponseBuilder.error(e.getMessage());
        }
    }
    
    public ResponseBuilder.Response getAllStudents() {
        try {
            List<Student> students = studentService.getAllStudents();
            return ResponseBuilder.success("Students retrieved successfully", students);
        } catch (Exception e) {
            return ResponseBuilder.error(e.getMessage());
        }
    }
    
    public ResponseBuilder.Response getStudentSchedule(Long studentId) {
        try {
            Schedule schedule = studentService.getStudentSchedule(studentId);
            return ResponseBuilder.success("Schedule retrieved successfully", schedule);
        } catch (Exception e) {
            return ResponseBuilder.error(e.getMessage());
        }
    }
    
    public ResponseBuilder.Response getStudentEnrollments(Long studentId) {
        try {
            List<Enrollment> enrollments = studentService.getStudentEnrollments(studentId);
            return ResponseBuilder.success("Enrollments retrieved successfully", enrollments);
        } catch (Exception e) {
            return ResponseBuilder.error(e.getMessage());
        }
    }
    
    public ResponseBuilder.Response enrollStudent(Long studentId, Long courseId) {
        try {
            EnrollmentResult result = enrollmentService.enrollStudent(studentId, courseId);
            if (result.isSuccess()) {
                return ResponseBuilder.success(result.getMessage(), result.getEnrollment());
            } else {
                return ResponseBuilder.error(result.getMessage());
            }
        } catch (Exception e) {
            return ResponseBuilder.error(e.getMessage());
        }
    }
    
    public ResponseBuilder.Response dropCourse(Long studentId, Long courseId) {
        try {
            EnrollmentResult result = enrollmentService.dropCourse(studentId, courseId);
            if (result.isSuccess()) {
                return ResponseBuilder.success(result.getMessage(), result.getEnrollment());
            } else {
                return ResponseBuilder.error(result.getMessage());
            }
        } catch (Exception e) {
            return ResponseBuilder.error(e.getMessage());
        }
    }
    
    public ResponseBuilder.Response createStudent(Student student) {
        try {
            Student createdStudent = studentService.createStudent(student);
            return ResponseBuilder.success("Student created successfully", createdStudent);
        } catch (Exception e) {
            return ResponseBuilder.error(e.getMessage());
        }
    }
}
