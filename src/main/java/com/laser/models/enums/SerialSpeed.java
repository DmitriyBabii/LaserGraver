package com.laser.models.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum SerialSpeed {
    _1200(1200), _2400(2400), _4800(4800), _9600(9600), _19200(19200), _38400(38400), _115200(115200);

    private final int speed;
}
