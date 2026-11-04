package widget;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Insets;
import javax.swing.JButton;

/*
public class Button extends usu.widget.ButtonGlass {
    private static final long serialVersionUID = 1L;

    public Button() {
        super();
        setFont(new java.awt.Font("Tahoma", 1, 11));
        setForeground(new Color(50,50,50));
        setGlassColor(new Color(240,245,240));
        setMargin(new Insets(2, 7, 2, 7));
        setIconTextGap(4);
        setRoundRect(true);
    }
}
*/

public class Button extends JButton {
    private static final long serialVersionUID = 1L;

    public Button() {
        super();
        setFont(new java.awt.Font("Tahoma", 1, 11));
        setMargin(new Insets(2, 7, 2, 7));
        setIconTextGap(4);
        setRolloverEnabled(true);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    public Color getGlassColor() {
        return null;
    }

    public void setGlassColor(Color glassColor) {
    }

    public boolean isRoundRect() {
        return false;
    }

    public void setRoundRect(boolean roundRect) {
    }
}
