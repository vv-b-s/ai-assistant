package com.contoso.adviser.controller;

import com.contoso.adviser.dto.FoodItemDTO;
import com.contoso.adviser.repository.FoodItemRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@RestController
@AllArgsConstructor
@RequestMapping("/food")
public class FoodItemController {

    private final FoodItemRepository foodItemRepository;

    @GetMapping
    public ResponseEntity<List<FoodItemDTO>> listAllFoodItems() {
        List<FoodItemDTO> foodItems = new ArrayList<>();
        for (var foodItem : foodItemRepository.findAll()) {
            FoodItemDTO dto = new FoodItemDTO(foodItem.getId(), foodItem.getName());
            foodItems.add(dto);
        }

        return ResponseEntity.ok(foodItems);
    }

    @GetMapping("/search")
    public ResponseEntity<List<FoodItemDTO>> getByNameOrId(@RequestParam(required = false, defaultValue = "0", value = "id") Long id,
                                                           @RequestParam(required = false, value = "name") String name) {
        List<FoodItemDTO> results = Stream.of(foodItemRepository.findById(id), foodItemRepository.findByName(name))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .distinct()
                .map(FoodItemDTO::new)
                .toList();

        return ResponseEntity.ok(results);
    }
}
