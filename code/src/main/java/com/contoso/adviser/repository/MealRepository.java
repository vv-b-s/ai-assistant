package com.contoso.adviser.repository;

import com.contoso.adviser.model.Meal;
import com.contoso.adviser.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MealRepository extends CrudRepository<Meal, Long> {

    List<Meal> findAllByUser(User user);

}
