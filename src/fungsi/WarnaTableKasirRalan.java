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

        Color foreground = new Color(50, 50, 50);
        Color background = new Color(255, 255, 255);
        Color alternatbg = darken(background, 0.05f);

        DefaultTableModel tabMode = (DefaultTableModel) table.getModel();

        row = table.convertRowIndexToModel(row);

        switch (tabMode.getValueAt(row, 10).toString()) {
            case "TTV":
                foreground = new Color(45, 40, 55);
                background = new Color(30, 230, 255);
                alternatbg = darken(background, 0.05f);
                break;
            case "Sudah":
                foreground = new Color(255, 230, 230);
                alternatbg = new Color(200, 0, 0);
                background = lighten(alternatbg, 0.1f);
                break;
            case "Batal":
                foreground = new Color(120, 110, 50);
                background = new Color(255, 243, 109);
                alternatbg = darken(background, 0.05f);
                break;
            case "Dirujuk":
            case "Meninggal":
            case "Pulang Paksa":
                foreground = new Color(245, 245, 255);
                background = new Color(152, 152, 156);
                alternatbg = darken(background, 0.05f);
                break;
            case "Dirawat":
                foreground = new Color(245, 255, 245);
                background = new Color(119, 221, 119);
                alternatbg = darken(background, 0.05f);
                break;
            default:
                foreground = new Color(50, 50, 50);
                background = new Color(255, 255, 255);
                alternatbg = darken(background, 0.05f);
                break;
        }

        if (tabMode.getValueAt(row, 15).toString().equals("Sudah Bayar")) {
            foreground = new Color(255, 255, 255);
            alternatbg = new Color(50, 50, 50);
            background = lighten(alternatbg, 0.1f);
        }

        if (row % 2 == 1) {
            setBackground(alternatbg);
        } else {
            setBackground(background);
        }
        setForeground(foreground);

        return this;
    }

    private Color lighten(Color color, double strength) {
        int min = 0, max = 255;

        double r = color.getRed();
        double g = color.getGreen();
        double b = color.getBlue();

        return new Color(
            (int) clamp(r + (strength * max), min, max),
            (int) clamp(g + (strength * max), min, max),
            (int) clamp(b + (strength * max), min, max)
        );
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
