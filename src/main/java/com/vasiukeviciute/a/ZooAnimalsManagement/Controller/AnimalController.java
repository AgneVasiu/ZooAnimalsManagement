package com.vasiukeviciute.a.ZooAnimalsManagement.Controller;

import com.vasiukeviciute.a.ZooAnimalsManagement.Model.Animal;
import com.vasiukeviciute.a.ZooAnimalsManagement.Model.Environment;
import com.vasiukeviciute.a.ZooAnimalsManagement.Repository.AnimalRepository;
import com.vasiukeviciute.a.ZooAnimalsManagement.Repository.EnvironmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/animals")
public class AnimalController {
    private final AnimalRepository animalRepository;
    private final EnvironmentRepository environmentRepository;


    @Autowired
    public AnimalController(AnimalRepository animalRepository, EnvironmentRepository environmentRepository) {
        this.animalRepository = animalRepository;
        this.environmentRepository = environmentRepository;
    }

    @GetMapping
    public List<Animal> getAllAnimals() {
        return animalRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Animal> getAnimalById(@PathVariable Long id) {
        Optional<Animal> optionalAnimal = animalRepository.findById(id);

        if (optionalAnimal.isPresent()) {
            Animal animal = optionalAnimal.get();
            if (animal.getEnvironment() != null) {
                animal.setEnclosureId(animal.getEnvironment().getId());
                animal.getEnvironment().setAnimals(null);
            }
            return ResponseEntity.ok(animal);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/enclosure")
    public List<Animal> getAllAnimalsInEnclosures() {
        List<Animal> animals = animalRepository.findAllWithEnclosure();
        animals.forEach(animal -> animal.setEnclosureId(animal.getEnvironment().getId()));
        return animals;
    }

    @PostMapping
    public ResponseEntity<Animal> addAnimal(@RequestBody Animal animal) {
        Animal savedAnimal = animalRepository.save(animal);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedAnimal);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Animal> updateAnimalEnclosure(@PathVariable Long id, @RequestParam(name = "enclosureId") Long enclosureId) {
        Optional<Animal> optionalAnimal = animalRepository.findById(id);
        Optional<Environment> optionalEnvironment = environmentRepository.findById(enclosureId);

        if (optionalAnimal.isPresent() && optionalEnvironment.isPresent()) {
            Animal animal = optionalAnimal.get();
            Environment environment = optionalEnvironment.get();
            animal.setEnvironment(environment);
            animalRepository.save(animal);
            return ResponseEntity.ok(animal);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAnimal(@PathVariable Long id) {
        animalRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/transfer")
    public ResponseEntity<String> transferAnimals() {
        List<Animal> allAnimals = animalRepository.findAll();
        List<Environment> allEnvironments = environmentRepository.findAll();

        // Group animals based on if they are herbivore or carnivore
        List<List<Animal>> animalGroups = groupAnimalsByFood(allAnimals);

        // Assign enclosures to the animals
        assignAnimalsToEnvironments(animalGroups, allEnvironments);

        return ResponseEntity.ok("Animals transferred to enclosures successfully.");
    }

    private List<List<Animal>> groupAnimalsByFood(List<Animal> animals) {
        List<List<Animal>> animalGroups = new ArrayList<>();
        List<Animal> vegetarianAnimals = new ArrayList<>();
        List<Animal> carnivoreAnimals = new ArrayList<>();

        for (Animal animal : animals) {
            if ("Herbivore".equalsIgnoreCase(animal.getFood())) {
                vegetarianAnimals.add(animal);
            } else if ("Carnivore".equalsIgnoreCase(animal.getFood())) {
                carnivoreAnimals.add(animal);
            }
        }

        animalGroups.add(vegetarianAnimals);
        animalGroups.add(carnivoreAnimals);

        return animalGroups;
    }

    private void assignAnimalsToEnvironments(List<List<Animal>> animalGroups, List<Environment> environments) {
        assignHerbivoreAnimals(animalGroups.get(0), environments);
        assignCarnivoreAnimals(animalGroups.get(1), environments);
    }

    private void assignHerbivoreAnimals(List<Animal> herbivoreAnimals, List<Environment> environments) {
        // Group herbivore animals by their species
        Map<String, List<Animal>> herbivoreGroups = herbivoreAnimals.stream()
                .collect(Collectors.groupingBy(Animal::getSpecies));

        for (List<Animal> group : herbivoreGroups.values()) {
            boolean assigned = false;
            for (Environment environment : environments) {
                if (environment.getAnimals().isEmpty() && isLargeOrHugeEnclosure(environment)) {
                    for (Animal herbivore : group) {
                        environment.addAnimal(herbivore);
                        environmentRepository.save(environment);
                    }
                    assigned = true;
                    break;
                }
            }

            if (!assigned && !environments.isEmpty()) {
                Environment suitableEnvironment = environments.get(0);
                for (Animal herbivore : group) {
                    suitableEnvironment.addAnimal(herbivore);
                    environmentRepository.save(suitableEnvironment);
                }
            }
        }
    }

    private void assignCarnivoreAnimals(List<Animal> carnivoreAnimals, List<Environment> environments) {
        // Group carnivore animals by their species
        Map<String, List<Animal>> carnivoreGroups = carnivoreAnimals.stream()
                .collect(Collectors.groupingBy(Animal::getSpecies));

        for (List<Animal> group : carnivoreGroups.values()) {
            for (Animal carnivore : group) {
                Environment suitableEnvironment = null;

                // Find an enclosure that is empty or is without herbivore animals
                for (Environment environment : environments) {
                    if (environment.getAnimals().isEmpty() && !containsConsumableObjects(environment, carnivore) && !containsHerbivores(environment)) {
                        suitableEnvironment = environment;
                        break;
                    }
                }

                // If no empty enclosures left find and assign enclosure without herbivores
                if (suitableEnvironment == null && !environments.isEmpty()) {
                    for (Environment environment : environments) {
                        if (!containsHerbivores(environment)) {
                            suitableEnvironment = environment;
                            break;
                        }
                    }
                }

                if (suitableEnvironment == null && !environments.isEmpty()) {
                    suitableEnvironment = environments.get(0);
                }

                if (suitableEnvironment != null) {
                    suitableEnvironment.addAnimal(carnivore);
                    environmentRepository.save(suitableEnvironment);
                }
            }
        }
    }

    private boolean containsConsumableObjects(Environment environment, Animal animal) {
        for (String object : environment.getObjects()) {
            if (object.equalsIgnoreCase(animal.getFood())) {
                return true;
            }
        }
        return false;
    }

    private boolean containsHerbivores(Environment environment) {
        for (Animal animal : environment.getAnimals()) {
            if ("Herbivore".equalsIgnoreCase(animal.getFood())) {
                return true;
            }
        }
        return false;
    }

    private boolean isLargeOrHugeEnclosure(Environment environment) {
        String size = environment.getSize().toLowerCase();
        return size.equals("large") || size.equals("huge");
    }
}


