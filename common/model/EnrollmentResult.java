package common.model;

public class EnrollmentResult {
    private boolean success;
    private String message;
    private Enrollment enrollment;
    
    public EnrollmentResult(boolean success, String message, Enrollment enrollment) {
        this.success = success;
        this.message = message;
        this.enrollment = enrollment;
    }
    
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public Enrollment getEnrollment() { return enrollment; }
    public void setEnrollment(Enrollment enrollment) { this.enrollment = enrollment; }
}
