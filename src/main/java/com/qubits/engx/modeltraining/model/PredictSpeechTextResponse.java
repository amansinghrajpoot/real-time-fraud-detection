package com.qubits.engx.modeltraining.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PredictSpeechTextResponse {
    private String message;
    private boolean isFraud;
    private double fraudProbability;
}
