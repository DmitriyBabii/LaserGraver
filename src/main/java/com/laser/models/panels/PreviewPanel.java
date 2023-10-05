package com.laser.models.panels;

import com.laser.configurations.ConnectionConfig;
import com.laser.models.InstructionFile;
import com.laser.models.enums.CommandType;
import com.laser.models.enums.CubicBezierAccuracy;
import com.laser.services.CubicBezierService;
import com.laser.windows.option_planes.ErrorPlane;
import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Getter
@Setter
public class PreviewPanel extends JPanel {
    private static final CubicBezierService CUBIC_BEZIER_SERVICE = new CubicBezierService();
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.##");

    private CubicBezierAccuracy cubicBezierAccuracy = ConnectionConfig.CUBIC_BEZIER_ACCURACY;
    private final JFrame parent;
    private double scale = 1.0;
    private int originWidth;
    private int originHeight;
    private final List<String> instructions = new LinkedList<>();

    public PreviewPanel(JFrame parent) {
        // ADD EVENT LISTENERS
        this.parent = parent;
        addMouseEvents();
    }

    private void addMouseEvents() {
        MouseActionListener mouseActionListener = new MouseActionListener();
        addMouseListener(mouseActionListener);
        addMouseMotionListener(mouseActionListener);
        addMouseWheelListener(mouseActionListener);
    }

    public void setInstruction(InstructionFile instructionFile) {
        this.originWidth = instructionFile.getWidth();
        this.originHeight = instructionFile.getHeight();
        this.instructions.clear();
        this.instructions.addAll(instructionFile.getInstructions());
        this.scale = 1.0;
        setBounds(0, 0, originWidth, originHeight);
        setVisible(true);
    }

    public boolean isLoad() {
        return instructions.size() > 0;
    }

    public void clear() {
        this.originWidth = 0;
        this.originHeight = 0;
        this.instructions.clear();
        this.scale = 1.0;
        setVisible(false);
        SwingUtilities.invokeLater(parent::repaint);
    }

    public void setScale(double scale) {
        this.scale = scale;
        if (this.scale < 0.1) {
            this.scale = 0.1;
        } else if (this.scale > 3.0) {
            this.scale = 3;
        }
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        boolean isFirst = true;
        Point startPoint = new Point();
        Point lastPoint = new Point();
        Graphics2D g2D = (Graphics2D) g;
        g2D.scale(scale, scale);

        try {
            for (String instruction : instructions) {

                String[] split = instruction.split(" ");
                if (isFirst) {
                    startPoint = getFirstPoint(split).get();
                    lastPoint.x = startPoint.x;
                    lastPoint.y = startPoint.y;
                    isFirst = false;
                    continue;
                }
                switch (CommandType.valueOf(split[0])) {
                    case M: {
                        int x = Integer.parseInt(split[1]);
                        int y = Integer.parseInt(split[2]);

                        startPoint.x = x;
                        startPoint.y = y;

                        lastPoint.x = x;
                        lastPoint.y = y;
                        break;
                    }
                    case L: {
                        int x = Integer.parseInt(split[1]);
                        int y = Integer.parseInt(split[2]);
                        g2D.drawLine(lastPoint.x, lastPoint.y, x, y);
                        lastPoint.x = x;
                        lastPoint.y = y;
                        break;
                    }
                    case H: {
                        int x = Integer.parseInt(split[1]);
                        g2D.drawLine(lastPoint.x, lastPoint.y, x, lastPoint.y);
                        lastPoint.x = x;
                        break;
                    }
                    case V: {
                        int y = Integer.parseInt(split[1]);
                        g2D.drawLine(lastPoint.x, lastPoint.y, lastPoint.x, y);
                        lastPoint.y = y;
                        break;
                    }
                    case C: {
                        int x1 = Integer.parseInt(split[1]);
                        int y1 = Integer.parseInt(split[2]);
                        int x2 = Integer.parseInt(split[3]);
                        int y2 = Integer.parseInt(split[4]);
                        int endX = Integer.parseInt(split[5]);
                        int endY = Integer.parseInt(split[6]);
                        CUBIC_BEZIER_SERVICE.setFactors(x1, y1, x2, y2);
                        CUBIC_BEZIER_SERVICE.setPoints(lastPoint.x, lastPoint.y, endX, endY);

                        Point bezierPoint;
                        for (double i = 0.0; i <= 1.0; i += cubicBezierAccuracy.getAccuracy()) {
                            i = Double.parseDouble(DECIMAL_FORMAT.format(i).replace(",", "."));
                            bezierPoint = CUBIC_BEZIER_SERVICE.getPoint(i);
                            g2D.drawLine(lastPoint.x, lastPoint.y, bezierPoint.x, bezierPoint.y);
                            lastPoint = bezierPoint;
                        }
                        break;
                    }
                    case Z: {
                        g2D.drawLine(lastPoint.x, lastPoint.y, startPoint.x, startPoint.y);
                        lastPoint.x = startPoint.x;
                        lastPoint.y = startPoint.y;
                        break;
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            new ErrorPlane("Incorrect instruction!");
            this.clear();
            SwingUtilities.invokeLater(parent::repaint);
        } finally {
            g2D.dispose();
        }
    }

    private Optional<Point> getFirstPoint(String[] split) {
        if (CommandType.valueOf(split[0]) == CommandType.M) {
            return Optional.of(new Point(Integer.parseInt(split[1]), Integer.parseInt(split[2])));
        }
        return Optional.empty();
    }

    private class MouseActionListener extends MouseAdapter {
        private Point prevPt;
        private Point prevMousePt;

        @Override
        public void mousePressed(MouseEvent e) {
            prevPt = getLocation();
            prevMousePt = e.getPoint();
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            Point currentMousePt = e.getPoint();
            int dx = currentMousePt.x - prevMousePt.x;
            int dy = currentMousePt.y - prevMousePt.y;
            setLocation(prevPt.x + dx, prevPt.y + dy);
            prevPt = getLocation();
        }
    }
}
