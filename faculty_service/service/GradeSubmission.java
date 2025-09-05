package faculty_service.service;

public class GradeSubmission {
    private Long studentId;
    private Long courseId;
    private String gradeValue;
    
    public GradeSubmission() {}
    
    public GradeSubmission(Long studentId, Long courseId, String gradeValue) {
        this.studentId = studentId;
        this.courseId = courseId;
        this.gradeValue = gradeValue;
    }
    
    // Getters and setters
    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }
    
    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }
    
    public String getGradeValue() { return gradeValue; }
    public void setGradeValue(String gradeValue) { this.gradeValue = gradeValue; }
}
