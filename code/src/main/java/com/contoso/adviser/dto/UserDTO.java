package com.contoso.adviser.dto;

import com.contoso.adviser.model.Gender;
import com.contoso.adviser.model.User;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record UserDTO(Long id, String firstName, String lastName, int age, int height, double weight, Gender gender,
                      int recommendedCal) {

    public UserDTO(User user) {
        this(user.getId(), user.getFirstName(), user.getLastName(), user.getAge(),
                user.getHeight(), user.getWeight(), user.getGender(), user.getRecommendedCal());
    }

    public User toUser() {
        return new User(firstName, lastName, age, height, weight, gender, recommendedCal);
    }

}
