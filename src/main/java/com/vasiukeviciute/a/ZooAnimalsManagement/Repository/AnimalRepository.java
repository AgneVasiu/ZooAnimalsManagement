package com.vasiukeviciute.a.ZooAnimalsManagement.Repository;

import com.vasiukeviciute.a.ZooAnimalsManagement.Model.Animal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AnimalRepository extends JpaRepository<Animal, Long> {
    Optional<Animal> findBySpecies(String species);
    @Query("SELECT a FROM Animal a JOIN FETCH a.environment")
    List<Animal> findAllWithEnclosure();
}
