package com.nexus.enrollment.student.service;

import com.nexus.enrollment.common.model.Student;
import com.nexus.enrollment.common.model.Schedule; 
import com.nexus.enrollment.common.model.Enrollment;
import com.nexus.enrollment.common.exceptions.NotFoundException;
import com.nexus.enrollment.student.repository.StudentRepository;
import java.util.List;

public class StudentServiceImpl implements StudentService {
    private final StudentRepository studentRepository;
    
    public StudentServiceImpl(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }
    
    @Override
    public Student getStudentById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Student", id));
    }
    
    @Override
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }
    
    @Override
    public Schedule getStudentSchedule(Long studentId) {
        Student student = getStudentById(studentId);
        // For now, return a simple schedule based on enrollments
        // In a real implementation, this would construct a proper schedule
        return new Schedule();
    }
    
    @Override
    public List<Enrollment> getStudentEnrollments(Long studentId) {
        Student student = getStudentById(studentId);
        return student.getEnrollments();
    }
    
    @Override
    public Student createStudent(Student student) {
        return studentRepository.save(student);
    }
    
    @Override
    public Student updateStudent(Long id, Student student) {
        if (!studentRepository.existsById(id)) {
            throw new NotFoundException("Student", id);
        }
        student.setId(id);
        return studentRepository.save(student);
    }
    
    @Override
    public void deleteStudent(Long id) {
        if (!studentRepository.existsById(id)) {
            throw new NotFoundException("Student", id);
        }
        studentRepository.deleteById(id);
    }
}
