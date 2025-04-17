package com.contoso.adviser.controller;

import com.contoso.adviser.dto.UserDTO;
import com.contoso.adviser.model.User;
import com.contoso.adviser.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserRepository userRepository;

    @PostMapping
    public ResponseEntity<UserDTO> createUser(@RequestBody UserDTO userDTO) {
        User user = userDTO.toUser();
        user = userRepository.save(user);
        return ResponseEntity.ok(new UserDTO(user));
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> listAllUsers() {
        List<UserDTO> users = new ArrayList<>();
        for (User user : userRepository.findAll()) {
            users.add(new UserDTO(user));
        }

        return ResponseEntity.ok(users);
    }
}
