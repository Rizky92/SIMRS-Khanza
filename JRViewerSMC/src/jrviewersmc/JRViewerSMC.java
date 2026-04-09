package jrviewersmc;

import java.io.File;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class JRViewerSMC {
    public static void openWindow() {
        SwingUtilities.invokeLater(() -> new JRViewerWindow().setVisible(true));
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        if (args.length > 0) {
            SwingUtilities.invokeLater(() -> Utils.open(new File(args[0])));
        } else {
            openWindow();
        }
    }
}
