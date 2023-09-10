package com.qubits.engx.config;

import com.azure.ai.openai.OpenAIClient;
import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.ai.openai.OpenAIServiceVersion;
import com.azure.core.credential.AzureKeyCredential;
import com.qubits.engx.utils.OpenAPIData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiClientConfig {

    @Autowired
    private OpenAPIData openAPIData;

    @Bean
    public OpenAIClient openAIClient(){
        return new OpenAIClientBuilder()
                .credential(new AzureKeyCredential(openAPIData.getApiKey()))
                .endpoint(openAPIData.getUrl())
                .serviceVersion(OpenAIServiceVersion.V2023_05_15)
                .buildClient();
    }
}
