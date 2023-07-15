package com.vasiukeviciute.a.ZooAnimalsManagement.Repository;

import com.vasiukeviciute.a.ZooAnimalsManagement.Model.Animal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AnimalRepository extends JpaRepository<Animal, Long> {
    Optional<Animal> findBySpecies(String species);
}
