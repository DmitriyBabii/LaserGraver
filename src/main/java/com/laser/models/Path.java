package com.laser.models;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class Path {
    private final String path;

    public Path(String pathInstruction) {
        this.path = pathInstruction;
    }
}
