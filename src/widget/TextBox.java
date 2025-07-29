
package widget;

import com.formdev.flatlaf.util.ColorFunctions;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.FocusManager;
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
        setSelectionColor(new Color(255,252,252));
        setSelectedTextColor(new Color(255,0,0));
        setForeground(new Color(50,50,50));
        setBackground(new Color(255,255,255));
        setHorizontalAlignment(LEFT);
        setSize(WIDTH,23);
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
        
        if (getText().isEmpty() && !hasFocus()) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(ColorFunctions.lighten(getForeground(), 0.3f));
            g2.setFont(getFont());
            
            FontMetrics fm = g2.getFontMetrics(getFont());
            int y = getInsets().top + fm.getAscent();
            int x = getInsets().left;
            
            g2.drawString(getPlaceholderText(), x, y);
            g2.dispose();
        }
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
