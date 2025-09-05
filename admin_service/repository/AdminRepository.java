package admin_service.repository;

import common.repository.CrudRepository;
import java.util.Map;

public interface AdminRepository extends CrudRepository<Map<String, Object>, Long> {
    // Admin repository for storing administrative data and reports
}
