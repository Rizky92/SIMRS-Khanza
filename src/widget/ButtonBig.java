package widget;

import javax.swing.JButton;

/**
 *
 * @author usu
 */
public class ButtonBig extends JButton {
    public ButtonBig() {
        setIconTextGap(16);
        setHorizontalAlignment(CENTER);
        setHorizontalTextPosition(CENTER);
        setVerticalAlignment(CENTER);
        setVerticalTextPosition(BOTTOM);
        putClientProperty("JButton.buttonType", "borderless");
    }
}
