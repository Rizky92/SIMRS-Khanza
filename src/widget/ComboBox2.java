/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package widget;

import java.awt.Color;
import java.awt.Component;
import java.util.Map;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 *
 * @author dosen3
 */
public final class ComboBox2 extends JComboBox {
    private Map<Object, Object> items;

    public ComboBox2() {
        setFont(new java.awt.Font("Tahoma", 0, 11));
        setBackground(new Color(255, 255, 255));
        setForeground(new Color(50, 50, 50));
        setSize(WIDTH, 23);
        setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

                return c;
            }
        });
    }
}
