package com.contoso.adviser.dto;

import com.contoso.adviser.model.Meal;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.List;
@JsonInclude(JsonInclude.Include.NON_NULL)
public record MealDto(long id, long userId, List<FoodItemAmountDTO> consumedFoods,
                      long timestamp, String review) {

    public MealDto(Meal meal) {
        this(meal.getId(), meal.getUser().getId(),
                meal.getConsumedFoods().stream().map(FoodItemAmountDTO::new).toList(),
                meal.getTimestamp().toInstant().toEpochMilli(), meal.getReview());
    }
}
