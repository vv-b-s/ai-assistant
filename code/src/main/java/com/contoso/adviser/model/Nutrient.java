package com.contoso.adviser.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Nutrient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Version
    private long version;

    private String name;

    @ManyToMany(mappedBy = "nutrients")
    private Set<FoodItem> foods;

    public Nutrient(String name) {
        this.name = name;
    }
}
