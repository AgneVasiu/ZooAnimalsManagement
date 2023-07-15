/*package Config;
import Model.Animal;
import Model.Environment;
import Repository.AnimalRepository;
import Repository.EnvironmentRepository;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;

import javax.json.*;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
@Component
public class JsonParser {
    private final AnimalRepository animalRepository;
    private final EnvironmentRepository environmentRepository;

    public JsonParser(AnimalRepository animalRepository, EnvironmentRepository environmentRepository) {
        this.animalRepository = animalRepository;
        this.environmentRepository = environmentRepository;
    }

    public void parseAnimalJson(String filename) {
        try (JsonReader reader = Json.createReader(new FileReader(filename))) {
            JsonObject jsonObject = reader.readObject();
            JsonArray animalsArray = jsonObject.getJsonArray("animals");

            for (int i = 0; i < animalsArray.size(); i++) {
                JsonObject animalObject = animalsArray.getJsonObject(i);
                String species = animalObject.getString("species");
                String food = animalObject.getString("food");
                int amount = animalObject.getInt("amount");

                Animal animal = new Animal();
                animal.setSpecies(species);
                animal.setFood(food);
                animal.setAmount(amount);

                animalRepository.save(animal);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void parseEnvironmentJson(String filename) {
        try (JsonReader reader = Json.createReader(new FileReader(filename))) {
            JsonObject jsonObject = reader.readObject();
            JsonArray environmentArray = jsonObject.getJsonArray("enclosures");

            for (int i = 0; i < environmentArray.size(); i++) {
                JsonObject environmentObject = environmentArray.getJsonObject(i);
                String name = environmentObject.getString("name");
                String size = environmentObject.getString("size");
                String location = environmentObject.getString("location");
                JsonArray objectsArray = environmentObject.getJsonArray("objects");

                Environment environment = new Environment();
                environment.setName(name);
                environment.setSize(size);
                environment.setLocation(location);

                List<String> objects = new ArrayList<>();
                for (int j = 0; j < objectsArray.size(); j++) {
                    objects.add(objectsArray.getString(j));
                }
                environment.setObjects(objects);

                environmentRepository.save(environment);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void initializeDataFromJsonFiles() {
        parseAnimalJson(String.valueOf(JsonParser.class.getResourceAsStream("/animals.json")));
        parseEnvironmentJson(String.valueOf(JsonParser.class.getResourceAsStream("/enclosures.json")));
    }
}

 */
