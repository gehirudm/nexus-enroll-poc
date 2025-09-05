## Revised Project Structure

```
nexus-enroll-poc/
├── common/
│   ├── model/
│   │   ├── Course.java
│   │   ├── Student.java
│   │   ├── Faculty.java
│   │   ├── Enrollment.java
│   │   ├── Grade.java
│   │   ├── Schedule.java
│   │   ├── Prerequisite.java
│   │   └── Notification.java
│   ├── repository/
│   │   └── CrudRepository.java
│   ├── exceptions/
│   │   ├── ValidationException.java
│   │   ├── EnrollmentException.java
│   │   ├── NotFoundException.java
│   │   └── SystemException.java
│   ├── enums/
│   │   ├── EnrollmentStatus.java
│   │   ├── GradeStatus.java
│   │   └── NotificationType.java
│   └── util/
│       ├── Validator.java
│       └── ResponseBuilder.java
├── student-service/
│   ├── StudentServiceApplication.java
│   ├── controller/
│   │   └── StudentController.java
│   ├── service/
│   │   ├── StudentService.java
│   │   ├── StudentServiceImpl.java
│   │   ├── EnrollmentService.java
│   │   └── EnrollmentServiceImpl.java
│   └── repository/
│       ├── StudentRepository.java
│       └── InMemoryStudentRepository.java
├── faculty-service/
│   ├── FacultyServiceApplication.java
│   ├── controller/
│   │   └── FacultyController.java
│   ├── service/
│   │   ├── FacultyService.java
│   │   ├── FacultyServiceImpl.java
│   │   ├── GradeService.java
│   │   └── GradeServiceImpl.java
│   └── repository/
│       ├── FacultyRepository.java
│       └── InMemoryFacultyRepository.java
├── admin-service/
│   ├── AdminServiceApplication.java
│   ├── controller/
│   │   └── AdminController.java
│   ├── service/
│   │   ├── AdminService.java
│   │   ├── AdminServiceImpl.java
│   │   ├── ReportService.java
│   │   └── ReportServiceImpl.java
│   └── repository/
│       ├── AdminRepository.java
│       └── InMemoryAdminRepository.java
├── course-service/
│   ├── CourseServiceApplication.java
│   ├── controller/
│   │   └── CourseController.java
│   ├── service/
│   │   ├── CourseService.java
│   │   └── CourseServiceImpl.java
│   └── repository/
│       ├── CourseRepository.java
│       └── InMemoryCourseRepository.java
└── notification-service/
    ├── NotificationServiceApplication.java
    ├── controller/
    │   └── NotificationController.java
    ├── service/
    │   ├── NotificationService.java
    │   └── NotificationServiceImpl.java
    └── repository/
        ├── NotificationRepository.java
        └── InMemoryNotificationRepository.java
```

## Common Repository Interface

```java
// common/repository/CrudRepository.java
public interface CrudRepository<T, ID> {
    T save(T entity);
    Optional<T> findById(ID id);
    List<T> findAll();
    void deleteById(ID id);
    boolean existsById(ID id);
}
```

## Service-Specific Repository Interfaces

```java
// student-service/repository/StudentRepository.java
public interface StudentRepository extends CrudRepository<Student, Long> {
    Optional<Student> findByEmail(String email);
    List<Student> findByCourseEnrolled(Long courseId);
}

// course-service/repository/CourseRepository.java
public interface CourseRepository extends CrudRepository<Course, Long> {
    List<Course> findByDepartment(String department);
    List<Course> findByInstructor(Long facultyId);
    List<Course> findAvailableCourses();
}
```

## Endpoints for Each Microservice

### Student Service Endpoints
```
GET    /students/{id}                  - Get student by ID
GET    /students/{id}/schedule         - Get student's current schedule
GET    /students/{id}/progress         - Get academic progress
GET    /students/{id}/enrollments      - Get all enrollments
POST   /students/{id}/enroll/{courseId} - Enroll in a course
DELETE /students/{id}/drop/{courseId}   - Drop a course
GET    /students/{id}/waitlist         - Get waitlisted courses
```

