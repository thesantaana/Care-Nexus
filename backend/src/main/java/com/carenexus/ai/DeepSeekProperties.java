package com.carenexus.ai;

import javax.annotation.PostConstruct;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@ConditionalOnProperty(name = "care-nexus.ai.mode", havingValue = "deepseek")
@ConfigurationProperties(prefix = "deepseek")
public class DeepSeekProperties {
    private String apiKey;
    private String baseUrl = "https://api.deepseek.com";
    private String model = "deepseek-chat";

    @PostConstruct
    public void validate() {
        if (!StringUtils.hasText(apiKey)) {
            throw new IllegalStateException(
                    "DEEPSEEK_API_KEY must be configured when AI_MODE is deepseek");
        }
        if (!StringUtils.hasText(baseUrl) || !StringUtils.hasText(model)) {
            throw new IllegalStateException("DeepSeek base URL and model must be configured");
        }
    }

    public String getApiKey() { return apiKey; }

    public void setApiKey(String value) { apiKey = value; }

    public String getBaseUrl() { return baseUrl; }

    public void setBaseUrl(String value) { baseUrl = value; }

    public String getModel() { return model; }

    public void setModel(String value) { model = value; }
}
