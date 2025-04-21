package com.contoso.adviser.ai;

import lombok.AllArgsConstructor;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
class OllamaAiService {
    private static final String CURRENT_PROMPT_INSTRUCTIONS = """
        
        Here's the `user_main_prompt`:
        
        
        """;

    private static final String PROMPT_GENERAL_INSTRUCTIONS = """
    Here are the general guidelines to answer the `user_main_prompt`
        
    """;

    private final OllamaChatModel client;

    public String call(String instructions, String request) {
        var generalInstructions = new SystemMessage(PROMPT_GENERAL_INSTRUCTIONS.concat(instructions));
        var promptMessage = new UserMessage(CURRENT_PROMPT_INSTRUCTIONS.concat(request));

        var prompt = new Prompt(List.of(generalInstructions, promptMessage));
        return client.call(prompt).getResult().getOutput().getText();
    }
}

