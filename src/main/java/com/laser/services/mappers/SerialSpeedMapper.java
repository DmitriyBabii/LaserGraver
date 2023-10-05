package com.laser.services.mappers;

import com.laser.models.enums.SerialSpeed;

import java.util.function.Function;

public class SerialSpeedMapper implements Function<SerialSpeed[], Integer[]> {
    @Override
    public Integer[] apply(SerialSpeed[] serialSpeeds) {
        Integer[] serialSpeedValues = new Integer[serialSpeeds.length];
        for (int i = 0; i < serialSpeeds.length; i++) {
            serialSpeedValues[i] = serialSpeeds[i].getSpeed();
        }
        return serialSpeedValues;
    }
}
