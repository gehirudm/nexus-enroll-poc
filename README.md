# Nexus Enrollment System - Proof of Concept

## Overview

This is a microservices-based proof of concept for a university course enrollment system. The system is designed with 5 independent microservices that handle different aspects of the enrollment process.

## Architecture

### Microservices

1. **Student Service** - Manages student data, enrollment, and academic progress
2. **Course Service** - Handles course management, scheduling, and availability
3. **Faculty Service** - Manages faculty data, grade submission, and class rosters
4. **Admin Service** - Provides administrative functions and reporting
5. **Notification Service** - Handles system notifications and messaging

### Common Module

All services share a common module containing:
- Domain models (Student, Course, Faculty, etc.)
- Enums (EnrollmentStatus, GradeStatus, NotificationType)
- Exceptions (ValidationException, NotFoundException, etc.)
- Base repository interface (CrudRepository)
- Utility classes (Validator, ResponseBuilder)

## Project Structure

```
nexus-enroll-poc/
├── common/                     # Shared models and utilities
│   ├── model/                  # Domain models
│   ├── enums/                  # Enumerations
│   ├── exceptions/             # Custom exceptions
│   ├── repository/             # Base repository interface
│   └── util/                   # Utility classes
├── student_service/            # Student management service
├── course_service/             # Course management service
├── faculty_service/            # Faculty management service
├── admin_service/              # Administrative service
├── notification_service/       # Notification service
├── NexusEnrollmentSystem.java  # Main application launcher
└── README.md                   # This file
```

## Running the Application

### Prerequisites

- Java 11 or higher
- Basic understanding of microservices architecture

### Compilation and Execution

1. **Compile all Java files:**
   ```bash
   find . -name "*.java" | xargs javac -cp .
   ```

2. **Run the main application:**
   ```bash
   java NexusEnrollmentSystem
   ```

3. **Run individual services:**
   ```bash
   # Student Service
   java student_service.StudentServiceApplication
   
   # Course Service
   java course_service.CourseServiceApplication
   
   # Faculty Service
   java faculty_service.FacultyServiceApplication
   
   # Admin Service
   java admin_service.AdminServiceApplication
   
   # Notification Service
   java notification_service.NotificationServiceApplication
   ```

## Features Implemented

### Student Service
- Student CRUD operations
- Course enrollment and dropping
- Schedule management
- Academic progress tracking
- Enrollment validation

### Course Service
- Course CRUD operations
- Course search by department/keyword
- Availability tracking
- Prerequisite management
- Capacity management

### Faculty Service
- Faculty CRUD operations
- Grade submission and management
- Class roster access
- Course assignment tracking

### Admin Service
- System-wide reporting
- Course management with admin privileges
- Student force enrollment
- Faculty workload reports
- Enrollment analytics

### Notification Service
- Notification creation and delivery
- User subscription management
- Notification type filtering
- Read/unread status tracking

## API Endpoints

### Student Service
- `GET /students/{id}` - Get student by ID
- `GET /students/{id}/schedule` - Get student's schedule
- `GET /students/{id}/enrollments` - Get student's enrollments
- `POST /students/{id}/enroll/{courseId}` - Enroll in course
- `DELETE /students/{id}/drop/{courseId}` - Drop course

### Course Service
- `GET /courses` - Get all courses
- `GET /courses/{id}` - Get course by ID
- `GET /courses/department/{dept}` - Get courses by department
- `GET /courses/available` - Get available courses
- `POST /courses` - Create new course

### Faculty Service
- `GET /faculty/{id}` - Get faculty by ID
- `GET /faculty/{id}/courses` - Get assigned courses
- `GET /faculty/{id}/roster/{courseId}` - Get class roster
- `POST /faculty/{id}/grades` - Submit grades

### Admin Service
- `GET /admin/students` - Get all students
- `GET /admin/faculty` - Get all faculty
- `GET /admin/reports/enrollment` - Generate enrollment report
- `GET /admin/reports/faculty-workload` - Generate workload report
- `POST /admin/courses` - Create course (admin)

### Notification Service
- `POST /notifications` - Send notification
- `GET /notifications/user/{userId}` - Get user notifications
- `GET /notifications/type/{type}` - Get notifications by type
- `PUT /notifications/{id}/read` - Mark as read

## Design Patterns Used

1. **Repository Pattern** - Data access abstraction
2. **Service Layer Pattern** - Business logic separation
3. **Strategy Pattern** - Enrollment validation
4. **Builder Pattern** - Response construction
5. **Factory Pattern** - Object creation

## Key Features

1. **Microservices Architecture** - Independent, loosely coupled services
2. **Shared Domain Models** - Consistent data structures across services
3. **In-Memory Storage** - Simple data persistence for POC
4. **Error Handling** - Comprehensive exception management
5. **Validation** - Input validation and business rule enforcement
6. **Separation of Concerns** - Clear layered architecture

## Sample Data

Each service initializes with sample data:
- 3 students with different departments
- 3 courses with schedules and capacity
- 3 faculty members with course assignments
- Sample notifications and subscriptions

## Future Enhancements

For a production system, consider:

1. **REST API Implementation** - HTTP endpoints with Spring Boot
2. **Database Integration** - PostgreSQL/MySQL with JPA
3. **Service Discovery** - Eureka or Consul
4. **API Gateway** - Zuul or Spring Cloud Gateway
5. **Configuration Management** - Spring Cloud Config
6. **Monitoring** - Prometheus, Grafana
7. **Authentication/Authorization** - JWT, OAuth2
8. **Message Queues** - RabbitMQ, Apache Kafka
9. **Containerization** - Docker, Kubernetes
10. **Circuit Breakers** - Hystrix, Resilience4j

## Testing

The application includes demonstration methods in each service that show:
- CRUD operations
- Business logic execution
- Inter-service communication patterns
- Error handling

## Notes

- This is a Proof of Concept implementation
- In-memory storage is used for simplicity
- No actual HTTP endpoints are implemented
- Services communicate through direct method calls
- Real implementation would use REST APIs and message queues