### Course Service Endpoints
```
GET    /courses                       - Get all courses
GET    /courses/{id}                  - Get course by ID
GET    /courses/department/{dept}     - Get courses by department
GET    /courses/instructor/{facultyId} - Get courses by instructor
GET    /courses/search?keyword={}     - Search courses by keyword
GET    /courses/available             - Get available courses (not full)
GET    /courses/{id}/prerequisites    - Get course prerequisites
GET    /courses/{id}/enrollments      - Get enrolled students count
```

### Faculty Service Endpoints
```
GET    /faculty/{id}                  - Get faculty by ID
GET    /faculty/{id}/courses          - Get faculty's assigned courses
GET    /faculty/{id}/roster/{courseId} - Get class roster for a course
POST   /faculty/{id}/grades           - Submit grades for a course
GET    /faculty/{id}/grades/{courseId} - Get submitted grades for a course
PUT    /faculty/{id}/course-request   - Submit course change request
```

### Admin Service Endpoints
```
GET    /admin/reports/enrollment?department={}&semester={} - Enrollment report
GET    /admin/reports/faculty-workload - Faculty workload report
GET    /admin/reports/course-trends    - Course popularity trends
POST   /admin/courses                 - Create new course
PUT    /admin/courses/{id}            - Update course
DELETE /admin/courses/{id}            - Delete course
POST   /admin/students/{id}/force-enroll/{courseId} - Force enroll student
GET    /admin/students                - Get all students
GET    /admin/faculty                 - Get all faculty
```

### Notification Service Endpoints
```
POST   /notifications                 - Send notification
GET    /notifications/user/{userId}   - Get user notifications
GET    /notifications/type/{type}     - Get notifications by type
PUT    /notifications/{id}/read       - Mark notification as read
POST   /notifications/subscribe       - Subscribe to notification type
```

## Service Interactions Example

**Student Enrollment Flow:**
1. `POST /students/123/enroll/456` (Student Service)
2. Student Service calls Course Service: `GET /courses/456` to check capacity
3. Student Service validates prerequisites, capacity, time conflicts
4. Student Service calls Notification Service: `POST /notifications` for waitlist notifications
5. Returns enrollment result

**Grade Submission Flow:**
1. `POST /faculty/789/grades` (Faculty Service)
2. Faculty Service calls Student Service: `PUT /students/{id}/grades` to update records
3. Faculty Service calls Notification Service: `POST /notifications` to notify students
4. Returns grade submission result

This structure keeps all models common for consistency, uses a common repository interface, and provides clear RESTful endpoints for each microservice that can be tested with Postman.

## Common Module Implementation Details

### common/model/Course.java
```java
public class Course {
    private Long id;
    private String courseCode;
    private String name;
    private String description;
    private Long instructorId;
    private String department;
    private int totalCapacity;
    private int availableSeats;
    private Schedule schedule;
    private List<Prerequisite> prerequisites;
    
    // Constructors, getters, setters
    public boolean isFull() { return availableSeats <= 0; }
    public void decrementSeats() { availableSeats--; }
    public void incrementSeats() { availableSeats++; }
}
```

### common/model/Student.java
```java
public class Student {
    private Long id;
    private String name;
    private String email;
    private String department;
    private List<Enrollment> enrollments;
    private List<Grade> grades;
    
    // Constructors, getters, setters
    public boolean hasCompletedCourse(Long courseId) {
        return grades.stream().anyMatch(grade -> 
            grade.getCourseId().equals(courseId) && grade.isPassing());
    }
}
```

### common/model/Faculty.java
```java
public class Faculty {
    private Long id;
    private String name;
    private String email;
    private String department;
    private List<Long> assignedCourseIds;
    
    // Constructors, getters, setters
}
```

### common/model/Enrollment.java
```java
public class Enrollment {
    private Long id;
    private Long studentId;
    private Long courseId;
    private EnrollmentStatus status;
    private Date enrollmentDate;
    
    // Constructors, getters, setters
}
```

