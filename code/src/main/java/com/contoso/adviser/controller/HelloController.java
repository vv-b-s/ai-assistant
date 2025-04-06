package com.contoso.adviser.controller;

import com.contoso.adviser.ai.OllamaAiService;
import com.contoso.adviser.model.User;
import com.contoso.adviser.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/hello")
public class HelloController {

    private final UserRepository userRepository;
    private final OllamaAiService ollamaAiService;

    @GetMapping
    public String sayHello(@RequestParam String name) {
        User user = new User(name);
        userRepository.save(user);
        String lastName = ollamaAiService.call("""
                You are a helpful assistant. You will be given a first name of a person. 
                Your purpose is to invent the last name of that person and return it as a repsponse.
                For example:
                
                User: John
                AI: Smith 
                
                User: Anne
                AI: Croft
                
                You should return only a string containing a single name.
                """, name);

        return "Hello, %s %s!".formatted(user.getFirstName(), lastName);
    }

}
