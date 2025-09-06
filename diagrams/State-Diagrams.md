# State Diagrams for Nexus Enrollment System

## Overview
State diagrams show the different states of objects in the Nexus Enrollment System and the transitions between these states. They help understand the lifecycle of various entities and the events that trigger state changes.

## 1. Student Lifecycle State Diagram

### Purpose
Shows the different states a student can be in throughout their academic journey.

### States
- **Initial State**: Entry point (application submitted)
- **Pending**: Application under review
- **Active**: Currently enrolled and taking courses
- **Inactive**: Temporarily not enrolled (leave of absence)
- **Suspended**: Disciplinary or academic suspension
- **Graduated**: Successfully completed program
- **Transferred**: Moved to another institution
- **Dropped**: Permanently left the institution
- **Final State**: Exit point

### State Transitions

#### From Pending
- **approve()** → Active
- **reject()** → Dropped
- **defer()** → Inactive

#### From Active
- **suspend()** → Suspended
- **takeLeave()** → Inactive
- **graduate()** → Graduated
- **transfer()** → Transferred
- **dropout()** → Dropped

#### From Inactive
- **reactivate()** → Active
- **graduate()** → Graduated (if requirements met)
- **dropout()** → Dropped

#### From Suspended
- **reinstate()** → Active
- **expel()** → Dropped

### Events and Guards
- **approve()**: Application meets admission requirements
- **suspend()**: Academic or disciplinary issues
- **takeLeave()**: Student requests leave of absence
- **reactivate()**: Student returns from leave [within time limit]
- **graduate()**: All degree requirements completed
- **transfer()**: Student moves to another institution

---

## 2. Course Lifecycle State Diagram

### Purpose
Illustrates the lifecycle of a course from creation to completion or cancellation.

### States
- **Initial State**: Course creation initiated
- **Draft**: Course being designed/planned
- **Approved**: Course approved by curriculum committee
- **Scheduled**: Course scheduled for specific semester
- **Open**: Registration open, accepting enrollments
- **Full**: Maximum capacity reached
- **In Progress**: Course actively being taught
- **Completed**: Course finished for the semester
- **Cancelled**: Course cancelled before completion
- **Archived**: Course permanently retired
- **Final State**: Course lifecycle ended

### State Transitions

#### From Draft
- **approve()** → Approved
- **reject()** → Cancelled

#### From Approved
- **schedule()** → Scheduled
- **cancel()** → Cancelled

#### From Scheduled
- **openRegistration()** → Open
- **cancel()** → Cancelled

#### From Open
- **reachCapacity()** → Full
- **startSemester()** → In Progress
- **cancel()** → Cancelled

#### From Full
- **dropStudent()** → Open
- **startSemester()** → In Progress
- **cancel()** → Cancelled

#### From In Progress
- **complete()** → Completed
- **emergency_cancel()** → Cancelled

#### From Completed
- **archive()** → Archived
- **reactivate()** → Approved (for next semester)

### Events and Guards
- **approve()**: Course meets academic standards
- **schedule()**: Time slot and resources available
- **reachCapacity()**: Enrolled students = maximum capacity
- **dropStudent()**: Student drops, creating opening [capacity > enrolled]
- **complete()**: Semester ends, grades submitted
- **emergency_cancel()**: Unexpected circumstances require cancellation

---

## 3. Enrollment State Diagram

### Purpose
Tracks the state of a student's enrollment in a specific course.

### States
- **Initial State**: Enrollment process initiated
- **Pending**: Enrollment request submitted
- **Waitlisted**: Course full, student on waiting list
- **Enrolled**: Successfully enrolled in course
- **Dropped**: Student dropped the course
- **Completed**: Course completed with grade
- **Failed**: Course completed but failed
- **Withdrawn**: Administratively withdrawn
- **Final State**: Enrollment process ended

### State Transitions

#### From Pending
- **approve()** → Enrolled [prerequisites met AND capacity available]
- **waitlist()** → Waitlisted [prerequisites met AND course full]
- **reject()** → Dropped [prerequisites not met]

