package smc.ui.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.beans.PropertyEditorSupport;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import org.openide.explorer.propertysheet.editors.XMLPropertyEditor;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Property editor for the "entries" property of {@link ComboBoxSMC}. The GUI editor shows the
 * items as a key-value table, and the .form file stores them as readable XML instead of a
 * serialized value.
 *
 * @author smc
 */
public final class ComboBoxEntriesEditorSMC extends PropertyEditorSupport implements XMLPropertyEditor {

    private static final String XML_ENTRIES = "Entries";
    private static final String XML_ENTRY = "Entry";
    private static final String ATTR_KEY = "key";
    private static final String ATTR_VALUE = "value";

    @Override
    public String getAsText() {
        StringBuilder text = new StringBuilder();

        for (ComboBoxSMC.Entry entry : entries()) {
            if (0 < text.length()) {
                text.append(", ");
            }

            text.append(entry.getKey()).append(": ").append(entry);
        }

        return text.toString();
    }

    @Override
    public void setAsText(String text) {
        if (!getAsText().equals(text)) {
            throw new IllegalArgumentException("Use the custom editor (...) to change the entries");
        }
    }

    @Override
    public String getJavaInitializationString() {
        String type = ComboBoxSMC.Entry.class.getCanonicalName();
        StringBuilder code = new StringBuilder("new ").append(type).append("[] {");
        ComboBoxSMC.Entry[] entries = entries();

        for (int i = 0; i < entries.length; i++) {
            if (0 < i) {
                code.append(", ");
            }

            code.append("new ").append(type).append("(").append(literal(entries[i].getKey())).append(", ").append(literal(entries[i].getValue())).append(")");
        }

        return code.append("}").toString();
    }

    @Override
    public boolean supportsCustomEditor() {
        return true;
    }

    @Override
    public Component getCustomEditor() {
        return new EntriesPanel();
    }

    @Override
    public void readFromXML(Node node) throws IOException {
        if (!XML_ENTRIES.equals(node.getNodeName())) {
            throw new IOException("Expected <" + XML_ENTRIES + "> but found <" + node.getNodeName() + ">");
        }

        List<ComboBoxSMC.Entry> entries = new ArrayList<>();
        NodeList children = node.getChildNodes();

        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);

            if (child instanceof Element && XML_ENTRY.equals(child.getNodeName())) {
                Element element = (Element) child;

                entries.add(new ComboBoxSMC.Entry(attribute(element, ATTR_KEY), attribute(element, ATTR_VALUE)));
            }
        }

        setValue(entries.toArray(new ComboBoxSMC.Entry[0]));
    }

    @Override
    public Node storeToXML(Document doc) {
        Element entriesElement = doc.createElement(XML_ENTRIES);

        for (ComboBoxSMC.Entry entry : entries()) {
            Element entryElement = doc.createElement(XML_ENTRY);

            if (null != entry.getKey()) {
                entryElement.setAttribute(ATTR_KEY, entry.getKey().toString());
            }

            if (null != entry.getValue()) {
                entryElement.setAttribute(ATTR_VALUE, entry.getValue().toString());
            }

            entriesElement.appendChild(entryElement);
        }

        return entriesElement;
    }

    private ComboBoxSMC.Entry[] entries() {
        Object value = getValue();

        return value instanceof ComboBoxSMC.Entry[] ? (ComboBoxSMC.Entry[]) value : new ComboBoxSMC.Entry[0];
    }

    private static String attribute(Element element, String name) {
        return element.hasAttribute(name) ? element.getAttribute(name) : null;
    }

    private static String literal(Object value) {
        if (null == value) {
            return "null";
        }

        String text = value.toString();
        StringBuilder literal = new StringBuilder("\"");

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);

            switch (c) {
                case '"':
                    literal.append("\\\"");
                    break;

                case '\\':
                    literal.append("\\\\");
                    break;

                case '\n':
                    literal.append("\\n");
                    break;

                case '\r':
                    literal.append("\\r");
                    break;

                case '\t':
                    literal.append("\\t");
                    break;

                default:
                    if (' ' > c) {
                        literal.append(String.format("\\u%04x", (int) c));
                    } else {
                        literal.append(c);
                    }
                    break;
            }
        }

        return literal.append("\"").toString();
    }

    /**
     * The custom editor: a key-value table with buttons to add, remove and reorder rows. Every
     * change is pushed to the property editor at once, so the GUI editor's OK button reads the
     * latest rows and its Cancel button discards them.
     */
    private final class EntriesPanel extends JPanel {

        /*
         * Serial version UID
         */
        private static final long serialVersionUID = 1L;

        private final DefaultTableModel model = new DefaultTableModel(new Object[] {"Key", "Value"}, 0);
        private final JTable table = new JTable(model);

        EntriesPanel() {
            super(new BorderLayout(6, 0));

            for (ComboBoxSMC.Entry entry : entries()) {
                model.addRow(new Object[] {entry.getKey(), entry.getValue()});
            }

            table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            table.setSurrendersFocusOnKeystroke(true);
            table.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
            table.getTableHeader().setReorderingAllowed(false);
            model.addTableModelListener(evt -> push());

            JPanel buttons = new JPanel(new GridLayout(0, 1, 0, 6));
            buttons.add(button("Add", this::addEntry));
            buttons.add(button("Remove", this::removeEntry));
            buttons.add(button("Move Up", () -> moveEntry(-1)));
            buttons.add(button("Move Down", () -> moveEntry(1)));

            JPanel side = new JPanel(new BorderLayout());
            side.add(buttons, BorderLayout.NORTH);

            add(new JScrollPane(table), BorderLayout.CENTER);
            add(side, BorderLayout.EAST);
            setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
            setPreferredSize(new Dimension(480, 300));
        }

        private JButton button(String text, Runnable action) {
            JButton button = new JButton(text);
            button.addActionListener(evt -> {
                stopEditing();
                action.run();
            });

            return button;
        }

        private void addEntry() {
            model.addRow(new Object[] {"", ""});

            int row = model.getRowCount() - 1;
            select(row);

            if (table.editCellAt(row, 0)) {
                table.getEditorComponent().requestFocusInWindow();
            }
        }

        private void removeEntry() {
            int row = table.getSelectedRow();

            if (-1 != row) {
                model.removeRow(row);
                select(Math.min(row, model.getRowCount() - 1));
            }
        }

        private void moveEntry(int step) {
            int row = table.getSelectedRow();
            int target = row + step;

            if (-1 != row && 0 <= target && target < model.getRowCount()) {
                model.moveRow(row, row, target);
                select(target);
            }
        }

        private void select(int row) {
            if (0 <= row) {
                table.setRowSelectionInterval(row, row);
                table.scrollRectToVisible(table.getCellRect(row, 0, true));
            }
        }

        private void stopEditing() {
            if (table.isEditing()) {
                table.getCellEditor().stopCellEditing();
            }
        }

        private void push() {
            ComboBoxSMC.Entry[] entries = new ComboBoxSMC.Entry[model.getRowCount()];

            for (int i = 0; i < entries.length; i++) {
                entries[i] = new ComboBoxSMC.Entry(model.getValueAt(i, 0), model.getValueAt(i, 1));
            }

            setValue(entries);
        }
    }
}
