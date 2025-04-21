package com.contoso.adviser.event;

import com.contoso.adviser.model.Meal;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class MealCreatedEvent extends ApplicationEvent {

    private final Meal meal;

    public MealCreatedEvent(Meal meal, Object source) {
        super(source);
        this.meal = meal;
    }
}
