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
public class Meal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Version
    private long version;

    @ManyToMany(cascade = {
            CascadeType.MERGE,
            CascadeType.PERSIST
    })
    @JoinTable(name = "meal_consummed_foods",
            joinColumns = @JoinColumn(name = "food_id"),
            inverseJoinColumns = @JoinColumn(name = "amount_id"))
    private Set<FoodItemAmount> consumedFoods;

    @ManyToOne
    private User user;

    public Meal(Set<FoodItemAmount> consumedFoods) {
        this.consumedFoods = consumedFoods;
    }

}
