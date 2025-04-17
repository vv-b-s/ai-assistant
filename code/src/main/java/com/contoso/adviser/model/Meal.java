package com.contoso.adviser.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.ZonedDateTime;
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

    private ZonedDateTime timestamp;

    @ManyToOne
    private User user;

    private String review = "Computing...";

    public Meal(User user, Set<FoodItemAmount> consumedFoods, ZonedDateTime timestamp) {
        this.user = user;
        this.consumedFoods = consumedFoods;
        this.timestamp = timestamp;
    }
}
