package com.shuttle.aac.outputconverter.controller;

import com.shuttle.aac.outputconverter.bean.MovieModel;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.converter.ListOutputConverter;
import org.springframework.ai.converter.MapOutputConverter;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @author: Shuttle
 * @description: OutputParserController
 */
@RestController
@RequestMapping("/output-converter")
public class OutputConverterController {

    @Value("${spring.ai.openai.api-key}")
    private String openAiApiKey;

    @Value("${spring.ai.openai.base-url}")
    private String baseUrl;

    @GetMapping("/list/top-10-history-events")
    public List<String> queryTop10HistoryEventsBySubject(@RequestParam(value = "subject", defaultValue = "science") String subject) {
        OpenAiApi openAiApi = new OpenAiApi(baseUrl, openAiApiKey);
        OpenAiChatModel openAiChatModel = new OpenAiChatModel(openAiApi);
        String contents = """
                Please list the top 10 events in the history of human {subject}.
                If you don't know the answer, just say I don't know.
                {outputFormat}
                """;

        ListOutputConverter listOutputConverter = new ListOutputConverter(new DefaultConversionService());
        PromptTemplate promptTemplate = new PromptTemplate(contents);
        Prompt prompt = promptTemplate.create(Map.of("subject", subject, "outputFormat", listOutputConverter.getFormat()));

        String content = ChatClient.builder(openAiChatModel).build()
                .prompt(prompt)
                .call()
                .content();

        return listOutputConverter.convert(content);
    }

    @GetMapping("/map/top-10-classical-music")
    public Map<String, Object> queryTop10ClassicalMusic() {
        OpenAiApi openAiApi = new OpenAiApi(baseUrl, openAiApiKey);
        OpenAiChatModel openAiChatModel = new OpenAiChatModel(openAiApi);
        String contents = """
                Please list the top 10 classical music and authors,
                the authors name as the key and music name as the value.
                If you don't know the answer, just say I don't know.
                {outputFormat}
                """;

        MapOutputConverter mapOutputConverter = new MapOutputConverter();
        PromptTemplate promptTemplate = new PromptTemplate(contents);
        Prompt prompt = promptTemplate.create(Map.of("outputFormat", mapOutputConverter.getFormat()));

        String content = ChatClient.builder(openAiChatModel).build()
                .prompt(prompt)
                .call()
                .content();

        return mapOutputConverter.convert(content);
    }

    @GetMapping("/bean/top-10-douban-movies")
    public List<MovieModel> queryTop10DoubanMovies() {
        OpenAiApi openAiApi = new OpenAiApi(baseUrl, openAiApiKey);
        OpenAiChatModel openAiChatModel = new OpenAiChatModel(
                openAiApi,
                OpenAiChatOptions.builder()
                        .withModel(OpenAiApi.ChatModel.GPT_4_O)
                        .build());
        String contents = """
                请在豆瓣电影网站上找出评分前十的电影
                {outputFormat}
                """;

        BeanOutputConverter<List<MovieModel>> beanOutputConverter = new BeanOutputConverter<>(new ParameterizedTypeReference<>() {
        });
        PromptTemplate promptTemplate = new PromptTemplate(contents);
        Prompt prompt = promptTemplate.create(Map.of("outputFormat", beanOutputConverter.getFormat()));

        String content = ChatClient.builder(openAiChatModel).build()
                .prompt(prompt)
                .call()
                .content();

        return beanOutputConverter.convert(content);
    }

}
