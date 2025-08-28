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
import javax.swing.table.DefaultTableModel;

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
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        Color backgroundColor = BACKGROUND_DEFAULT;
        Color foregroundColor = FOREGROUND_DEFAULT;
        switch (table.getValueAt(row, 10).toString()) {
            case "TTV":
                backgroundColor = row % 2 == 1 ? ColorFunctions.darken(BACKGROUND_TTV, 0.05f) : BACKGROUND_TTV;
                foregroundColor = FOREGROUND_TTV;
                break;
            case "Sudah":
                backgroundColor = row % 2 == 1 ? ColorFunctions.darken(BACKGROUND_SUDAH, 0.05f) : BACKGROUND_SUDAH;
                foregroundColor = FOREGROUND_SUDAH;
                break;
            case "Batal":
                backgroundColor = row % 2 == 1 ? ColorFunctions.darken(BACKGROUND_BATAL, 0.05f): BACKGROUND_BATAL;
                foregroundColor = FOREGROUND_BATAL;
                break;
            case "Dirujuk":
            case "Meninggal":
            case "Pulang Paksa":
                backgroundColor = row % 2 == 1 ? ColorFunctions.darken(BACKGROUND_KELUAR, 0.05f) : BACKGROUND_KELUAR;
                foregroundColor = FOREGROUND_KELUAR;
                break;
            case "Dirawat":
                backgroundColor = row % 2 == 1 ? ColorFunctions.darken(BACKGROUND_DIRAWAT, 0.05f) : BACKGROUND_DIRAWAT;
                foregroundColor = FOREGROUND_DIRAWAT;
                break;
            default:
                backgroundColor = row % 2 == 1 ? ColorFunctions.darken(BACKGROUND_DEFAULT, 0.05f) : BACKGROUND_DEFAULT;
                foregroundColor = FOREGROUND_DEFAULT;
                break;
        }
        if (table.getValueAt(row, 15).toString().equals("Sudah Bayar")) {
            backgroundColor = row % 2 == 0 ? ColorFunctions.lighten(BACKGROUND_BAYAR, 0.05f) : BACKGROUND_BAYAR;
            foregroundColor = FOREGROUND_BAYAR;
        }
        if (isSelected) {
            backgroundColor = BACKGROUND_SELECTION;
            foregroundColor = FOREGROUND_SELECTION;
        }
        component.setBackground(backgroundColor);
        component.setForeground(foregroundColor);
        return component;
    }

}
