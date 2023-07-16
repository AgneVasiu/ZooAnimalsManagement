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
//get all the enclosures
    @GetMapping
    public List<Environment> getAllEnvironments() {
        return environmentRepository.findAll();
    }
    //get enclosure by id
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
//add new enclosure
    @PostMapping
    public ResponseEntity<Environment> addEnvironment(@RequestBody Environment environment) {
        Environment savedEnvironment = environmentRepository.save(environment);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedEnvironment);
    }
    //update enclosure details by id
    @PutMapping("/{id}")
    public ResponseEntity<Environment> updateEnvironment(@PathVariable Long id, @RequestBody Environment updatedEnvironment) {
        Optional<Environment> optionalEnvironment = environmentRepository.findById(id);

        if (optionalEnvironment.isPresent()) {
            Environment existingEnvironment = optionalEnvironment.get();
            existingEnvironment.setName(updatedEnvironment.getName());
            existingEnvironment.setSize(updatedEnvironment.getSize());
            existingEnvironment.setObjects(updatedEnvironment.getObjects());
            environmentRepository.save(existingEnvironment);
            return ResponseEntity.ok(existingEnvironment);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
//delete enclosure by id
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEnvironment(@PathVariable Long id) {
        environmentRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
