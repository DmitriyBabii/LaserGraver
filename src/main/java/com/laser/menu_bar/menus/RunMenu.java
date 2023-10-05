package com.laser.menu_bar.menus;

import lombok.Getter;

import javax.swing.*;
import java.awt.event.KeyEvent;

@Getter
public class RunMenu extends JMenu {
    private final JMenuItem run = new JMenuItem("Run");
    private final JMenuItem stop = new JMenuItem("Stop");

    public RunMenu() {
        super("Run");
        setMnemonic(KeyEvent.VK_R);

        run.setMnemonic(KeyEvent.VK_R);
        stop.setMnemonic(KeyEvent.VK_S);
        run.setEnabled(false);
        stop.setEnabled(false);

        add(run);
        add(stop);
    }
}
