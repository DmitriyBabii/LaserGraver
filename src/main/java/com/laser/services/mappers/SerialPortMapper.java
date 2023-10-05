package com.laser.services.mappers;

import com.fazecast.jSerialComm.SerialPort;

import java.util.function.Function;

public class SerialPortMapper implements Function<SerialPort[], String[]> {
    @Override
    public String[] apply(SerialPort[] ports) {
        String[] systemPortNames = new String[ports.length];
        for (int i = 0; i < ports.length; i++) {
            systemPortNames[i] = ports[i].getSystemPortName();
        }
        return systemPortNames;
    }
}
