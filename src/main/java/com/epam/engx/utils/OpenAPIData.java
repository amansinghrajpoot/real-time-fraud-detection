package com.epam.engx.utils;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "openapi")
public class OpenAPIData {
    private String apiKey;
    private String model;
    private String url;

    public String getApiKey() {
        return apiKey;
    }

    public String getModel() {
        return model;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
