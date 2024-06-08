package com.shuttle.aac.stuffingprompt.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author: Shuttle
 * @description: StuffingPromptController
 */
@RestController
@RequestMapping("/stuffing-prompt")
public class StuffingPromptController {

    @Value("${spring.ai.openai.api-key}")
    private String openAiApiKey;

    @Value("${spring.ai.openai.base-url}")
    private String baseUrl;

    @Value("classpath:/prompts/olympic-sports.st")
    private Resource olympicSportsResource;

    @Value("classpath:/docs/olympic-sports.txt")
    private Resource docsToStuff2024OlympicSportsResource;

    @GetMapping("/olympic-sports")
    public String query2024OlympicSportsByYear(@RequestParam(value = "stuffSwitch", defaultValue = "false") boolean stuffSwitch) {
        OpenAiApi openAiApi = new OpenAiApi(baseUrl, openAiApiKey);
        OpenAiChatModel openAiChatModel = new OpenAiChatModel(openAiApi);

        PromptTemplate promptTemplate = new PromptTemplate(olympicSportsResource);
        Prompt prompt = promptTemplate.create(Map.of(
                "question", "What sports are being included in the 2024 Summer Olympics?",
                "context", stuffSwitch ? docsToStuff2024OlympicSportsResource : ""));

        return ChatClient.builder(openAiChatModel).build()
                .prompt(prompt)
                .call()
                .content();
    }

}
