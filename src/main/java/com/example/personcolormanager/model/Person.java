package com.example.personcolormanager.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "persons")
public class Person {

    @JsonProperty("id")
    @Id()
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private long id;

    @JsonProperty("name")
    @Column(name = "name")
    private String name;

    @JsonProperty("lastname")
    @Column(name = "lastName")
    private String lastName;

    @JsonProperty("zipcode")
    @Column(name = "zipCode")
    private String zipCode;

    @JsonProperty("city")
    @Column(name = "city")
    private String city;

    @JsonProperty("color")
    @Column(name = "colorName")
    private String color;

    @JsonProperty("colorId")
    @Column(name = "colorId")
    private int colorId;

    public Person(int id, String name, String colorName) {
    }

}
