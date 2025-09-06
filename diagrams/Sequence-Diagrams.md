# Sequence Diagrams for Nexus Enrollment System

## Overview
Sequence diagrams show the interaction between different objects/services over time in the Nexus Enrollment System. They illustrate how requests flow through the microservices architecture and demonstrate the temporal order of method calls and responses.

## 1. Student Enrollment Process

### Purpose
Shows the complete sequence of interactions when a student enrolls in a course.

### Participants
- **Student** (Actor)
- **StudentServiceApp** (HTTP Server)
- **StudentController**
- **EnrollmentService**
- **StudentService**
- **CourseService** (External call)
- **StudentRepository**
- **NotificationService** (External call)

### Sequence Flow
```
Student -> StudentServiceApp: POST /students/{id}/enroll/{courseId}
StudentServiceApp -> StudentController: enrollStudent(studentId, courseId)
StudentController -> EnrollmentService: enrollStudent(studentId, courseId)

EnrollmentService -> StudentService: getStudentById(studentId)
StudentService -> StudentRepository: findById(studentId)
StudentRepository -> StudentService: return Student
StudentService -> EnrollmentService: return Student

EnrollmentService -> CourseService: getCourseById(courseId)
CourseService -> EnrollmentService: return Course

EnrollmentService -> EnrollmentService: validatePrerequisites(student, course)
EnrollmentService -> EnrollmentService: checkCapacity(course)

alt [Enrollment Valid]
    EnrollmentService -> StudentRepository: saveEnrollment(enrollment)
    StudentRepository -> EnrollmentService: return savedEnrollment
    
    EnrollmentService -> NotificationService: sendNotification(userId, ENROLLMENT_CONFIRMATION)
    NotificationService -> EnrollmentService: return notification
    
    EnrollmentService -> StudentController: return EnrollmentResult(success=true)
    StudentController -> StudentServiceApp: return ResponseBuilder.success()
    StudentServiceApp -> Student: HTTP 200 OK + JSON response
else [Enrollment Invalid]
    EnrollmentService -> StudentController: return EnrollmentResult(success=false)
    StudentController -> StudentServiceApp: return ResponseBuilder.error()
    StudentServiceApp -> Student: HTTP 400 Bad Request + JSON error
end
```

### Key Interactions
1. HTTP request handling and routing
2. Cross-service communication for course validation
3. Business logic validation (prerequisites, capacity)
4. Data persistence operations
5. Asynchronous notification sending
6. Response formatting and error handling

---

## 2. Faculty Grade Submission

### Purpose
Demonstrates the sequence when faculty submits grades for their course.

### Participants
- **Faculty** (Actor)
- **FacultyServiceApp** (HTTP Server)
- **FacultyController**
- **GradeService**
- **FacultyService**
- **CourseService** (External call)
- **GradeRepository**
- **NotificationService** (External call)

### Sequence Flow
```
Faculty -> FacultyServiceApp: POST /faculty/{id}/grades
FacultyServiceApp -> FacultyController: submitGrades(facultyId, gradeSubmissions)
FacultyController -> GradeService: submitGrades(facultyId, gradeSubmissions)

GradeService -> FacultyService: getFacultyById(facultyId)
FacultyService -> FacultyController: return Faculty

GradeService -> CourseService: validateFacultyAssignment(facultyId, courseId)
CourseService -> GradeService: return validationResult

loop [For each grade submission]
    GradeService -> GradeService: validateGrade(gradeSubmission)
    
    alt [Grade Valid]
        GradeService -> GradeRepository: saveGrade(grade)
        GradeRepository -> GradeService: return savedGrade
        
        GradeService -> NotificationService: sendNotification(studentId, GRADE_SUBMITTED)
        NotificationService -> GradeService: return notification
    else [Grade Invalid]
        GradeService -> GradeService: addToErrorList(gradeSubmission)
    end
end

GradeService -> FacultyController: return GradeSubmissionResult
FacultyController -> FacultyServiceApp: return ResponseBuilder response
FacultyServiceApp -> Faculty: HTTP response with results
```

### Key Interactions
1. Batch processing of multiple grade submissions
2. Faculty authorization validation
3. Individual grade validation and persistence
4. Parallel notification sending to multiple students
5. Comprehensive error handling and reporting

---

## 3. Course Search and Discovery

### Purpose
Shows how students search for courses with real-time filtering and results.

### Participants
- **Student** (Actor)
- **CourseServiceApp** (HTTP Server)
- **CourseController**
- **CourseService**
- **CourseRepository**

