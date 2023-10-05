package com.laser.configurations;

import com.laser.models.enums.CubicBezierAccuracy;
import com.laser.models.enums.Repeat;
import com.laser.models.enums.SerialSpeed;

public class ConnectionConfig {
    public static final SerialSpeed SERIAL_SPEED = SerialSpeed._9600;
    public static final CubicBezierAccuracy CUBIC_BEZIER_ACCURACY = CubicBezierAccuracy._A3;
    public static final Repeat REPEAT = Repeat._1;
    public static final int LASER_SPEED = 5;
}
