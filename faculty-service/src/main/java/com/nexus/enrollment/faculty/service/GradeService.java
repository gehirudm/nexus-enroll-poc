package com.nexus.enrollment.faculty.service;

import com.nexus.enrollment.common.model.Grade;
import com.nexus.enrollment.common.enums.GradeStatus;
import com.nexus.enrollment.faculty.repository.GradeRepository;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class GradeService {
    private final GradeRepository gradeRepository;
    
    public GradeService(GradeRepository gradeRepository) {
        this.gradeRepository = gradeRepository;
    }
    
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
                grade.setStatus(GradeStatus.PENDING); // Initial status is PENDING, requires approval
                gradeRepository.save(grade);
                submittedGrades.add(grade);
            }
            
            return new GradeSubmissionResult(true, "Grades submitted successfully", submittedGrades);
        } catch (Exception e) {
            return new GradeSubmissionResult(false, "Failed to submit grades: " + e.getMessage(), new ArrayList<>());
        }
    }
    
    public List<Grade> getSubmittedGrades(Long facultyId, Long courseId) {
        return gradeRepository.findByCourseId(courseId).stream()
                .filter(grade -> grade.getFacultyId().equals(facultyId))
                .collect(Collectors.toList());
    }
    
    public List<Grade> getPendingGrades(Long facultyId) {
        return gradeRepository.findByFacultyId(facultyId).stream()
                .filter(grade -> grade.getStatus() == GradeStatus.PENDING)
                .collect(Collectors.toList());
    }
    
    public List<Grade> getPendingGradesForCourse(Long facultyId, Long courseId) {
        return gradeRepository.findByCourseId(courseId).stream()
                .filter(grade -> grade.getFacultyId().equals(facultyId) && 
                               grade.getStatus() == GradeStatus.PENDING)
                .collect(Collectors.toList());
    }
    
    public GradeApprovalResult approveGrade(Long facultyId, Long gradeId) {
        try {
            Grade grade = gradeRepository.findById(gradeId)
                    .orElse(null);
            
            if (grade == null) {
                return new GradeApprovalResult(false, "Grade not found", null);
            }
            
            if (!grade.getFacultyId().equals(facultyId)) {
                return new GradeApprovalResult(false, "Faculty does not have permission to approve this grade", null);
            }
            
            if (grade.getStatus() != GradeStatus.PENDING) {
                return new GradeApprovalResult(false, "Grade is not in PENDING status", null);
            }
            
            grade.setStatus(GradeStatus.SUBMITTED);
            gradeRepository.save(grade);
            
            return new GradeApprovalResult(true, "Grade approved successfully", grade);
        } catch (Exception e) {
            return new GradeApprovalResult(false, "Failed to approve grade: " + e.getMessage(), null);
        }
    }
    
    public GradeApprovalResult rejectGrade(Long facultyId, Long gradeId, String reason) {
        try {
            Grade grade = gradeRepository.findById(gradeId)
                    .orElse(null);
            
            if (grade == null) {
                return new GradeApprovalResult(false, "Grade not found", null);
            }
            
            if (!grade.getFacultyId().equals(facultyId)) {
                return new GradeApprovalResult(false, "Faculty does not have permission to reject this grade", null);
            }
            
            if (grade.getStatus() != GradeStatus.PENDING) {
                return new GradeApprovalResult(false, "Grade is not in PENDING status", null);
            }
            
            grade.setStatus(GradeStatus.REJECTED);
            gradeRepository.save(grade);
            
            return new GradeApprovalResult(true, "Grade rejected: " + reason, grade);
        } catch (Exception e) {
            return new GradeApprovalResult(false, "Failed to reject grade: " + e.getMessage(), null);
        }
    }
}
