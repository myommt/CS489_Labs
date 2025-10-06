package edu.miu.cs.cs489.lab5.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import edu.miu.cs.cs489.lab5.model.Surgery;
import edu.miu.cs.cs489.lab5.repository.SurgeryRepository;

@RestController
@RequestMapping("/api/surgeries")
public class SurgeryController {

    private final SurgeryRepository repo;

    public SurgeryController(SurgeryRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<Surgery> list() {
        return repo.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Surgery> get(@PathVariable Long id) {
        return repo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Surgery create(@RequestBody Surgery surgery) {
        return repo.save(surgery);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Surgery> update(@PathVariable Long id, @RequestBody Surgery surgery) {
        return repo.findById(id).map(existing -> {
            existing.setName(surgery.getName());
            existing.setContactNumber(surgery.getContactNumber());
            existing.setLocation(surgery.getLocation());
            repo.save(existing);
            return ResponseEntity.ok(existing);
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return repo.findById(id).map(existing -> {
            repo.delete(existing);
            return ResponseEntity.noContent().<Void>build();
        }).orElse(ResponseEntity.notFound().build());
    }
}
