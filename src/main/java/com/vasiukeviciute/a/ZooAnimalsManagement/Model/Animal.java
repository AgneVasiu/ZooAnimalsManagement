package com.vasiukeviciute.a.ZooAnimalsManagement.Model;


import com.fasterxml.jackson.annotation.JsonBackReference;
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

    @JsonBackReference
    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "enclosureId")
    private Environment environment;
    @Transient
    private Long enclosureId;
    public Long getEnclosureId() {
        if (environment != null) {
            return environment.getId();
        } else {
            return null;
        }
    }

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