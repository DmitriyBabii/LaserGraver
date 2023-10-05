package com.laser.windows;

import com.fazecast.jSerialComm.SerialPort;
import com.laser.Main;
import com.laser.menu_bar.MenuBar;
import com.laser.menu_bar.menus.ConnectionMenu;
import com.laser.menu_bar.menus.EditMenu;
import com.laser.menu_bar.menus.FileMenu;
import com.laser.menu_bar.menus.RunMenu;
import com.laser.models.IntegerField;
import com.laser.models.enums.CommandType;
import com.laser.models.enums.CubicBezierAccuracy;
import com.laser.models.enums.Repeat;
import com.laser.models.enums.SerialSpeed;
import com.laser.models.panels.ContentPanel;
import com.laser.models.panels.PreviewPanel;
import com.laser.services.FileService;
import com.laser.services.LaserConnection;
import com.laser.services.mappers.CubicBezierAccuracyMapper;
import com.laser.services.mappers.RepeatMapper;
import com.laser.services.mappers.SerialPortMapper;
import com.laser.services.mappers.SerialSpeedMapper;
import com.laser.windows.option_planes.InfoPlane;
import com.laser.windows.option_planes.WarningPlane;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.InputStream;

public class MainFrame extends JFrame {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 500;
    private static final double SCALE_FACTOR = 0.05;
    private static final FileService FILE_SERVICE = new FileService();
    private static final LaserConnection LASER_CONNECTION = new LaserConnection();
    private static final MenuBar MENU_BAR = new MenuBar();
    private static final ContentPanel CONTENT_PANEL = new ContentPanel();
    private RunThread runThread = new RunThread();
    private final PreviewPanel previewPanel = new PreviewPanel(this);

