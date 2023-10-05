package com.laser.windows.option_planes;

import javax.swing.*;

public class WarningPlane {
    public WarningPlane(String message) {
        JOptionPane.showMessageDialog(null, message, "Warning", JOptionPane.WARNING_MESSAGE);
    }
}
