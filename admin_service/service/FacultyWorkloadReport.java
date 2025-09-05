package admin_service.service;

import java.util.Map;
import java.util.List;

public class FacultyWorkloadReport {
    private Map<String, Integer> facultyCourseCount;
    private Map<String, Integer> facultyStudentCount;
    private List<Map<String, Object>> workloadDistribution;
    
    public FacultyWorkloadReport() {}
    
    public FacultyWorkloadReport(Map<String, Integer> facultyCourseCount, 
                                Map<String, Integer> facultyStudentCount,
                                List<Map<String, Object>> workloadDistribution) {
        this.facultyCourseCount = facultyCourseCount;
        this.facultyStudentCount = facultyStudentCount;
        this.workloadDistribution = workloadDistribution;
    }
    
    // Getters and setters
    public Map<String, Integer> getFacultyCourseCount() { return facultyCourseCount; }
    public void setFacultyCourseCount(Map<String, Integer> facultyCourseCount) { this.facultyCourseCount = facultyCourseCount; }
    
    public Map<String, Integer> getFacultyStudentCount() { return facultyStudentCount; }
    public void setFacultyStudentCount(Map<String, Integer> facultyStudentCount) { this.facultyStudentCount = facultyStudentCount; }
    
    public List<Map<String, Object>> getWorkloadDistribution() { return workloadDistribution; }
    public void setWorkloadDistribution(List<Map<String, Object>> workloadDistribution) { this.workloadDistribution = workloadDistribution; }
}
