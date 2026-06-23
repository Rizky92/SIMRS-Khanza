package widget;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import usu.widget.glass.TextBoxGlass;

/**
 *
 * @author usu
 */
public class TextBox extends TextBoxGlass {
    public TextBox() {
        super();
        setFont(new java.awt.Font("Tahoma", 0, 11));
        setSelectionColor(new Color(190, 210, 215));
        setSelectedTextColor(new Color(50, 50, 50));
        setForeground(new Color(50, 50, 50));
        setBackground(new Color(255, 255, 255));
        setHorizontalAlignment(LEFT);
        setSize(WIDTH, 23);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
        g2.dispose();
        super.paintComponent(g);
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
