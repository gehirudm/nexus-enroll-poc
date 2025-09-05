package admin_service.repository;

import java.util.*;

public class InMemoryAdminRepository implements AdminRepository {
    private final Map<Long, Map<String, Object>> adminData = new HashMap<>();
    private Long nextId = 1L;
    
    @Override
    public Map<String, Object> save(Map<String, Object> entity) {
        Long id = (Long) entity.get("id");
        if (id == null) {
            id = nextId++;
            entity.put("id", id);
        }
        adminData.put(id, entity);
        return entity;
    }
    
    @Override
    public Optional<Map<String, Object>> findById(Long id) {
        return Optional.ofNullable(adminData.get(id));
    }
    
    @Override
    public List<Map<String, Object>> findAll() {
        return new ArrayList<>(adminData.values());
    }
    
    @Override
    public void deleteById(Long id) {
        adminData.remove(id);
    }
    
    @Override
    public boolean existsById(Long id) {
        return adminData.containsKey(id);
    }
}
