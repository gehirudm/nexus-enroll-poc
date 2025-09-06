# Class Diagram for Nexus Enrollment System

## Overview
The class diagram represents the static structure of the Nexus Enrollment System, showing the classes, their attributes, methods, and relationships. The system follows a microservices architecture with shared common models and utilities.

## Package Structure

### 1. Common Package (`com.nexus.enrollment.common`)
Contains shared models, utilities, and enums used across all microservices.

---

## Core Domain Models

### Student Class
```java
class Student {
    - Long id
    - String name
    - String email
    - String department
    - Date enrollmentDate
    - StudentStatus status
    - List<Enrollment> enrollments
    
    + Student(name: String, email: String, department: String)
    + getId(): Long
    + getName(): String
    + setName(name: String): void
    + getEmail(): String
    + setEmail(email: String): void
    + getDepartment(): String
    + setDepartment(department: String): void
    + getEnrollmentDate(): Date
    + getStatus(): StudentStatus
    + setStatus(status: StudentStatus): void
    + getEnrollments(): List<Enrollment>
    + addEnrollment(enrollment: Enrollment): void
    + toString(): String
}
```

**Relationships:**
- Has many `Enrollment` (1:N)
- Associated with `StudentStatus` enum
- Participates in `Schedule`

---

### Course Class
```java
class Course {
    - Long id
    - String code
    - String title
    - String description
    - int credits
    - String department
    - Long instructorId
    - int capacity
    - int enrolled
    - Schedule schedule
    - List<Long> prerequisites
    - CourseStatus status
    
    + Course(code: String, title: String, credits: int, department: String)
    + getId(): Long
    + getCode(): String
    + setCode(code: String): void
    + getTitle(): String
    + setTitle(title: String): void
    + getDescription(): String
    + setDescription(description: String): void
    + getCredits(): int
    + setCredits(credits: int): void
    + getDepartment(): String
    + setDepartment(department: String): void
    + getInstructorId(): Long
    + setInstructorId(instructorId: Long): void
    + getCapacity(): int
    + setCapacity(capacity: int): void
    + getEnrolled(): int
    + setEnrolled(enrolled: int): void
    + getSchedule(): Schedule
    + setSchedule(schedule: Schedule): void
    + getPrerequisites(): List<Long>
    + addPrerequisite(courseId: Long): void
    + getStatus(): CourseStatus
    + setStatus(status: CourseStatus): void
    + hasCapacity(): boolean
    + isPrerequisiteMet(studentId: Long): boolean
}
```

**Relationships:**
- Has one `Schedule` (1:1)
- Has many prerequisites (self-reference, M:N)
- Taught by one `Faculty` (N:1)
- Has many `Enrollment` (1:N)
- Associated with `CourseStatus` enum

---

### Faculty Class
```java
class Faculty {
    - Long id
    - String name
    - String email
    - String department
    - String title
    - String officeLocation
    - List<Long> assignedCourses
    - FacultyStatus status
    
    + Faculty(name: String, email: String, department: String, title: String)
    + getId(): Long
    + getName(): String
    + setName(name: String): void
    + getEmail(): String
    + setEmail(email: String): void
    + getDepartment(): String
    + setDepartment(department: String): void
    + getTitle(): String
    + setTitle(title: String): void
    + getOfficeLocation(): String
    + setOfficeLocation(location: String): void
    + getAssignedCourses(): List<Long>
    + addAssignedCourse(courseId: Long): void
    + removeAssignedCourse(courseId: Long): void
    + getStatus(): FacultyStatus
    + setStatus(status: FacultyStatus): void
    + getWorkload(): int
}
```

**Relationships:**
- Teaches many `Course` (1:N)
- Submits many `Grade` (1:N)
- Associated with `FacultyStatus` enum

---

### Enrollment Class
```java
class Enrollment {
    - Long id
    - Long studentId
    - Long courseId
    - Date enrollmentDate
    - EnrollmentStatus status
    - String semester
    - Grade finalGrade
    
    + Enrollment(studentId: Long, courseId: Long, semester: String)
    + getId(): Long
    + getStudentId(): Long
    + getCourseId(): Long
    + getEnrollmentDate(): Date
    + getStatus(): EnrollmentStatus
    + setStatus(status: EnrollmentStatus): void
    + getSemester(): String
    + setSemester(semester: String): void
    + getFinalGrade(): Grade
    + setFinalGrade(grade: Grade): void
    + isActive(): boolean
    + canDrop(): boolean
}
```

**Relationships:**
- Links `Student` and `Course` (M:N association)
- Has one `Grade` (1:1)
- Associated with `EnrollmentStatus` enum

---

