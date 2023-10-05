package com.laser.services;

import com.fazecast.jSerialComm.SerialPort;
import com.laser.configurations.ConnectionConfig;
import com.laser.models.enums.CubicBezierAccuracy;
import com.laser.models.enums.Repeat;
import com.laser.models.enums.SerialSpeed;
import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.atomic.AtomicBoolean;

@Getter
@Setter
public class LaserConnection {
    private static final int WRITE_DELAY = 20;
    private static final int READ_DELAY = 10;

    private int laserSpeed = ConnectionConfig.LASER_SPEED;
    private CubicBezierAccuracy cubicBezierAccuracy = ConnectionConfig.CUBIC_BEZIER_ACCURACY;
    private Repeat repeat = ConnectionConfig.REPEAT;

    private SerialPort serialPort;
    private SerialSpeed serialSpeed = ConnectionConfig.SERIAL_SPEED;
    private boolean preview = true;
    private boolean laser = false;

    public boolean open() {
        if (serialPort == null) {
            throw new RuntimeException("Port is undefined");
        }
        this.serialPort.setBaudRate(this.serialSpeed.getSpeed());
        if (serialPort.openPort()) {
            sendUntil("I C", 3000);
            return true;
        }
        return false;
    }

    public boolean isOpen() {
        if (serialPort == null) {
            return false;
        }
        return serialPort.isOpen();
    }

    public void close() {
        if (serialPort == null) {
            throw new RuntimeException("Port is undefined");
        }
        if (isOpen()) {
            sendUntil("I UC", 3000);
        }
        serialPort.closePort();
    }

    public boolean isReady() {
        return serialPort != null && serialSpeed != null;
    }

    public void sendConfigure() {
        sendUntil("I A " + cubicBezierAccuracy.getAccuracy(), 3000);
        sendUntil("I S " + laserSpeed, 3000);
    }

    public void sendUntil(String data, long timeOutMillis) {
        long timerStart = System.currentTimeMillis();
        final AtomicBoolean flag = new AtomicBoolean(true);

        Thread readThread = new Thread(() -> {
            byte[] readBuffer = new byte[1024];
            while (flag.get()) {
                int numBytes = serialPort.readBytes(readBuffer, readBuffer.length);
                if (numBytes > 0) {
                    String receivedData = new String(readBuffer, 0, numBytes);
                    System.out.println("Received data from Arduino: " + receivedData);
                    if (receivedData.equals("O")) {
                        flag.set(false);
                        System.out.println("Done");
                    }
                }
                sleep(READ_DELAY);
            }
        });

        Thread writeThread = new Thread(() -> {
            byte[] dataBytes = data.getBytes();
            while (flag.get()) {
                System.out.println(data);
                serialPort.writeBytes(dataBytes, dataBytes.length);
                sleep(WRITE_DELAY);
            }
        });

        writeThread.start();
        readThread.start();

        while (flag.get() && (System.currentTimeMillis() - timerStart < timeOutMillis)) {
            sleep(WRITE_DELAY);
        }

        flag.set(false);
    }

    public void sendOnce(String data) {
        byte[] dataBytes = data.getBytes();
        System.out.println(data);
        serialPort.writeBytes(dataBytes, dataBytes.length);
        sleep(WRITE_DELAY);


        byte[] readBuffer = new byte[1024];
        int numBytes = serialPort.readBytes(readBuffer, readBuffer.length);

        while (numBytes <= 0) {
            sleep(READ_DELAY);          // update every READ_DELAY millis
            numBytes = serialPort.readBytes(readBuffer, readBuffer.length);
        }

        String receivedData = new String(readBuffer, 0, numBytes);
        System.out.println("Received data from Arduino: " + receivedData);
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void setLaser(boolean laser) {
        if (!isOpen()) {
            return;
        }
        if (laser) {
            sendUntil("N", 3000);
        } else {
            sendUntil("E", 3000);
        }
        this.laser = laser;
    }
}
