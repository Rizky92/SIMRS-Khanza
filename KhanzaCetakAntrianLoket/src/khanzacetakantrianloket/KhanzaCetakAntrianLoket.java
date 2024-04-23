/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package khanzacetakantrianloket;

import fungsi.koneksiDB;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;

/**
 *
 * @author igos
 */
public class KhanzaCetakAntrianLoket {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String printerAntrean = null;
        for (PrintService ps: PrintServiceLookup.lookupPrintServices(null, null)) {
            System.out.println("Printer ditemukan: " + ps.getName());
            if (ps.getName().equals(koneksiDB.PRINTERCETAKANTREAN())) {
                printerAntrean = ps.getName();
            }
        }
        if (printerAntrean != null) {
            System.out.println("Setting PRINTERCETAKANTREAN menggunakan printer: " + printerAntrean);
        } 
        frmUtama antrian=new frmUtama();
        antrian.setVisible(true);
    }
}
