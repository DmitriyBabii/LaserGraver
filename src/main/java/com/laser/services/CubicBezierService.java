package com.laser.services;

import java.awt.*;

public class CubicBezierService {
    private int startX;
    private int startY;
    private int endX;
    private int endY;
    private int x1;
    private int y1;
    private int x2;
    private int y2;


    public CubicBezierService() {
    }

    public Point getPoint(double t) {
        double pow2 = Math.pow((1.0 - t), 2);
        double pow3 = Math.pow((1.0 - t), 3);
        int Xc = (int) (pow3 * startX + pow2 * 3 * t * x1 + (1.0 - t) * 3 * Math.pow(t, 2) * x2 + Math.pow(t, 3) * endX);
        int Yc = (int) (Math.pow((1.0 - t), 3) * startY + Math.pow((1.0 - t), 2) * 3 * t * y1 + (1.0 - t) * 3 * Math.pow(t, 2) * y2 + Math.pow(t, 3) * endY);
        return new Point(Xc, Yc);
    }

    public void setPoints(int startX, int startY, int endX, int endY) {
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
    }

    public void setFactors(int x1, int y1, int x2, int y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }
}
