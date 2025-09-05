package faculty_service.service;

import common.model.Faculty;
import common.model.Course;
import common.model.Student;
import common.exceptions.NotFoundException;
import faculty_service.repository.FacultyRepository;
import java.util.List;
import java.util.ArrayList;

public class FacultyServiceImpl implements FacultyService {
    private final FacultyRepository facultyRepository;
    
    public FacultyServiceImpl(FacultyRepository facultyRepository) {
        this.facultyRepository = facultyRepository;
    }
    
    @Override
    public Faculty getFacultyById(Long id) {
        return facultyRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Faculty", id));
    }
    
    @Override
    public List<Course> getAssignedCourses(Long facultyId) {
        Faculty faculty = getFacultyById(facultyId);
        // In a real implementation, this would call Course Service to get course details
        // For now, returning empty list as placeholder
        return new ArrayList<>();
    }
    
    @Override
    public List<Student> getClassRoster(Long facultyId, Long courseId) {
        Faculty faculty = getFacultyById(facultyId);
        // Verify faculty is assigned to this course
        if (!faculty.getAssignedCourseIds().contains(courseId)) {
            throw new RuntimeException("Faculty is not assigned to this course");
        }
        
        // In a real implementation, this would call Student Service to get enrolled students
        // For now, returning empty list as placeholder
        return new ArrayList<>();
    }
    
    @Override
    public Faculty createFaculty(Faculty faculty) {
        return facultyRepository.save(faculty);
    }
    
    @Override
    public Faculty updateFaculty(Long id, Faculty faculty) {
        if (!facultyRepository.existsById(id)) {
            throw new NotFoundException("Faculty", id);
        }
        faculty.setId(id);
        return facultyRepository.save(faculty);
    }
    
    @Override
    public void deleteFaculty(Long id) {
        if (!facultyRepository.existsById(id)) {
            throw new NotFoundException("Faculty", id);
        }
        facultyRepository.deleteById(id);
    }
    
    @Override
    public List<Faculty> getAllFaculty() {
        return facultyRepository.findAll();
    }
}
