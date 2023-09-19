package com.qubits.engx.modeltraining.validate;


import com.opencsv.CSVReader;
import com.qubits.engx.utils.CSVFileReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;

public class ValidateCsvTrainingData {

    private Logger logger = LoggerFactory.getLogger(ValidateCsvTrainingData.class);
    public void validateCSV(String filePath) throws IOException, URISyntaxException {

        logger.info("Started validating "+ filePath);

        CSVReader csvReader = CSVFileReader.getCSVReader(filePath);

        int lineNumber = 0; // Initialize the line number

        String[] record;
        while ((record = csvReader.readNext()) != null) {
            lineNumber++; // Increment the line number


            if (record.length != 2) {
                throw new IllegalArgumentException("Invalid number of columns at line " + lineNumber +
                        ": Each row should have exactly two columns.");
            }

            String column1 = record[0].trim();
            String column2 = record[1].trim();

            if (column1.isEmpty() || column2.isEmpty()) {
                throw new IllegalArgumentException("Empty column(s) at line " + lineNumber +
                        ": Neither of the columns in a row should be empty.");
            }

            try {
                int number = Integer.parseInt(column1);
                if (number < 0 || number > 1) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid integer at line " + lineNumber +
                        ": The first column should be a valid integer.");
            }
        }
        csvReader.close();
        logger.info("finished validating "+ filePath);
    }
}
