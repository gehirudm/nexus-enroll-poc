package admin_service.controller;

import common.model.Course;
import common.model.Student;
import common.model.Faculty;
import common.util.ResponseBuilder;
import admin_service.service.AdminService;
import admin_service.service.ReportService;
import admin_service.service.EnrollmentReport;
import admin_service.service.FacultyWorkloadReport;
import admin_service.service.CourseTrendsReport;
import java.util.List;

public class AdminController {
    private final AdminService adminService;
    private final ReportService reportService;
    
    public AdminController(AdminService adminService, ReportService reportService) {
        this.adminService = adminService;
        this.reportService = reportService;
    }
    
    public ResponseBuilder.Response getAllStudents() {
        try {
            List<Student> students = adminService.getAllStudents();
            return ResponseBuilder.success("Students retrieved successfully", students);
        } catch (Exception e) {
            return ResponseBuilder.error(e.getMessage());
        }
    }
    
    public ResponseBuilder.Response getAllFaculty() {
        try {
            List<Faculty> faculty = adminService.getAllFaculty();
            return ResponseBuilder.success("Faculty retrieved successfully", faculty);
        } catch (Exception e) {
            return ResponseBuilder.error(e.getMessage());
        }
    }
    
    public ResponseBuilder.Response getAllCourses() {
        try {
            List<Course> courses = adminService.getAllCourses();
            return ResponseBuilder.success("Courses retrieved successfully", courses);
        } catch (Exception e) {
            return ResponseBuilder.error(e.getMessage());
        }
    }
    
    public ResponseBuilder.Response createCourse(Course course) {
        try {
            Course createdCourse = adminService.createCourse(course);
            return ResponseBuilder.success("Course created successfully", createdCourse);
        } catch (Exception e) {
            return ResponseBuilder.error(e.getMessage());
        }
    }
    
    public ResponseBuilder.Response updateCourse(Long courseId, Course course) {
        try {
            Course updatedCourse = adminService.updateCourse(courseId, course);
            return ResponseBuilder.success("Course updated successfully", updatedCourse);
        } catch (Exception e) {
            return ResponseBuilder.error(e.getMessage());
        }
    }
    
    public ResponseBuilder.Response deleteCourse(Long courseId) {
        try {
            adminService.deleteCourse(courseId);
            return ResponseBuilder.success("Course deleted successfully", null);
        } catch (Exception e) {
            return ResponseBuilder.error(e.getMessage());
        }
    }
    
    public ResponseBuilder.Response forceEnrollStudent(Long studentId, Long courseId) {
        try {
            adminService.forceEnrollStudent(studentId, courseId);
            return ResponseBuilder.success("Student force enrolled successfully", null);
        } catch (Exception e) {
            return ResponseBuilder.error(e.getMessage());
        }
    }
    
    public ResponseBuilder.Response generateEnrollmentReport(String department, String semester) {
        try {
            EnrollmentReport report = reportService.generateEnrollmentReport(department, semester);
            return ResponseBuilder.success("Enrollment report generated successfully", report);
        } catch (Exception e) {
            return ResponseBuilder.error(e.getMessage());
        }
    }
    
    public ResponseBuilder.Response generateFacultyWorkloadReport() {
        try {
            FacultyWorkloadReport report = reportService.generateFacultyWorkloadReport();
            return ResponseBuilder.success("Faculty workload report generated successfully", report);
        } catch (Exception e) {
            return ResponseBuilder.error(e.getMessage());
        }
    }
    
    public ResponseBuilder.Response generateCourseTrendsReport() {
        try {
            CourseTrendsReport report = reportService.generateCourseTrendsReport();
            return ResponseBuilder.success("Course trends report generated successfully", report);
        } catch (Exception e) {
            return ResponseBuilder.error(e.getMessage());
        }
    }
}
