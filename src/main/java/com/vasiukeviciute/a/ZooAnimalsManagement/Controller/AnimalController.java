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
@CrossOrigin("http://localhost:3000")
@RequestMapping("/animals")
public class AnimalController {
    private final AnimalRepository animalRepository;
    private final EnvironmentRepository environmentRepository;


    @Autowired
    public AnimalController(AnimalRepository animalRepository, EnvironmentRepository environmentRepository) {
        this.animalRepository = animalRepository;
        this.environmentRepository = environmentRepository;
    }
//Get request to get all the animals with or without enclosure prescribed
    @GetMapping
    public List<Animal> getAllAnimals() {
        return animalRepository.findAll();
    }
//Get Animals by id show only one animal details with the selected id
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
//gets animals that have enclosures
    @GetMapping("/enclosure")
    public List<Animal> getAllAnimalsInEnclosures() {
        List<Animal> animals = animalRepository.findAllWithEnclosure();
        animals.forEach(animal -> animal.setEnclosureId(animal.getEnvironment().getId()));
        return animals;
    }
//Add new animal
    @PostMapping
    public ResponseEntity<Animal> addAnimal(@RequestBody Animal animal) {
        Animal savedAnimal = animalRepository.save(animal);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedAnimal);
    }
//update the enclosure that the animal is put in by animal id
    @PutMapping("/transfer/{id}")
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
    //Update selected animal data by animal id
    @PutMapping("/{id}")
    public ResponseEntity<Animal> updateAnimal(@PathVariable Long id, @RequestBody Animal updatedAnimal) {
        Optional<Animal> optionalAnimal = animalRepository.findById(id);

        if (optionalAnimal.isPresent()) {
            Animal existingAnimal = optionalAnimal.get();
            existingAnimal.setSpecies(updatedAnimal.getSpecies());
            existingAnimal.setFood(updatedAnimal.getFood());
            existingAnimal.setAmount(updatedAnimal.getAmount());

            Animal savedAnimal = animalRepository.save(existingAnimal);
            return ResponseEntity.ok(savedAnimal);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
//remove animal
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAnimal(@PathVariable Long id) {
        animalRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
//Automatically prescribe to all animals that does not have enclosures enclosure that is based on their food performances
    //Carnivores only put with carnivores if there is no free enclosures and herbivores are added together
    @PostMapping("/transfer")
    public ResponseEntity<String> transferAnimals() {
        List<Animal> allAnimals = animalRepository.findAll();
        List<Environment> allEnvironments = environmentRepository.findAll();

        // Grouping animals based on if they are herbivore or carnivore
        List<List<Animal>> animalGroups = groupAnimalsByFood(allAnimals);

        // Assigning enclosures to the animals
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
        // Grouping animals by their species
        Map<String, List<Animal>> herbivoreGroups = herbivoreAnimals.stream()
                .collect(Collectors.groupingBy(Animal::getSpecies));

        boolean assignedToHugeEnclosure = false; // To track if animals are assigned to huge enclosure

        for (List<Animal> group : herbivoreGroups.values()) {
            boolean assigned = false;

            // Assigning to huge enclosure if enclosure is available
            for (Environment environment : environments) {
                if (isHugeEnclosure(environment) && environment.getAnimals().isEmpty()) {
                    for (Animal herbivore : group) {
                        if (herbivore.getEnvironment() == null) {
                            // Function to see if animal has not been assigned to the enclosure
                            environment.addAnimal(herbivore);
                            herbivore.setEnvironment(environment);
                            environmentRepository.save(environment);
                            animalRepository.save(herbivore);
                        }
                    }
                    assigned = true;
                    assignedToHugeEnclosure = true;
                    break;
                }
            }

            if (!assignedToHugeEnclosure) {
                // Assign to other enclosure
                for (Environment environment : environments) {
                    if (isLargeEnclosure(environment) && environment.getAnimals().isEmpty()) {
                        for (Animal herbivore : group) {
                            if (herbivore.getEnvironment() == null) {
                                environment.addAnimal(herbivore);
                                herbivore.setEnvironment(environment);
                                environmentRepository.save(environment);
                                animalRepository.save(herbivore);
                            }
                        }
                        assigned = true;
                        break;
                    }
                }
            }

            if (!assigned && !environments.isEmpty()) {
                Environment suitableEnvironment = environments.get(0);
                for (Animal herbivore : group) {
                    if (herbivore.getEnvironment() == null) {
                        suitableEnvironment.addAnimal(herbivore);
                        herbivore.setEnvironment(suitableEnvironment);
                        environmentRepository.save(suitableEnvironment);
                        animalRepository.save(herbivore);
                    }
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
                //function to prevent animals assigned to the enclosure to be resigned again
                if (carnivore.getEnvironment() != null) {
                    continue;
                }
                Environment suitableEnvironment = null;

                //Function to find the empty environment without herbivore animals
                for (Environment environment : environments) {
                    if (environment.getAnimals().isEmpty() && !containsConsumableObjects(environment, carnivore) && !containsHerbivores(environment) && environment.getAnimals().isEmpty()) {
                        suitableEnvironment = environment;
                        break;
                    }
                }

                // function to see if there is any empty enclosures left and in case there is not than assign
                // the enclosure with carnivores in them and not herbivores
                if (suitableEnvironment == null && !environments.isEmpty()) {
                    for (Environment environment : environments) {
                        if (!containsHerbivores(environment) && environment.getAnimals().size() <= 2) {
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

    private boolean isHugeEnclosure(Environment environment) {
        return environment.getSize().equalsIgnoreCase("Huge");
    }

    private boolean isLargeEnclosure(Environment environment) {
        return environment.getSize().equalsIgnoreCase("Large");
    }
}