### common/model/Grade.java
```java
public class Grade {
    private Long id;
    private Long studentId;
    private Long courseId;
    private String gradeValue;
    private GradeStatus status;
    private Long facultyId;
    
    // Constructors, getters, setters
    public boolean isPassing() { 
        return !gradeValue.equals("F") && !gradeValue.equals("W");
    }
}
```

### common/repository/CrudRepository.java
```java
public interface CrudRepository<T, ID> {
    T save(T entity);
    Optional<T> findById(ID id);
    List<T> findAll();
    void deleteById(ID id);
    boolean existsById(ID id);
}
```

### common/enums/EnrollmentStatus.java
```java
public enum EnrollmentStatus {
    ENROLLED, WAITLISTED, DROPPED, COMPLETED
}
```

### common/enums/GradeStatus.java
```java
public enum GradeStatus {
    PENDING, SUBMITTED, APPROVED, REJECTED
}
```

### common/enums/NotificationType.java
```java
public enum NotificationType {
    ENROLLMENT_CONFIRMATION, COURSE_DROPPED, WAITLIST_AVAILABLE, 
    GRADE_SUBMITTED, SYSTEM_ERROR, COURSE_FULL
}
```

### common/exceptions/ValidationException.java
```java
public class ValidationException extends RuntimeException {
    public ValidationException(String message) { super(message); }
}
```

### common/exceptions/NotFoundException.java
```java
public class NotFoundException extends RuntimeException {
    public NotFoundException(String resource, Long id) {
        super(resource + " with ID " + id + " not found");
    }
}
```

## Student Service Implementation

### student-service/StudentServiceApplication.java
```java
public class StudentServiceApplication {
    public static void main(String[] args) {
        // Initialize repositories
        StudentRepository studentRepo = new InMemoryStudentRepository();
        
        // Initialize services
        StudentService studentService = new StudentServiceImpl(studentRepo);
        EnrollmentService enrollmentService = new EnrollmentServiceImpl(studentRepo);
        
        // Start HTTP server with controllers
        // (Simple HTTP server implementation for PoC)
    }
}
```

### student-service/controller/StudentController.java
```java
public class StudentController {
    private final StudentService studentService;
    private final EnrollmentService enrollmentService;
    
    public StudentController(StudentService studentService, EnrollmentService enrollmentService) {
        this.studentService = studentService;
        this.enrollmentService = enrollmentService;
    }
    
    // HTTP endpoint handlers
    public Response getStudent(Long id) {
        Student student = studentService.getStudentById(id);
        return Response.ok(student);
    }
    
    public Response enrollStudent(Long studentId, Long courseId) {
        EnrollmentResult result = enrollmentService.enrollStudent(studentId, courseId);
        return Response.ok(result);
    }
}
```

### student-service/service/StudentService.java
```java
public interface StudentService {
    Student getStudentById(Long id);
    List<Student> getAllStudents();
    Schedule getStudentSchedule(Long studentId);
    AcademicProgress getAcademicProgress(Long studentId);
    List<Enrollment> getStudentEnrollments(Long studentId);
}
```

### student-service/service/StudentServiceImpl.java
```java
public class StudentServiceImpl implements StudentService {
    private final StudentRepository studentRepository;
    
    public StudentServiceImpl(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }
    
    public Student getStudentById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Student", id));
    }
    
    public Schedule getStudentSchedule(Long studentId) {
        Student student = getStudentById(studentId);
        return new Schedule(student.getEnrollments());
    }
}
```

### student-service/service/EnrollmentService.java
```java
public interface EnrollmentService {
    EnrollmentResult enrollStudent(Long studentId, Long courseId);
    EnrollmentResult dropCourse(Long studentId, Long courseId);
    List<Course> getWaitlistedCourses(Long studentId);
}
```

