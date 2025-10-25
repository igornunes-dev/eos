package ${package};

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import java.util.List;

@RestController
@RequestMapping("/api/${requestMapping}")
@RequiredArgsConstructor
public class ${className} {

    // TODO: Inject your service here
    // private final ${entityName}Service ${entityName?uncap_first}Service;

    @GetMapping
    public ResponseEntity<List<${entityName}>> findAll() {
        // TODO: Implement
        return ResponseEntity.ok(List.of());
    }

    @GetMapping("/{id}")
    public ResponseEntity<${entityName}> findById(@PathVariable Long id) {
        // TODO: Implement
        return ResponseEntity.ok(null);
    }

    @PostMapping
    public ResponseEntity<${entityName}> create(@RequestBody ${entityName} entity) {
        // TODO: Implement
        return ResponseEntity.ok(null);
    }

    @PutMapping("/{id}")
    public ResponseEntity<${entityName}> update(
            @PathVariable Long id,
            @RequestBody ${entityName} entity) {
        // TODO: Implement
        return ResponseEntity.ok(null);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        // TODO: Implement
        return ResponseEntity.noContent().build();
    }
}
