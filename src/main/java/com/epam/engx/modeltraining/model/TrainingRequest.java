package com.epam.engx.modeltraining.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
public class TrainingRequest {
    public double learningRate;
    public int iterations;
    private int featureSize;
}
