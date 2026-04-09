/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package jrviewersmc;

import java.io.File;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;

/**
 *
 * @author USER
 */
public class Utils {

    private Utils() {}

    public static void open(File file) {
        if (!file.exists()) {
            JOptionPane.showMessageDialog(null, "File tidak ditemukan:\n" + file.getAbsolutePath(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            JasperPrint jasperPrint = (JasperPrint) JRLoader.loadObject(file);
            JasperViewer viewer = new JasperViewer(jasperPrint, false);
            viewer.setExtendedState(JFrame.MAXIMIZED_BOTH);
            viewer.setLocationRelativeTo(null);
            viewer.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            viewer.setTitle("JRViewer SMC - " + file.getName());
            viewer.setVisible(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Gagal membuka file:\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
