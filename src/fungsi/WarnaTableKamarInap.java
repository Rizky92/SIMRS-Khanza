/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
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
 * @author USER
 */
public class WarnaTableKamarInap extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        if (row % 2 == 1) {
            component.setBackground(LookAndFeelSMC.ALT_WHITE);
            component.setForeground(LookAndFeelSMC.TEXT_DARK);
        } else {
            component.setBackground(Color.WHITE);
            component.setForeground(LookAndFeelSMC.TEXT_DARK);
        }

        if (isSelected) {
            component.setForeground(Color.RED);
            component.setFont(component.getFont().deriveFont(Font.BOLD));
        } else {
            component.setForeground(LookAndFeelSMC.TEXT_DARK);
            component.setFont(component.getFont().deriveFont(Font.PLAIN));
        }

        if (table.getValueAt(row, 21).toString().equals("Sudah Bayar")) {
            component.setBackground(LookAndFeelSMC.TEXT_DARK);
            component.setForeground(Color.WHITE);
        }

        return component;
    }
}
