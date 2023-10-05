package com.laser.services.mappers;

import com.laser.models.enums.Repeat;

import java.util.function.Function;

public class RepeatMapper implements Function<Repeat[], Integer[]> {
    @Override
    public Integer[] apply(Repeat[] repeats) {
        Integer[] values = new Integer[repeats.length];
        for (int i = 0; i < values.length; i++) {
            values[i] = repeats[i].getValue();
        }
        return values;
    }
}
