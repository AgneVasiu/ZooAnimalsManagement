package com.vasiukeviciute.a.ZooAnimalsManagement;

import com.vasiukeviciute.a.ZooAnimalsManagement.Controller.AnimalController;
import com.vasiukeviciute.a.ZooAnimalsManagement.Model.Animal;
import com.vasiukeviciute.a.ZooAnimalsManagement.Repository.AnimalRepository;
import com.vasiukeviciute.a.ZooAnimalsManagement.Repository.EnvironmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AnimalControllerTest {
    private AnimalRepository animalRepository;
    private EnvironmentRepository environmentRepository;
    private AnimalController animalController;
    @BeforeEach
    void setup() {
        animalRepository = mock(AnimalRepository.class);
        environmentRepository = mock(EnvironmentRepository.class);
        animalController = new AnimalController(animalRepository, environmentRepository);
    }
    @Test
    void testGetAllAnimals() {
        List<Animal> animals = new ArrayList<>();
        animals.add(new Animal(1L, "Lion", "Carnivore", 2, null));
        animals.add(new Animal(2L, "Elephant", "Herbivore", 5, null));
        when(animalRepository.findAll()).thenReturn(animals);
        List<Animal> result = animalController.getAllAnimals();
        assertEquals(2, result.size());
    }
    @Test
    void testGetAnimalById() {
        long animalId = 1L;
        Animal animal = new Animal(animalId, "Lion", "Carnivore", 2, null);
        when(animalRepository.findById(animalId)).thenReturn(Optional.of(animal));
        ResponseEntity<Animal> response = animalController.getAnimalById(animalId);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(animal, response.getBody());
    }

}
