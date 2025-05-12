package com.contoso.adviser.ai;

import com.contoso.adviser.model.FoodItem;
import com.contoso.adviser.utils.JsonUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
class NutrientGenerator {

    record FoodItemNutrients(String name, List<String> nutrients) {
    }

    private static final String PROMPT = """
            You're a helpful assistant. You are going to be given a list of foods.
            In return you have to respond with a json formatted list of the nutrients associated with that food.
            For example:
            ----
            User: Banana, Pistachio, Cheese
            AI: [
                    {
                        "name": "Banana",
                        "nutrients": ["Vitamin C", "Vitamin B6", "Fiber", "Potassium", "Protein", "Magnesium", "Manganese", "Saturated fat"]
                    },
                    {
                        "name": "Pistachio",
                        "nutrients": ["Dietary fiber", "Protein", "G carbohydrate", "Vitamin B6", "Copper", "Iron", "Magnesium", "Phosphorus", "Manganese"]
                    },
                    {
                        "name": "Cheese",
                        "nutrients": ["Calcium", "Vitamin B12", "Phosphorus", "Sodium", "Vitamin A", "Phosphorus", "Potassium"]
                    }
            ]
                        
            ----
                        
            Do not limit yourself to the list given above. Answer with as many nutrients as you know. If you don't recognize the food item, set the nutrients value to null.
                        
                        
            For example:
            ----
            User: Banana, Facebook, Cheese
            AI: [
                    {
                        "name": "Banana",
                        "nutrients": ["Vitamin C", "Vitamin B6", "Fiber", "Potassium", "Protein", "Magnesium", "Manganese", "Saturated fat"]
                    },
                    {
                        "name": "Facebook",
                        "nutrients": null
                    },
                    {
                        "name": "Cheese",
                        "nutrients": ["Calcium", "Vitamin B12", "Phosphorus", "Sodium", "Vitamin A", "Phosphorus", "Potassium"]
                    }
            ]
            ----
                        
            Please answer only in the format shown in "----". Do not add anything else to your answer.
            """;

    private final OllamaAiService ollamaAiService;
    private final Logger logger;

    public Map<FoodItem, List<String>> obtainNutrientData(Collection<FoodItem> foodItems) {
        Map<String, FoodItem> reverseMap = foodItems.stream().collect(Collectors.toMap(fi -> fi.getName().toLowerCase(), fi -> fi));

        String foodList = String.join(", ", reverseMap.keySet());
        String aiOutput = ollamaAiService.call(PROMPT, foodList);

        Map<FoodItem, List<String>> result = new HashMap<>();
        try {
            FoodItemNutrients[] foodNutrientResult = JsonUtils.toObject(aiOutput, FoodItemNutrients[].class);
            for (var item : foodNutrientResult) {
                FoodItem foodItem = reverseMap.get(item.name.toLowerCase());
                if (foodItem == null) {
                    logger.warning("Invalid food item for: %s".formatted(item.name));
                    continue;
                }

                result.putIfAbsent(foodItem, item.nutrients);
            }

            return result;
        } catch (JsonProcessingException jpe) {
            throw new IllegalStateException("Could not compute nutrient results. AI response error: %s"
                    .formatted(aiOutput), jpe);
        }
    }
}
