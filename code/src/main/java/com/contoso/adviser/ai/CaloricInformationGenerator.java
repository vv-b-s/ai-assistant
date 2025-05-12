package com.contoso.adviser.ai;

import com.contoso.adviser.model.FoodItemAmount;
import com.contoso.adviser.utils.JsonUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
class CaloricInformationGenerator {
    record FoodCaloricImpact(String name, int calories) {
    }

    private static final String PROMPT = """
            You're a helpful assistant. You are going to be given a list of foods and their amounts.
            In return you have to respond with a json formatted list of the name of the food, its amount and its caloric impact, based on the amount given.
            For example:
            ----
            User: 500 gr Banana, 100 gr Pistachio, 1 slice Cheese
            AI: [
                    {
                        "name": "500 gr Banana",
                        "calories": "445"
                    },
                    {
                        "name": "100 gr Pistachio",
                        "calories": 560
                    },
                    {
                        "name": "1 slice Cheese",
                        "calories": 40
                    }
            ]
                        
            ----
                        
            Do not limit yourself to the list given above. You may receive the same food item in different amounts.
            Give an answer for each ane every amount set. Calories will be integers only.
            If you don't recognize the food item, or the amount unit or size, set the caloric value to null.
                        
                        
            For example:
            ----
            User: 500 gr Banana, 100 gr Pistachio, 1 click Facebook
            AI: [
                    {
                        "name": "500 gr Banana",
                        "calories": "445"
                    },
                    {
                        "name": "100 gr Pistachio",
                        "calories": 560
                    },
                    {
                        "name": "1 click Facebook",
                        "calories": null
                    }
            ]
            ----
                        
            Please answer only in the format shown in "----". Do not add anything else to your answer.
            """;

    private final OllamaAiService ollamaAiService;
    private final Logger logger;

    public Map<FoodItemAmount, Integer> computeCaloricInformation(Collection<FoodItemAmount> foodItemAmounts) {
        Map<String, FoodItemAmount> amountDataInput = new HashMap<>();
        for (var foodItemAmount : foodItemAmounts) {
            String listItem = "%.2f %s %s".formatted(foodItemAmount.getAmount(), foodItemAmount.getUnit(),
                    foodItemAmount.getFoodItem().getName()).toLowerCase(Locale.ROOT);
            amountDataInput.put(listItem, foodItemAmount);
        }

        String inputList = String.join(", ", amountDataInput.keySet());
        String generatedResponse = ollamaAiService.call(PROMPT, inputList);
        try {
            FoodCaloricImpact[] parsedResults = JsonUtils.toObject(generatedResponse, FoodCaloricImpact[].class);

            //We don't want to implicitly set the data to the entity here. It should be done in a safe place where database
            //relations are managed.
            Map<FoodItemAmount, Integer> computedCalories = new HashMap<>();
            for (var result : parsedResults) {
                FoodItemAmount foodItemAmount = amountDataInput.get(result.name.toLowerCase());
                if (foodItemAmount == null) {
                    logger.warning("Invalid foodItemAmount for %s".formatted(result.name));
                    continue;
                }

                computedCalories.put(foodItemAmount, result.calories);
            }

            return computedCalories;
        } catch (JsonProcessingException jpe) {
            throw new IllegalStateException("Could not compute caloric information. AI response error: %s"
                    .formatted(generatedResponse), jpe);
        }
    }
}
