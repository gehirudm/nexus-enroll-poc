package com.nexus.enrollment.faculty;

import com.nexus.enrollment.common.model.Faculty;
import com.nexus.enrollment.common.web.WebServer;
import com.nexus.enrollment.faculty.repository.FacultyRepository;
import com.nexus.enrollment.faculty.repository.InMemoryFacultyRepository;
import com.nexus.enrollment.faculty.repository.GradeRepository;
import com.nexus.enrollment.faculty.repository.InMemoryGradeRepository;
import com.nexus.enrollment.faculty.service.FacultyService;
import com.nexus.enrollment.faculty.service.GradeService;
import com.nexus.enrollment.faculty.handler.FacultyHandler;
import io.javalin.Javalin;
import java.util.Arrays;

public class FacultyServiceApplication {
    
    private static final int PORT = 8083;
    private static FacultyHandler facultyHandler;
    
    public static void main(String[] args) {
        // Initialize repositories
        FacultyRepository facultyRepo = new InMemoryFacultyRepository();
        GradeRepository gradeRepo = new InMemoryGradeRepository();
        
        // Initialize services
        FacultyService facultyService = new FacultyService(facultyRepo);
        GradeService gradeService = new GradeService(gradeRepo);
        
        // Initialize handler
        facultyHandler = new FacultyHandler(facultyService, gradeService);
        
        // Initialize with sample data
        initializeSampleData(facultyRepo);
        
        // Start Javalin server
        startJavalinServer();
    }
    
    private static void startJavalinServer() {
        Javalin app = WebServer.createAndConfigureServer();
        
        app.start(PORT);
        
        System.out.println("Faculty Service started on port " + PORT);
        System.out.println("Available endpoints:");
        
        // Faculty Service Endpoints using FacultyHandler methods
        app.get("/faculty/{id}", facultyHandler::getFacultyById);
        app.get("/faculty/{id}/courses", facultyHandler::getFacultyCourses);
        app.get("/faculty/{id}/roster/{courseId}", facultyHandler::getClassRoster);
        app.post("/faculty/{id}/grades", facultyHandler::submitGrades);
        
        // Grade approval endpoints - MUST come before parameterized routes
        app.get("/faculty/{id}/grades/pending", facultyHandler::getPendingGrades);
        app.post("/faculty/{id}/grades/{gradeId}/approve", facultyHandler::approveGrade);
        app.post("/faculty/{id}/grades/{gradeId}/reject", facultyHandler::rejectGrade);
        
        // Parameterized grade routes - MUST come after specific routes
        app.get("/faculty/{id}/grades/{courseId}", facultyHandler::getSubmittedGrades);
        
        app.put("/faculty/{id}/course-request", facultyHandler::submitCourseRequest);
        app.post("/faculty/{id}/courses/{courseId}", facultyHandler::assignCourseToFaculty);
        
        System.out.println("  GET /faculty/{id} - Get faculty by ID");
        System.out.println("  GET /faculty/{id}/courses - Get faculty's assigned courses");
        System.out.println("  GET /faculty/{id}/roster/{courseId} - Get class roster for a course");
        System.out.println("  POST /faculty/{id}/grades - Submit grades for a course");
        System.out.println("  GET /faculty/{id}/grades/pending - Get pending grades for approval");
        System.out.println("  POST /faculty/{id}/grades/{gradeId}/approve - Approve a pending grade");
        System.out.println("  POST /faculty/{id}/grades/{gradeId}/reject - Reject a pending grade");
        System.out.println("  GET /faculty/{id}/grades/{courseId} - Get submitted grades for a course");
        System.out.println("  PUT /faculty/{id}/course-request - Submit course change request");
        System.out.println("  POST /faculty/{id}/courses/{courseId} - Assign course to faculty");
    }
    
    private static void initializeSampleData(FacultyRepository repo) {
        Faculty faculty1 = new Faculty("Dr. Alice Johnson", "alice.johnson@university.edu", "Computer Science");
        faculty1.setAssignedCourseIds(Arrays.asList(1L, 2L));
        
        Faculty faculty2 = new Faculty("Prof. Bob Smith", "bob.smith@university.edu", "Mathematics");
        faculty2.setAssignedCourseIds(Arrays.asList(3L));
        
        Faculty faculty3 = new Faculty("Dr. Carol Davis", "carol.davis@university.edu", "Physics");
        faculty3.setAssignedCourseIds(Arrays.asList(4L, 5L));
        
        repo.save(faculty1);
        repo.save(faculty2);
        repo.save(faculty3);
        
        System.out.println("Sample data initialized - 3 faculty members created");
    }
}
