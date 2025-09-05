package student_service.service;

import common.model.Student;
import common.model.Schedule;
import common.model.Enrollment;
import java.util.List;

public interface StudentService {
    Student getStudentById(Long id);
    List<Student> getAllStudents();
    Schedule getStudentSchedule(Long studentId);
    List<Enrollment> getStudentEnrollments(Long studentId);
    Student createStudent(Student student);
    Student updateStudent(Long id, Student student);
    void deleteStudent(Long id);
}
