package com.nexus.enrollment.faculty.service;

import com.nexus.enrollment.common.model.Grade;
import com.nexus.enrollment.common.enums.GradeStatus;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class GradeServiceImpl implements GradeService {
    private final List<Grade> grades = new ArrayList<>();
    
    @Override
    public GradeSubmissionResult submitGrades(Long facultyId, List<GradeSubmission> gradeSubmissions) {
        try {
            List<Grade> submittedGrades = new ArrayList<>();
            
            for (GradeSubmission submission : gradeSubmissions) {
                Grade grade = new Grade(
                    submission.getStudentId(),
                    submission.getCourseId(),
                    submission.getGradeValue(),
                    facultyId
                );
                grade.setStatus(GradeStatus.SUBMITTED);
                grades.add(grade);
                submittedGrades.add(grade);
            }
            
            return new GradeSubmissionResult(true, "Grades submitted successfully", submittedGrades);
        } catch (Exception e) {
            return new GradeSubmissionResult(false, "Failed to submit grades: " + e.getMessage(), new ArrayList<>());
        }
    }
    
    @Override
    public List<Grade> getSubmittedGrades(Long facultyId, Long courseId) {
        return grades.stream()
                .filter(grade -> grade.getFacultyId().equals(facultyId) && grade.getCourseId().equals(courseId))
                .collect(Collectors.toList());
    }
}
