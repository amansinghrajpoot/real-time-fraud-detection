package com.epam.engx.embeddings;

import com.azure.ai.openai.OpenAIClient;
import com.azure.ai.openai.models.EmbeddingsOptions;
import com.epam.engx.utils.OpenAPIData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Collections;

@Service
public class EmbeddingsHandler {

    @Autowired
    private OpenAIClient openAIClient;
    @Autowired
    private OpenAPIData openAPIData;

    /**Invoke Azure OpenAI (text-embedding-ada-002)**/

    public double[] getTextEmbedding(String originalText) {
        EmbeddingsOptions embeddingsOptions = new EmbeddingsOptions(Collections.singletonList(originalText));
        var result = openAIClient.getEmbeddings(openAPIData.getModel(), embeddingsOptions);
        return result.getData().stream().findFirst().get().getEmbedding()
                .stream().mapToDouble(Double::doubleValue).toArray();
    }

}
