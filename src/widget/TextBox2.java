
package widget;

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
        setSelectedTextColor(LookAndFeelSMC.TEXT_DARK);
        setForeground(LookAndFeelSMC.TEXT_DARK);
        setBackground(Color.WHITE);
        setHorizontalAlignment(LEFT);
        setSize(WIDTH,23);
    }
}
