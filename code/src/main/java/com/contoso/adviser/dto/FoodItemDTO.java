package com.contoso.adviser.dto;

import com.contoso.adviser.model.FoodItem;
import com.contoso.adviser.model.Nutrient;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record FoodItemDTO(long id, String name, List<String> nutrients) {

    public FoodItemDTO(long id, String name) {
        this(id, name, null);
    }

    public FoodItemDTO(FoodItem foodItem) {
        this(foodItem.getId(), foodItem.getName(),
                foodItem.getNutrients().stream().map(Nutrient::getName).toList());
    }

}
