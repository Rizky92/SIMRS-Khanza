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
public class WarnaTableValidasiResep extends DefaultTableCellRenderer {

    private int posisiWarna;
    private int posisiKondisi;

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
        }
        if (column == posisiWarna) {
            component.setBackground(LookAndFeelSMC.LAVENDER);
            component.setForeground(Color.WHITE);
            try {
                if (! table.getValueAt(row, posisiWarna).toString().isEmpty()) {
                    component.setBackground(Color.WHITE);
                    component.setForeground(LookAndFeelSMC.DARK_BLUE);
                }
            } catch (Exception e) {
            }
        }

        try {
            if (Integer.parseInt(table.getValueAt(row, posisiKondisi).toString()) > 0) {
                component.setBackground(LookAndFeelSMC.DARK_RED);
                component.setForeground(LookAndFeelSMC.PALE_RED);
                if (column == posisiWarna) {
                    if (! table.getValueAt(row, posisiWarna).toString().isEmpty()) {
                        component.setBackground(Color.WHITE);
                        component.setForeground(LookAndFeelSMC.DARK_BLUE);
                    }
                }
            }
        } catch (Exception e) {
        }
        if (isSelected) {
            component.setFont(component.getFont().deriveFont(Font.BOLD));
        } else {
            component.setFont(component.getFont().deriveFont(Font.PLAIN));
        }
        return component;
    }

    public void setWarnaKolom(int posisi) {
        posisiWarna = posisi;
    }

    public void setWarnaKolomKondisi(int posisi) {
        posisiKondisi = posisi;
    }
}
