package student_service.repository;

import common.model.Student;
import common.model.Enrollment;
import common.enums.EnrollmentStatus;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryStudentRepository implements StudentRepository {
    private final Map<Long, Student> students = new HashMap<>();
    private Long nextId = 1L;
    
    @Override
    public Student save(Student student) {
        if (student.getId() == null) {
            student.setId(nextId++);
        }
        students.put(student.getId(), student);
        return student;
    }
    
    @Override
    public Optional<Student> findById(Long id) {
        return Optional.ofNullable(students.get(id));
    }
    
    @Override
    public List<Student> findAll() {
        return new ArrayList<>(students.values());
    }
    
    @Override
    public void deleteById(Long id) {
        students.remove(id);
    }
    
    @Override
    public boolean existsById(Long id) {
        return students.containsKey(id);
    }
    
    @Override
    public Optional<Student> findByEmail(String email) {
        return students.values().stream()
                .filter(student -> student.getEmail().equals(email))
                .findFirst();
    }
    
    @Override
    public List<Student> findByCourseEnrolled(Long courseId) {
        return students.values().stream()
                .filter(student -> student.getEnrollments().stream()
                        .anyMatch(enrollment -> enrollment.getCourseId().equals(courseId) 
                                && enrollment.getStatus() == EnrollmentStatus.ENROLLED))
                .collect(Collectors.toList());
    }
}
