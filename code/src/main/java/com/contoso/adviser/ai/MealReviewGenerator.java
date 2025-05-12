package com.contoso.adviser.ai;

import com.contoso.adviser.model.Meal;
import com.contoso.adviser.model.User;
import com.contoso.adviser.utils.JsonUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
class MealReviewGenerator {

    record MealAIInput(int age, String gender, String height, String weight, int dailyKcal, List<String> meal) {
    }

    private static final String PROMPT = """
            You are a helpful assistant.
            You will receive a json object, containing information about a user, consisting of their age, gender, height, weight and recommended daily caloric intake.
            Inside that json you will get a list of food items this person ate on a single meal, along with the amounts of the food they ate.
            Your ultimate goal is to do a breakdown of that meal and advice the user whether this meal was ideal for them and whether it should be improved next time.
                        
            Here is an example:
            ----
            User: {
                "age": 15,
                "gender": MALE,
                "height": "165 cm",
                "weight": "56.8 kg",
                "dailyKcal": 2200,
                "meal": [
                    "100 gr banana",
                    "400 gr greek yogurt",
                    "100 gr bread",
                    "10 gr blueberries",
                    "10 gr peanut butter",
                    "2 whole eggs"
                   
                ]
            }
             
            AI:
            Your meal is well-balanced overall, especially for someone with a daily intake of 2200 kcal. Here's a breakdown of how it aligns with your nutritional needs:
            Pros:
                        
                High Protein (83.6 g):
                    Excellent for muscle repair and growth, especially for a 15-year-old male in a growth phase. It covers ~38% of your daily protein needs (recommended ~1.2-2.0 g/kg of body weight).
                Good Balance of Carbs (92.3 g):
                    Provides quick energy for physical and mental activities. ~42% of the meal's calories come from carbs, which is within the acceptable range.
                Healthy Fats (33.5 g):
                    Includes healthy fats from peanut butter and eggs, supporting hormone production and overall health. Fat contributes ~30% of this meal’s calories, a good ratio.
                Micronutrients:
                    Banana: Rich in potassium.
                    Blueberries: Antioxidants for overall health.
                    Eggs: High in B vitamins, choline, and selenium.
                    Greek Yogurt: Calcium and probiotics for bone and gut health.
                        
            Cons/Improvements:
                        
                Low Fiber:
                    Only ~6-7 g of fiber in this meal (ideal daily target: ~25-30 g). Adding whole grains (like whole wheat bread) or more fruits/veggies would help.
                High Saturated Fat from Eggs and Peanut Butter:
                    Saturated fat is fine in moderation, but with 5 large eggs, it’s a bit high (~10 g). Reducing to 2-3 eggs or adding egg whites could lower saturated fat without compromising protein.
                Low Veggies:
                    No vegetables in the meal. Adding spinach, tomatoes, or bell peppers would boost vitamins A, C, and fiber.
                Sodium (Potentially High):
                    Depending on the bread and yogurt, sodium could be high. Opt for low-sodium options.
                        
            Suggestions to Improve:
                        
                Replace Some Eggs with Egg Whites:
                    Use 3 whole eggs + 2-3 egg whites to maintain protein but reduce calories and saturated fat.
                Swap Bread for Whole Grain/Seeded Bread:
                    Increases fiber and micronutrients like magnesium and zinc.
                Increase Vegetables:
                    Add 100-200 g of sautéed or fresh veggies (spinach, mushrooms, peppers) to the eggs.
                Include Healthy Fats:
                    Instead of peanut butter, try avocado for healthier monounsaturated fats.
                        
            How Good Is This Meal?
                        
            8/10: It’s an excellent high-protein, well-rounded meal, especially for a post-workout or breakfast. With small adjustments (e.g., more veggies, fiber, and balanced fats), it can become even healthier while maintaining its convenience and flavor!"
            ----
                        
            Please answer only in the format I gave you within the "----".
            """;

    private final OllamaAiService ollamaAiService;
    private final Logger logger;

    public String generateMealAnalysis(Meal meal) {
        User user = meal.getUser();
        String height = "%d cm".formatted(user.getHeight());
        String weight = "%f kg".formatted(user.getWeight());
        String gender = user.getGender().toString();

        List<String> consumedFoods = meal.getConsumedFoods()
                .stream().map(fia -> "%f %s %s".formatted(fia.getAmount(), fia.getUnit(), fia.getFoodItem().getName()))
                .toList();

        logger.info("Generating review for %s".formatted(consumedFoods));
        MealAIInput aiInput = new MealAIInput(user.getAge(), gender, height, weight, user.getRecommendedCal(), consumedFoods);

        try {
            String userInput = JsonUtils.toJson(aiInput);

            //We are not updating meal entity here, as we want to do it safely outside of this class.
            return ollamaAiService.call(PROMPT, userInput);
        } catch (JsonProcessingException jpe) {
            throw new IllegalStateException("Something went wrong with Json processing! Can't generate meal review", jpe);
        }
    }
}
