package widget;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 *
 * @author usu
 */
public class TextBox extends javax.swing.JTextField {


    @FunctionalInterface
    public interface CustomDocumentListener extends DocumentListener {
        void changed(DocumentEvent e);

        @Override
        default void changedUpdate(DocumentEvent e) {
            changed(e);
        }

        @Override
        default void removeUpdate(DocumentEvent e) {
            changed(e);
        }

        @Override
        default void insertUpdate(DocumentEvent e) {
            changed(e);
        }
    }
}