#### From Waitlisted
- **promote()** → Enrolled [space becomes available]
- **remove()** → Dropped [student withdraws from waitlist]
- **timeout()** → Dropped [waitlist period expires]

#### From Enrolled
- **drop()** → Dropped [before drop deadline]
- **withdraw()** → Withdrawn [after drop deadline]
- **complete_pass()** → Completed [course finished with passing grade]
- **complete_fail()** → Failed [course finished with failing grade]

### Events and Guards
- **approve()**: Prerequisites verified AND course has capacity
- **waitlist()**: Prerequisites met BUT course at capacity
- **promote()**: Space available from waitlist [student still interested]
- **drop()**: Student-initiated withdrawal [before deadline]
- **withdraw()**: Administrative withdrawal [after deadline]
- **complete_pass()**: Final grade ≥ passing threshold
- **complete_fail()**: Final grade < passing threshold

---

## 4. Grade Submission State Diagram

### Purpose
Shows the progression of grade submission from faculty input to final recording.

### States
- **Initial State**: Grading period begins
- **Not Started**: No grades entered
- **In Progress**: Some grades entered, not all
- **Draft**: All grades entered, not submitted
- **Submitted**: Faculty submitted grades for review
- **Under Review**: Administrative review in progress
- **Approved**: Grades approved by administration
- **Published**: Grades visible to students
- **Final**: Grades permanently recorded
- **Rejected**: Grades rejected, need revision
- **Final State**: Grading process complete

### State Transitions

#### From Not Started
- **enterGrade()** → In Progress
- **timeout()** → Rejected [grading deadline passed]

#### From In Progress
- **enterMoreGrades()** → In Progress [loop]
- **completeAllGrades()** → Draft
- **timeout()** → Rejected [deadline passed]

#### From Draft
- **submit()** → Submitted
- **editGrade()** → In Progress
- **save()** → Draft [loop]

#### From Submitted
- **approve()** → Approved
- **reject()** → Rejected
- **return_for_revision()** → In Progress

#### From Approved
- **publish()** → Published

#### From Published
- **finalize()** → Final

#### From Rejected
- **revise()** → In Progress [if deadline not passed]
- **administrative_override()** → Final

### Events and Guards
- **enterGrade()**: Faculty inputs student grade
- **submit()**: All grades complete [all students have grades]
- **approve()**: Grades meet institutional standards
- **publish()**: Administrative approval to release grades
- **finalize()**: End of grade change period
- **timeout()**: Deadline exceeded [varies by state]

---

## 5. Notification State Diagram

### Purpose
Tracks the lifecycle of notifications sent to users.

### States
- **Initial State**: Notification trigger event
- **Created**: Notification object created
- **Queued**: Added to delivery queue
- **Processing**: Being prepared for delivery
- **Sent**: Delivered to recipient
- **Delivered**: Confirmed delivery
- **Read**: Recipient opened notification
- **Failed**: Delivery failed
- **Expired**: Notification expired before delivery
- **Retrying**: Attempting redelivery
- **Final State**: Notification lifecycle ended

### State Transitions

#### From Created
- **queue()** → Queued
- **expire()** → Expired [time-sensitive notification]

#### From Queued
- **process()** → Processing
- **expire()** → Expired [queue timeout]

#### From Processing
- **send()** → Sent
- **fail()** → Failed
- **expire()** → Expired

#### From Sent
- **confirm_delivery()** → Delivered
- **delivery_fail()** → Failed
- **timeout()** → Failed [no delivery confirmation]

#### From Delivered
- **read()** → Read
- **expire()** → Expired [auto-expire after time]

#### From Failed
- **retry()** → Retrying [retry attempts remaining]
- **give_up()** → Expired [max retries exceeded]

#### From Retrying
- **send()** → Sent
- **fail_final()** → Expired

### Events and Guards
- **queue()**: Notification ready for processing
- **send()**: Delivery service available
- **confirm_delivery()**: Recipient system confirms receipt
- **read()**: User opens/views notification
- **retry()**: Retry policy allows another attempt [attempts < max_retries]
- **expire()**: Time limit exceeded [various timeouts apply]