### student-service/service/EnrollmentServiceImpl.java
```java
public class EnrollmentServiceImpl implements EnrollmentService {
    private final StudentRepository studentRepository;
    private final List<EnrollmentValidator> validators;
    
    public EnrollmentServiceImpl(StudentRepository studentRepository, 
                               List<EnrollmentValidator> validators) {
        this.studentRepository = studentRepository;
        this.validators = validators;
    }
    
    public EnrollmentResult enrollStudent(Long studentId, Long courseId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new NotFoundException("Student", studentId));
        
        // Validation logic using Strategy pattern
        for (EnrollmentValidator validator : validators) {
            ValidationResult result = validator.validate(student, courseId);
            if (!result.isValid()) {
                throw new ValidationException(result.getMessage());
            }
        }
        
        // Enrollment processing
        Enrollment enrollment = new Enrollment(studentId, courseId, EnrollmentStatus.ENROLLED);
        student.getEnrollments().add(enrollment);
        studentRepository.save(student);
        
        return new EnrollmentResult(true, "Enrollment successful", enrollment);
    }
}
```

### student-service/repository/StudentRepository.java
```java
public interface StudentRepository extends CrudRepository<Student, Long> {
    Optional<Student> findByEmail(String email);
    List<Student> findByCourseEnrolled(Long courseId);
}
```

### student-service/repository/InMemoryStudentRepository.java
```java
public class InMemoryStudentRepository implements StudentRepository {
    private final Map<Long, Student> students = new HashMap<>();
    private Long nextId = 1L;
    
    public Student save(Student student) {
        if (student.getId() == null) {
            student.setId(nextId++);
        }
        students.put(student.getId(), student);
        return student;
    }
    
    public Optional<Student> findById(Long id) {
        return Optional.ofNullable(students.get(id));
    }
    
    public List<Student> findAll() {
        return new ArrayList<>(students.values());
    }
}
```

### student-service/validator/EnrollmentValidator.java
```java
public interface EnrollmentValidator {
    ValidationResult validate(Student student, Long courseId);
}
```

### student-service/validator/PrerequisiteValidator.java
```java
public class PrerequisiteValidator implements EnrollmentValidator {
    public ValidationResult validate(Student student, Long courseId) {
        // Check if student has completed all prerequisites for the course
        // Returns ValidationResult with success/failure and message
        return new ValidationResult(true, "Prerequisites satisfied");
    }
}
```

## Course Service Implementation

### course-service/CourseServiceApplication.java
```java
public class CourseServiceApplication {
    public static void main(String[] args) {
        CourseRepository courseRepo = new InMemoryCourseRepository();
        CourseService courseService = new CourseServiceImpl(courseRepo);
        // Start HTTP server
    }
}
```

### course-service/controller/CourseController.java
```java
public class CourseController {
    private final CourseService courseService;
    
    public Response getCourse(Long id) {
        Course course = courseService.getCourseById(id);
        return Response.ok(course);
    }
    
    public Response searchCourses(String department, String keyword) {
        List<Course> courses = courseService.searchCourses(department, keyword);
        return Response.ok(courses);
    }
}
```

### course-service/service/CourseService.java
```java
public interface CourseService {
    Course getCourseById(Long id);
    List<Course> getAllCourses();
    List<Course> getCoursesByDepartment(String department);
    List<Course> getCoursesByInstructor(Long facultyId);
    List<Course> searchCourses(String department, String keyword);
    List<Course> getAvailableCourses();
    int getEnrollmentCount(Long courseId);
}
```

### course-service/service/CourseServiceImpl.java
```java
public class CourseServiceImpl implements CourseService {
    private final CourseRepository courseRepository;
    
    public CourseServiceImpl(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }
    
    public Course getCourseById(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Course", id));
    }
    
    public List<Course> getAvailableCourses() {
        return courseRepository.findAll().stream()
                .filter(course -> !course.isFull())
                .collect(Collectors.toList());
    }
}
```

### course-service/repository/CourseRepository.java
```java
public interface CourseRepository extends CrudRepository<Course, Long> {
    List<Course> findByDepartment(String department);
    List<Course> findByInstructor(Long facultyId);
    List<Course> findAvailableCourses();
}
```

