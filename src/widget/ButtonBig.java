package widget;

import java.awt.Color;

/**
 *
 * @author usu
 */
public class ButtonBig extends Button {

    /*
     * Serial version UID
     */
    private static final long serialVersionUID = 1L;

    public ButtonBig() {
        super();
        setForeground(new Color(50,50,50));
        setFont(new java.awt.Font("Tahoma", 0, 11));
        setHorizontalTextPosition(CENTER);
        setVerticalTextPosition(BOTTOM);
        setIconTextGap(8);
    }
}
