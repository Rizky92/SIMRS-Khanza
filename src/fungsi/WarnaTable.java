package fungsi;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import javax.swing.JTable;
import javax.swing.plaf.UIResource;
import javax.swing.table.DefaultTableCellRenderer;

public class WarnaTable extends DefaultTableCellRenderer implements UIResource {
    private static final long serialVersionUID = 4L;
    private static final Color PUTIH = new Color(255, 255, 255);
    private static final Color ZEBRA = new Color(245, 250, 245);

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        if (!isSelected) {
            component.setForeground(new Color(50, 50, 50));
            component.setBackground(row % 2 == 1 ? ZEBRA : PUTIH);
        } else {
            component.setForeground(new Color(255, 0, 0));
            component.setFont(component.getFont().deriveFont(Font.BOLD));
        }

        return component;
    }
}