    public MainFrame() {
        // INIT
        setTitle("Laser Engraver");
        setSize(WIDTH, HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        addWindowListener(new ExitAction());
        setJMenuBar(MENU_BAR);
        setLayout(new BorderLayout());

        // ADD CONTENT
        try {
            InputStream inputStream = Main.class.getClassLoader().getResourceAsStream("logo.png");
            assert inputStream != null;
            Image image = ImageIO.read(inputStream);
            setIconImage(image);
        } catch (Exception ex) {
            new WarningPlane("No icon");
        }

        CONTENT_PANEL.add(previewPanel);
        add(CONTENT_PANEL, BorderLayout.CENTER);

        // ADD EVENT LISTENERS
        MouseActionListener mouseActionListener = new MouseActionListener();
        addMouseWheelListener(mouseActionListener);
        addActionListeners();
        addChangeListeners();

        // SHOW
        setVisible(true);
    }

    private void addActionListeners() {
        // FILE MENU
        FileMenu fileMenu = MENU_BAR.getFileMenu();
        fileMenu.getOpen().addActionListener(new OpenAction(this));
        fileMenu.getClose().addActionListener(event -> previewPanel.clear());
        fileMenu.getExit().addActionListener(event -> System.exit(0));


        // EDIT MENU
        EditMenu editMenu = MENU_BAR.getEditMenu();
        editMenu.getAccuracy().addActionListener(new ChooseCubicBezierAccuracyAction());
        editMenu.getRepeat().addActionListener(new ChooseRepeatAction());
        editMenu.getSpeed().addActionListener(new ChooseSpeedAction());
        editMenu.getPreview().addActionListener(event -> LASER_CONNECTION.setPreview(!LASER_CONNECTION.isPreview()));
        editMenu.getLaserOn().addActionListener(event -> LASER_CONNECTION.setLaser(!LASER_CONNECTION.isLaser()));

        // CONNECTION MENU
        ConnectionMenu connectionMenu = MENU_BAR.getConnectMenu();
        connectionMenu.getConnect().addActionListener(new ConnectAction());
        connectionMenu.getUnConnect().addActionListener(event -> LASER_CONNECTION.close());
        connectionMenu.getPort().addActionListener(new ChoosePortAction());
        connectionMenu.getSpeed().addActionListener(new ChoosePortSpeedAction());

        // RUN MENU
        RunMenu runMenu = MENU_BAR.getRunMenu();
        runMenu.getRun().addActionListener(new RunAction());
        runMenu.getStop().addActionListener(new StopAction());
    }

    private void addChangeListeners() {
        ConnectionMenu connectionMenu = MENU_BAR.getConnectMenu();
        JMenuItem port = connectionMenu.getPort();
        JMenuItem connectionSpeed = connectionMenu.getSpeed();
        JMenuItem connect = connectionMenu.getConnect();
        JMenuItem unConnect = connectionMenu.getUnConnect();
        connectionMenu.addChangeListener(new ConnectionMenuChangeListener(port, connectionSpeed, connect, unConnect));

        EditMenu editMenu = MENU_BAR.getEditMenu();
        JMenuItem accuracy = editMenu.getAccuracy();
        JMenuItem repeat = editMenu.getRepeat();
        JMenuItem laserSpeed = editMenu.getSpeed();
        JMenuItem preview = editMenu.getPreview();
        JMenuItem laserOn = editMenu.getLaserOn();
        editMenu.addChangeListener(new EditMenuChangeListener(accuracy, repeat, laserSpeed, preview, laserOn));

        RunMenu runMenu = MENU_BAR.getRunMenu();
        JMenuItem run = runMenu.getRun();
        JMenuItem stop = runMenu.getStop();
        runMenu.addChangeListener(new RunMenuListener(run, stop));
    }

    /**
     * WORKFLOW
     */
    private class MouseActionListener extends MouseAdapter {
        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {
            int notches = e.getWheelRotation();
            if (notches < 0) {
                previewPanel.setScale(previewPanel.getScale() + SCALE_FACTOR);
            } else {
                previewPanel.setScale(previewPanel.getScale() - SCALE_FACTOR);
            }

            int newWidth = (int) (previewPanel.getOriginWidth() * previewPanel.getScale());
            int newHeight = (int) (previewPanel.getOriginHeight() * previewPanel.getScale());
            previewPanel.setSize(newWidth, newHeight);
        }
    }

    /**
     * FILE
     */
    @RequiredArgsConstructor
    private class OpenAction implements ActionListener {
        private final JFrame parent;

        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser fileChooser = new JFileChooser();
            if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                FILE_SERVICE.getInstructionFile(
                        new File(fileChooser.getSelectedFile().getAbsoluteFile().toURI())
                ).ifPresent(instructionFile -> {
                    previewPanel.clear();
                    previewPanel.setInstruction(instructionFile);
                    System.out.println(previewPanel.getInstructions());
                    parent.repaint();
                });
            }
        }
    }


    /**
     * EDIT
     */
    private class ChooseCubicBezierAccuracyAction implements ActionListener {
        private final CubicBezierAccuracyMapper ACCURACY_MAPPER = new CubicBezierAccuracyMapper();

        @Override
        public void actionPerformed(ActionEvent e) {
            CubicBezierAccuracy[] accuracies = CubicBezierAccuracy.values();

            Double[] values = ACCURACY_MAPPER.apply(accuracies);

            JComboBox<Double> jComboBox = new JComboBox<>(values);
            jComboBox.setSelectedIndex(previewPanel.getCubicBezierAccuracy().ordinal());

            JPanel panel = new JPanel();
            panel.setLayout(new GridLayout());
            panel.add(jComboBox);

            int option = JOptionPane.showConfirmDialog(null, panel, "Select cubic bezier accuracy",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (option == JOptionPane.OK_OPTION) {
                CubicBezierAccuracy accuracy = accuracies[jComboBox.getSelectedIndex()];
                LASER_CONNECTION.setCubicBezierAccuracy(accuracy);
                previewPanel.setCubicBezierAccuracy(accuracy);
                SwingUtilities.invokeLater(previewPanel::repaint);
            }
        }
    }

    private class ChooseRepeatAction implements ActionListener {
        private final RepeatMapper REPEAT_MAPPER = new RepeatMapper();

        @Override
        public void actionPerformed(ActionEvent e) {
            Repeat[] repeats = Repeat.values();
            Integer[] values = REPEAT_MAPPER.apply(repeats);

            JComboBox<Integer> jComboBox = new JComboBox<>(values);
            jComboBox.setSelectedIndex(LASER_CONNECTION.getRepeat().ordinal());

            JPanel panel = new JPanel();
            panel.setLayout(new GridLayout());
            panel.add(jComboBox);

            int option = JOptionPane.showConfirmDialog(null, panel, "Select count of repeat",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (option == JOptionPane.OK_OPTION) {
                Repeat repeat = repeats[jComboBox.getSelectedIndex()];
                LASER_CONNECTION.setRepeat(repeat);
                SwingUtilities.invokeLater(previewPanel::repaint);
            }
        }
    }

    private static class ChooseSpeedAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            IntegerField integerField = new IntegerField(1);

            JPanel panel = new JPanel();
            panel.setLayout(new GridLayout());
            panel.add(integerField);

            int option = JOptionPane.showConfirmDialog(null, panel, "Enter laser power",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (option == JOptionPane.OK_OPTION) {
                LASER_CONNECTION.setLaserSpeed(integerField.getValue());
            }
        }
    }

    @RequiredArgsConstructor
    private static class EditMenuChangeListener implements ChangeListener {
        private final JMenuItem accuracy;
        private final JMenuItem repeat;
        private final JMenuItem speed;
        private final JMenuItem preview;
        private final JMenuItem laserOn;

        @Override
        public void stateChanged(ChangeEvent e) {
            accuracy.setText("Cubic bezier accuracy: " + LASER_CONNECTION.getCubicBezierAccuracy().getAccuracy());
            repeat.setText("Repeat: " + LASER_CONNECTION.getRepeat().getValue());
            speed.setText("Laser power: " + LASER_CONNECTION.getLaserSpeed());
            preview.setText("Preview: " + (LASER_CONNECTION.isPreview() ? "ON" : "OFF"));
            laserOn.setText("Laser: " + (LASER_CONNECTION.isLaser() ? "ON" : "OFF"));

            laserOn.setEnabled(LASER_CONNECTION.isOpen());
        }
    }


    /**
     * CONNECTION
     */
    private static class ChoosePortAction implements ActionListener {
        private static final SerialPortMapper SERIAL_PORT_MAPPER = new SerialPortMapper();

        @Override
        public void actionPerformed(ActionEvent e) {
            SerialPort[] ports = SerialPort.getCommPorts();
            if (ports.length == 0) {
                new InfoPlane("There are no connected devices");
                LASER_CONNECTION.setSerialPort(null);
                return;
            }

            String[] systemPortNames = SERIAL_PORT_MAPPER.apply(ports);

            JComboBox<String> jComboBox = new JComboBox<>(systemPortNames);
            JPanel panel = new JPanel();
            panel.setLayout(new GridLayout());
            panel.add(jComboBox);

            int option = JOptionPane.showConfirmDialog(null, panel, "Select port",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (option == JOptionPane.OK_OPTION) {
                SerialPort serialPort = ports[jComboBox.getSelectedIndex()];
                LASER_CONNECTION.setSerialPort(serialPort);
                System.out.printf("%s(%s)%n", serialPort, serialPort.getSystemPortName());
            }
        }

    }

    private static class ChoosePortSpeedAction implements ActionListener {
        private static final SerialSpeedMapper SERIAL_SPEED_MAPPER = new SerialSpeedMapper();

        @Override
        public void actionPerformed(ActionEvent e) {
            Integer[] serialSpeedValues = SERIAL_SPEED_MAPPER.apply(SerialSpeed.values());

            JComboBox<Integer> jComboBox = new JComboBox<>(serialSpeedValues);
            jComboBox.setSelectedIndex(LASER_CONNECTION.getSerialSpeed().ordinal());

            JPanel panel = new JPanel();
            panel.setLayout(new GridLayout());
            panel.add(jComboBox);

            int option = JOptionPane.showConfirmDialog(null, panel, "Select port speed",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (option == JOptionPane.OK_OPTION) {
                SerialSpeed serialSpeed = SerialSpeed.values()[jComboBox.getSelectedIndex()];
                System.out.println(serialSpeed);
                LASER_CONNECTION.setSerialSpeed(serialSpeed);
            }
        }

    }

    private static class ConnectAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (!(LASER_CONNECTION.isReady() && LASER_CONNECTION.open())) {
                new WarningPlane("Can`t connect to the port. Port is busy or doesn`t exist.");
            }
        }
    }

    @RequiredArgsConstructor
    private static class ConnectionMenuChangeListener implements ChangeListener {
        private final JMenuItem port;
        private final JMenuItem speed;
        private final JMenuItem connect;
        private final JMenuItem unConnect;

        @Override
        public void stateChanged(ChangeEvent e) {
            SerialPort serialPort = LASER_CONNECTION.getSerialPort();
            port.setText(
                    (serialPort == null) ? "Port" : "Port: " + serialPort.getSystemPortName()
            );
            speed.setText("Port speed: " + LASER_CONNECTION.getSerialSpeed().getSpeed());
            connect.setEnabled(LASER_CONNECTION.isReady() && !LASER_CONNECTION.isOpen());
            unConnect.setEnabled(LASER_CONNECTION.isOpen());
        }
    }

    /**
     * RUN
     */
    private class RunAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            System.out.println(previewPanel.getInstructions());
            runThread.start();
        }
    }

    private class StopAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            runThread.setStop(true);
        }
    }

    @RequiredArgsConstructor
    private class RunMenuListener implements ChangeListener {
        private final JMenuItem run;
        private final JMenuItem stop;

        @Override
        public void stateChanged(ChangeEvent e) {
            if (runThread.wasRun && !runThread.isAlive()) {
                runThread = new RunThread();
            }
            run.setEnabled(LASER_CONNECTION.isOpen() && previewPanel.isLoad() && !runThread.isAlive());
            stop.setEnabled(runThread.isAlive());
        }
    }

    @Getter
    @Setter
    private class RunThread extends Thread {
        private boolean wasRun = false;
        private boolean stop = false;

        @Override
        public synchronized void start() {
            stop = false;
            wasRun = true;
            super.start();
        }

        @Override
        public void run() {
            LASER_CONNECTION.sendConfigure();
            for (int i = 0; i < LASER_CONNECTION.getRepeat().getValue() && !stop; i++) {
                previewPanel.getInstructions().forEach(instruction -> {
                    if (!stop) {
                        LASER_CONNECTION.sendOnce(
                                LASER_CONNECTION.isPreview() ?
                                        instruction.replace("M", "L") : instruction
                        );
                    }
                });
            }
            if (stop) {
                LASER_CONNECTION.sendOnce(CommandType.R.name());
                stop = false;
            }
        }
    }

    /**
     * EXIT
     */
    private static class ExitAction extends WindowAdapter {
        @Override
        public void windowClosing(WindowEvent e) {
            if (LASER_CONNECTION.isOpen()) {
                LASER_CONNECTION.close();
            }
            System.out.println("Exit");
            System.exit(0);
        }
    }
}
