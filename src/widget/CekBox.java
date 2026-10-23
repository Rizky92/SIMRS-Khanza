/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package widget;

import java.awt.Cursor;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.plaf.basic.BasicCheckBoxUI;

public class CekBox extends JCheckBox {
    /*
    private static final long serialVersionUID = 1L;

    public CekBox(){
        super();
        setFont(new java.awt.Font("Tahoma", 0, 11));
        //setBackground(new Color(209,209,209));
        //setForeground(new Color(90,90,90));
        setBackground(new Color(255,255,255));
        setForeground(new Color(50,50,50));
        setFocusPainted(false);

        this.setBorder(javax.swing.BorderFactory.createLineBorder(new Color(239,244,234)));
        setOpaque(true);
        setSize(WIDTH,23);
    }
    */
    private static final long serialVersionUID = 2L;

    public CekBox() {
        super();
        setFont(new Font("Tahoma", Font.PLAIN, 11));
        setFocusPainted(false);
        setRolloverEnabled(true);
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
        setIconTextGap(4);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }
}