---

## 6. Faculty Course Assignment State Diagram

### Purpose
Shows the process of assigning and managing faculty course assignments.

### States
- **Initial State**: Assignment process begins
- **Requested**: Faculty requests course assignment
- **Under Review**: Department reviewing assignment
- **Approved**: Assignment approved by department
- **Scheduled**: Course scheduled with faculty
- **Active**: Faculty actively teaching course
- **Completed**: Course teaching completed
- **Cancelled**: Assignment cancelled
- **Reassigned**: Course reassigned to different faculty
- **Final State**: Assignment process ended

### State Transitions

#### From Requested
- **approve()** → Approved [faculty qualified]
- **reject()** → Cancelled [faculty not qualified]
- **defer()** → Under Review [need more information]

#### From Under Review
- **approve()** → Approved
- **reject()** → Cancelled

#### From Approved
- **schedule()** → Scheduled
- **cancel()** → Cancelled [course cancelled]

#### From Scheduled
- **start_semester()** → Active
- **reassign()** → Reassigned [faculty unavailable]
- **cancel()** → Cancelled

#### From Active
- **complete()** → Completed [semester ends]
- **emergency_reassign()** → Reassigned [faculty emergency]

#### From Reassigned
- **assign_new_faculty()** → Approved [new faculty assigned]
- **cancel_course()** → Cancelled [no replacement found]

### Events and Guards
- **approve()**: Faculty meets qualifications [credentials verified]
- **schedule()**: Time and resources allocated
- **start_semester()**: Academic calendar start date
- **complete()**: All course responsibilities fulfilled
- **reassign()**: Faculty becomes unavailable [emergency or request]

---

## 7. System Authentication State Diagram

### Purpose
Models the authentication states for users accessing the system.

### States
- **Initial State**: User accesses system
- **Unauthenticated**: No valid session
- **Authenticating**: Credentials being verified
- **Authenticated**: Valid session established
- **Session Active**: User actively using system
- **Session Idle**: No recent activity
- **Session Expired**: Timeout occurred
- **Locked Out**: Too many failed attempts
- **Logged Out**: User explicitly logged out
- **Final State**: Session ended

### State Transitions

#### From Unauthenticated
- **login_attempt()** → Authenticating

#### From Authenticating
- **valid_credentials()** → Authenticated
- **invalid_credentials()** → Unauthenticated [attempts < max]
- **too_many_failures()** → Locked Out [attempts ≥ max]

#### From Authenticated
- **activity()** → Session Active
- **no_activity()** → Session Idle

#### From Session Active
- **activity()** → Session Active [loop]
- **idle_timeout()** → Session Idle
- **logout()** → Logged Out
- **session_timeout()** → Session Expired

#### From Session Idle
- **activity()** → Session Active
- **idle_timeout()** → Session Expired
- **logout()** → Logged Out

#### From Locked Out
- **unlock_timer()** → Unauthenticated [after lockout period]
- **admin_unlock()** → Unauthenticated

### Events and Guards
- **valid_credentials()**: Username/password verified
- **activity()**: User performs system action
- **idle_timeout()**: No activity for idle period [configurable timeout]
- **session_timeout()**: Maximum session duration exceeded
- **too_many_failures()**: Failed attempts ≥ threshold [security policy]

---

## Implementation Considerations

### State Persistence
- States are persisted in the database for crash recovery
- State transitions are logged for audit trails
- Historical state information maintained for reporting

### Concurrent State Management
- Multiple objects can be in different states simultaneously
- State transitions may trigger events in other objects
- Proper synchronization required for shared resources

### Error Handling
- Invalid state transitions are rejected
- System errors during transitions trigger rollback
- Graceful degradation for partial failures

### Performance Optimization
- State queries optimized with database indexes
- Frequent state transitions cached in memory
- Batch processing for bulk state changes

### Business Rules Integration
- State guards implement business logic validation
- Transitions may require approval workflows
- Some state changes trigger external system notifications
