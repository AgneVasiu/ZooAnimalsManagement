package com.vasiukeviciute.a.ZooAnimalsManagement.Model;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "animals")
public class Animal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String species;
    private String food;
    private int amount;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "environment_id")
    private Environment environment;



    public Animal() {
    }

    public Animal(Long id, String species, String food, int amount, Environment environment) {
        this.id = id;
        this.species = species;
        this.food = food;
        this.amount = amount;
        this.environment = environment;
    }
}