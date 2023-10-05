package com.laser.windows.option_planes;

import javax.swing.*;

public class InfoPlane {
    public InfoPlane(String message) {
        JOptionPane.showMessageDialog(null, message, "Info", JOptionPane.INFORMATION_MESSAGE);
    }
}
