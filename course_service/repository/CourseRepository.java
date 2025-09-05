package course_service.repository;

import common.model.Course;
import common.repository.CrudRepository;
import java.util.List;

public interface CourseRepository extends CrudRepository<Course, Long> {
    List<Course> findByDepartment(String department);
    List<Course> findByInstructor(Long facultyId);
    List<Course> findAvailableCourses();
}
