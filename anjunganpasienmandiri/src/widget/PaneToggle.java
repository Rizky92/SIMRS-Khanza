package widget;

import com.formdev.flatlaf.extras.components.FlatCheckBox;
import java.awt.Color;
import java.awt.Font;
import javax.swing.ImageIcon;

public class PaneToggle extends FlatCheckBox {
    public PaneToggle() {
        setBorder(null);
        setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/143.png"))); // NOI18N
        setText(".: Informasi tambahan :.");
        setBorderPainted(true);
        setBorderPaintedFlat(true);
        setFocusable(false);
        setHorizontalAlignment(CENTER);
        setHorizontalTextPosition(RIGHT);
        setRolloverIcon(new ImageIcon(getClass().getResource("/picture/143.png"))); // NOI18N
        setRolloverSelectedIcon(new ImageIcon(getClass().getResource("/picture/145.png"))); // NOI18N
        setSelectedIcon(new ImageIcon(getClass().getResource("/picture/145.png"))); // NOI18N

        setFont(new Font("Inter", Font.PLAIN, 10));
        setBackground(new Color(240, 249, 255));
        setForeground(new Color(180, 185, 190));
        setFocusPainted(false);

        setOpaque(true);
        setSize(WIDTH, 30);
    }
}
