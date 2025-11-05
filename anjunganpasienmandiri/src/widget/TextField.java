package widget;

import com.formdev.flatlaf.extras.components.FlatTextField;
import javax.swing.UIManager;

public class TextField extends FlatTextField {
    public TextField() {
        super();
        setHorizontalAlignment(LEFT);
        setSize(WIDTH, 35);
    }

    @Override
    public void setEditable(boolean b) {
        super.setEditable(b);
        if (UIManager.getLookAndFeelDefaults().containsKey("TextField.inactiveBackground")) {
            super.setBackground(b ? UIManager.getColor("TextField.background") : UIManager.getColor("TextField.inactiveBackground"));
        }
    }
}
