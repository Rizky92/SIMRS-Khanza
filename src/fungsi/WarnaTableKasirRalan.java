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
public class WarnaTableKasirRalan extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column){
        Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        if (row % 2 == 1){
            component.setBackground(LookAndFeelSMC.ALT_WHITE);
        }else{
            component.setBackground(Color.WHITE);
        }
        if (isSelected) {
            component.setForeground(Color.RED);
            component.setFont(component.getFont().deriveFont(Font.BOLD));
        } else {
            component.setForeground(LookAndFeelSMC.TEXT_DARK);
            component.setFont(component.getFont().deriveFont(Font.PLAIN));
        }
        if(table.getValueAt(row,10).toString().equals("Sudah")){
            component.setBackground(LookAndFeelSMC.DARK_RED);
            component.setForeground(LookAndFeelSMC.PALE_RED);
        }else if(table.getValueAt(row,10).toString().equals("Batal")){
            component.setBackground(new Color(255,243,109));
            component.setForeground(new Color(67,60,25));
        }else if(table.getValueAt(row,10).toString().equals("Dirujuk")||table.getValueAt(row,10).toString().equals("Meninggal")||table.getValueAt(row,10).toString().equals("Pulang Paksa")){
            component.setBackground(new Color(152,152,156));
            component.setForeground(new Color(245,245,255));
        }else if(table.getValueAt(row,10).toString().equals("Dirawat")){
            component.setBackground(new Color(119,221,119));
            component.setForeground(new Color(245,255,245));
        }else if(table.getValueAt(row,10).toString().equals("TTV")){
            if (row % 2 == 1) {
                component.setBackground(new Color(17, 217, 242));
            } else {
                component.setBackground(LookAndFeelSMC.BRIGHT_CYAN);
            }
            component.setForeground(LookAndFeelSMC.DARK_PURPLE);
        }else if (table.getValueAt(row, 10).toString().equals("Rujuk Internal")) {
            component.setBackground(new Color(138, 1, 148));
            component.setForeground(new Color(250, 232, 255));
        }
        if(table.getValueAt(row,15).toString().equals("Sudah Bayar")){
            component.setBackground(LookAndFeelSMC.TEXT_DARK);
            component.setForeground(Color.WHITE);
        }
        return component;
    }
}