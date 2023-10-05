package com.laser.services;

import java.io.IOException;

public class FileConverter {
    public static boolean pdfToSvg(String inputFile, String outputFile) throws IOException, InterruptedException {
        String[] cmd = {".\\pdf2svg\\pdf2svg", inputFile, outputFile};
        ProcessBuilder processBuilder = new ProcessBuilder(cmd);

        Process process = processBuilder.start();

        return process.waitFor() == 0;
    }
}
