package fungsi;

import AESsecurity.EnkripsiAES;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author khanzasoft
 */
public class koneksiDB {
    private static Connection connection = null;
    private static final Properties prop = new Properties();
    private static final MysqlDataSource dataSource = new MysqlDataSource();


    public static Connection condb() {
        try {
            if (connection == null || connection.isClosed()) {
                try (FileInputStream fis = new FileInputStream("setting/database.xml")) {
                    prop.loadFromXML(fis);
                    dataSource.setURL("jdbc:mysql://" + EnkripsiAES.decrypt(prop.getProperty("HOST")) + ":" + EnkripsiAES.decrypt(prop.getProperty("PORT")) + "/" + EnkripsiAES.decrypt(prop.getProperty("DATABASE")) + "?zeroDateTimeBehavior=convertToNull&autoReconnect=true&useCompression=true");
                    dataSource.setUser(EnkripsiAES.decrypt(prop.getProperty("USER")));
                    dataSource.setPassword(EnkripsiAES.decrypt(prop.getProperty("PAS")));
                    // dataSource.setCachePreparedStatements(true);
                    dataSource.setUseCompression(true);
                    // dataSource.setAutoReconnectForPools(true);
                    // dataSource.setUseLocalSessionState(true);
                    // dataSource.setUseLocalTransactionState(true);

                    int retries = 3;
                    while (retries > 0) {
                        try {
                            connection = dataSource.getConnection();
                            System.out.println("\n" +
                                "  Koneksi Berhasil. Sorry bro loading, silahkan baca dulu.... \n\n" +
                                "  Software ini adalah Software Menejemen Rumah Sakit/Klinik/\n" +
                                "  Puskesmas yang gratis dan boleh digunakan siapa saja tanpa dikenai \n" +
                                "  biaya apapun. Dilarang keras memperjualbelikan/mengambil \n" +
                                "  keuntungan dari Software ini dalam bentuk apapun tanpa seijin pembuat \n" +
                                "  software (Khanza.Soft Media).\n\n" +
                                "  #    ____  ___  __  __  ____   ____    _  __ _                              \n" +
                                "  #   / ___||_ _||  \\/  ||  _ \\ / ___|  | |/ /| |__    __ _  _ __   ____ __ _ \n" +
                                "  #   \\___ \\ | | | |\\/| || |_) |\\___ \\  | ' / | '_ \\  / _` || '_ \\ |_  // _` |\n" +
                                "  #    ___) || | | |  | ||  _ <  ___) | | . \\ | | | || (_| || | | | / /| (_| |\n" +
                                "  #   |____/|___||_|  |_||_| \\_\\|____/  |_|\\_\\|_| |_| \\__,_||_| |_|/___|\\__,_|\n" +
                                "  #                                                                           \n\n" +
                                "  Lisensi yang dianut di software ini https://en.wikipedia.org/wiki/Aladdin_Free_Public_License \n" +
                                "  Informasi dan panduan bisa dicek di halaman https://github.com/mas-elkhanza/SIMRS-Khanza/wiki \n" +
                                "  Bagi yang ingin berdonasi untuk pengembangan aplikasi ini bisa ke BSI 1015369872 atas nama Windiarto");
                            break;
                        } catch (SQLException e) {
                            retries--;
                            JOptionPane.showMessageDialog(null, "Gagal koneksi ke database. Sisa percobaan : " + retries);
                            if (retries == 0) {
                                JOptionPane.showMessageDialog(null, "Koneksi ke database gagal. Silakan periksa koneksi jaringan atau konfigurasi database.");
                                throw new SQLException("Gagal koneksi ke database setelah beberapa percobaan.", e);
                            }
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException ie) {
                                Thread.currentThread().interrupt();
                                throw new SQLException("Thread terinterupsi saat mencoba koneksi." + ie);
                            }
                        }
                    }
                } catch (IOException e) {
                    throw new SQLException("Notif : " + e);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(koneksiDB.class.getName()).log(Level.SEVERE, null, ex);
        }
        return connection;
    }

    private static String raw(String key, String defaultValue) {
        try (FileInputStream fs = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fs);
            return prop.getProperty(key, defaultValue).trim();
        } catch (Exception e) {
            return defaultValue;
        }
    }

    private static String raw(String key) {
        return raw(key, "");
    }

