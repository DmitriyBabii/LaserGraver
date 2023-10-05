package com.laser.models.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum CubicBezierAccuracy {
    _A1(0.2), _A2(0.1), _A3(0.05), _A4(0.02);

    private final double accuracy;
}
