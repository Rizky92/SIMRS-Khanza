/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package id.yaski.simrskhanza.widget;

import java.awt.Color;
import com.toedter.calendar.JCalendar;

/**
 *
 * @author khanzasoft
 */
public final class Tanggal extends JCalendar {
    public Tanggal(){
        super();
        //setBackground(new Color(245,160,245));
        //setForeground(new Color(90,90,90));
        setForeground(new Color(50,50,50));
        setBackground(new Color(255,255,255));
        setFont(new java.awt.Font("Tahoma", 0, 11));
        //setBorder(javax.swing.BorderFactory.createLineBorder(new Color(212,212,152)));
        setSize(WIDTH,23);
    }

}