### Grade Class
```java
class Grade {
    - Long id
    - Long studentId
    - Long courseId
    - Long facultyId
    - String letterGrade
    - double numericGrade
    - Date submissionDate
    - String comments
    - GradeStatus status
    
    + Grade(studentId: Long, courseId: Long, facultyId: Long)
    + getId(): Long
    + getStudentId(): Long
    + getCourseId(): Long
    + getFacultyId(): Long
    + getLetterGrade(): String
    + setLetterGrade(grade: String): void
    + getNumericGrade(): double
    + setNumericGrade(grade: double): void
    + getSubmissionDate(): Date
    + setSubmissionDate(date: Date): void
    + getComments(): String
    + setComments(comments: String): void
    + getStatus(): GradeStatus
    + setStatus(status: GradeStatus): void
    + calculateGPA(): double
    + isPassingGrade(): boolean
}
```

**Relationships:**
- Belongs to one `Student` (N:1)
- Associated with one `Course` (N:1)
- Submitted by one `Faculty` (N:1)
- Associated with `GradeStatus` enum

---

### Schedule Class
```java
class Schedule {
    - Long id
    - DayOfWeek dayOfWeek
    - LocalTime startTime
    - LocalTime endTime
    - String location
    - String building
    - String room
    
    + Schedule(dayOfWeek: DayOfWeek, startTime: LocalTime, endTime: LocalTime)
    + getId(): Long
    + getDayOfWeek(): DayOfWeek
    + setDayOfWeek(day: DayOfWeek): void
    + getStartTime(): LocalTime
    + setStartTime(time: LocalTime): void
    + getEndTime(): LocalTime
    + setEndTime(time: LocalTime): void
    + getLocation(): String
    + setLocation(location: String): void
    + getBuilding(): String
    + setBuilding(building: String): void
    + getRoom(): String
    + setRoom(room: String): void
    + getDuration(): Duration
    + conflictsWith(other: Schedule): boolean
    + toString(): String
}
```

**Relationships:**
- Associated with `Course` (1:1)
- Uses `DayOfWeek` enum

---

### Notification Class
```java
class Notification {
    - Long id
    - Long userId
    - NotificationType type
    - String message
    - Date createdDate
    - Date sentDate
    - boolean isRead
    - NotificationStatus status
    - Map<String, Object> metadata
    
    + Notification(userId: Long, type: NotificationType, message: String)
    + getId(): Long
    + getUserId(): Long
    + getType(): NotificationType
    + setType(type: NotificationType): void
    + getMessage(): String
    + setMessage(message: String): void
    + getCreatedDate(): Date
    + getSentDate(): Date
    + setSentDate(date: Date): void
    + isRead(): boolean
    + markAsRead(): void
    + getStatus(): NotificationStatus
    + setStatus(status: NotificationStatus): void
    + getMetadata(): Map<String, Object>
    + addMetadata(key: String, value: Object): void
}
```

**Relationships:**
- Belongs to one User (Student/Faculty) (N:1)
- Associated with `NotificationType` enum
- Associated with `NotificationStatus` enum

---

## Enumeration Classes

### StudentStatus
```java
enum StudentStatus {
    ACTIVE, INACTIVE, GRADUATED, SUSPENDED, TRANSFERRED
}
```

### CourseStatus
```java
enum CourseStatus {
    ACTIVE, INACTIVE, CANCELLED, FULL
}
```

### FacultyStatus
```java
enum FacultyStatus {
    ACTIVE, INACTIVE, ON_LEAVE, RETIRED
}
```

### EnrollmentStatus
```java
enum EnrollmentStatus {
    ENROLLED, DROPPED, COMPLETED, WAITLISTED
}
```

### GradeStatus
```java
enum GradeStatus {
    DRAFT, SUBMITTED, APPROVED, FINAL
}
```

### NotificationType
```java
enum NotificationType {
    ENROLLMENT_CONFIRMATION, COURSE_DROPPED, WAITLIST_AVAILABLE, 
    GRADE_SUBMITTED, SYSTEM_ERROR, COURSE_FULL
}
```

### NotificationStatus
```java
enum NotificationStatus {
    PENDING, SENT, DELIVERED, FAILED, READ
}
```

---

## Service Layer Classes

### StudentService Interface
```java
interface StudentService {
    + getStudentById(id: Long): Student
    + getAllStudents(): List<Student>
    + createStudent(student: Student): Student
    + updateStudent(student: Student): Student
    + deleteStudent(id: Long): void
    + getStudentSchedule(studentId: Long): Schedule
    + getStudentEnrollments(studentId: Long): List<Enrollment>
    + searchStudents(criteria: String): List<Student>
}
```

