package admin_service.service;

import java.util.Map;
import java.util.List;

public class EnrollmentReport {
    private String department;
    private String semester;
    private int totalEnrollments;
    private Map<String, Integer> courseEnrollments;
    private List<Map<String, Object>> studentData;
    
    public EnrollmentReport() {}
    
    public EnrollmentReport(String department, String semester, int totalEnrollments, 
                           Map<String, Integer> courseEnrollments, List<Map<String, Object>> studentData) {
        this.department = department;
        this.semester = semester;
        this.totalEnrollments = totalEnrollments;
        this.courseEnrollments = courseEnrollments;
        this.studentData = studentData;
    }
    
    // Getters and setters
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    
    public String getSemester() { return semester; }
    public void setSemester(String semester) { this.semester = semester; }
    
    public int getTotalEnrollments() { return totalEnrollments; }
    public void setTotalEnrollments(int totalEnrollments) { this.totalEnrollments = totalEnrollments; }
    
    public Map<String, Integer> getCourseEnrollments() { return courseEnrollments; }
    public void setCourseEnrollments(Map<String, Integer> courseEnrollments) { this.courseEnrollments = courseEnrollments; }
    
    public List<Map<String, Object>> getStudentData() { return studentData; }
    public void setStudentData(List<Map<String, Object>> studentData) { this.studentData = studentData; }
}