    private static String rawe(String key, String defaultValue) {
        defaultValue = EnkripsiAES.encrypt(defaultValue);
        try (FileInputStream fs = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fs);
            return EnkripsiAES.decrypt(prop.getProperty(key, defaultValue)).trim();
        } catch (Exception e) {
            return EnkripsiAES.decrypt(defaultValue).trim();
        }
    }

    private static String rawe(String key) {
        return rawe(key, "");
    }

    private static String rawAPM(String key, String defaultValue) {
        try (FileInputStream fs = new FileInputStream("setting/apm.xml")) {
            prop.loadFromXML(fs);
            return prop.getProperty(key, defaultValue).trim();
        } catch (Exception e) {
            return defaultValue;
        }
    }

    private static String rawAPM(String key) {
        return rawAPM(key, "");
    }

    private static String raweAPM(String key, String defaultValue) {
        defaultValue = EnkripsiAES.encrypt(defaultValue);
        try (FileInputStream fs = new FileInputStream("setting/apm.xml")) {
            prop.loadFromXML(fs);
            return EnkripsiAES.decrypt(prop.getProperty(key, defaultValue)).trim();
        } catch (Exception e) {
            return EnkripsiAES.decrypt(defaultValue).trim();
        }
    }

    private static String raweAPM(String key) {
        return raweAPM(key, "");
    }

    public static String PRINTER_REGISTRASI() {
        return rawAPM("PRINTER_REGISTRASI");
    }

    public static String PRINTER_BARCODE() {
        return rawAPM("PRINTER_BARCODE");
    }

    public static String PRINTER_ANTRIAN() {
        return rawAPM("PRINTER_ANTRIAN");
    }

    public static int PRINTJUMLAHBARCODE() {
        return Integer.parseInt(rawAPM("PRINTJUMLAHBARCODE", "3"));
    }

    public static int PRINTJUMLAHANTRIANFARMASI() {
        return Integer.parseInt(rawAPM("PRINTJUMLAHANTRIANFARMASI", "2"));
    }

    public static String URLAPLIKASIFINGERPRINTBPJS() {
        return rawAPM("URLAPLIKASIFINGERPRINTBPJS");
    }

    public static String URLAPLIKASIFRISTABPJS() {
        return rawAPM("URLAPLIKASIFRISTABPJS");
    }

    public static String USERFINGERPRINTBPJS() {
        return raweAPM("USERFINGERPRINTBPJS");
    }

    public static String PASSWORDFINGERPRINTBPJS() {
        return raweAPM("PASSWORDFINGERPRINTBPJS");
    }

    public static boolean ANTRIANPREFIXHURUF() {
        return raw("ANTRIANPREFIXHURUF", "no").equalsIgnoreCase("yes");
    }

    public static String[] PREFIXHURUFAKTIF() {
        if (!ANTRIANPREFIXHURUF()) {
            return null;
        }
        return raw("PREFIXHURUFAKTIF").replaceAll("\\s+", "").split(",");
    }

    public static String[] TOMBOLDIMATIKAN() {
        return rawAPM("TOMBOLDIMATIKAN").toLowerCase().replaceAll("\\s+", "").split(",");
    }

    public static String KODEPOLIEKSEKUTIF() {
        return rawAPM("KODEPOLIEKSEKUTIF");
    }

    public static boolean JADIKANBOOKINGSURATKONTROL() {
        return raw("JADIKANBOOKINGSURATKONTROL", "no").equalsIgnoreCase("yes");
    }

    public static boolean BOOKINGLANGSUNGREGISTRASI() {
        return raw("BOOKINGLANGSUNGREGISTRASI", "no").equalsIgnoreCase("yes");
    }

    public static boolean PREVIEWHASILPRINT() {
        return rawAPM("PREVIEWHASILPRINT", "no").equalsIgnoreCase("yes");
    }

    public static boolean REGISTRASISATUJAMSEBELUMJAMPRAKTEK() {
        return rawAPM("REGISTRASISATUJAMSEBELUMJAMPRAKTEK", "no").equalsIgnoreCase("yes");
    }

    public static String[] VALIDASIBIOMETRIKAKTIF() {
        return rawAPM("VALIDASIBIOMETRIKAKTIF", "fingerprint,frista").toLowerCase().replaceAll("\\s+", "").split(",");
    }

    public static String HOST() {
        return rawe("HOST");
    }

    public static String DATABASE() {
        return rawe("DATABASE");
    }

    public static String PORT() {
        return rawe("PORT");
    }

    public static String USER() {
        return rawe("USER");
    }

    public static String CARICEPAT() {
        return raw("CARICEPAT", "no");
    }

    public static String URLAPIBPJS() {
        return raw("URLAPIBPJS");
    }

    public static String SECRETKEYAPIBPJS() {
        return rawe("SECRETKEYAPIBPJS");
    }

    public static String USERKEYAPIBPJS() {
        return rawe("USERKEYAPIBPJS");
    }

    public static String CONSIDAPIBPJS() {
        return rawe("CONSIDAPIBPJS");
    }

    public static String URLAPIMOBILEJKN() {
        return raw("URLAPIMOBILEJKN");
    }

    public static String SECRETKEYAPIMOBILEJKN() {
        return rawe("SECRETKEYAPIMOBILEJKN");
    }

    public static String USERKEYAPIMOBILEJKN() {
        return rawe("USERKEYAPIMOBILEJKN");
    }

    public static String CONSIDAPIMOBILEJKN() {
        return rawe("CONSIDAPIMOBILEJKN");
    }

    public static boolean ADDANTRIANAPIMOBILEJKN() {
        return raw("ADDANTRIANAPIMOBILEJKN", "no").equalsIgnoreCase("yes");
    }

    public static String BASENOREG() {
        return raw("BASENOREG");
    }

    public static String URUTNOREG() {
        return raw("URUTNOREG");
    }

    public static boolean JADWALDOKTERDIREGISTRASI() {
        return raw("JADWALDOKTERDIREGISTRASI", "no").equalsIgnoreCase("yes");
    }

    public static String IPPRINTERTRACER() {
        return raw("IPPRINTERTRACER");
    }

    public static boolean AKTIFKANTRACKSQL() {
        return rawe("AKTIFKANTRACKSQL", "no").equalsIgnoreCase("yes");
    }
    public koneksiDB() {
    }
}
