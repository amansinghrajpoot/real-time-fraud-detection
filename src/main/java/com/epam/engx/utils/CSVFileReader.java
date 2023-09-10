package com.epam.engx.utils;

import com.opencsv.CSVReader;

import java.io.IOException;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CSVFileReader {
    public static CSVReader getCSVReader(String fileName) throws URISyntaxException, IOException {
        Reader reader = Files.newBufferedReader(getFilePath(fileName));
        return new CSVReader(reader);
    }

    private static Path getFilePath(String fileName) throws URISyntaxException {
        URI uri = ClassLoader.getSystemResource(fileName).toURI();
        return Paths.get(uri);
    }
}
