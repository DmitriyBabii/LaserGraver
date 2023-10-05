package com.laser.menu_bar.menus;

import lombok.Getter;

import javax.swing.*;
import java.awt.event.KeyEvent;

@Getter
public class FileMenu extends JMenu {
    private final JMenuItem open = new JMenuItem("Open");
    private final JMenuItem close = new JMenuItem("Close");
    private final JMenuItem exit = new JMenuItem("Exit");

    public FileMenu() {
        super("File");

        setMnemonic(KeyEvent.VK_F);

        open.setMnemonic(KeyEvent.VK_O);
        close.setMnemonic(KeyEvent.VK_C);
        exit.setMnemonic(KeyEvent.VK_E);

        add(open);
        add(close);
        add(exit);
    }
}
