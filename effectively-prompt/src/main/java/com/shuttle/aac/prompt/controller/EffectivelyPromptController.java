package com.shuttle.aac.prompt.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @author: Shuttle
 * @description: EffectivelyPromptController
 */
@RestController
@RequestMapping("/effectively-prompt")
public class EffectivelyPromptController {

    @Value("${spring.ai.openai.api-key}")
    private String openAiApiKey;

    @Value("${spring.ai.openai.base-url}")
    private String baseUrl;

    @GetMapping("/top-10-history-events")
    public String queryTop10HistoryEventsBySubject(@RequestParam(value = "subject", defaultValue = "science") String subject) {
        OpenAiApi openAiApi = new OpenAiApi(baseUrl, openAiApiKey);
        OpenAiChatModel openAiChatModel = new OpenAiChatModel(openAiApi);
        String contents = """
                Please list the top 10 events in the history of human {subject} and when they occurred.
                If you don't know the answer, just say I don't know.
                """;
        PromptTemplate promptTemplate = new PromptTemplate(contents);
        Prompt prompt = promptTemplate.create(Map.of("subject", subject));

        return ChatClient.builder(openAiChatModel).build()
                .prompt(prompt)
                .call()
                .content();
    }

    @GetMapping("/message-role")
    public String messageRole(@RequestParam(value = "message", defaultValue = "Tell me a horror story") String message) {
        OpenAiApi openAiApi = new OpenAiApi(baseUrl, openAiApiKey);
        OpenAiChatModel openAiChatModel = new OpenAiChatModel(openAiApi);
        Message systemMessage = new SystemMessage("""
                Your primary function is to tell a fairy tale.
                If someone wants you to tell a different story of any kind, tell them your primary function.
                """);
        Message userMessage = new UserMessage(message);
        Prompt prompt = new Prompt(List.of(systemMessage, userMessage));

        return ChatClient.builder(openAiChatModel).build()
                .prompt(prompt)
                .call()
                .content();
    }

}
