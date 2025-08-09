package fungsi;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class WarnaTableKasirRalan extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        
        Color foreground = new Color(50, 50, 50);
        Color background = new Color(255, 255, 255);
        Color alternatbg = darken(background, 0.05f);
        
        row = table.convertRowIndexToModel(row);
        
        if (column == 10 && table.getModel().getValueAt(row, 10).toString().equals("TTV")) {
            foreground = new Color(230, 230, 255);
            background = new Color(0, 30, 230);
            alternatbg = lighten(background, 0.1f);
        }
        
        if (table.getModel().getValueAt(row, 10).toString().equals("Sudah")) {
            foreground = new Color(255, 230, 230);
            background = new Color(200, 0, 0);
            alternatbg = lighten(background, 0.1f);
        } else if (table.getModel().getValueAt(row, 10).toString().equals("Batal")) {
            foreground = new Color(120, 110, 50);
            background = new Color(255, 243, 109);
            alternatbg = darken(background, 0.1f);
        } else if (table.getModel().getValueAt(row, 10).toString().equals("Dirujuk") || table.getModel().getValueAt(row, 10).toString().equals("Meninggal") || table.getModel().getValueAt(row, 10).toString().equals("Pulang Paksa")) {
            foreground = new Color(245, 245, 255);
            background = new Color(152, 152, 156);
            alternatbg = darken(background, 0.1f);
        } else if (table.getModel().getValueAt(row, 10).toString().equals("Dirawat")) {
            foreground = new Color(245, 255, 245);
            background = new Color(119, 221, 119);
            alternatbg = darken(background, 0.1f);
        }
        
        if (table.getModel().getValueAt(row, 15).toString().equals("Sudah Bayar")) {
            foreground = new Color(255, 255, 255);
            background = new Color(50, 50, 50);
            alternatbg = lighten(background, 0.1f);
        }
        
        if (row % 2 == 1) {
            setBackground(alternatbg);
        } else {
            setBackground(background);
        }
        setForeground(foreground);
        
        ((DefaultTableModel) table.getModel()).fireTableRowsUpdated(row, row);
        
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
