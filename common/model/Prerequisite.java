package common.model;

public class Prerequisite {
    private Long id;
    private Long courseId;
    private Long prerequisiteCourseId;
    private String minimumGrade;
    
    public Prerequisite() {}
    
    public Prerequisite(Long courseId, Long prerequisiteCourseId, String minimumGrade) {
        this.courseId = courseId;
        this.prerequisiteCourseId = prerequisiteCourseId;
        this.minimumGrade = minimumGrade;
    }
    
    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }
    
    public Long getPrerequisiteCourseId() { return prerequisiteCourseId; }
    public void setPrerequisiteCourseId(Long prerequisiteCourseId) { this.prerequisiteCourseId = prerequisiteCourseId; }
    
    public String getMinimumGrade() { return minimumGrade; }
    public void setMinimumGrade(String minimumGrade) { this.minimumGrade = minimumGrade; }
}
