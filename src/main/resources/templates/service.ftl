package ${package};

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ${entityName}Service {

    // TODO: Inject your repository here
    // private final ${entityName}Repository ${entityName?uncap_first}Repository;

    public List<${entityName}> findAll() {
        // TODO: Implement findAll logic
        return List.of();
    }

    public ${entityName} findById(Long id) {
        // TODO: Implement findById logic
        return null;
    }

    public ${entityName} create(${entityName} entity) {
        // TODO: Implement create logic
        return null;
    }

    public ${entityName} update(Long id, ${entityName} entity) {
        // TODO: Implement update logic
        return null;
    }

    public void delete(Long id) {
        // TODO: Implement delete logic
    }
}
