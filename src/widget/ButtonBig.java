package widget;

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
        setForeground(LookAndFeelSMC.TEXT_DARK);
        setFont(new java.awt.Font("Tahoma", 0, 11));
        setHorizontalTextPosition(CENTER);
        setVerticalTextPosition(BOTTOM);
        setIconTextGap(8);
    }
}
