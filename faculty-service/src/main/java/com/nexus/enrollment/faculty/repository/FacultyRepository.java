package com.nexus.enrollment.faculty.repository;

import com.nexus.enrollment.common.model.Faculty;
import com.nexus.enrollment.common.repository.CrudRepository;
import java.util.Optional;

public interface FacultyRepository extends CrudRepository<Faculty, Long> {
    Optional<Faculty> findByEmail(String email);
}
