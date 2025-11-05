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
public class WarnaTableKasirRalan extends DefaultTableCellRenderer {

    private static Color BACKGROUND_DEFAULT = UIManager.getColor("Table.background");
    private static Color FOREGROUND_DEFAULT = UIManager.getColor("Table.foreground");
    private static Color BACKGROUND_SELECTION = UIManager.getColor("Table.selectionBackground");
    private static Color FOREGROUND_SELECTION = UIManager.getColor("Table.selectionForeground");
    private static Color BACKGROUND_TTV = new Color(30, 230, 255);
    private static Color FOREGROUND_TTV = new Color(45, 40, 55);
    private static Color BACKGROUND_SUDAH = new Color(200, 0, 0);
    private static Color FOREGROUND_SUDAH = new Color(255, 230, 230);
    private static Color BACKGROUND_BATAL = new Color(255, 243, 109);
    private static Color FOREGROUND_BATAL = new Color(120, 110, 50);
    private static Color BACKGROUND_KELUAR = new Color(152, 152, 156);
    private static Color FOREGROUND_KELUAR = new Color(245, 245, 255);
    private static Color BACKGROUND_DIRAWAT = new Color(119, 221, 119);
    private static Color FOREGROUND_DIRAWAT = new Color(245, 255, 245);
    private static Color BACKGROUND_BAYAR = new Color(50, 50, 50);
    private static Color FOREGROUND_BAYAR = new Color(255, 255, 255);

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column){
        Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        if (row % 2 == 1){
            component.setBackground(new Color(255,244,244));
            component.setForeground(new Color(50,50,50));
        }else{
            component.setBackground(new Color(255,255,255));
            component.setForeground(new Color(50,50,50));
        }
        if(table.getValueAt(row,10).toString().equals("Sudah")){
            component.setBackground(new Color(200,0,0));
            component.setForeground(new Color(255,230,230));
        }else if(table.getValueAt(row,10).toString().equals("Batal")){
            component.setBackground(new Color(255,243,109));
            component.setForeground(new Color(120,110,50));
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
                component.setBackground(new Color(30, 230, 255));
            }
            component.setForeground(new Color(45, 40, 55));
        }
        if(table.getValueAt(row,15).toString().equals("Sudah Bayar")){
            component.setBackground(new Color(50,50,50));
            component.setForeground(new Color(255,255,255));
        }
        return component;
    }

}