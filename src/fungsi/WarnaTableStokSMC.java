package fungsi;

import java.awt.Color;
import java.awt.Component;
import java.util.Collection;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author SMC
 */
public class WarnaTableStokSMC extends DefaultTableCellRenderer {

    private final Collection<String> stokTidakCukup;
    private final int kolomKode;

    public WarnaTableStokSMC(Collection<String> stokTidakCukup, int kolomKode) {
        this.stokTidakCukup = stokTidakCukup;
        this.kolomKode = kolomKode;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        TableCellRenderer dasar = table.getDefaultRenderer(table.getColumnClass(column));
        Component component = (null == dasar || this == dasar)
            ? super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column)
            : dasar.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        try {
            if (stokTidakCukup.contains(table.getValueAt(row, kolomKode).toString())) {
                component.setForeground(new Color(200, 0, 0));
            }
        } catch (Exception e) {
        }

        return component;
    }
}
