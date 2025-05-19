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
import fungsi.sekuel;
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
            UIManager.setLookAndFeel(new FlatLightLaf());
            System.setProperty("flatlaf.animation", "true");
            UIManager.put("MenuBar.selectionArc", 6);
            UIManager.put("TabbedPane.showTabSeparators", false);
            UIManager.put("TabbedPane.tabSeparatorsFullHeight", false);
            UIManager.put("TabbedPane.cardTabArc", 0);
            UIManager.put("ScrollBar.showButtons", true);
            UIManager.put("ScrollBar.width", 16);
            UIManager.put("ScrollPane.smoothScrolling", 1);
            UIManager.put("Table.alternateRowColor", new java.awt.Color(255, 240, 240));
            UIManager.put("Table.foreground", new java.awt.Color(50, 50, 50));
            UIManager.put("Table.background", new java.awt.Color(255, 255, 255));
            UIManager.put("Table.gridColor", new java.awt.Color(226, 231, 221));
            UIManager.put("Table.showHorizontalLines", false);
            UIManager.put("Table.showVerticalLines", true);
            UIManager.put("Table.rowHeight", 26);
            UIManager.put("Table.cellMargins", new java.awt.Insets(2, 4, 2, 4));
            // UIManager.put("Table.intercellSpacing", new java.awt.Dimension(1, 1));
            UIManager.put("Component.arrowType", "chevron");
            UIManager.put("Component.innerFocusWidth", 1);
            UIManager.put("TextBox.focusWidth", 3);
            UIManager.put("Component.focusWidth", 1);
            UIManager.put("buttonType", "borderless");
            UIManager.put("Button.innerFocusWidth", 1);
            UIManager.put("Button.arc", 6);
            UIManager.put("Component.arc", 6);
            UIManager.put("CheckBox.arc", 6);
            UIManager.put("ProgressBar.arc", 6);
            UIManager.put("TextBox.arc", 6);
            UIManager.put("PasswordField.showCapsLock", true);
            UIManager.put("PasswordField.showRevealButton", false);
            UIManager.put("TextArea.selectionForeground", new java.awt.Color(255, 255, 255));
            UIManager.put("TextArea.selectionBackground", new java.awt.Color(38, 117, 191));
            UIManager.put("TextField.selectionForeground", new java.awt.Color(255, 255, 255));
            UIManager.put("TextField.selectionBackground", new java.awt.Color(38, 117, 191));
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
