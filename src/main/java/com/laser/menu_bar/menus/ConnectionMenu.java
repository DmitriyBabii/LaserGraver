package com.laser.menu_bar.menus;

import lombok.Getter;

import javax.swing.*;
import java.awt.event.KeyEvent;

@Getter
public class ConnectionMenu extends JMenu {
    private final JMenuItem port = new JMenuItem("Port");
    private final JMenuItem speed = new JMenuItem("Port speed");
    private final JMenuItem connect = new JMenuItem("Connect");
    private final JMenuItem unConnect = new JMenuItem("Unconnect");

    public ConnectionMenu() {
        super("Connection");

        setMnemonic(KeyEvent.VK_C);

        port.setMnemonic(KeyEvent.VK_P);
        speed.setMnemonic(KeyEvent.VK_S);
        connect.setMnemonic(KeyEvent.VK_C);
        unConnect.setMnemonic(KeyEvent.VK_U);

        connect.setEnabled(false);
        unConnect.setEnabled(false);

        add(port);
        add(speed);
        add(connect);
        add(unConnect);
    }

}
