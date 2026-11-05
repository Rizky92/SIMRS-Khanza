package widget;

import java.awt.Color;

/**
 *
 * @author usu
 */
public class Label extends usu.widget.Label {

    /*
     * Serial version UID
     */
    private static final long serialVersionUID = 1L;

    public Label() {
        super();
        //setForeground(new Color(90,90,90));
        setForeground(LookAndFeelSMC.TEXT_DARK);
        setFont(new java.awt.Font("Tahoma", 0, 11));

        setHorizontalAlignment(RIGHT);
        setVerticalAlignment(CENTER);
        setHorizontalTextPosition(CENTER);
        setVerticalTextPosition(CENTER);
    }
}
