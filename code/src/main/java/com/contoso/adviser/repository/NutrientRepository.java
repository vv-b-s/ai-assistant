package com.contoso.adviser.repository;

import com.contoso.adviser.model.Nutrient;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NutrientRepository extends CrudRepository<Nutrient, Long> {
}
