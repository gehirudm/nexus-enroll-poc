package faculty_service.repository;

import common.model.Faculty;
import common.repository.CrudRepository;
import java.util.Optional;

public interface FacultyRepository extends CrudRepository<Faculty, Long> {
    Optional<Faculty> findByEmail(String email);
}
