package com.nexus.enrollment.faculty;

import com.nexus.enrollment.common.model.Faculty;
import com.nexus.enrollment.faculty.repository.FacultyRepository;
import com.nexus.enrollment.faculty.repository.InMemoryFacultyRepository;
import com.nexus.enrollment.faculty.service.FacultyService;
import com.nexus.enrollment.faculty.service.FacultyServiceImpl;
import com.nexus.enrollment.faculty.service.GradeService;
import com.nexus.enrollment.faculty.service.GradeServiceImpl;
import com.nexus.enrollment.faculty.controller.FacultyController;
import com.nexus.enrollment.faculty.handler.FacultyHandler;
import com.nexus.enrollment.faculty.handler.FacultyByIdHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Arrays;

public class FacultyServiceApplication {
    
    private static final int PORT = 8083;
    private static FacultyController controller;
    
    public static void main(String[] args) throws IOException {
        // Initialize repositories
        FacultyRepository facultyRepo = new InMemoryFacultyRepository();
        
        // Initialize services
        FacultyService facultyService = new FacultyServiceImpl(facultyRepo);
        GradeService gradeService = new GradeServiceImpl();
        
        // Initialize controller
        controller = new FacultyController(facultyService, gradeService);
        
        // Initialize with sample data
        initializeSampleData(facultyRepo);
        
        // Start HTTP server
        startHttpServer();
    }
    
    private static void startHttpServer() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        
        // Faculty Service Endpoints
        server.createContext("/faculty", new FacultyHandler(controller));
        server.createContext("/faculty/", new FacultyByIdHandler(controller));
        
        server.setExecutor(null);
        server.start();
        
        System.out.println("Faculty Service started on port " + PORT);
        System.out.println("Available endpoints:");
        System.out.println("  GET /faculty/{id} - Get faculty by ID");
        System.out.println("  GET /faculty/{id}/courses - Get faculty's assigned courses");
        System.out.println("  GET /faculty/{id}/roster/{courseId} - Get class roster for a course");
        System.out.println("  POST /faculty/{id}/grades - Submit grades for a course");
        System.out.println("  GET /faculty/{id}/grades/{courseId} - Get submitted grades for a course");
        System.out.println("  PUT /faculty/{id}/course-request - Submit course change request");
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
