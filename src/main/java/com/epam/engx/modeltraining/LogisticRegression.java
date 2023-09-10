package com.epam.engx.modeltraining;

import com.epam.engx.embeddings.EmbeddingsHandler;
import com.epam.engx.utils.ApplicationConstants;
import com.epam.engx.utils.CSVFileReader;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.opencsv.CSVReader;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;

/**
 * Initial implementation of logistic regression algo.
 * please modify if needed to gain more accuracy
 * In the context of the sigmoid function, the "logit" refers to the logarithm of the odds of a binary event occurring.
 *
 * @version 1.1
 *
 */

@Service
@Getter
@Setter
public class LogisticRegression {

    private double learningRate;
    private double[] weights;
    private int iterations;

    @Value("${training.filename}")
    private String trainingFilename;

    @Value("${training.outputFilePath}")
    private String outputFilePath;

    @Value("${training.batchSize}")
    private int batchSize;

    @Autowired
    private EmbeddingsHandler embeddingsHandler;

    private double sigmoid(double z){
        return 1.0 / ( 1.0 + Math.exp(-z));
    }

    public void startTraining() throws IOException, URISyntaxException {
        System.out.println("Started training: " + trainingFilename);
        System.out.println("learningRate: " + learningRate + " iterations: " +iterations);

        int progressBarWidth = 50;
        int currentProgress = 0;

        int batchCount = 0;
        int lineCount = 0;
        double[] gradientSum = new double[weights.length];

        for( int n = 0; n < iterations; n++){
            CSVReader csvReader = CSVFileReader.getCSVReader(trainingFilename);
            currentProgress = (n + 1) * 100 / iterations;

            String[] line;

            while(true){
                try {
                    line = csvReader.readNext();
                    if(line == null) break;

                    train(line[1], Double.parseDouble(line[0]), gradientSum);
                    batchCount++;
                    lineCount++;

                    drawProgressBar(currentProgress, progressBarWidth, lineCount);

                    if (batchCount % batchSize == 0) {
                        updateModel(gradientSum, batchSize);
                        gradientSum = new double[weights.length];
                        batchCount = 0;
                    }
                    System.out.flush();
                }catch (IOException e) {
                    e.printStackTrace();
                }
            }
            csvReader.close();

            if (batchCount > 0) {
                updateModel(gradientSum, batchCount);
            }

        }
        drawProgressBar(currentProgress, progressBarWidth, lineCount);
        System.out.println("\nFinished training");
        saveModel();
    }

    private void train(String statement, double label, double[] gradientSum){

        if(statement == null) return;

        double[] features = embeddingsHandler.getTextEmbedding(statement);
        double predicted = classify(features);

        for (int i = 0; i < weights.length; i++) {
            gradientSum[i] += (label - predicted) * features[i];
        }
    }

    private void updateModel(double[] gradientSum, int batchSize) {
        for (int i = 0; i < weights.length; i++) {
            weights[i] += learningRate * gradientSum[i] / batchSize;
        }
    }

    private double classify(double[] features) {

        double logit = .0;
        for(int i = 0; i < weights.length; i++){
            logit += weights[i] * features[i];
        }
        return sigmoid(logit);
    }

    private void saveModel() throws IOException {
        HashMap<String, Object> dataToAppend = new HashMap<>();

        dataToAppend.put(ApplicationConstants.VERSION, 1.0);
        dataToAppend.put(ApplicationConstants.TRAINING_FILE_NAME, trainingFilename);
        dataToAppend.put(ApplicationConstants.WEIGHTS, weights);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        File jsonFile = new File(outputFilePath);

        objectMapper.writeValue(jsonFile, dataToAppend);

        System.out.println("Appended data: " + objectMapper.writeValueAsString(dataToAppend));
    }

    private void drawProgressBar(int percentage, int width, int lineCount) {
        int numberOfHashes = (int) (percentage / 100.0 * width);

        System.out.print("[");
        for (int i = 0; i < width; i++) {
            if (i < numberOfHashes) {
                System.out.print("#");
            } else {
                System.out.print(" ");
            }
        }
        System.out.print("] " + percentage + "% (" + lineCount + " lines)\r");
    }
}
