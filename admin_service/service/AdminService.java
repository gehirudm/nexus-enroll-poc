package admin_service.service;

import common.model.Course;
import common.model.Student;
import common.model.Faculty;
import java.util.List;

public interface AdminService {
    Course createCourse(Course course);
    Course updateCourse(Long courseId, Course course);
    void deleteCourse(Long courseId);
    void forceEnrollStudent(Long studentId, Long courseId);
    List<Student> getAllStudents();
    List<Faculty> getAllFaculty();
    List<Course> getAllCourses();
}
