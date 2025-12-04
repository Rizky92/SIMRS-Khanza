/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fungsi;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author Owner
 */
public class WarnaTable extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column){
        Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        if (row % 2 == 1){
            component.setBackground(new Color(255,244,244));
        }else{
            component.setBackground(new Color(255,255,255));
        }

        if (isSelected) {
            component.setForeground(new Color(255, 0, 0));
            component.setFont(component.getFont().deriveFont(Font.BOLD));
        } else {
            component.setForeground(new Color(50, 50, 50));
            component.setFont(component.getFont().deriveFont(Font.PLAIN));
        }

        return component;
    }
}
