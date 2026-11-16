package widget;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.EventObject;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.plaf.UIResource;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.text.JTextComponent;

public class TableSMC extends JTable {
    private static final long serialVersionUID = 2L;
    private static final Color SELECTED_FOREGROUND = Color.RED;

    private Font boldFontBase;
    private Font boldFont;
    private boolean replaceOnType;

    public TableSMC() {
        super();
        setFont(new Font("Tahoma", Font.PLAIN, 11));
        getTableHeader().setBorder(BorderFactory.createEmptyBorder());
        getTableHeader().setFont(new java.awt.Font("Tahoma", 0, 11));
        getTableHeader().setReorderingAllowed(false);
        getTableHeader().setDefaultRenderer(new LeftHeaderRendererSMC(getTableHeader().getDefaultRenderer()));
    }

    /**
     * Cells of the default renderers mark a selected row with red bold text instead
     * of the selection colors, like the {@code fungsi.WarnaTable} renderers do,
     * because row colors carry conditional information.
     */
    @Override
    public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
        return renderer instanceof UIResource ? prepareDefaultRendererSmc(renderer, row, column) : super.prepareRenderer(renderer, row, column);
    }

    private Component prepareDefaultRendererSmc(TableCellRenderer renderer, int row, int column) {
        boolean printing = isPaintingForPrint();
        boolean selected = !printing && isCellSelected(row, column);
        boolean hasFocus = !printing && row == getSelectionModel().getLeadSelectionIndex() && column == getColumnModel().getSelectionModel().getLeadSelectionIndex() && isFocusOwner();
        if (renderer instanceof DefaultTableCellRenderer) {
            ((DefaultTableCellRenderer) renderer).setForeground(null);
        }
        Component component = renderer.getTableCellRendererComponent(this, getValueAt(row, column), false, hasFocus, row, column);
        component.setFont(selected ? boldFontSmc() : getFont());
        if (selected) {
            component.setForeground(SELECTED_FOREGROUND);
        }

        return component;
    }

    /**
     * Gives a check box editor the background its cell shows, where {@code DefaultCellEditor} would take the selection background and flash it while the check box is pressed.
     */
    @Override
    public Component prepareEditor(TableCellEditor editor, int row, int column) {
        Component component = super.prepareEditor(editor, row, column);
        if (component instanceof JCheckBox && component.isOpaque()) {
            component.setBackground(prepareRenderer(getCellRenderer(row, column), row, column).getBackground());
        }
        return component;
    }

    /**
     * Whether typing a character on a cell that is not being edited replaces its value, the way a spreadsheet does.
     * Without it the typed character is appended to the cell's text, so typing {@code 25} on a cell showing {@code 0.0} edits it to {@code 0.025}.
     * Editing started by a double click or F2 keeps the text either way.
     *
     * @return {@code true} when typing replaces the cell's value
     */
    public boolean isReplaceOnType() {
        return replaceOnType;
    }

    /**
     * @param replaceOnType {@code true} to make typing replace the cell's value
     * @see #isReplaceOnType()
     */
    public void setReplaceOnType(boolean replaceOnType) {
        this.replaceOnType = replaceOnType;
    }

    @Override
    public boolean editCellAt(int row, int column, EventObject e) {
        boolean editing = super.editCellAt(row, column, e);
        if (editing && replaceOnType && e instanceof KeyEvent && getEditorComponent() instanceof JTextComponent) {
            char typed = ((KeyEvent) e).getKeyChar();
            if (KeyEvent.CHAR_UNDEFINED != typed && !Character.isISOControl(typed)) {
                ((JTextComponent) getEditorComponent()).setText("");
            }
        }
        return editing;
    }

    private Font boldFontSmc() {
        Font font = getFont();
        if (null == boldFont || font != boldFontBase) {
            boldFontBase = font;
            boldFont = font.deriveFont(Font.BOLD);
        }
        return boldFont;
    }

    @Override
    public Point getPopupLocation(MouseEvent e) {
        if (null != e) {
            selectCellAt(e.getPoint());
            super.processMouseEvent(e);
            super.processMouseEvent(new MouseEvent(this, MouseEvent.MOUSE_CLICKED, e.getWhen(), e.getModifiersEx() & ~InputEvent.BUTTON3_DOWN_MASK, e.getX(), e.getY(), e.getXOnScreen(), e.getYOnScreen(), e.getClickCount(), false, MouseEvent.BUTTON3));
        }
        return super.getPopupLocation(e);
    }

    @Override
    protected void processMouseEvent(MouseEvent e) {
        if (MouseEvent.BUTTON3 == e.getButton()) {
            if (MouseEvent.MOUSE_PRESSED == e.getID()) {
                selectCellAt(e.getPoint());
            }
            if (null != getComponentPopupMenu()) {
                if (MouseEvent.MOUSE_CLICKED == e.getID()) {
                    return;
                }
                if (e.isPopupTrigger()) {
                    if (!e.isConsumed()) {
                        Point pt = getPopupLocation(e);
                        if (null == pt) {
                            pt = e.getPoint();
                        }
                        getComponentPopupMenu().show(this, pt.x, pt.y);
                        e.consume();
                    }
                    return;
                }
            }
        }
        super.processMouseEvent(e);
    }

    private void selectCellAt(Point p) {
        int r = rowAtPoint(p);
        int c = columnAtPoint(p);
        if (r != -1 && c != -1) {
            changeSelection(r, c, false, false);
        }
    }

    private static class LeftHeaderRendererSMC implements TableCellRenderer {
        private final TableCellRenderer delegate;

        LeftHeaderRendererSMC(TableCellRenderer delegate) {
            this.delegate = delegate;
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component component = delegate.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (component instanceof JLabel) {
                ((JLabel) component).setHorizontalAlignment(SwingConstants.LEADING);
            }

            return component;
        }
    }
}
