package widget;

import com.formdev.flatlaf.util.ColorFunctions;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.util.Map;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import usu.widget.glass.TextBoxGlass;

/**
 *
 * @author usu
 */
public class TextBox extends TextBoxGlass {
    private String placeholderText = "";

    public TextBox() {
        super();
        setFont(new java.awt.Font("Tahoma", 0, 11));
        setSelectionColor(new Color(231, 33, 35));
        setSelectedTextColor(new Color(255, 255, 255));
        setForeground(new Color(50, 50, 50));
        setBackground(new Color(255, 255, 255));
        setHorizontalAlignment(LEFT);
        setSize(WIDTH, 23);
    }

    public void setPlaceholderText(String placeholderText) {
        this.placeholderText = placeholderText;
    }

    public String getPlaceholderText() {
        return this.placeholderText;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (getPlaceholderText() == null || getPlaceholderText().isEmpty() || getText().length() > 0) {
            return;
        }

        Map<?, ?> desktopHints = (Map<?, ?>) Toolkit.getDefaultToolkit().getDesktopProperty("awt.font.desktophints");

        Graphics2D g2 = (Graphics2D) g;

        g2.setColor(new Color(132, 132, 132));

        if (desktopHints != null) {
            g2.setRenderingHints(desktopHints);
        }

        g2.drawString(getPlaceholderText(), getInsets().left, getInsets().top + g2.getFontMetrics(getFont()).getAscent());
        g2.dispose();
    }

    @FunctionalInterface
    public interface CustomDocumentListener extends DocumentListener {
        void changed(DocumentEvent e);

        @Override
        default void changedUpdate(DocumentEvent e) {
            changed(e);
        }

        @Override
        default void removeUpdate(DocumentEvent e) {
            changed(e);
        }

        @Override
        default void insertUpdate(DocumentEvent e) {
            changed(e);
        }
    }
}
