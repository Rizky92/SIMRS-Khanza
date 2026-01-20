package widget;

import com.formdev.flatlaf.extras.components.FlatLabel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

public class Label extends FlatLabel {
    public Label() {
        super();
        setForeground(new Color(0, 131, 62));
        setFont(new java.awt.Font("Inter Medium", Font.PLAIN, 18));
        setHorizontalAlignment(RIGHT);
        setVerticalAlignment(CENTER);
        setHorizontalTextPosition(CENTER);
        setVerticalTextPosition(CENTER);
        setPreferredSize(new Dimension(getPreferredSize().width, 35));
    }
}
