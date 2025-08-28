/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fungsi;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Owner
 */
public class WarnaTableKasirRalan extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        DefaultTableModel tabMode = (DefaultTableModel) table.getModel();
        Color background = new Color(255, 255, 255);

        row = table.convertRowIndexToModel(row);

        switch (tabMode.getValueAt(row, 10).toString()) {
            case "TTV":
                setForeground(new Color(45, 40, 55));
                background = new Color(30, 230, 255);
                if (row % 2 == 1) {
                    setBackground(darken(background, 0.05f));
                } else {
                    setBackground(background);
                }
                break;
            case "Sudah":
                setForeground(new Color(255, 230, 230));
                setBackground(new Color(230, 10, 0));
                break;
            case "Batal":
                setForeground(new Color(90, 80, 30));
                setBackground(new Color(255, 243, 109));
                break;
            case "Dirujuk":
            case "Meninggal":
            case "Pulang Paksa":
                setForeground(new Color(40, 40, 50));
                setBackground(new Color(160, 160, 170));
                break;
            case "Dirawat":
                setForeground(new Color(30, 60, 30));
                setBackground(new Color(119, 221, 119));
                break;
            default:
                setForeground(new Color(50, 50, 50));
                background = new Color(255, 255, 255);
                if (row % 2 == 1) {
                    setBackground(darken(background, 0.05f));
                } else {
                    setBackground(background);
                }
                break;
        }

        if (tabMode.getValueAt(row, 15).toString().equals("Sudah Bayar")) {
            setForeground(new Color(255, 255, 255));
            setBackground(new Color(50, 50, 50));
        }

        return this;
    }

    private Color darken(Color color, double strength) {
        int min = 0, max = 255;

        double r = color.getRed();
        double g = color.getGreen();
        double b = color.getBlue();

        return new Color(
            (int) clamp(r - (strength * max), min, max),
            (int) clamp(g - (strength * max), min, max),
            (int) clamp(b - (strength * max), min, max)
        );
    }

    private double clamp(double value, int min, int max) {
        if (value < min) {
            return min;
        }

        if (value > max) {
            return max;
        }

        return value;
    }
}
