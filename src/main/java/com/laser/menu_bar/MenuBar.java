package com.laser.menu_bar;

import com.laser.menu_bar.menus.ConnectionMenu;
import com.laser.menu_bar.menus.EditMenu;
import com.laser.menu_bar.menus.FileMenu;
import com.laser.menu_bar.menus.RunMenu;
import lombok.Getter;

import javax.swing.*;

@Getter
public class MenuBar extends JMenuBar {
    private final FileMenu fileMenu = new FileMenu();
    private final EditMenu editMenu = new EditMenu();
    private final ConnectionMenu connectMenu = new ConnectionMenu();
    private final RunMenu runMenu = new RunMenu();

    public MenuBar() {
        add(fileMenu);
        add(editMenu);
        add(connectMenu);
        add(runMenu);
    }

}
