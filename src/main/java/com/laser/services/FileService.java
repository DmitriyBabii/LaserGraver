package com.laser.services;

import com.laser.models.InstructionFile;
import com.laser.models.Path;
import com.laser.windows.option_planes.ErrorPlane;
import com.laser.windows.option_planes.WarningPlane;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class FileService {
    private static final String SVG_FILE_NAME = "output.svg";

    private String formatInstruction(String instruction) {
        return instruction.replaceAll("(?<=\\d)(?=[A-z])|(?<=[A-z])(?=\\d)|(?<=[A-z])(?=[A-z])|(?=-)|(,)", " ");
    }

    private String formatRawParameter(String parameter) {
        return parameter.replaceAll("[^0-9.]+", "");
    }

    public Optional<InstructionFile> getInstructionFile(File file) {
        double width, height;
        List<Path> paths = new LinkedList<>();
        File convertedFile;

        try {
            if (checkFileType(file, "pdf")) {
                if (FileConverter.pdfToSvg(file.getAbsolutePath(), SVG_FILE_NAME)) {
                    convertedFile = new File(SVG_FILE_NAME);
                    file = convertedFile;
                } else {
                    throw new RuntimeException("File can`t be converted");
                }
            } else if (!checkFileType(file, "svg")) {
                throw new UnsupportedOperationException("Incorrect file!");
            }

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new InputSource(new FileInputStream(file)));

            // CONFIGURATIONS
            NodeList configure = document.getElementsByTagName("svg");

            if (configure.getLength() != 0) {
                Element svg = (Element) configure.item(0);
                width = Double.parseDouble(formatRawParameter(svg.getAttribute("width")));
                height = Double.parseDouble(formatRawParameter(svg.getAttribute("height")));
            } else {
                throw new IOException("The file does not contain the required data!");
            }

            // INSTRUCTIONS
            NodeList pathList = document.getElementsByTagName("path");

            for (int i = 0; i < pathList.getLength(); i++) {
                Element path = (Element) pathList.item(i);
//                if (!path.getAttribute("transform").isEmpty()) {
//                    continue;
//                }
                String dValue = path.getAttribute("d");
                paths.add(new Path(formatInstruction(dValue)));
            }
            return Optional.of(new InstructionFile(width, height, paths));
        } catch (FileNotFoundException ex) {
            new WarningPlane("File doesn`t exist");
        } catch (UnsupportedOperationException | IOException ex) {
            new WarningPlane(ex.getMessage());
        } catch (RuntimeException ex) {
            new ErrorPlane(ex.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
            new ErrorPlane("Cant`t read the file!!!");
        }

        return Optional.empty();
    }

    private String getFileType(File file) throws IOException {
        if (!file.exists()) {
            throw new IOException("File not found: " + file.getAbsolutePath());
        }

        return Files.probeContentType(file.toPath());
    }

    private boolean checkFileType(File file, String type) throws IOException {
        return getFileType(file).contains(type);
    }
}
