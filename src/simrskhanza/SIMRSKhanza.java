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

import fungsi.sekuel;
import java.util.TimeZone;
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
        System.setProperty("user.timezone", "Asia/Makassar");
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Makassar"));
        WidgetUtilities.invokeLater(() -> {
            frmUtama utama = frmUtama.getInstance();
            utama.isWall();
            utama.setVisible(true);

            sekuel.nyalakanBatasEdit();
            TimeZone tz = TimeZone.getDefault();
            int offsetMillis = tz.getRawOffset();
            int hour = offsetMillis / (1000 * 60 * 60);
            int minute = Math.abs((offsetMillis / (1000 * 60)) % 60);
            
            System.out.println("Timezone : " + TimeZone.getDefault().getID() + " (UTC " + String.format("%+03d:%02d", hour, minute) + ")");
        });
    }
}
