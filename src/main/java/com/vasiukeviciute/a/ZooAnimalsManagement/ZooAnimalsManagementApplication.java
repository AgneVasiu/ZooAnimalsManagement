package com.vasiukeviciute.a.ZooAnimalsManagement;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vasiukeviciute.a.ZooAnimalsManagement.Controller.AnimalController;
import com.vasiukeviciute.a.ZooAnimalsManagement.Model.Animal;
import com.vasiukeviciute.a.ZooAnimalsManagement.Model.Environment;
import com.vasiukeviciute.a.ZooAnimalsManagement.Repository.AnimalRepository;
import com.vasiukeviciute.a.ZooAnimalsManagement.Repository.EnvironmentRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.transaction.annotation.Transactional;

import javax.json.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@SpringBootApplication
public class ZooAnimalsManagementApplication {
	private final AnimalRepository animalRepository;
	private final EnvironmentRepository environmentRepository;
	private final AnimalController animalController;

	public ZooAnimalsManagementApplication(AnimalRepository animalRepository, EnvironmentRepository environmentRepository,
										   AnimalController animalController) {
		this.animalRepository = animalRepository;
		this.environmentRepository = environmentRepository;
		this.animalController = animalController;
	}

	public static void main(String[] args) {
		SpringApplication.run(ZooAnimalsManagementApplication.class, args);
	}
	@EventListener(ApplicationReadyEvent.class)
	@Transactional
	public void loadDataOnStartup() {
		loadAnimals();
		loadEnvironments();

	}
	private void loadEnvironments() {
		try{
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode jsonNode = objectMapper.readTree(getClass().getResourceAsStream("/enclosures.json"));
			JsonNode environmentArray = jsonNode.get("enclosures");

			for (JsonNode environmentNode : environmentArray) {
				String name = environmentNode.get("name").asText();
				String size = environmentNode.get("size").asText();
				String location = environmentNode.get("location").asText();

				// Check if the environment with the same name already exists in the database
				Optional<Environment> existingEnvironment = environmentRepository.findByName(name);
				if (existingEnvironment.isPresent()) {
					// Environment already exists, skip insertion
					System.out.println("Environment with name " + name + " already exists in the database.");
					continue;
				}

				JsonNode objectsArray = environmentNode.get("objects");
				List<String> objects = new ArrayList<>();

				for (JsonNode objectNode : objectsArray) {
					String object = objectNode.asText();
					System.out.println("Object: " + object);
					objects.add(object);
				}

				Environment environment = new Environment();
				environment.setName(name);
				environment.setSize(size);
				environment.setLocation(location);
				environment.setObjects(objects);

				environmentRepository.save(environment);
				System.out.println();
				System.out.println("Name: " + name);
				System.out.println("Size: " + size);
				System.out.println("Location: " + location);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void loadAnimals() {
		try {
			ObjectMapper objectMapper = new ObjectMapper();

			// Read the JSON file as a JsonNode
			JsonNode jsonNode = objectMapper.readTree(getClass().getResourceAsStream("/animals.json"));

			JsonNode animalArray = jsonNode.get("animals");

			for (JsonNode animals : animalArray) {
				String species = animals.get("species").asText();
				String food = animals.get("food").asText();
				int amount = animals.get("amount").asInt();
				// Check if the animal with the same species already exists in the database
				Optional<Animal> existingAnimal = animalRepository.findBySpecies(species);
				if (existingAnimal.isPresent()) {
					// Animal already exists, skip insertion
					System.out.println("Animal with species " + species + " already exists in the database.");
					continue;
				}

				//set data
				Animal animal = new Animal();
				animal.setSpecies(species);
				animal.setFood(food);
				animal.setAmount(amount);
				animalRepository.save(animal);
				// Do something with the data
				System.out.println("species: " + species);
				System.out.println("food: " + food);
				System.out.println("amount: " + amount);
				System.out.println();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}




}
