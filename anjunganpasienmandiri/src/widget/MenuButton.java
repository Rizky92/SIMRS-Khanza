package widget;

import com.formdev.flatlaf.extras.components.FlatButton;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.SwingConstants;

public class MenuButton extends FlatButton {
    public MenuButton() {
        super();
        setFont(new Font("Inter", Font.BOLD, 30));
        setBackground(new Color(240, 249, 255));
        setForeground(new Color(0, 131, 62));
        setHorizontalTextPosition(SwingConstants.CENTER);
        setVerticalTextPosition(SwingConstants.BOTTOM);
        setIconTextGap(8);
        setPreferredSize(new Dimension(200, 90));
        setBorder(BorderFactory.createEmptyBorder());
    }
}