### Sequence Flow
```
Student -> CourseServiceApp: GET /courses/search?department=CS&keyword=programming
CourseServiceApp -> CourseController: searchCourses(department, keyword)
CourseController -> CourseService: searchCourses(department, keyword)

CourseService -> CourseRepository: findByDepartment(department)
CourseRepository -> CourseService: return departmentCourses

CourseService -> CourseRepository: searchByKeyword(keyword)
CourseRepository -> CourseService: return keywordCourses

CourseService -> CourseService: intersectResults(departmentCourses, keywordCourses)
CourseService -> CourseService: filterAvailableCourses(results)

loop [For each course in results]
    CourseService -> CourseService: enrichCourseData(course)
    CourseService -> CourseService: calculateAvailableSpots(course)
end

CourseService -> CourseController: return enrichedCourses
CourseController -> CourseServiceApp: return ResponseBuilder.success(courses)
CourseServiceApp -> Student: HTTP 200 OK + JSON course list
```

### Alternative Flows
```
alt [No courses found]
    CourseService -> CourseController: return empty list
    CourseController -> CourseServiceApp: return ResponseBuilder.success(empty)
    CourseServiceApp -> Student: HTTP 200 OK + empty results
end

alt [Department filter only]
    Student -> CourseServiceApp: GET /courses/department/CS
    CourseServiceApp -> CourseController: getCoursesByDepartment(department)
    # ... similar flow without keyword filtering
end
```

---

## 4. Administrative Report Generation

### Purpose
Illustrates the complex process of generating administrative reports with data aggregation.

### Participants
- **Administrator** (Actor)
- **AdminServiceApp** (HTTP Server)
- **AdminController**
- **ReportService**
- **StudentService** (External call)
- **CourseService** (External call)
- **FacultyService** (External call)
- **ReportRepository**

### Sequence Flow
```
Administrator -> AdminServiceApp: GET /admin/reports/enrollment?department=CS&semester=Fall2024
AdminServiceApp -> AdminController: generateEnrollmentReport(department, semester)
AdminController -> ReportService: generateEnrollmentReport(department, semester)

ReportService -> StudentService: getStudentsByDepartment(department)
StudentService -> ReportService: return students

ReportService -> CourseService: getCoursesByDepartment(department)
CourseService -> ReportService: return courses

par [Parallel data collection]
    ReportService -> StudentService: getEnrollmentData(students, semester)
    StudentService -> ReportService: return enrollmentData
and
    ReportService -> CourseService: getCourseStatistics(courses, semester)
    CourseService -> ReportService: return courseStats
and
    ReportService -> FacultyService: getFacultyWorkload(department, semester)
    FacultyService -> ReportService: return workloadData
end

ReportService -> ReportService: aggregateData(enrollmentData, courseStats, workloadData)
ReportService -> ReportService: calculateMetrics(aggregatedData)
ReportService -> ReportService: formatReport(metrics)

ReportService -> ReportRepository: saveReport(report)
ReportRepository -> ReportService: return savedReport

ReportService -> AdminController: return EnrollmentReport
AdminController -> AdminServiceApp: return ResponseBuilder.success(report)
AdminServiceApp -> Administrator: HTTP 200 OK + JSON report
```

### Key Interactions
1. Parallel data collection from multiple services
2. Complex data aggregation and calculation
3. Report persistence for future reference
4. Performance optimization through concurrent processing

---

## 5. Notification Processing and Delivery

### Purpose
Shows how notifications are created, queued, and delivered to users.

### Participants
- **TriggerEvent** (Various system events)
- **NotificationServiceApp** (HTTP Server)
- **NotificationController**
- **NotificationService**
- **NotificationRepository**
- **EmailService** (External)
- **User** (Recipient)

### Sequence Flow
```
TriggerEvent -> NotificationServiceApp: POST /notifications
NotificationServiceApp -> NotificationController: createAndSendNotification(userId, type, message)
NotificationController -> NotificationService: createNotification(userId, type, message)

NotificationService -> NotificationService: validateNotificationData(userId, type, message)
NotificationService -> NotificationRepository: saveNotification(notification)
NotificationRepository -> NotificationService: return savedNotification

NotificationService -> NotificationService: queueForDelivery(notification)

# Asynchronous processing begins
activate NotificationService
NotificationService -> NotificationService: processDeliveryQueue()

loop [For each notification in queue]
    NotificationService -> NotificationService: determineDeliveryMethod(notification)
    
    alt [Email notification]
        NotificationService -> EmailService: sendEmail(recipient, subject, body)
        EmailService -> NotificationService: return deliveryStatus
    else [In-app notification]
        NotificationService -> NotificationRepository: updateNotificationStatus(DELIVERED)
        NotificationRepository -> NotificationService: return updated
    end
    
    NotificationService -> NotificationRepository: updateDeliveryStatus(notification, status)
    NotificationRepository -> NotificationService: return updated
end
deactivate NotificationService

NotificationService -> NotificationController: return notificationResult
NotificationController -> NotificationServiceApp: return ResponseBuilder.success()
NotificationServiceApp -> TriggerEvent: HTTP 201 Created
```

