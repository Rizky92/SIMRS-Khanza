package widget;

import com.formdev.flatlaf.extras.components.FlatPasswordField;
import java.awt.Color;

public class PasswordField extends FlatPasswordField {
    public PasswordField() {
        super();
        setSelectionColor(new Color(0, 131, 62));
        setSelectedTextColor(new Color(255, 255, 255));
        setForeground(new Color(0, 131, 62));
        setBackground(new Color(255, 255, 255));
        setHorizontalAlignment(LEFT);
        setSize(WIDTH, 35);
    }
}
