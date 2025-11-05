/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fungsi;

import com.formdev.flatlaf.util.ColorFunctions;
import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author Owner
 */
public class WarnaTable extends DefaultTableCellRenderer {
    private static Color BACKGROUND_DEFAULT = UIManager.getColor("Table.background");
    private static Color FOREGROUND_DEFAULT = UIManager.getColor("Table.foreground");
    private static Color BACKGROUND_SELECTION = UIManager.getColor("Table.selectionBackground");
    private static Color FOREGROUND_SELECTION = UIManager.getColor("Table.selectionForeground");
    
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        Color backgroundColor = BACKGROUND_DEFAULT;
        Color foregroundColor = FOREGROUND_DEFAULT;
        if (isSelected) {
            backgroundColor = BACKGROUND_SELECTION;
            foregroundColor = FOREGROUND_SELECTION;
        } else {
            backgroundColor = row % 2 == 1 ? ColorFunctions.darken(BACKGROUND_DEFAULT, 0.05f) : BACKGROUND_DEFAULT;
            foregroundColor = FOREGROUND_DEFAULT;
        }
        component.setBackground(backgroundColor);
        component.setForeground(foregroundColor);
        return component;
    }
}
