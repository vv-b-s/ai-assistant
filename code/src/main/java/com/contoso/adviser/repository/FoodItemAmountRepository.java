package com.contoso.adviser.repository;

import com.contoso.adviser.model.FoodItem;
import com.contoso.adviser.model.FoodItemAmount;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FoodItemAmountRepository extends CrudRepository<FoodItemAmount, Long> {
    Optional<FoodItemAmount> findByFoodAmountAndUnit(FoodItem foodItem, double amount, String unit);
}
