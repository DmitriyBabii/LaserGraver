package com.laser.models;

import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

public class IntegerField extends JTextField {
    public IntegerField(int columns) {
        super(columns);
        setDocument(new NumberDocument());
    }

    private static class NumberDocument extends PlainDocument {
        @Override
        public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
            if (str == null) return;

            String newValue;
            int length = getLength();
            if (length == 0) {
                newValue = str;
            } else {
                String currentText = getText(0, length);
                StringBuilder sb = new StringBuilder(currentText);
                sb.insert(offs, str);
                newValue = sb.toString();
            }

            try {
                Integer.parseInt(newValue);
                super.insertString(offs, str, a);
            } catch (NumberFormatException e) {
                // Ignore invalid input
            }
        }
    }

    public int getValue() {
        return Integer.parseInt(getText());
    }
}
