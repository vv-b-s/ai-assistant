package com.contoso.adviser.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Version
    private long version;

    public User(String firstName) {
        this.firstName = firstName;
    }

    public User(String firstName, String lastName, int age, int height, double weight, Gender gender, int recommendedCal) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.height = height;
        this.weight = weight;
        this.gender = gender;
        this.recommendedCal = recommendedCal;
    }

    private String firstName;
    private String lastName;
    private int age;
    private int height;
    private double weight;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private int recommendedCal;
}
