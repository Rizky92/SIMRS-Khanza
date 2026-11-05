package widget;

import java.awt.Color;
import java.awt.Frame;
import java.awt.event.WindowEvent;
import javax.swing.JDialog;

public class Dialog extends JDialog {
    private boolean activationDeferred = false;

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

    @Override
    protected void processWindowEvent(WindowEvent e) {
        if (e.getID() == WindowEvent.WINDOW_ACTIVATED && LoadingOverlaySMC.isBusy(this)) {
            if (!activationDeferred) {
                activationDeferred = true;
                LoadingOverlaySMC.whenIdle(this, () -> {
                    activationDeferred = false;
                    if (isActive()) {
                        super.processWindowEvent(e);
                    }
                });
            }
            return;
        }
        super.processWindowEvent(e);
    }
}
