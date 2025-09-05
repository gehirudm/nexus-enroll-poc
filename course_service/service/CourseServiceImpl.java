package course_service.service;

import common.model.Course;
import common.exceptions.NotFoundException;
import course_service.repository.CourseRepository;
import java.util.List;
import java.util.stream.Collectors;

public class CourseServiceImpl implements CourseService {
    private final CourseRepository courseRepository;
    
    public CourseServiceImpl(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }
    
    @Override
    public Course getCourseById(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Course", id));
    }
    
    @Override
    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }
    
    @Override
    public List<Course> getCoursesByDepartment(String department) {
        return courseRepository.findByDepartment(department);
    }
    
    @Override
    public List<Course> getCoursesByInstructor(Long facultyId) {
        return courseRepository.findByInstructor(facultyId);
    }
    
    @Override
    public List<Course> searchCourses(String department, String keyword) {
        List<Course> courses = department != null && !department.isEmpty() 
            ? getCoursesByDepartment(department) 
            : getAllCourses();
            
        if (keyword != null && !keyword.isEmpty()) {
            String lowerKeyword = keyword.toLowerCase();
            return courses.stream()
                    .filter(course -> 
                        course.getName().toLowerCase().contains(lowerKeyword) ||
                        course.getDescription().toLowerCase().contains(lowerKeyword) ||
                        course.getCourseCode().toLowerCase().contains(lowerKeyword))
                    .collect(Collectors.toList());
        }
        
        return courses;
    }
    
    @Override
    public List<Course> getAvailableCourses() {
        return courseRepository.findAvailableCourses();
    }
    
    @Override
    public int getEnrollmentCount(Long courseId) {
        Course course = getCourseById(courseId);
        return course.getTotalCapacity() - course.getAvailableSeats();
    }
    
    @Override
    public Course createCourse(Course course) {
        return courseRepository.save(course);
    }
    
    @Override
    public Course updateCourse(Long id, Course course) {
        if (!courseRepository.existsById(id)) {
            throw new NotFoundException("Course", id);
        }
        course.setId(id);
        return courseRepository.save(course);
    }
    
    @Override
    public void deleteCourse(Long id) {
        if (!courseRepository.existsById(id)) {
            throw new NotFoundException("Course", id);
        }
        courseRepository.deleteById(id);
    }
}
