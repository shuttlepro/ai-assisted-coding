package com.shuttle.aac.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: Shuttle
 * @description: AIAssistedCodingController
 */
@RestController
@RequestMapping("/ai-assisted-coding")
public class AIAssistedCodingController {

    @Value("${spring.ai.openai.api-key}")
    private String openAiApiKey;

    @Value("${spring.ai.openai.base-url}")
    private String baseUrl;

    @GetMapping("/openai/generate")
    public String generate(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        OpenAiApi openAiApi = new OpenAiApi(baseUrl, openAiApiKey);
        OpenAiChatModel openAiChatModel = new OpenAiChatModel(
                openAiApi,
                OpenAiChatOptions.builder()
                        .withModel(OpenAiApi.ChatModel.GPT_3_5_TURBO)
                        .withTemperature(0.8f)
                        .build());

        return ChatClient.builder(openAiChatModel).build()
                .prompt()
                .user(message)
                .call()
                .content();
    }

}
