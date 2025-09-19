# Postman Test Collections for Nexus Enrollment System

This directory contains comprehensive Postman test collections for all microservices in the Nexus Enrollment System.

## Collections Included

### 1. Student Service Tests
**File:** `Student-Service.postman_collection.json`
**Port:** 8081

**Test Coverage:**
- Get student by ID
- Get student schedule
- Get student enrollments
- Enroll student in course
- Drop course for student
- Error handling for invalid student IDs

### 2. Course Service Tests
**File:** `Course-Service.postman_collection.json`
**Port:** 8082

**Test Coverage:**
- Get all courses
- Get course by ID
- Get courses by department
- Get courses by instructor
- Search courses by keyword
- Get available courses
- Get course prerequisites
- Get course enrollment count
- Create new course

### 3. Faculty Service Tests
**File:** `Faculty-Service.postman_collection.json`
**Port:** 8083

**Test Coverage:**
- Get faculty by ID
- Get faculty assigned courses
- Get class roster
- Submit grades
- Get submitted grades
- Submit course change requests
- Error handling for invalid faculty IDs

### 4. Admin Service Tests
**File:** `Admin-Service.postman_collection.json`
**Port:** 8084

**Test Coverage:**
- Get all students
- Get all faculty
- Generate enrollment reports
- Generate faculty workload reports
- Generate course trends reports
- Create new courses
- Update courses
- Delete courses
- Force enroll students

### 5. Notification Service Tests
**File:** `Notification-Service.postman_collection.json`
**Port:** 8085

**Test Coverage:**
- Send notifications
- Get user notifications
- Get notifications by type
- Mark notifications as read
- Subscribe to notification types
- Get all notifications
- Error handling for invalid notification IDs

## Environment Configuration

**File:** `Nexus-Environment.postman_environment.json`

Contains pre-configured variables for:
- Service URLs for all microservices
- Test data (student IDs, course IDs, faculty IDs)
- Common parameters (departments, semesters)

## How to Use

### 1. Import Collections
1. Open Postman
2. Click "Import" button
3. Select all `.json` files from this directory
4. Collections will be imported with all tests

### 2. Set Up Environment
1. Import the `Nexus-Environment.postman_environment.json` file
2. Select "Nexus Enrollment System Environment" from the environment dropdown
3. All variables will be automatically configured

### 3. Start Services
Before running tests, ensure the corresponding microservice is running:

```bash
# Start the service you want to test
mvn exec:java -pl student-service -Dexec.mainClass="com.nexus.enrollment.student.StudentServiceApplication"
```

### 4. Run Tests

**Individual Requests:**
- Click on any request in a collection
- Click "Send" to execute the test
- View results in the response section

**Collection Runner:**
1. Right-click on a collection
2. Select "Run collection"
3. Configure test settings
4. Click "Run" to execute all tests in the collection

**Automated Testing:**
- Use Newman (Postman CLI) for automated testing
- Run collections in CI/CD pipelines

## Test Features

### Comprehensive Validation
Each test includes:
- **Status Code Validation:** Ensures proper HTTP response codes
- **Response Content Validation:** Checks for expected JSON structure and content
- **Response Time Validation:** Monitors performance
- **Error Handling:** Tests invalid inputs and edge cases

### Dynamic Variables
Tests use Postman variables for:
- Service URLs (easily switch between environments)
- Test data (student IDs, course IDs, etc.)
- Timestamps and dynamic values

### Request Naming Convention
All requests follow a consistent naming pattern:
- **Format:** `{Action} {Resource}` (e.g., "Get Student by ID", "Create Course")
- **HTTP Methods:** Clearly indicated in request names
- **Error Cases:** Named with "Error" suffix (e.g., "Get Student by ID - Error")

### Pre-request Scripts
- Set up dynamic timestamps
- Configure request-specific variables
- Prepare test data

### Test Scripts
- Validate response structure
- Check business logic
- Extract data for subsequent requests
- Generate test reports

## Sample Test Scenarios

### End-to-End Workflow Tests

**Student Enrollment Flow:**
1. Get available courses (Course Service)
2. Enroll student in course (Student Service)
3. Verify enrollment (Student Service)
4. Send enrollment notification (Notification Service)

**Faculty Grade Submission Flow:**
1. Get class roster (Faculty Service)
2. Submit grades (Faculty Service)
3. Verify grade submission (Faculty Service)
4. Send grade notifications (Notification Service)

**Admin Reporting Flow:**
1. Get all students (Admin Service)
2. Get all faculty (Admin Service)
3. Generate enrollment report (Admin Service)
4. Generate faculty workload report (Admin Service)

## Test Data

### Default Test Values
- **Student ID:** 1
- **Course ID:** 1
- **Faculty ID:** 1
- **Department:** Computer Science
- **Semester:** Fall2024

### Sample Request Bodies
All POST/PUT requests include realistic sample data:
- Course creation with proper structure
- Grade submissions with multiple students
- Notification subscriptions with preferences

## Error Testing

Each collection includes negative test cases:
- Invalid IDs (404 responses)
- Malformed requests (400 responses)
- Missing required fields
- Edge case scenarios

## Performance Testing

Tests include basic performance validations:
- Response time thresholds
- Timeout handling
- Load testing capabilities (via Collection Runner)

## Continuous Integration

These collections can be integrated into CI/CD pipelines using Newman:

```bash
# Install Newman
npm install -g newman

# Run collection
newman run Student-Service.postman_collection.json -e Nexus-Environment.postman_environment.json

# Generate HTML report
newman run Student-Service.postman_collection.json -e Nexus-Environment.postman_environment.json -r html
```

## Troubleshooting

### Common Issues

1. **Connection Refused**
   - Ensure the microservice is running on the correct port
   - Check firewall settings

2. **404 Errors**
   - Verify endpoint URLs match the service implementation
   - Check if the service is properly started

3. **Timeout Issues**
   - Increase timeout settings in Postman
   - Check service performance

### Debug Tips
- Use Postman Console to view detailed request/response logs
- Enable verbose logging in the services
- Check service startup logs for errors

## Contributing

When adding new endpoints to services:
1. Add corresponding test requests to the appropriate collection
2. Include proper test scripts for validation
3. Update environment variables if needed
4. Test both positive and negative scenarios
5. Update this README with new test coverage

## Future Enhancements

Potential improvements for the test suite:
- Data-driven testing with CSV files
- Mock server integration for isolated testing
- Advanced workflow tests
- Performance benchmarking
- Security testing scenarios
