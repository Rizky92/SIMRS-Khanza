package widget;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Insets;
import javax.swing.ImageIcon;
import javax.swing.JToggleButton;

/**
 *
 * @author usu
 */
public class PasswordBox extends usu.widget.glass.PasswordBox {
    private static final int PEEK_WIDTH = 22;
    private final JToggleButton peekButton = new JToggleButton();
    private char hiddenEchoChar;

    public PasswordBox() {
        super();
        setSelectionColor(Color.BLUE.brighter());
        setCaretColor(Color.red);
        setFont(getFont().deriveFont(Font.BOLD,12));
        setForeground(Color.WHITE);
        setHorizontalAlignment(LEFT);
        initPeekButton();
    }

    private void initPeekButton() {
        if (0 != getEchoChar()) {
            hiddenEchoChar = getEchoChar();
        } else if (0 == hiddenEchoChar) {
            hiddenEchoChar = '•';
        }
        peekButton.setIcon(new ImageIcon(PasswordBox.class.getResource("/picture/matatutup.png")));
        peekButton.setSelectedIcon(new ImageIcon(PasswordBox.class.getResource("/picture/matabuka.png")));
        peekButton.setToolTipText("Tampilkan password");
        peekButton.setBorder(null);
        peekButton.setContentAreaFilled(false);
        peekButton.setFocusPainted(false);
        peekButton.setFocusable(false);
        peekButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        peekButton.setSelected(0 == getEchoChar());
        peekButton.addActionListener(e -> setPeeking(peekButton.isSelected()));
        setLayout(null);
        add(peekButton);
    }

    /**
     * Shows the password as plain text, or masks it again.
     *
     * @param peeking {@code true} to show the password, {@code false} to mask it
     */
    public void setPeeking(boolean peeking) {
        setEchoChar(peeking ? 0 : hiddenEchoChar);
    }

    /**
     * Tells whether the password is currently shown as plain text.
     *
     * @return {@code true} when the password is shown
     */
    public boolean isPeeking() {
        return 0 == getEchoChar();
    }

    @Override
    public void setEchoChar(char c) {
        super.setEchoChar(c);
        if (0 != c) {
            hiddenEchoChar = c;
        }
        if (null != peekButton) {
            peekButton.setSelected(0 == c);
            peekButton.setToolTipText(0 == c ? "Sembunyikan password" : "Tampilkan password");
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (null != peekButton) {
            peekButton.setEnabled(enabled);
        }
    }

    @Override
    public Insets getInsets() {
        Insets insets = super.getInsets();
        return new Insets(insets.top, insets.left, insets.bottom, insets.right + PEEK_WIDTH);
    }

    @Override
    public Insets getInsets(Insets insets) {
        Insets border = super.getInsets(insets);
        border.right += PEEK_WIDTH;
        return border;
    }

    @Override
    public void doLayout() {
        peekButton.setBounds(getWidth() - super.getInsets().right - PEEK_WIDTH, 0, PEEK_WIDTH, getHeight());
    }
}
