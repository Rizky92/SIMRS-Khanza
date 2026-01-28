
package id.yaski.simrskhanza.widget;

import java.awt.Color;
import javax.swing.JTextField;

/**
 *
 * @author usu
 */
public class TextBox2 extends JTextField {
    public TextBox2() {
        super();
        setFont(new java.awt.Font("Tahoma", 0, 11));
        setSelectionColor(new Color(190, 210, 215));
        setSelectedTextColor(new Color(50, 50, 50));
        setForeground(new Color(50,50,50));
        setBackground(new Color(255,255,255));
        setHorizontalAlignment(LEFT);
        setSize(WIDTH,23);
    }
}
