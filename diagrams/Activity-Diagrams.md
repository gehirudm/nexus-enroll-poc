# Activity Diagrams for Nexus Enrollment System

## Overview
Activity diagrams show the flow of activities and actions within the Nexus Enrollment System. They illustrate the step-by-step process flow for various business operations, helping to understand the workflow and decision points in the system.

## 1. Student Enrollment Process

### Purpose
This activity diagram shows the complete workflow of a student enrolling in a course.

### Key Activities
- **Start**: Student initiates enrollment request
- **Authentication**: Verify student credentials
- **Course Search**: Student searches for available courses
- **Course Selection**: Student selects desired course
- **Prerequisite Check**: System validates if student meets prerequisites
- **Capacity Check**: System verifies course capacity
- **Enrollment Processing**: System processes the enrollment
- **Notification**: System sends confirmation to student
- **End**: Process completes

### Decision Points
1. **Prerequisites Met?**
   - Yes → Continue to capacity check
   - No → Display error message and end process

2. **Course Has Capacity?**
   - Yes → Process enrollment
   - No → Add to waitlist or suggest alternatives

3. **Payment Required?**
   - Yes → Process payment
   - No → Complete enrollment

### Swimlanes
- **Student**: Initiates request, makes selections
- **Student Service**: Handles authentication and student data
- **Course Service**: Manages course information and capacity
- **Enrollment Service**: Processes enrollment logic
- **Notification Service**: Sends confirmations and updates

---

## 2. Faculty Grade Submission Process

### Purpose
Illustrates the workflow for faculty members submitting grades for their courses.

### Key Activities
- **Start**: Faculty logs into system
- **Course Selection**: Faculty selects course to grade
- **Student Roster**: System displays enrolled students
- **Grade Entry**: Faculty enters grades for each student
- **Grade Validation**: System validates grade entries
- **Submission Review**: Faculty reviews entered grades
- **Grade Submission**: System processes grade submission
- **Grade Storage**: Grades saved to database
- **Student Notification**: Students notified of new grades
- **End**: Process completes

### Decision Points
1. **Valid Grades?**
   - Yes → Continue to review
   - No → Return to grade entry with error messages

2. **Confirm Submission?**
   - Yes → Submit grades
   - No → Return to grade entry for modifications

3. **All Students Graded?**
   - Yes → Enable submission
   - No → Continue grade entry

---

## 3. Course Search and Discovery Process

### Purpose
Shows how students can search and discover courses based on various criteria.

### Key Activities
- **Start**: Student accesses course catalog
- **Search Criteria**: Student enters search parameters
- **Filter Application**: System applies filters (department, time, instructor)
- **Results Display**: System shows matching courses
- **Course Details**: Student views detailed course information
- **Prerequisite Display**: System shows course prerequisites
- **Schedule Conflict Check**: System checks for scheduling conflicts
- **Course Comparison**: Student compares multiple courses
- **Selection**: Student makes course selection
- **End**: Process completes

### Parallel Activities
- Real-time search suggestions
- Concurrent filtering options
- Live capacity updates

---

## 4. Administrative Report Generation

### Purpose
Demonstrates the workflow for administrators generating various reports.

### Key Activities
- **Start**: Administrator accesses reporting module
- **Report Type Selection**: Choose report type (enrollment, faculty workload, trends)
- **Parameter Setting**: Set report parameters (date range, department, etc.)
- **Data Collection**: System gathers relevant data
- **Data Processing**: System processes and aggregates data
- **Report Generation**: System creates formatted report
- **Report Review**: Administrator reviews generated report
- **Export Options**: Choose export format (PDF, Excel, etc.)
- **Report Distribution**: Send report to stakeholders
- **End**: Process completes

### Decision Points
1. **Sufficient Data Available?**
   - Yes → Generate report
   - No → Display warning and suggest alternative parameters

2. **Report Satisfactory?**
   - Yes → Export and distribute
   - No → Modify parameters and regenerate

---

## 5. Notification Processing Workflow

### Purpose
Shows how the system processes and delivers notifications to users.

### Key Activities
- **Start**: System event triggers notification
- **Event Analysis**: System analyzes event type and context
- **Recipient Determination**: Identify who should receive notification
- **Template Selection**: Choose appropriate notification template
- **Content Generation**: Generate personalized notification content
- **Delivery Channel Selection**: Choose delivery method (email, in-app, SMS)
- **Notification Sending**: Deliver notification to recipient
- **Delivery Confirmation**: Verify notification was delivered
- **Status Update**: Update notification status
- **End**: Process completes

### Parallel Flows
- Multiple notification types can be processed simultaneously
- Different delivery channels can be used concurrently
- Retry mechanisms for failed deliveries

---

## 6. Course Prerequisites Validation

### Purpose
Illustrates the complex logic for validating course prerequisites.

### Key Activities
- **Start**: Student attempts to enroll in course
- **Prerequisite Retrieval**: System retrieves course prerequisites
- **Student Transcript**: System accesses student's academic history
- **Prerequisite Matching**: Match completed courses with requirements
- **Grade Verification**: Verify minimum grades were achieved
- **Waiver Check**: Check for any prerequisite waivers
- **Alternative Path Check**: Verify alternative qualification paths
- **Final Validation**: Make final enrollment eligibility decision
- **Result Communication**: Inform student of decision
- **End**: Process completes

### Complex Decision Logic
- Multiple prerequisite paths (AND/OR logic)
- Minimum grade requirements
- Time-based prerequisites (course must be completed within X years)
- Department-specific waiver policies

---

## 7. Waitlist Management Process

### Purpose
Shows how the system manages course waitlists when courses reach capacity.

### Key Activities
- **Start**: Student requests enrollment in full course
- **Waitlist Check**: Verify if waitlist is available
- **Position Assignment**: Assign student position on waitlist
- **Notification Setup**: Set up notifications for waitlist updates
- **Monitoring**: System monitors for course openings
- **Opening Detection**: System detects when space becomes available
- **Priority Processing**: Process waitlist in priority order
- **Automatic Enrollment**: Enroll next student from waitlist
- **Notification Sending**: Notify student of enrollment
- **Waitlist Update**: Update remaining waitlist positions
- **End**: Process completes

### Concurrent Activities
- Multiple waitlists managed simultaneously
- Real-time position updates
- Time-limited enrollment offers

---

## Implementation Notes

### Service Integration
Each activity diagram maps to specific microservices in the system:
- **Student Service**: Handles student-related activities
- **Course Service**: Manages course and enrollment activities
- **Faculty Service**: Processes faculty-related workflows
- **Admin Service**: Handles administrative processes
- **Notification Service**: Manages all notification workflows

### Error Handling
All activity diagrams include error handling paths:
- System failure recovery
- User input validation
- Business rule violations
- External service failures

### Performance Considerations
- Parallel processing where possible
- Asynchronous operations for non-blocking activities
- Caching for frequently accessed data
- Timeout handling for long-running processes

### Audit Trail
Each activity generates audit logs for:
- Compliance requirements
- Debugging purposes
- Performance monitoring
- Security tracking
