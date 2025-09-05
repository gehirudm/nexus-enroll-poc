package com.nexus.enrollment.faculty.service;

import com.nexus.enrollment.common.model.Grade;
import java.util.List;

public interface GradeService {
    GradeSubmissionResult submitGrades(Long facultyId, List<GradeSubmission> grades);
    List<Grade> getSubmittedGrades(Long facultyId, Long courseId);
}
