/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package id.yaski.simrskhanza.fungsi;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author USER
 */
public class WarnaTableKamarInap extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        if (row % 2 == 1) {
            component.setBackground(new Color(255, 244, 244));
            component.setForeground(new Color(50, 50, 50));
        } else {
            component.setBackground(new Color(255, 255, 255));
            component.setForeground(new Color(50, 50, 50));
        }

        if (isSelected) {
            component.setForeground(new Color(255, 0, 0));
            component.setFont(component.getFont().deriveFont(Font.BOLD));
        } else {
            component.setForeground(new Color(50, 50, 50));
            component.setFont(component.getFont().deriveFont(Font.PLAIN));
        }

        if (table.getValueAt(row, 20).toString().equals("Sudah Bayar")) {
            component.setBackground(new Color(50, 50, 50));
            component.setForeground(new Color(255, 255, 255));
        }

        return component;
    }
}
