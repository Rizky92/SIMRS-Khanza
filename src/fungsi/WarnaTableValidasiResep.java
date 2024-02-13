/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fungsi;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

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
            component.setBackground(new Color(255, 244, 244));
            component.setForeground(new Color(50, 50, 50));
        } else {
            component.setBackground(new Color(255, 255, 255));
            component.setForeground(new Color(50, 50, 50));
        }
        
        try {
            if (Integer.parseInt(table.getValueAt(row, posisiKondisi).toString()) > 0) {
                component.setBackground(new Color(200, 0, 0));
                component.setForeground(new Color(255, 230, 230));
                if (column == posisiWarna) {
                    if (! table.getValueAt(row, posisiWarna).toString().isEmpty()) {
                        component.setBackground(new Color(255, 255, 255));
                        component.setForeground(new Color(55, 55, 175));
                    }            
                }
            }
        } catch (Exception e) {
        }
        
        return component;
    }
    
    public void setWarnaKolom(int posisi)
    {
        posisiWarna = posisi;
    }
    
    public void setWarnaKolomKondisi(int posisi)
    {
        posisiKondisi = posisi;
    }
}
