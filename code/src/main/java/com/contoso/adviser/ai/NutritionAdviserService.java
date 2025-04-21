package com.contoso.adviser.ai;

import com.contoso.adviser.event.MealCreatedEvent;
import com.contoso.adviser.model.FoodItem;
import com.contoso.adviser.model.FoodItemAmount;
import com.contoso.adviser.model.Meal;
import com.contoso.adviser.model.Nutrient;
import com.contoso.adviser.repository.FoodItemAmountRepository;
import com.contoso.adviser.repository.FoodItemRepository;
import com.contoso.adviser.repository.MealRepository;
import com.contoso.adviser.repository.NutrientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NutritionAdviserService {

    private final NutrientGenerator nutrientGenerator;
    private final CaloricInformationGenerator caloricInformationGenerator;
    private final MealReviewGenerator mealReviewGenerator;

    private final NutrientRepository nutrientRepository;
    private final FoodItemRepository foodItemRepository;
    private final FoodItemAmountRepository foodItemAmountRepository;
    private final MealRepository mealRepository;

    @EventListener
    public void obtainMealDataFromLLM(MealCreatedEvent event) {
        Meal meal = event.getMeal();
        obtainNutrientData(meal);
        obtainCaloricData(meal);
        obtainMealReview(meal);
    }

    private void obtainNutrientData(Meal meal) {
        List<FoodItem> foodItemsWithoutNutrients = meal.getConsumedFoods().stream()
                .map(FoodItemAmount::getFoodItem)
                .filter(foodItem -> foodItem.getNutrients().isEmpty()).toList();

        if (foodItemsWithoutNutrients.isEmpty()) {
            return;
        }

        Map<FoodItem, List<String>> foodNutrients = nutrientGenerator.obtainNutrientData(foodItemsWithoutNutrients);

        //Finding or creating new nutrient entries, to avoid duplication in the database
        Map<String, Nutrient> nutrientMap = computeNutrientMap(foodNutrients.values()
                .stream().flatMap(List::stream).collect(Collectors.toSet()));

        for (var entry : foodNutrients.entrySet()) {
            FoodItem foodItem = entry.getKey();
            List<String> generatedNutrients = entry.getValue();
            if (generatedNutrients != null) {
                List<Nutrient> nutrients = generatedNutrients.stream().map(nutrientMap::get).toList();
                foodItem.getNutrients().addAll(nutrients);
                foodItemRepository.save(foodItem);
            }
        }
    }

    private void obtainCaloricData(Meal meal) {
        List<FoodItemAmount> foodAmountsWithoutCaloricData = meal.getConsumedFoods()
                .stream().filter(fia -> fia.getCalories() == null).toList();

        if (foodAmountsWithoutCaloricData.isEmpty()) {
            return;
        }

        Map<FoodItemAmount, Integer> obtainedCaloricInformation = caloricInformationGenerator
                .computeCaloricInformation(foodAmountsWithoutCaloricData);

        for (var entry : obtainedCaloricInformation.entrySet()) {
            FoodItemAmount foodItemAmount = entry.getKey();
            foodItemAmount.setCalories(entry.getValue());
            foodItemAmountRepository.save(foodItemAmount);
        }
    }

    private void obtainMealReview(Meal meal) {
        if (!meal.getReview().equals(Meal.DEFAULT_REVIEW_VALUE)) {
            return;
        }

        String review = mealReviewGenerator.generateMealAnalysis(meal);
        meal.setReview(review);
        mealRepository.save(meal);
    }

    private Map<String, Nutrient> computeNutrientMap(Set<String> nutrientNames) {
        Map<String, Nutrient> nutrientMap = new HashMap<>();
        for (var name : nutrientNames) {
            Nutrient nutrient = nutrientRepository.findByName(name)
                    .orElseGet(() -> nutrientRepository.save(new Nutrient(name)));
            nutrientMap.put(name, nutrient);
        }

        return nutrientMap;
    }
}
