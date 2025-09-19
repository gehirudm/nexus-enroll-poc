package com.nexus.enrollment.faculty.handler;

import com.nexus.enrollment.common.model.Faculty;
import com.nexus.enrollment.common.model.Course;
import com.nexus.enrollment.common.model.Student;
import com.nexus.enrollment.common.model.Grade;
import com.nexus.enrollment.common.handler.BaseHandler;
import com.nexus.enrollment.common.service.ServiceClient;
import com.nexus.enrollment.common.service.ServiceResponse;
import com.nexus.enrollment.common.exceptions.BadRequestException;
import com.nexus.enrollment.faculty.service.FacultyService;
import com.nexus.enrollment.faculty.service.GradeService;
import com.nexus.enrollment.faculty.service.GradeSubmission;
import com.nexus.enrollment.faculty.service.GradeSubmissionResult;
import com.nexus.enrollment.faculty.service.GradeApprovalResult;
import io.javalin.http.Context;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.List;
import java.util.ArrayList;

public class FacultyHandler extends BaseHandler {

    private final FacultyService facultyService;
    private final GradeService gradeService;
    private final ServiceClient serviceClient;

    public FacultyHandler(FacultyService facultyService, GradeService gradeService) {
        this.facultyService = facultyService;
        this.gradeService = gradeService;
        this.serviceClient = new ServiceClient();
    }

    // Javalin handler methods
    public void getFacultyById(Context ctx) {
        Long id = Long.parseLong(ctx.pathParam("id")); // NumberFormatException handled globally
        Faculty faculty = facultyService.getFacultyById(id); // NotFoundException handled globally
        ctx.json(createSuccessResponse("Faculty retrieved successfully", faculty));
    }

    public void getFacultyCourses(Context ctx) {
        Long facultyId = Long.parseLong(ctx.pathParam("id")); // NumberFormatException handled globally
        List<Course> courses = facultyService.getAssignedCourses(facultyId);
        ctx.json(createSuccessResponse("Faculty courses retrieved successfully", courses));
    }

    public void getClassRoster(Context ctx) {
        Long facultyId = Long.parseLong(ctx.pathParam("id"));
        Long courseId = Long.parseLong(ctx.pathParam("courseId"));
        List<Student> roster = facultyService.getClassRoster(facultyId, courseId);
        ctx.json(createSuccessResponse("Class roster retrieved successfully", roster));
    }

    public void submitGrades(Context ctx) {
        Long facultyId = Long.parseLong(ctx.pathParam("id"));
        
        try {
            // Properly deserialize the JSON body
            String requestBody = ctx.body();
            Gson gson = new Gson();
            Type listType = new TypeToken<List<GradeSubmission>>(){}.getType();
            List<GradeSubmission> gradeSubmissions = gson.fromJson(requestBody, listType);
            
            // Call the grade service to actually store the grades
            GradeSubmissionResult result = gradeService.submitGrades(facultyId, gradeSubmissions);
            
            if (result.isSuccess()) {
                ctx.json(createSuccessResponse(result.getMessage(), result.getSubmittedGrades()));
            } else {
                ctx.status(400).json(createErrorResponse(result.getMessage()));
            }
        } catch (Exception e) {
            ctx.status(400).json(createErrorResponse("Failed to submit grades: " + e.getMessage()));
        }
    }

    public void getSubmittedGrades(Context ctx) {
        Long facultyId = Long.parseLong(ctx.pathParam("id"));
        Long courseId = Long.parseLong(ctx.pathParam("courseId"));
        List<Grade> grades = gradeService.getSubmittedGrades(facultyId, courseId);
        ctx.json(createSuccessResponse("Grades retrieved successfully", grades));
    }

    public void submitCourseRequest(Context ctx) {
        Long facultyId = Long.parseLong(ctx.pathParam("id"));
        // For now, return a simple success message
        ctx.json(createSuccessResponse("Course request submitted successfully", null));
    }

    public void assignCourseToFaculty(Context ctx) {
        Long facultyId = Long.parseLong(ctx.pathParam("id"));
        Long courseId = Long.parseLong(ctx.pathParam("courseId"));

        // Verify course exists using Course Service
        ServiceResponse<Course> courseResponse = serviceClient.get("course", "/courses/" + courseId, Course.class);
        if (!courseResponse.isSuccess()) {
            throw new BadRequestException("Course not found: " + courseResponse.getMessage());
        }

        // Assign course to faculty
        facultyService.assignCourse(facultyId, courseId);
        ctx.json(createSuccessResponse("Course assigned to faculty successfully", null));
    }

    // Grade approval endpoints
    public void getPendingGrades(Context ctx) {
        Long facultyId = Long.parseLong(ctx.pathParam("id"));
        String courseIdParam = ctx.queryParam("courseId");

        List<Grade> pendingGrades;
        if (courseIdParam != null) {
            Long courseId = Long.parseLong(courseIdParam);
            pendingGrades = gradeService.getPendingGradesForCourse(facultyId, courseId);
        } else {
            pendingGrades = gradeService.getPendingGrades(facultyId);
        }

        ctx.json(createSuccessResponse("Pending grades retrieved successfully", pendingGrades));
    }

    public void approveGrade(Context ctx) {
        Long facultyId = Long.parseLong(ctx.pathParam("id"));
        Long gradeId = Long.parseLong(ctx.pathParam("gradeId"));

        GradeApprovalResult result = gradeService.approveGrade(facultyId, gradeId);

        if (result.isSuccess()) {
            ctx.json(createSuccessResponse("Grade approved successfully", result.getGrade()));
        } else {
            throw new BadRequestException(result.getMessage());
        }
    }

    public void rejectGrade(Context ctx) {
        Long facultyId = Long.parseLong(ctx.pathParam("id"));
        Long gradeId = Long.parseLong(ctx.pathParam("gradeId"));

        // Get rejection reason from request body
        String reason = ctx.bodyAsClass(String.class);
        if (reason == null || reason.trim().isEmpty()) {
            reason = "Grade rejected by faculty";
        }

        GradeApprovalResult result = gradeService.rejectGrade(facultyId, gradeId, reason);

        if (result.isSuccess()) {
            ctx.json(createSuccessResponse("Grade rejected successfully", result.getGrade()));
        } else {
            throw new BadRequestException(result.getMessage());
        }
    }
}
