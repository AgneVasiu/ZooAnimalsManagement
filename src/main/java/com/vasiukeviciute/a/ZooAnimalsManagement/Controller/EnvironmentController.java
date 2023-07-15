package com.vasiukeviciute.a.ZooAnimalsManagement.Controller;

import com.vasiukeviciute.a.ZooAnimalsManagement.Model.Environment;
import com.vasiukeviciute.a.ZooAnimalsManagement.Repository.EnvironmentRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/environment")
public class EnvironmentController {
    private final EnvironmentRepository environmentRepository;

    public EnvironmentController(EnvironmentRepository environmentRepository) {
        this.environmentRepository = environmentRepository;
    }

    @GetMapping
    public List<Environment> getAllEnvironments() {
        return environmentRepository.findAll();
    }

    @PostMapping
    public Environment addEnvironment(@RequestBody Environment environment) {
        return environmentRepository.save(environment);
    }

    @DeleteMapping("/{id}")
    public void deleteEnvironment(@PathVariable Long id) {
        environmentRepository.deleteById(id);
    }
}
