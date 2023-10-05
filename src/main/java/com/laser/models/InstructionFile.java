package com.laser.models;

import com.laser.models.enums.CommandType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Getter
@Setter
@ToString
public class InstructionFile {
    private static final double VALUE_COEFFICIENT = 1.769911;

    private double width;
    private double height;
    private List<Path> paths;

    public InstructionFile(double width, double height, List<Path> pathInstructions) {
        this.width = width * VALUE_COEFFICIENT;
        this.height = height * VALUE_COEFFICIENT;
        this.paths = pathInstructions;
    }

    public List<String> getInstructions() {
        List<String> instructions = new LinkedList<>();

        boolean isFirst = true;
        Point point = new Point();
        CommandType lastCommand = CommandType.M;

        for (Path path : paths) {
            String[] splitPath = removeEmptyStrings(path.getPath().toUpperCase().split(" "));
            for (int i = 0; i < splitPath.length; i++) {

                if (isFirst && splitPath[i].equals(CommandType.M.name())) {
                    point.x = parseValue(splitPath[i + 1]);
                    point.y = parseValue(splitPath[i + 2]);
                    instructions.add(String.format("%s %d %d", CommandType.M, point.x, point.y));
                    lastCommand = CommandType.L;
                    i += 2;
                    isFirst = false;
                } else {
                    try {
                        switch (lastCommand) {
                            case M: {
                                setParsedPoint(point, splitPath, i);
                                instructions.add(CommandType.E.name());
                                instructions.add(String.format("%s %d %d", lastCommand, point.x, point.y));
                                i++;
                                break;
                            }
                            case L: {
                                setParsedPoint(point, splitPath, i);
                                instructions.add(String.format("%s %d %d", lastCommand, point.x, point.y));
                                i++;
                                break;
                            }
                            case H: {
                                point.x = parseValue(splitPath[i]);
                                instructions.add(String.format("%s %d", lastCommand, point.x));
                                break;
                            }
                            case V: {
                                point.y = parseValue(splitPath[i]);
                                instructions.add(String.format("%s %d", lastCommand, point.y));
                                break;
                            }
                            case C: {
                                setParsedPoint(point, splitPath, i);
                                instructions.add(
                                        String.format(
                                                "%s %d %d %d %d %d %d", lastCommand, point.x, point.y,
                                                parseValue(splitPath[i + 2]), parseValue(splitPath[i + 3]),
                                                parseValue(splitPath[i + 4]), parseValue(splitPath[i + 5])
                                        )
                                );
                                i += 5;
                                break;
                            }
                            case Z: {
                                instructions.add(lastCommand.name());
                                instructions.add(CommandType.E.name());
                                lastCommand = CommandType.M;
                                break;
                            }
                        }
                    } catch (Exception ex) {
                        try {
                            lastCommand = CommandType.valueOf(splitPath[i]);
                            if (lastCommand == CommandType.Z) {
                                i--;
                            }
                        } catch (RuntimeException rx) {
                            rx.printStackTrace();
                        }
                    }
                }
            }
            instructions.add(CommandType.E.name());
            isFirst = true;
        }
        instructions.add(CommandType.R.name());
        return instructions;
    }

    private int parseValue(String rawValue) {
        return (int) Math.round(Double.parseDouble(rawValue) * VALUE_COEFFICIENT);
    }

    private String[] removeEmptyStrings(String[] strings) {
        List<String> nonEmptyStrings = new ArrayList<>();
        for (int i = 0; i < strings.length; i++) {
            strings[i] = strings[i].trim();
            if (!strings[i].isEmpty()) {
                nonEmptyStrings.add(strings[i]);
            }
        }
        return nonEmptyStrings.toArray(new String[0]);
    }

    private void setParsedPoint(Point point, String[] splitPath, int currentIndex) {
        point.x = parseValue(splitPath[currentIndex]);
        point.y = parseValue(splitPath[currentIndex + 1]);
    }

    public int getWidth() {
        return (int) Math.round(width);
    }

    public int getHeight() {
        return (int) Math.round(height);
    }
}
