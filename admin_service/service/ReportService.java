package admin_service.service;

public interface ReportService {
    EnrollmentReport generateEnrollmentReport(String department, String semester);
    FacultyWorkloadReport generateFacultyWorkloadReport();
    CourseTrendsReport generateCourseTrendsReport();
}
