package com.laser.services.mappers;

import com.laser.models.enums.CubicBezierAccuracy;

import java.util.function.Function;

public class CubicBezierAccuracyMapper implements Function<CubicBezierAccuracy[], Double[]> {
    @Override
    public Double[] apply(CubicBezierAccuracy[] cubicBezierAccuracies) {
        Double[] values = new Double[cubicBezierAccuracies.length];
        for (int i = 0; i < cubicBezierAccuracies.length; i++) {
            values[i] = cubicBezierAccuracies[i].getAccuracy();
        }
        return values;
    }
}
