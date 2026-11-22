package org.example.short_code.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Component
public class AIComponent {
    private static final Logger log = LoggerFactory.getLogger(AIComponent.class);
    @Value("${app.ai.enabled:false}")
    private boolean enabled;
    @Value("${app.ai.api-key}")
    private String apiKey;
    private final HttpClient httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(2)).build();
    private final ObjectMapper objectMapper = new ObjectMapper();
    public String summarize(String url){
        if(!enabled)    return null;
        long startTime = System.currentTimeMillis();
        try {
            String jsonBody = String.format("""
                {
                  "model": "deepseek-chat",
                  "messages": [
                    {"role": "user", "content": "请概括这个网址的用处，并返回50字以内的网站介绍：%s"}
                  ]
                }
                """, url);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.deepseek.com/chat/completions"))
                    .timeout(Duration.ofSeconds(2))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            long timeCost = System.currentTimeMillis() - startTime;
            if(response.statusCode() != 200){
                throw new IllegalArgumentException("AI摘要生成失败，耗时:"+timeCost+"ms");
            }
            String body = response.body();
            String content = objectMapper.readTree(body).path("choices").path(0).path("message").path("content").asText();
            return content;
        }catch(Exception e){
            long timeCost = System.currentTimeMillis() - startTime;
            log.warn("AI摘要生成异常。url = {},耗时: {}ms",url,timeCost,e);
            return null;
        }
    }
    public boolean isSafe(String url) {
        if (!enabled) return true;
        long startTime = System.currentTimeMillis();
        try {
            String jsonBody = String.format("""
                {
                  "model": "deepseek-chat",
                  "messages": [
                    {"role": "system", "content": "你是一个风控专家，只回答'安全'或'违规'。"},
                    {"role": "user", "content": "请判断这个网址是否涉及诈骗、黄赌毒：%s"}
                  ]
                }
                """, url);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.deepseek.com/chat/completions"))
                    .timeout(Duration.ofSeconds(2))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if(response.statusCode() != 200){
                throw new IllegalArgumentException("AI 风控调用失败，状态码: " + response.statusCode());
            }
            String body = response.body();
            String content = objectMapper.readTree(body).path("choices").path(0).path("message").path("content").asText();
            return !content.contains("违规");
        } catch (Exception e) {
            long timeCost = System.currentTimeMillis() - startTime;
            log.warn("AI 风控调用异常，降级放行。url = {},耗时: {}ms",url,timeCost,e);
            return true;
        }
    }
}
