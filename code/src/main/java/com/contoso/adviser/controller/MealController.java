package com.contoso.adviser.controller;

import com.contoso.adviser.dto.FoodItemAmountDTO;
import com.contoso.adviser.dto.MealDto;
import com.contoso.adviser.model.FoodItem;
import com.contoso.adviser.model.FoodItemAmount;
import com.contoso.adviser.model.Meal;
import com.contoso.adviser.model.User;
import com.contoso.adviser.repository.FoodItemAmountRepository;
import com.contoso.adviser.repository.FoodItemRepository;
import com.contoso.adviser.repository.MealRepository;
import com.contoso.adviser.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.time.ZoneOffset.UTC;

@RestController
@AllArgsConstructor
@RequestMapping("/meal")
public class MealController {

    private final FoodItemRepository foodItemRepository;
    private final FoodItemAmountRepository foodItemAmountRepository;
    private final MealRepository mealRepository;
    private final UserRepository userRepository;

    @PostMapping("/user/{id}")
    public ResponseEntity<MealDto> createMeal(@PathVariable("id") Long userId, @RequestBody MealDto mealDto) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        ZonedDateTime timestamp = Instant.ofEpochMilli(mealDto.timestamp()).atZone(UTC);
        Set<FoodItemAmount> foodItemAmounts = obtainFoodItemAmounts(mealDto.consumedFoods());
        Meal meal = new Meal(user, foodItemAmounts, timestamp);
        mealRepository.save(meal);
        return ResponseEntity.ok(new MealDto(meal));
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<List<MealDto>> listAllUserMeals(@PathVariable("id") Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        List<MealDto> meals = mealRepository.findAllByUser(user)
                .stream().map(MealDto::new).toList();

        return ResponseEntity.ok(meals);
    }


    private Set<FoodItemAmount> obtainFoodItemAmounts(List<FoodItemAmountDTO> dtos) {
        Set<FoodItemAmount> foodItemAmounts = new HashSet<>();
        for (var dto : dtos) {
            FoodItem foodItem = foodItemRepository.findByName(dto.foodName())
                    .orElseGet(() -> foodItemRepository.save(new FoodItem(dto.foodName())));

            FoodItemAmount fia = foodItemAmountRepository.findByFoodAmountAndUnit(foodItem, dto.amount(), dto.unit())
                    .orElseGet(() -> foodItemAmountRepository.save(new FoodItemAmount(foodItem, dto.amount(), dto.unit())));

            foodItemAmounts.add(fia);
        }

        return foodItemAmounts;
    }
}
