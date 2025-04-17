package com.contoso.adviser.repository;

import com.contoso.adviser.model.FoodItem;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FoodItemRepository extends CrudRepository<FoodItem, Long> {

    Optional<FoodItem> findByName(String name);
}
