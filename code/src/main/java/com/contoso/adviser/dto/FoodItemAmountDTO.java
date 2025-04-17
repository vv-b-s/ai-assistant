package com.contoso.adviser.dto;

import com.contoso.adviser.model.FoodItemAmount;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record FoodItemAmountDTO(String foodName, double amount, String unit, int calories) {

    public FoodItemAmountDTO(FoodItemAmount foodItemAmount) {
        this(foodItemAmount.getFoodItem().getName(), foodItemAmount.getAmount(),
                foodItemAmount.getUnit(), foodItemAmount.getCalories());
    }
}
