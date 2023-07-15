package com.vasiukeviciute.a.ZooAnimalsManagement.Repository;

import com.vasiukeviciute.a.ZooAnimalsManagement.Model.Environment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EnvironmentRepository extends JpaRepository<Environment, Long> {
    Optional<Environment> findByName(String name);
}
