package com.carenexus.ai;

import com.carenexus.common.error.ErrorCode;
import com.carenexus.common.exception.BusinessException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
@ConditionalOnProperty(name = "care-nexus.ai.mode", havingValue = "deepseek")
public class DeepSeekClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(DeepSeekClient.class);
    private final DeepSeekProperties properties;
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;

    public DeepSeekClient(DeepSeekProperties properties, ObjectMapper objectMapper) {
        this.properties = properties;
        this.objectMapper = objectMapper;
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(10000);
        factory.setReadTimeout(60000);
        restTemplate = new RestTemplate(factory);
    }

    public String chat(String systemPrompt, String userPrompt) {
        Map<String, Object> body = new HashMap<String, Object>();
        body.put("model", properties.getModel());
        body.put("messages", messages(systemPrompt, userPrompt));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(properties.getApiKey());
        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    normalize(properties.getBaseUrl()) + "/chat/completions", HttpMethod.POST,
                    new HttpEntity<String>(objectMapper.writeValueAsString(body), headers), String.class);
            return content(response.getBody());
        } catch (BusinessException exception) {
            throw exception;
        } catch (RestClientException exception) {
            LOGGER.error("DeepSeek API request failed", exception);
            throw unavailable();
        } catch (Exception exception) {
            LOGGER.error("DeepSeek API response could not be processed", exception);
            throw unavailable();
        }
    }

    private List<Map<String, String>> messages(String systemPrompt, String userPrompt) {
        List<Map<String, String>> messages = new ArrayList<Map<String, String>>();
        messages.add(message("system", systemPrompt));
        messages.add(message("user", userPrompt));
        return messages;
    }

    private Map<String, String> message(String role, String content) {
        Map<String, String> message = new HashMap<String, String>();
        message.put("role", role);
        message.put("content", content);
        return message;
    }

    private String content(String responseBody) throws Exception {
        JsonNode root = objectMapper.readTree(responseBody);
        String result = root.path("choices").path(0).path("message").path("content").asText("");
        if (!StringUtils.hasText(result)) {
            LOGGER.warn("DeepSeek API returned no usable content");
            throw unavailable();
        }
        return result.trim();
    }

    private BusinessException unavailable() {
        return new BusinessException(ErrorCode.SYSTEM_ERROR, "AI服务暂时不可用，请稍后重试");
    }

    private String normalize(String baseUrl) {
        return baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
    }
}
