package faculty_service.service;

import common.model.Faculty;
import common.model.Course;
import common.model.Student;
import java.util.List;

public interface FacultyService {
    Faculty getFacultyById(Long id);
    List<Course> getAssignedCourses(Long facultyId);
    List<Student> getClassRoster(Long facultyId, Long courseId);
    Faculty createFaculty(Faculty faculty);
    Faculty updateFaculty(Long id, Faculty faculty);
    void deleteFaculty(Long id);
    List<Faculty> getAllFaculty();
}
