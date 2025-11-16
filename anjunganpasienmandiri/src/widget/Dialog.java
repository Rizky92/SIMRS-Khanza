package widget;

import java.awt.Color;
import java.awt.Frame;
import javax.swing.JDialog;

public class Dialog extends JDialog {
    public Dialog() {
        super();
        setup();
    }

    public Dialog(Frame parent, boolean modal) {
        super(parent, modal);
        setup();
    }

    private void setup() {
        setBackground(new Color(240, 249, 255));
        setForeground(new Color(0, 131, 62));
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setModal(true);
        setUndecorated(true);
        setResizable(false);
        getContentPane().setLayout(new java.awt.BorderLayout(0, 0));
    }
}
