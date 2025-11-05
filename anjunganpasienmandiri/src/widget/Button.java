package widget;

import com.formdev.flatlaf.extras.components.FlatButton;
import java.awt.Color;
import java.awt.Font;

public class Button extends FlatButton {
    public Button() {
        super();
        setFont(new Font("Inter", Font.BOLD, 18));
        setBackground(new Color(0, 131, 62));
        setForeground(new Color(255, 255, 255));
    }
}
