package com.nexus.enrollment.admin.repository;

import com.nexus.enrollment.common.repository.CrudRepository;
import java.util.Map;

public interface AdminRepository extends CrudRepository<Map<String, Object>, Long> {
    // Admin repository for storing administrative data and reports
}
