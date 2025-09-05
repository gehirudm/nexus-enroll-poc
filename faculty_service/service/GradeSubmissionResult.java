package faculty_service.service;

import common.model.Grade;
import java.util.List;

public class GradeSubmissionResult {
    private boolean success;
    private String message;
    private List<Grade> submittedGrades;
    
    public GradeSubmissionResult(boolean success, String message, List<Grade> submittedGrades) {
        this.success = success;
        this.message = message;
        this.submittedGrades = submittedGrades;
    }
    
    // Getters and setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public List<Grade> getSubmittedGrades() { return submittedGrades; }
    public void setSubmittedGrades(List<Grade> submittedGrades) { this.submittedGrades = submittedGrades; }
}
