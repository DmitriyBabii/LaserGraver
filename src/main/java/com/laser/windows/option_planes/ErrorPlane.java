package com.laser.windows.option_planes;

import javax.swing.*;

public class ErrorPlane {
    public ErrorPlane(String message) {
        JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