### Asynchronous Flow
```
# User checking notifications
User -> NotificationServiceApp: GET /notifications/user/{userId}
NotificationServiceApp -> NotificationController: getUserNotifications(userId)
NotificationController -> NotificationService: getUserNotifications(userId)

NotificationService -> NotificationRepository: findByUserId(userId)
NotificationRepository -> NotificationService: return userNotifications

NotificationService -> NotificationController: return notifications
NotificationController -> NotificationServiceApp: return ResponseBuilder.success(notifications)
NotificationServiceApp -> User: HTTP 200 OK + notification list

# User marking as read
User -> NotificationServiceApp: PUT /notifications/{id}/read
NotificationServiceApp -> NotificationController: markAsRead(notificationId)
NotificationController -> NotificationService: markAsRead(notificationId)

NotificationService -> NotificationRepository: updateReadStatus(notificationId, true)
NotificationRepository -> NotificationService: return updatedNotification

NotificationService -> NotificationController: return success
NotificationController -> NotificationServiceApp: return ResponseBuilder.success()
NotificationServiceApp -> User: HTTP 200 OK
```

---

## 6. Student Schedule Retrieval

### Purpose
Demonstrates how a student's complete schedule is assembled from multiple courses.

### Participants
- **Student** (Actor)
- **StudentServiceApp** (HTTP Server)
- **StudentController**
- **StudentService**
- **StudentRepository**
- **CourseService** (External call)

### Sequence Flow
```
Student -> StudentServiceApp: GET /students/{id}/schedule
StudentServiceApp -> StudentController: getStudentSchedule(studentId)
StudentController -> StudentService: getStudentSchedule(studentId)

StudentService -> StudentRepository: findById(studentId)
StudentRepository -> StudentService: return Student

StudentService -> StudentRepository: getStudentEnrollments(studentId)
StudentRepository -> StudentService: return enrollments

loop [For each enrollment]
    StudentService -> CourseService: getCourseById(enrollment.courseId)
    CourseService -> StudentService: return Course
    
    StudentService -> StudentService: addToSchedule(course.schedule)
end

StudentService -> StudentService: consolidateSchedule(scheduleItems)
StudentService -> StudentService: checkForConflicts(schedule)

alt [No conflicts]
    StudentService -> StudentController: return Schedule(items)
    StudentController -> StudentServiceApp: return ResponseBuilder.success(schedule)
    StudentServiceApp -> Student: HTTP 200 OK + JSON schedule
else [Conflicts found]
    StudentService -> StudentController: return Schedule(items, conflicts)
    StudentController -> StudentServiceApp: return ResponseBuilder.success(schedule, warnings)
    StudentServiceApp -> Student: HTTP 200 OK + JSON schedule with warnings
end
```

---

## 7. Course Waitlist Management

### Purpose
Shows the complex interactions when managing course waitlists.

### Participants
- **Student** (Actor)
- **StudentServiceApp** (HTTP Server)
- **StudentController**
- **EnrollmentService**
- **WaitlistService**
- **CourseService** (External call)
- **NotificationService** (External call)

