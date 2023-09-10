package com.epam.engx.controller;

import com.epam.engx.classifier.LogisticClassifier;
import com.epam.engx.embeddings.EmbeddingsHandler;
import com.epam.engx.modeltraining.LogisticRegression;
import com.epam.engx.modeltraining.model.PredictSpeechTextRequest;
import com.epam.engx.modeltraining.model.PredictSpeechTextResponse;
import com.epam.engx.modeltraining.model.TrainingRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@CrossOrigin("*")
@RequestMapping("/fraud-detection/api/v1")
public class FraudDetectionController {
    private Logger logger = LoggerFactory.getLogger(FraudDetectionController.class);
    @Autowired
    private EmbeddingsHandler embeddingsHandler;
    @Autowired
    private LogisticRegression logisticRegression;

    @Autowired
    private LogisticClassifier logisticClassifier;

    @Value("${classifier.threshold}")
    private double threshold;

    //testing, remove it after done;
    @GetMapping("/test")
    public String testApp() {
        String jsonData = "{\"input\": \"Sample Document goes here\"}";
        double[] features = embeddingsHandler.getTextEmbedding(jsonData);
        System.out.println("Features size : " + features.length);
        System.out.println("Probability:" + logisticClassifier.classify(features));
        return "Application Running Fine";
    }

    @PostMapping("/train")
    public void training(@RequestBody TrainingRequest trainingRequest){

        logisticRegression.setIterations(trainingRequest.getIterations());
        logisticRegression.setLearningRate(trainingRequest.getLearningRate());
        logisticRegression.setWeights(new double[trainingRequest.getFeatureSize()]);

        try {
            logisticRegression.startTraining();
        } catch (Exception e) {
            throw new RuntimeException("Training failed: " +e );
        }
    }

    @PostMapping("/predict")
    public ResponseEntity<PredictSpeechTextResponse> predictSpeechText(@RequestBody PredictSpeechTextRequest request) {
        logger.info("Received request on endpoint /predict => {}",request.getSpeechText());
        PredictSpeechTextResponse response = new PredictSpeechTextResponse();
        double[] features = embeddingsHandler.getTextEmbedding(request.getSpeechText());
        double fraudProbability = logisticClassifier.classify(features);
        logger.info("Fraud probability = {}", fraudProbability);
        response.setFraudProbability(fraudProbability);
        response.setFraud(fraudProbability > threshold);
        if (fraudProbability>threshold){
            response.setMessage("Hey user, beware of fraud/scam!!");
        }
        else {
            response.setMessage("Hey user, everything looks fine");
        }
        logger.info("Response on endpoint /predict => {}", response.getMessage());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
