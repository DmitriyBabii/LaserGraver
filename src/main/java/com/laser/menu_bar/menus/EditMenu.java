package com.laser.menu_bar.menus;

import lombok.Getter;

import javax.swing.*;
import java.awt.event.KeyEvent;

@Getter
public class EditMenu extends JMenu {
    private final JMenuItem accuracy = new JMenuItem("Cubic bezier accuracy");
    private final JMenuItem repeat = new JMenuItem("Repeat");
    private final JMenuItem speed = new JMenuItem("Laser power");
    private final JMenuItem preview = new JMenuItem("Preview");
    private final JMenuItem laserOn = new JMenuItem("Laser");

    public EditMenu() {
        super("Edit");
        setMnemonic(KeyEvent.VK_E);

        accuracy.setMnemonic(KeyEvent.VK_C);
        repeat.setMnemonic(KeyEvent.VK_R);
        speed.setMnemonic(KeyEvent.VK_S);
        preview.setMnemonic(KeyEvent.VK_P);
        laserOn.setMnemonic(KeyEvent.VK_L);

        laserOn.setEnabled(false);

        add(accuracy);
        add(repeat);
        add(speed);
        add(preview);
        add(laserOn);
    }
}
