package com.nexus.enrollment.faculty.repository;

import com.nexus.enrollment.common.model.Faculty;
import java.util.*;

public class InMemoryFacultyRepository implements FacultyRepository {
    private final Map<Long, Faculty> faculty = new HashMap<>();
    private Long nextId = 1L;
    
    @Override
    public Faculty save(Faculty facultyMember) {
        if (facultyMember.getId() == null) {
            facultyMember.setId(nextId++);
        }
        faculty.put(facultyMember.getId(), facultyMember);
        return facultyMember;
    }
    
    @Override
    public Optional<Faculty> findById(Long id) {
        return Optional.ofNullable(faculty.get(id));
    }
    
    @Override
    public List<Faculty> findAll() {
        return new ArrayList<>(faculty.values());
    }
    
    @Override
    public void deleteById(Long id) {
        faculty.remove(id);
    }
    
    @Override
    public boolean existsById(Long id) {
        return faculty.containsKey(id);
    }
    
    @Override
    public Optional<Faculty> findByEmail(String email) {
        return faculty.values().stream()
                .filter(f -> f.getEmail().equals(email))
                .findFirst();
    }
}
