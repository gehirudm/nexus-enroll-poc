package course_service.service;

import common.model.Course;
import java.util.List;

public interface CourseService {
    Course getCourseById(Long id);
    List<Course> getAllCourses();
    List<Course> getCoursesByDepartment(String department);
    List<Course> getCoursesByInstructor(Long facultyId);
    List<Course> searchCourses(String department, String keyword);
    List<Course> getAvailableCourses();
    int getEnrollmentCount(Long courseId);
    Course createCourse(Course course);
    Course updateCourse(Long id, Course course);
    void deleteCourse(Long id);
}
