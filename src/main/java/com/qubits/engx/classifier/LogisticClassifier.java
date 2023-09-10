package com.qubits.engx.classifier;

import com.qubits.engx.utils.ApplicationConstants;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.File;
import java.io.IOException;

@Service
public final class LogisticClassifier {

    @Value("${training.outputFilePath}")
    private String outputFilePath;

    public double classify(double[] features){
        double probability = 0.0;

        double[] weights = getModel();
        double logit = 0.0;

        if (weights.length != features.length) {
            throw new IllegalArgumentException("Number of features must match the number of weights.");
        }

        for(int i = 0; i < weights.length; i++){
            logit += weights[i] * features[i];
        }
        return sigmoid(logit);
    }

    private double sigmoid(double z){
        return 1.0 / ( 1.0 + Math.exp(-z));
    }

    private double[] getModel(){
        ObjectMapper objectMapper = new ObjectMapper();

        File jsonFile = new File(outputFilePath);
        JsonNode jsonNode = null;

        try {
            jsonNode = objectMapper.readTree(jsonFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        JsonNode weightsNode = jsonNode.get(ApplicationConstants.WEIGHTS);

        return objectMapper.convertValue(weightsNode, double[].class);
    }
}
