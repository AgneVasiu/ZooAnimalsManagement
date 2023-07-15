package com.vasiukeviciute.a.ZooAnimalsManagement.Model;




import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "enclosures")
public class Environment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String size;
    private String location;
    @ElementCollection
    private List<String> objects;

    @OneToMany(mappedBy = "environment", cascade = CascadeType.PERSIST)
    private List<Animal> animals = new ArrayList<>();


    public Environment() {

    }
    public Environment(Long id, String name, String size, String location, List<String> objects, List<Animal> animals) {
        this.id = id;
        this.name = name;
        this.size = size;
        this.location = location;
        this.objects = objects;
        this.animals = animals;
    }

}
