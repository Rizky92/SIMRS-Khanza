package widget;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Objects;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

public class Numpad extends Panel {

    private JTextField textBox;
    private int fontSize = 36;
    private long textLimit = Long.MAX_VALUE;
    private boolean skdpMode = false;

    public Numpad() {
        initPanelNumpad();
    }

    public void setTextBox(JTextField textBox) {
        Objects.requireNonNull(textBox);
        this.textBox = textBox;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
        initPanelNumpad();
    }

    public void setTextLimit(long textLimit) {
        if (textLimit <= -1) {
            this.textLimit = Long.MAX_VALUE;
        } else {
            this.textLimit = textLimit;
        }
    }

    public long getTextLimit() {
        return this.textLimit;
    }

    public void setSkdpMode(boolean mode) {
        this.skdpMode = mode;
        initPanelNumpad();
    }

    public boolean getSkdpMode() {
        return this.skdpMode;
    }

    private void initPanelNumpad() {
        removeAll();

        GridBagConstraints gbc;
        Font font = new Font("Inter", Font.BOLD, this.fontSize);
        Color background = new Color(204, 251, 241);
        Color foreground = new Color(0, 131, 62);
        Insets inset = new Insets(2, 2, 2, 2);

        int buttonSize = (this.fontSize * 2) + (this.fontSize / 4) + (this.fontSize / 8);

        setLayout(new GridBagLayout());

        com.formdev.flatlaf.extras.components.FlatButton btnAngka7 = new com.formdev.flatlaf.extras.components.FlatButton();
        btnAngka7.setText("7");
        btnAngka7.setFont(font);
        btnAngka7.setBackground(background);
        btnAngka7.setForeground(foreground);
        btnAngka7.setFocusable(false);
        btnAngka7.setMinimumSize(new Dimension(buttonSize, buttonSize));
        btnAngka7.setPreferredSize(new Dimension(buttonSize, buttonSize));
        btnAngka7.setHorizontalTextPosition(SwingConstants.CENTER);
        btnAngka7.addActionListener(evt -> addNumber("7"));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.insets = inset;
        add(btnAngka7, gbc);

        com.formdev.flatlaf.extras.components.FlatButton btnAngka8 = new com.formdev.flatlaf.extras.components.FlatButton();
        btnAngka8.setText("8");
        btnAngka8.setFont(font);
        btnAngka8.setBackground(background);
        btnAngka8.setForeground(foreground);
        btnAngka8.setFocusable(false);
        btnAngka8.setMinimumSize(new Dimension(buttonSize, buttonSize));
        btnAngka8.setPreferredSize(new Dimension(buttonSize, buttonSize));
        btnAngka8.setHorizontalTextPosition(SwingConstants.CENTER);
        btnAngka8.addActionListener(evt -> addNumber("8"));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.insets = inset;
        add(btnAngka8, gbc);

        com.formdev.flatlaf.extras.components.FlatButton btnAngka9 = new com.formdev.flatlaf.extras.components.FlatButton();
        btnAngka9.setText("9");
        btnAngka9.setFont(font);
        btnAngka9.setBackground(background);
        btnAngka9.setForeground(foreground);
        btnAngka9.setFocusable(false);
        btnAngka9.setMinimumSize(new Dimension(buttonSize, buttonSize));
        btnAngka9.setPreferredSize(new Dimension(buttonSize, buttonSize));
        btnAngka9.setHorizontalTextPosition(SwingConstants.CENTER);
        btnAngka9.addActionListener(evt -> addNumber("9"));
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.insets = inset;
        add(btnAngka9, gbc);

        com.formdev.flatlaf.extras.components.FlatButton btnAngka4 = new com.formdev.flatlaf.extras.components.FlatButton();
        btnAngka4.setText("4");
        btnAngka4.setFont(font);
        btnAngka4.setBackground(background);
        btnAngka4.setForeground(foreground);
        btnAngka4.setFocusable(false);
        btnAngka4.setMinimumSize(new Dimension(buttonSize, buttonSize));
        btnAngka4.setPreferredSize(new Dimension(buttonSize, buttonSize));
        btnAngka4.setHorizontalTextPosition(SwingConstants.CENTER);
        btnAngka4.addActionListener(evt -> addNumber("4"));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.insets = inset;
        add(btnAngka4, gbc);

        com.formdev.flatlaf.extras.components.FlatButton btnAngka5 = new com.formdev.flatlaf.extras.components.FlatButton();
        btnAngka5.setText("5");
        btnAngka5.setFont(font);
        btnAngka5.setBackground(background);
        btnAngka5.setForeground(foreground);
        btnAngka5.setFocusable(false);
        btnAngka5.setMinimumSize(new Dimension(buttonSize, buttonSize));
        btnAngka5.setPreferredSize(new Dimension(buttonSize, buttonSize));
        btnAngka5.setHorizontalTextPosition(SwingConstants.CENTER);
        btnAngka5.addActionListener(evt -> addNumber("5"));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.insets = inset;
        add(btnAngka5, gbc);

        com.formdev.flatlaf.extras.components.FlatButton btnAngka6 = new com.formdev.flatlaf.extras.components.FlatButton();
        btnAngka6.setText("6");
        btnAngka6.setFont(font);
        btnAngka6.setBackground(background);
        btnAngka6.setForeground(foreground);
        btnAngka6.setFocusable(false);
        btnAngka6.setMinimumSize(new Dimension(buttonSize, buttonSize));
        btnAngka6.setPreferredSize(new Dimension(buttonSize, buttonSize));
        btnAngka6.setHorizontalTextPosition(SwingConstants.CENTER);
        btnAngka6.addActionListener(evt -> addNumber("6"));
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.insets = inset;
        add(btnAngka6, gbc);

        com.formdev.flatlaf.extras.components.FlatButton btnAngka1 = new com.formdev.flatlaf.extras.components.FlatButton();
        btnAngka1.setText("1");
        btnAngka1.setFont(font);
        btnAngka1.setBackground(background);
        btnAngka1.setForeground(foreground);
        btnAngka1.setFocusable(false);
        btnAngka1.setMinimumSize(new Dimension(buttonSize, buttonSize));
        btnAngka1.setPreferredSize(new Dimension(buttonSize, buttonSize));
        btnAngka1.setHorizontalTextPosition(SwingConstants.CENTER);
        btnAngka1.addActionListener(evt -> addNumber("1"));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.insets = inset;
        add(btnAngka1, gbc);

        com.formdev.flatlaf.extras.components.FlatButton btnAngka2 = new com.formdev.flatlaf.extras.components.FlatButton();
        btnAngka2.setText("2");
        btnAngka2.setFont(font);
        btnAngka2.setBackground(background);
        btnAngka2.setForeground(foreground);
        btnAngka2.setFocusable(false);
        btnAngka2.setMinimumSize(new Dimension(buttonSize, buttonSize));
        btnAngka2.setPreferredSize(new Dimension(buttonSize, buttonSize));
        btnAngka2.setHorizontalTextPosition(SwingConstants.CENTER);
        btnAngka2.addActionListener(evt -> addNumber("2"));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.insets = inset;
        add(btnAngka2, gbc);

        com.formdev.flatlaf.extras.components.FlatButton btnAngka3 = new com.formdev.flatlaf.extras.components.FlatButton();
        btnAngka3.setText("3");
        btnAngka3.setFont(font);
        btnAngka3.setBackground(background);
        btnAngka3.setForeground(foreground);
        btnAngka3.setFocusable(false);
        btnAngka3.setMinimumSize(new Dimension(buttonSize, buttonSize));
        btnAngka3.setPreferredSize(new Dimension(buttonSize, buttonSize));
        btnAngka3.setHorizontalTextPosition(SwingConstants.CENTER);
        btnAngka3.addActionListener(evt -> addNumber("3"));
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.insets = inset;
        add(btnAngka3, gbc);

        if (this.skdpMode) {
            com.formdev.flatlaf.extras.components.FlatButton btnHurufK = new com.formdev.flatlaf.extras.components.FlatButton();
            btnHurufK.setText("K");
            btnHurufK.setFont(font);
            btnHurufK.setBackground(background);
            btnHurufK.setForeground(foreground);
            btnHurufK.setFocusable(false);
            btnHurufK.setMinimumSize(new Dimension(buttonSize, buttonSize));
            btnHurufK.setPreferredSize(new Dimension(buttonSize, buttonSize));
            btnHurufK.setHorizontalTextPosition(SwingConstants.CENTER);
            btnHurufK.addActionListener(evt -> addNumber("K"));
            gbc = new GridBagConstraints();
            gbc.gridx = 3;
            gbc.gridy = 0;
            gbc.fill = GridBagConstraints.BOTH;
            gbc.weightx = 1.0;
            gbc.weighty = 1.0;
            gbc.insets = inset;
            add(btnHurufK, gbc);

            com.formdev.flatlaf.extras.components.FlatButton btnHurufR = new com.formdev.flatlaf.extras.components.FlatButton();
            btnHurufR.setText("R");
            btnHurufR.setFont(font);
            btnHurufR.setBackground(background);
            btnHurufR.setForeground(foreground);
            btnHurufR.setFocusable(false);
            btnHurufR.setMinimumSize(new Dimension(buttonSize, buttonSize));
            btnHurufR.setPreferredSize(new Dimension(buttonSize, buttonSize));
            btnHurufR.setHorizontalTextPosition(SwingConstants.CENTER);
            btnHurufR.addActionListener(evt -> addNumber("R"));
            gbc = new GridBagConstraints();
            gbc.gridx = 3;
            gbc.gridy = 1;
            gbc.fill = GridBagConstraints.BOTH;
            gbc.weightx = 1.0;
            gbc.weighty = 1.0;
            gbc.insets = inset;
            add(btnHurufR, gbc);

            com.formdev.flatlaf.extras.components.FlatButton btnTemplate = new com.formdev.flatlaf.extras.components.FlatButton();
            btnTemplate.setText("TP");
            btnTemplate.setFont(font);
            btnTemplate.setBackground(background);
            btnTemplate.setForeground(foreground);
            btnTemplate.setFocusable(false);
            btnTemplate.setMinimumSize(new Dimension(buttonSize, buttonSize));
            btnTemplate.setPreferredSize(new Dimension(buttonSize, buttonSize));
            btnTemplate.setHorizontalTextPosition(SwingConstants.CENTER);
            btnTemplate.addActionListener(evt -> textBox.setText("0302R110"));
            gbc = new GridBagConstraints();
            gbc.gridx = 3;
            gbc.gridy = 2;
            gbc.fill = GridBagConstraints.BOTH;
            gbc.weightx = 1.0;
            gbc.weighty = 1.0;
            gbc.insets = inset;
            add(btnTemplate, gbc);
        }

        com.formdev.flatlaf.extras.components.FlatButton btnClear = new com.formdev.flatlaf.extras.components.FlatButton();
        btnClear.setText("C");
        btnClear.setToolTipText("Clear");
        btnClear.setFont(font);
        btnClear.setBackground(background);
        btnClear.setForeground(foreground);
        btnClear.setFocusable(false);
        btnClear.setMinimumSize(new Dimension(buttonSize, buttonSize));
        btnClear.setPreferredSize(new Dimension(buttonSize, buttonSize));
        btnClear.setHorizontalTextPosition(SwingConstants.CENTER);
        btnClear.addActionListener(evt -> {
            if (textBox.getText().length() > 0) {
                textBox.setText("");
            }
        });
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridheight = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.insets = inset;
        add(btnClear, gbc);

        com.formdev.flatlaf.extras.components.FlatButton btnAngka0 = new com.formdev.flatlaf.extras.components.FlatButton();
        btnAngka0.setText("0");
        btnAngka0.setFont(font);
        btnAngka0.setBackground(background);
        btnAngka0.setForeground(foreground);
        btnAngka0.setFocusable(false);
        btnAngka0.setMinimumSize(new Dimension(buttonSize, buttonSize));
        btnAngka0.setPreferredSize(new Dimension(buttonSize, buttonSize));
        btnAngka0.setHorizontalTextPosition(SwingConstants.CENTER);
        btnAngka0.addActionListener(evt -> addNumber("0"));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.insets = inset;
        add(btnAngka0, gbc);

        com.formdev.flatlaf.extras.components.FlatButton btnBackspace = new com.formdev.flatlaf.extras.components.FlatButton();
        if (this.skdpMode) {
            btnBackspace.setText("🡐");
        } else {
            btnBackspace.setText("←");
        }
        btnBackspace.setToolTipText("Backspace");
        btnBackspace.setFont(font);
        btnBackspace.setBackground(background);
        btnBackspace.setForeground(foreground);
        btnBackspace.setFocusable(false);
        btnBackspace.setMinimumSize(new Dimension(buttonSize, buttonSize));
        btnBackspace.setPreferredSize(new Dimension(buttonSize, buttonSize));
        btnBackspace.setHorizontalTextPosition(SwingConstants.CENTER);
        btnBackspace.addActionListener(evt -> {
            String input = textBox.getText();
            if (input.length() > 0) {
                textBox.setText(input.substring(0, input.length() - 1));
            }
        });
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 3;
        if (this.skdpMode) {
            gbc.gridwidth = 2;
        } else {
            gbc.gridwidth = 1;
        }
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.insets = inset;
        add(btnBackspace, gbc);

        setBackground(new Color(240, 249, 255));
        setForeground(new Color(0, 131, 62));
        if (this.skdpMode) {
            setMinimumSize(new Dimension(buttonSize * 4, buttonSize * 4));
            setPreferredSize(new Dimension(buttonSize * 4, buttonSize * 4));
        } else {
            setMinimumSize(new Dimension(buttonSize * 3, buttonSize * 4));
            setPreferredSize(new Dimension(buttonSize * 3, buttonSize * 4));
        }

        if (this.textBox != null) {
            this.textBox.setDocument(new PlainDocument() {
                @Override
                public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
                    if (str == null) {
                        return;
                    }
                    if (getLength() + str.length() <= getTextLimit()) {
                        super.insertString(offs, str, a);
                    }
                }
            });
        }

        revalidate();
    }

    private void addNumber(String number) {
        if (textBox.getText().length() < this.textLimit) {
            textBox.setText(textBox.getText().concat(number));
        }
    }
}
