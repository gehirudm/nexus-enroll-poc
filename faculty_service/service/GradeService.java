package faculty_service.service;

import common.model.Grade;
import java.util.List;

public interface GradeService {
    GradeSubmissionResult submitGrades(Long facultyId, List<GradeSubmission> grades);
    List<Grade> getSubmittedGrades(Long facultyId, Long courseId);
}
