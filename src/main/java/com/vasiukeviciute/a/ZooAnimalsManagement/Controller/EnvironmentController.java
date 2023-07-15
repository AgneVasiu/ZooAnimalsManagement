package com.vasiukeviciute.a.ZooAnimalsManagement.Controller;

import com.vasiukeviciute.a.ZooAnimalsManagement.Model.Environment;
import com.vasiukeviciute.a.ZooAnimalsManagement.Repository.EnvironmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/environment")
public class EnvironmentController {
    private final EnvironmentRepository environmentRepository;
    @Autowired
    public EnvironmentController(EnvironmentRepository environmentRepository) {
        this.environmentRepository = environmentRepository;
    }

    @GetMapping
    public List<Environment> getAllEnvironments() {
        return environmentRepository.findAll();
    }
    @GetMapping("/{id}")
    public ResponseEntity<Environment> getEnvironmentById(@PathVariable Long id) {
        Optional<Environment> optionalEnvironment = environmentRepository.findById(id);

        if (optionalEnvironment.isPresent()) {
            Environment environment = optionalEnvironment.get();
            return ResponseEntity.ok(environment);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<Environment> addEnvironment(@RequestBody Environment environment) {
        Environment savedEnvironment = environmentRepository.save(environment);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedEnvironment);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEnvironment(@PathVariable Long id) {
        environmentRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
