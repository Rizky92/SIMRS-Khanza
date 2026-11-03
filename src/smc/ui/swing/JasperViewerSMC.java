package smc.ui.swing;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.WindowEvent;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeListener;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.view.JasperViewer;

/**
 * A {@link JasperViewer} that can be closed while showing a report book. JasperReports 6.8.0 shows
 * the parts of a book in a {@link JTabbedPane}, and clearing the viewer on close removes those
 * tabs with their change listener still attached. The listener then reads a null selected tab and
 * throws a {@link NullPointerException}. This viewer detaches the JasperReports tab listeners
 * before the window closes.
 *
 * @author smc
 */
public class JasperViewerSMC extends JasperViewer {

    public JasperViewerSMC(JasperPrint jasperPrint, boolean isExitOnClose) {
        super(jasperPrint, isExitOnClose);
    }

    @Override
    protected void processWindowEvent(WindowEvent e) {
        if (WindowEvent.WINDOW_CLOSING == e.getID()) {
            detachTabListeners(getContentPane());
        }
        super.processWindowEvent(e);
    }

    private void detachTabListeners(Container container) {
        if (container instanceof JTabbedPane) {
            JTabbedPane tabs = (JTabbedPane) container;
            for (ChangeListener listener : tabs.getChangeListeners()) {
                if (listener.getClass().getName().startsWith("net.sf.jasperreports.")) {
                    tabs.removeChangeListener(listener);
                }
            }
        }
        for (Component component : container.getComponents()) {
            if (component instanceof Container) {
                detachTabListeners((Container) component);
            }
        }
    }
}