### CourseService Interface
```java
interface CourseService {
    + getCourseById(id: Long): Course
    + getAllCourses(): List<Course>
    + createCourse(course: Course): Course
    + updateCourse(id: Long, course: Course): Course
    + deleteCourse(id: Long): void
    + getCoursesByDepartment(department: String): List<Course>
    + getCoursesByInstructor(facultyId: Long): List<Course>
    + searchCourses(department: String, keyword: String): List<Course>
    + getAvailableCourses(): List<Course>
    + getEnrollmentCount(courseId: Long): int
}
```

### FacultyService Interface
```java
interface FacultyService {
    + getFacultyById(id: Long): Faculty
    + getAllFaculty(): List<Faculty>
    + createFaculty(faculty: Faculty): Faculty
    + updateFaculty(faculty: Faculty): Faculty
    + deleteFaculty(id: Long): void
    + getAssignedCourses(facultyId: Long): List<Course>
    + getClassRoster(facultyId: Long, courseId: Long): List<Student>
}
```

### EnrollmentService Interface
```java
interface EnrollmentService {
    + enrollStudent(studentId: Long, courseId: Long): EnrollmentResult
    + dropCourse(studentId: Long, courseId: Long): EnrollmentResult
    + getEnrollmentsByStudent(studentId: Long): List<Enrollment>
    + getEnrollmentsByCourse(courseId: Long): List<Enrollment>
    + validateEnrollment(studentId: Long, courseId: Long): boolean
}
```

---

## Repository Layer Classes

### Repository Interface (Generic)
```java
interface Repository<T, ID> {
    + save(entity: T): T
    + findById(id: ID): Optional<T>
    + findAll(): List<T>
    + update(entity: T): T
    + deleteById(id: ID): void
    + exists(id: ID): boolean
}
```

### StudentRepository Interface
```java
interface StudentRepository extends Repository<Student, Long> {
    + findByEmail(email: String): Optional<Student>
    + findByDepartment(department: String): List<Student>
    + findByStatus(status: StudentStatus): List<Student>
    + searchByName(name: String): List<Student>
}
```

### CourseRepository Interface
```java
interface CourseRepository extends Repository<Course, Long> {
    + findByCode(code: String): Optional<Course>
    + findByDepartment(department: String): List<Course>
    + findByInstructor(instructorId: Long): List<Course>
    + findByStatus(status: CourseStatus): List<Course>
    + findAvailableCourses(): List<Course>
    + searchByKeyword(keyword: String): List<Course>
}
```

---

## Controller Layer Classes

### StudentController Class
```java
class StudentController {
    - StudentService studentService
    - EnrollmentService enrollmentService
    
    + StudentController(studentService: StudentService, enrollmentService: EnrollmentService)
    + getStudent(id: Long): ResponseBuilder.Response
    + getAllStudents(): ResponseBuilder.Response
    + getStudentSchedule(studentId: Long): ResponseBuilder.Response
    + getStudentEnrollments(studentId: Long): ResponseBuilder.Response
    + enrollStudent(studentId: Long, courseId: Long): ResponseBuilder.Response
    + dropCourse(studentId: Long, courseId: Long): ResponseBuilder.Response
    + createStudent(student: Student): ResponseBuilder.Response
}
```

---

## Utility Classes

### ResponseBuilder Class
```java
class ResponseBuilder {
    + static success(data: Object): Response
    + static success(message: String, data: Object): Response
    + static error(message: String): Response
    + static error(message: String, data: Object): Response
    
    static class Response {
        - boolean success
        - String message
        - Object data
        - Map<String, Object> metadata
        
        + isSuccess(): boolean
        + getMessage(): String
        + getData(): Object
        + getMetadata(): Map<String, Object>
    }
}
```

---

## Key Relationships and Patterns

### Inheritance Relationships
- All repository interfaces extend the generic `Repository<T, ID>` interface
- Service implementation classes implement their respective service interfaces

### Composition Relationships
- `Student` has many `Enrollment` objects
- `Course` has one `Schedule` object
- `Enrollment` links `Student` and `Course`
- Controllers compose service objects

### Association Relationships
- `Faculty` teaches `Course` (1:N)
- `Student` enrolls in `Course` through `Enrollment` (M:N)
- `Grade` associates `Student`, `Course`, and `Faculty`

### Dependency Relationships
- Controllers depend on Services
- Services depend on Repositories
- All layers depend on common models and utilities

### Design Patterns Used
1. **Repository Pattern**: Data access abstraction
2. **Service Layer Pattern**: Business logic encapsulation
3. **Controller Pattern**: Request handling
4. **Builder Pattern**: ResponseBuilder for consistent responses
5. **Strategy Pattern**: Different enrollment validation strategies

### Package Dependencies
```
Controllers → Services → Repositories → Models
     ↓
  Common (Models, Enums, Utilities)
```

This class diagram represents a well-structured, layered architecture that supports the microservices implementation while maintaining clear separation of concerns and proper encapsulation.
