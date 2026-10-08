package fungsi;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import javax.swing.JTable;
import javax.swing.plaf.UIResource;
import javax.swing.table.DefaultTableCellRenderer;

//public class WarnaTable extends DefaultTableCellRenderer implements UIResource {
public class WarnaTable extends DefaultTableCellRenderer {
    private static final long serialVersionUID = 4L;
    private static final Color PUTIH = new Color(255, 255, 255);
    private static final Color ZEBRA = new Color(245, 250, 245);
    private static final Color BLACK = new Color(50, 50, 50);

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        component.setForeground(BLACK);
        component.setBackground(row % 2 == 1 ? ZEBRA : PUTIH);

        if (isSelected) {
            component.setForeground(Color.RED);
            component.setFont(component.getFont().deriveFont(Font.BOLD));
        }

        return component;
    }
}