### Sequence Flow
```
# Student tries to enroll in full course
Student -> StudentServiceApp: POST /students/{id}/enroll/{courseId}
StudentServiceApp -> StudentController: enrollStudent(studentId, courseId)
StudentController -> EnrollmentService: enrollStudent(studentId, courseId)

EnrollmentService -> CourseService: getCourseById(courseId)
CourseService -> EnrollmentService: return Course

EnrollmentService -> EnrollmentService: checkCapacity(course)

alt [Course has capacity]
    # Normal enrollment flow
    EnrollmentService -> EnrollmentService: processEnrollment(studentId, courseId)
else [Course is full]
    EnrollmentService -> WaitlistService: addToWaitlist(studentId, courseId)
    WaitlistService -> StudentController: return WaitlistResult
    
    StudentController -> NotificationService: sendNotification(studentId, WAITLISTED)
    NotificationService -> StudentController: return notification
    
    StudentController -> StudentServiceApp: return ResponseBuilder.success(waitlisted)
    StudentServiceApp -> Student: HTTP 200 OK + waitlist confirmation
end

# Later: Another student drops the course
activate WaitlistService
CourseService -> WaitlistService: notifyCourseOpening(courseId)
WaitlistService -> WaitlistService: getNextWaitlistStudent(courseId)

WaitlistService -> EnrollmentService: enrollStudent(nextStudentId, courseId)
EnrollmentService -> WaitlistService: return EnrollmentResult

alt [Enrollment successful]
    WaitlistService -> NotificationService: sendNotification(nextStudentId, WAITLIST_AVAILABLE)
    NotificationService -> WaitlistService: return notification
    
    WaitlistService -> WaitlistService: removeFromWaitlist(nextStudentId, courseId)
    WaitlistService -> WaitlistService: updateWaitlistPositions(courseId)
else [Enrollment failed]
    WaitlistService -> WaitlistService: processNextWaitlistStudent(courseId)
end
deactivate WaitlistService
```

---

## 8. Cross-Service Communication Pattern

### Purpose
Illustrates the general pattern for communication between microservices.

### Participants
- **ClientService** (Initiating service)
- **TargetServiceApp** (HTTP Server)
- **TargetController**
- **TargetService**
- **Repository**

### Generic Sequence Flow
```
ClientService -> TargetServiceApp: HTTP Request (GET/POST/PUT/DELETE)
TargetServiceApp -> TargetServiceApp: routeRequest(path, method)
TargetServiceApp -> TargetController: controllerMethod(parameters)

TargetController -> TargetController: validateRequest(parameters)

alt [Request valid]
    TargetController -> TargetService: serviceMethod(parameters)
    TargetService -> Repository: repositoryMethod(parameters)
    Repository -> TargetService: return data
    TargetService -> TargetController: return result
    
    TargetController -> TargetController: formatResponse(result)
    TargetController -> TargetServiceApp: return ResponseBuilder.success(data)
    TargetServiceApp -> ClientService: HTTP 200 OK + JSON response
else [Request invalid]
    TargetController -> TargetServiceApp: return ResponseBuilder.error(message)
    TargetServiceApp -> ClientService: HTTP 4xx Error + JSON error
end

alt [Service error]
    TargetService -> TargetController: throw Exception
    TargetController -> TargetServiceApp: return ResponseBuilder.error(exception.message)
    TargetServiceApp -> ClientService: HTTP 500 Error + JSON error
end
```

---

## 9. System Startup and Initialization

### Purpose
Shows the sequence of operations when each microservice starts up.

### Participants
- **ServiceApplication** (Main class)
- **Repository** (Data layer)
- **Service** (Business layer)
- **Controller** (Presentation layer)
- **HttpServer** (Java built-in server)

### Sequence Flow
```
ServiceApplication -> ServiceApplication: main(args)
ServiceApplication -> Repository: new Repository()
Repository -> ServiceApplication: return repositoryInstance

ServiceApplication -> Service: new Service(repository)
Service -> ServiceApplication: return serviceInstance

ServiceApplication -> Controller: new Controller(service)
Controller -> ServiceApplication: return controllerInstance

ServiceApplication -> ServiceApplication: initializeSampleData(repository)
ServiceApplication -> ServiceApplication: startHttpServer()

ServiceApplication -> HttpServer: HttpServer.create(port)
HttpServer -> ServiceApplication: return server

ServiceApplication -> HttpServer: createContext(path, handler)
HttpServer -> ServiceApplication: return context

ServiceApplication -> HttpServer: start()
HttpServer -> HttpServer: listen on port

ServiceApplication -> ServiceApplication: printStartupMessage()
```

---

## Implementation Considerations

### Error Handling Patterns
- All sequences include error handling paths
- Proper HTTP status codes returned
- Consistent error response format
- Exception logging for debugging

### Performance Optimizations
- Parallel processing where possible
- Connection pooling for external services
- Caching frequently accessed data
- Asynchronous operations for non-blocking calls

### Security Considerations
- Authentication validation in controllers
- Authorization checks before service calls
- Input validation and sanitization
- Audit logging for sensitive operations

### Monitoring and Logging
- Request/response logging at service boundaries
- Performance metrics collection
- Error rate monitoring
- Business event tracking

These sequence diagrams provide a comprehensive view of how the Nexus Enrollment System's microservices interact to deliver functionality while maintaining separation of concerns and proper error handling.
