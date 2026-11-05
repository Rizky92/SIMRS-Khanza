/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fungsi;

import java.awt.Color;
import java.awt.Component;
import java.awt.Insets;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.TableCellRenderer;
import widget.LookAndFeelSMC;

/**
 *
 * @author Owner
 */
public class WarnaTable5 extends JTextArea implements TableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column){
        this.setText((String) value);
        this.setWrapStyleWord(true);
        this.setLineWrap(true);
        this.setBackground(Color.WHITE);
        this.setSelectionColor(Color.WHITE);
        this.setSelectedTextColor(Color.RED);
        this.setForeground(LookAndFeelSMC.TEXT_DARK);
        this.setMargin(new Insets(2, 3, 2, 2));
        this.setFont(new java.awt.Font("Tahoma", 0, 11));
        return this;
    }

}
