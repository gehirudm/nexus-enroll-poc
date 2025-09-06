package com.nexus.enrollment.course.handler;

import com.nexus.enrollment.common.model.Course;
import com.nexus.enrollment.common.model.Schedule;
import com.nexus.enrollment.common.handler.BaseHandler;
import com.nexus.enrollment.common.exceptions.BadRequestException;
import com.nexus.enrollment.course.service.CourseService;
import com.nexus.enrollment.course.repository.CourseRepository;
import io.javalin.http.Context;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

public class CoursesHandler extends BaseHandler {
    
    private final CourseService courseService;
    private final CourseRepository courseRepository;
    
    public CoursesHandler(CourseService courseService, CourseRepository courseRepository) {
        this.courseService = courseService;
        this.courseRepository = courseRepository;
    }
    
    // Javalin handler methods
    public void getAllCourses(Context ctx) {
        List<Course> courses = courseService.getAllCourses();
        ctx.json(createSuccessResponse("Courses retrieved successfully", courses));
    }
    
    public void getCourseById(Context ctx) {
        Long id = Long.parseLong(ctx.pathParam("id")); // NumberFormatException handled globally
        Course course = courseService.getCourseById(id); // NotFoundException handled globally
        ctx.json(createSuccessResponse("Course retrieved successfully", course));
    }
    
    public void getCoursesByDepartment(Context ctx) {
        String department = ctx.pathParam("dept");
        List<Course> courses = courseService.getCoursesByDepartment(department);
        ctx.json(createSuccessResponse("Courses retrieved successfully", courses));
    }
    
    public void getCoursesByInstructor(Context ctx) {
        Long facultyId = Long.parseLong(ctx.pathParam("facultyId")); // NumberFormatException handled globally
        List<Course> courses = courseService.getCoursesByInstructor(facultyId);
        ctx.json(createSuccessResponse("Courses retrieved successfully", courses));
    }
    
    public void searchCourses(Context ctx) {
        String keyword = ctx.queryParam("keyword");
        String department = ctx.queryParam("department");
        if (department == null) department = ""; // Default to empty string
        if (keyword == null || keyword.trim().isEmpty()) {
            throw new BadRequestException("Keyword parameter is required");
        }
        List<Course> courses = courseService.searchCourses(department, keyword);
        ctx.json(createSuccessResponse("Courses retrieved successfully", courses));
    }
    
    public void getAvailableCourses(Context ctx) {
        List<Course> courses = courseService.getAvailableCourses();
        ctx.json(createSuccessResponse("Available courses retrieved successfully", courses));
    }
    
    public void getCoursePrerequisites(Context ctx) {
        Long id = Long.parseLong(ctx.pathParam("id")); // NumberFormatException handled globally
        Course course = courseRepository.findById(id).orElse(null);
        if (course != null && course.getPrerequisites() != null) {
            ctx.json(createSuccessResponse("Prerequisites for course " + id, course.getPrerequisites()));
        } else if (course != null) {
            ctx.json(createSuccessResponse("No prerequisites for course " + id, java.util.Collections.emptyList()));
        } else {
            ctx.status(404).json(createErrorResponse("Course not found"));
        }
    }
    
    public void getCourseEnrollments(Context ctx) {
        Long id = Long.parseLong(ctx.pathParam("id")); // NumberFormatException handled globally
        int count = courseService.getEnrollmentCount(id); // NotFoundException handled globally
        ctx.json(createSuccessResponse("Enrollment count retrieved successfully", count));
    }
    
    public void createCourse(Context ctx) {
        // Use Javalin's automatic JSON parsing with GSON
        Course course = ctx.bodyAsClass(Course.class);
        
        // Set defaults if needed
        if (course.getId() != null) {
            course.setId(null); // Will be auto-generated
        }
        if (course.getAvailableSeats() == 0 && course.getTotalCapacity() > 0) {
            course.setAvailableSeats(course.getTotalCapacity());
        }
        
        // Create a default schedule if none provided
        if (course.getSchedule() == null) {
            Schedule schedule = new Schedule(DayOfWeek.FRIDAY, LocalTime.of(10, 0), LocalTime.of(11, 30), "Room TBD");
            schedule.setId(System.currentTimeMillis()); // Generate a simple ID for the schedule
            course.setSchedule(schedule);
        }
        
        // Save the course using the service layer
        Course savedCourse = courseService.createCourse(course);
        
        ctx.status(201).json(createSuccessResponse("Course created successfully", savedCourse));
    }
}
