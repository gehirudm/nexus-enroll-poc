package faculty_service.controller;

import common.model.Faculty;
import common.model.Student;
import common.model.Course;
import common.util.ResponseBuilder;
import faculty_service.service.FacultyService;
import faculty_service.service.GradeService;
import faculty_service.service.GradeSubmission;
import faculty_service.service.GradeSubmissionResult;
import java.util.List;

public class FacultyController {
    private final FacultyService facultyService;
    private final GradeService gradeService;
    
    public FacultyController(FacultyService facultyService, GradeService gradeService) {
        this.facultyService = facultyService;
        this.gradeService = gradeService;
    }
    
    public ResponseBuilder.Response getFaculty(Long id) {
        try {
            Faculty faculty = facultyService.getFacultyById(id);
            return ResponseBuilder.success("Faculty retrieved successfully", faculty);
        } catch (Exception e) {
            return ResponseBuilder.error(e.getMessage());
        }
    }
    
    public ResponseBuilder.Response getAllFaculty() {
        try {
            List<Faculty> faculty = facultyService.getAllFaculty();
            return ResponseBuilder.success("Faculty retrieved successfully", faculty);
        } catch (Exception e) {
            return ResponseBuilder.error(e.getMessage());
        }
    }
    
    public ResponseBuilder.Response getAssignedCourses(Long facultyId) {
        try {
            List<Course> courses = facultyService.getAssignedCourses(facultyId);
            return ResponseBuilder.success("Assigned courses retrieved successfully", courses);
        } catch (Exception e) {
            return ResponseBuilder.error(e.getMessage());
        }
    }
    
    public ResponseBuilder.Response getClassRoster(Long facultyId, Long courseId) {
        try {
            List<Student> roster = facultyService.getClassRoster(facultyId, courseId);
            return ResponseBuilder.success("Class roster retrieved successfully", roster);
        } catch (Exception e) {
            return ResponseBuilder.error(e.getMessage());
        }
    }
    
    public ResponseBuilder.Response submitGrades(Long facultyId, List<GradeSubmission> grades) {
        try {
            GradeSubmissionResult result = gradeService.submitGrades(facultyId, grades);
            if (result.isSuccess()) {
                return ResponseBuilder.success(result.getMessage(), result.getSubmittedGrades());
            } else {
                return ResponseBuilder.error(result.getMessage());
            }
        } catch (Exception e) {
            return ResponseBuilder.error(e.getMessage());
        }
    }
    
    public ResponseBuilder.Response getSubmittedGrades(Long facultyId, Long courseId) {
        try {
            var grades = gradeService.getSubmittedGrades(facultyId, courseId);
            return ResponseBuilder.success("Grades retrieved successfully", grades);
        } catch (Exception e) {
            return ResponseBuilder.error(e.getMessage());
        }
    }
    
    public ResponseBuilder.Response createFaculty(Faculty faculty) {
        try {
            Faculty createdFaculty = facultyService.createFaculty(faculty);
            return ResponseBuilder.success("Faculty created successfully", createdFaculty);
        } catch (Exception e) {
            return ResponseBuilder.error(e.getMessage());
        }
    }
}
