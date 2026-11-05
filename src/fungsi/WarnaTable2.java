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
import widget.LookAndFeelSMC;

/**
 *
 * @author Owner
 */
public class WarnaTable2 extends DefaultTableCellRenderer {
    public int kolom;
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column){
        Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        if (row % 2 == 1){
            component.setBackground(LookAndFeelSMC.ALT_WHITE);
        }else{
            component.setBackground(Color.WHITE);
        }
        if (column == kolom){
            component.setBackground(LookAndFeelSMC.LAVENDER);
            component.setForeground(Color.WHITE);
            try {
                if(!table.getValueAt(row,kolom).toString().equals("")){
                    component.setBackground(Color.WHITE);
                    component.setForeground(LookAndFeelSMC.DARK_BLUE);
                }
            } catch (Exception e) {
            }
        }else if (isSelected) {
            component.setForeground(Color.RED);
        }else{
            component.setForeground(LookAndFeelSMC.TEXT_GRAY);
        }
        if (isSelected) {
            component.setFont(component.getFont().deriveFont(Font.BOLD));
        } else {
            component.setFont(component.getFont().deriveFont(Font.PLAIN));
        }
        return component;
    }

}
