/*
  Dilarang keras memperjualbelikan/mengambil keuntungan dari Software 
  ini dalam bentuk apapun tanpa seijin pembuat software
  (Khanza.Soft Media). Bagi yang sengaja membajak softaware ini ta
  npa ijin, kami sumpahi sial 1000 turunan, miskin sampai 500 turu
  nan. Selalu mendapat kecelakaan sampai 400 turunan. Anak pertama
  nya cacat tidak punya kaki sampai 300 turunan. Susah cari jodoh
  sampai umur 50 tahun sampai 200 turunan. Ya Alloh maafkan kami 
  karena telah berdoa buruk, semua ini kami lakukan karena kami ti
  dak pernah rela karya kami dibajak tanpa ijin.
 */
package simrskhanza;

import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.fonts.inter.FlatInterFont;
import fungsi.sekuel;
import java.awt.RenderingHints;
import java.io.File;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import usu.widget.util.WidgetUtilities;

/**
 *
 * @author khanzasoft
 */
public class SIMRSKhanza {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            FlatInterFont.install();
            FlatLightLaf.setPreferredFontFamily(FlatInterFont.FAMILY);
            FlatLightLaf.registerCustomDefaultsSource(new File("themes"));
            FlatLightLaf.setup();
            System.setProperty("flatlaf.animation", "true");
        } catch (Exception ex) {
            System.err.println("Failed to initialize LaF");
        }
        SwingUtilities.invokeLater(() -> {
            frmUtama utama = frmUtama.getInstance();
            utama.isWall();
            utama.setVisible(true);
            sekuel.nyalakanBatasEdit();
        });
    }
}