### course-service/repository/InMemoryCourseRepository.java
```java
public class InMemoryCourseRepository implements CourseRepository {
    private final Map<Long, Course> courses = new HashMap<>();
    private Long nextId = 1L;
    
    public Course save(Course course) {
        if (course.getId() == null) {
            course.setId(nextId++);
        }
        courses.put(course.getId(), course);
        return course;
    }
    
    public Optional<Course> findById(Long id) {
        return Optional.ofNullable(courses.get(id));
    }
    
    public List<Course> findByDepartment(String department) {
        return courses.values().stream()
                .filter(course -> course.getDepartment().equalsIgnoreCase(department))
                .collect(Collectors.toList());
    }
}
```

## Faculty Service Implementation

### faculty-service/FacultyServiceApplication.java
```java
public class FacultyServiceApplication {
    public static void main(String[] args) {
        FacultyRepository facultyRepo = new InMemoryFacultyRepository();
        FacultyService facultyService = new FacultyServiceImpl(facultyRepo);
        GradeService gradeService = new GradeServiceImpl();
        // Start HTTP server
    }
}
```

### faculty-service/controller/FacultyController.java
```java
public class FacultyController {
    private final FacultyService facultyService;
    private final GradeService gradeService;
    
    public Response getFacultyRoster(Long facultyId, Long courseId) {
        List<Student> roster = facultyService.getClassRoster(facultyId, courseId);
        return Response.ok(roster);
    }
    
    public Response submitGrades(Long facultyId, List<GradeSubmission> grades) {
        GradeSubmissionResult result = gradeService.submitGrades(facultyId, grades);
        return Response.ok(result);
    }
}
```

### faculty-service/service/FacultyService.java
```java
public interface FacultyService {
    Faculty getFacultyById(Long id);
    List<Course> getAssignedCourses(Long facultyId);
    List<Student> getClassRoster(Long facultyId, Long courseId);
    void requestCourseChange(CourseChangeRequest request);
}
```

### faculty-service/service/GradeService.java
```java
public interface GradeService {
    GradeSubmissionResult submitGrades(Long facultyId, List<GradeSubmission> grades);
    List<Grade> getSubmittedGrades(Long facultyId, Long courseId);
}
```

## Admin Service Implementation

### admin-service/AdminServiceApplication.java
```java
public class AdminServiceApplication {
    public static void main(String[] args) {
        AdminService adminService = new AdminServiceImpl();
        ReportService reportService = new ReportServiceImpl();
        // Start HTTP server
    }
}
```

### admin-service/service/AdminService.java
```java
public interface AdminService {
    Course createCourse(Course course);
    Course updateCourse(Long courseId, Course course);
    void deleteCourse(Long courseId);
    void forceEnrollStudent(Long studentId, Long courseId);
    List<Student> getAllStudents();
    List<Faculty> getAllFaculty();
}
```

### admin-service/service/ReportService.java
```java
public interface ReportService {
    EnrollmentReport generateEnrollmentReport(String department, String semester);
    FacultyWorkloadReport generateFacultyWorkloadReport();
    CourseTrendsReport generateCourseTrendsReport();
}
```

## Notification Service Implementation

### notification-service/NotificationServiceApplication.java
```java
public class NotificationServiceApplication {
    public static void main(String[] args) {
        NotificationRepository notificationRepo = new InMemoryNotificationRepository();
        NotificationService notificationService = new NotificationServiceImpl(notificationRepo);
        // Start HTTP server
    }
}
```

### notification-service/service/NotificationService.java
```java
public interface NotificationService {
    void sendNotification(Notification notification);
    List<Notification> getUserNotifications(Long userId);
    List<Notification> getNotificationsByType(NotificationType type);
    void subscribeToNotifications(Long userId, NotificationType type);
}
```

This implementation provides a complete PoC structure with clear responsibilities for each class and proper separation of concerns across microservices.