/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fungsi;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.PlainDocument;

/**
 *
 * @author Owner
 */
public class BatasInput {
    private int length;

    public BatasInput(int length) {
        this.length = length;
    }

    public BatasInput() {

    }

    public PlainDocument hanyaInteger(final int limit) {
        PlainDocument document = new PlainDocument();
        document.setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(DocumentFilter.FilterBypass fb, int offset, String text, AttributeSet attr) throws BadLocationException {
                if (text == null) {
                    return;
                }

                String newText = fb.getDocument().getText(0, fb.getDocument().getLength());
                newText = newText.substring(0, offset) + text + newText.substring(offset);

                if (newText.matches("\\d*") && newText.length() <= limit) {
                    super.insertString(fb, offset, text, attr);
                }
            }

            @Override
            public void replace(DocumentFilter.FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                String current = fb.getDocument().getText(0, fb.getDocument().getLength());
                String newText = current.substring(0, offset) +
                    (text == null ? "" : text) +
                    current.substring(offset + length);

                if (newText.matches("\\d*") && newText.length() <= limit) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        });

        return document;
    }
}
