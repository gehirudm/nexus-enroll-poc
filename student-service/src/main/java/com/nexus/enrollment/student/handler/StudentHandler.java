package com.nexus.enrollment.student.handler;

import com.nexus.enrollment.common.model.Student;
import com.nexus.enrollment.common.model.Enrollment;
import com.nexus.enrollment.common.model.EnrollmentResult;
import com.nexus.enrollment.common.model.Schedule;
import com.nexus.enrollment.common.model.Course;
import com.nexus.enrollment.common.handler.BaseHandler;
import com.nexus.enrollment.student.service.StudentService;
import com.nexus.enrollment.student.service.EnrollmentService;
import io.javalin.http.Context;
import java.util.List;

public class StudentHandler extends BaseHandler {
    
    private final StudentService studentService;
    private final EnrollmentService enrollmentService;
    
    public StudentHandler(StudentService studentService, EnrollmentService enrollmentService) {
        this.studentService = studentService;
        this.enrollmentService = enrollmentService;
    }
    
    // Javalin handler methods
    public void getAllStudents(Context ctx) {
        List<Student> students = studentService.getAllStudents();
        ctx.json(createSuccessResponse("Students retrieved successfully", students));
    }
    
    public void getStudentById(Context ctx) {
        Long id = Long.parseLong(ctx.pathParam("id")); // NumberFormatException handled globally
        Student student = studentService.getStudentById(id); // NotFoundException handled globally
        ctx.json(createSuccessResponse("Student retrieved successfully", student));
    }
    
    public void createStudent(Context ctx) {
        Student student = ctx.bodyAsClass(Student.class);
        Student createdStudent = studentService.createStudent(student);
        ctx.status(201);
        ctx.json(createSuccessResponse("Student created successfully", createdStudent));
    }
    
    public void updateStudent(Context ctx) {
        Long id = Long.parseLong(ctx.pathParam("id"));
        Student student = ctx.bodyAsClass(Student.class);
        Student updatedStudent = studentService.updateStudent(id, student);
        ctx.json(createSuccessResponse("Student updated successfully", updatedStudent));
    }
    
    public void deleteStudent(Context ctx) {
        Long id = Long.parseLong(ctx.pathParam("id"));
        studentService.deleteStudent(id);
        ctx.json(createSuccessResponse("Student deleted successfully", null));
    }
    
    public void getStudentSchedule(Context ctx) {
        Long studentId = Long.parseLong(ctx.pathParam("id"));
        Schedule schedule = studentService.getStudentSchedule(studentId);
        ctx.json(createSuccessResponse("Schedule retrieved successfully", schedule));
    }
    
    public void getStudentEnrollments(Context ctx) {
        Long studentId = Long.parseLong(ctx.pathParam("id"));
        List<Enrollment> enrollments = studentService.getStudentEnrollments(studentId);
        ctx.json(createSuccessResponse("Enrollments retrieved successfully", enrollments));
    }
    
    public void enrollStudent(Context ctx) {
        Long studentId = Long.parseLong(ctx.pathParam("id"));
        Long courseId = Long.parseLong(ctx.pathParam("courseId"));
        
        EnrollmentResult result = enrollmentService.enrollStudent(studentId, courseId);
        
        if (result.isSuccess()) {
            ctx.json(createSuccessResponse(result.getMessage(), result.getEnrollment()));
        } else {
            ctx.status(400);
            ctx.json(createErrorResponse(result.getMessage()));
        }
    }
    
    public void dropCourse(Context ctx) {
        Long studentId = Long.parseLong(ctx.pathParam("id"));
        Long courseId = Long.parseLong(ctx.pathParam("courseId"));
        
        EnrollmentResult result = enrollmentService.dropCourse(studentId, courseId);
        
        if (result.isSuccess()) {
            ctx.json(createSuccessResponse(result.getMessage(), result.getEnrollment()));
        } else {
            ctx.status(400);
            ctx.json(createErrorResponse(result.getMessage()));
        }
    }
    
    public void getWaitlistedCourses(Context ctx) {
        Long studentId = Long.parseLong(ctx.pathParam("id"));
        List<Course> waitlistedCourses = enrollmentService.getWaitlistedCourses(studentId);
        ctx.json(createSuccessResponse("Waitlisted courses retrieved successfully", waitlistedCourses));
    }
    
    public void addToWaitlist(Context ctx) {
        Long studentId = Long.parseLong(ctx.pathParam("id"));
        Long courseId = Long.parseLong(ctx.pathParam("courseId"));
        
        EnrollmentResult result = enrollmentService.addToWaitlist(studentId, courseId);
        
        if (result.isSuccess()) {
            ctx.json(createSuccessResponse(result.getMessage(), result.getEnrollment()));
        } else {
            ctx.status(400);
            ctx.json(createErrorResponse(result.getMessage()));
        }
    }
}
