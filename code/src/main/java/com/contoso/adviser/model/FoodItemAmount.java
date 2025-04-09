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
public class FoodItemAmount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Version
    private long version;

    private double amount;
    private String unit;
    private int calories;

    @ManyToOne
    private FoodItem foodItem;

    @ManyToMany(mappedBy = "consumedFoods")
    private Set<Meal> meal;

    public FoodItemAmount(FoodItem foodItem, double amount, String unit) {
        this.foodItem = foodItem;
        this.amount = amount;
        this.unit = unit;
    }
}
