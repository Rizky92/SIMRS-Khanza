/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fungsi;

import AESsecurity.EnkripsiAES;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author khanzasoft
 */
public final class koneksiDB {
    public koneksiDB(){}    
    private static Connection connection=null;
    private static final Properties prop = new Properties();  
    private static final MysqlDataSource dataSource=new MysqlDataSource();
    private static String caricepat="",var="";
    public static Connection condb(){      
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
                            System.out.println("\n"
                                + "  Koneksi Berhasil. Sorry bro loading, silahkan baca dulu.... \n\n"
                                + "  Software ini adalah Software Menejemen Rumah Sakit/Klinik/\n"
                                + "  Puskesmas yang gratis dan boleh digunakan siapa saja tanpa dikenai \n"
                                + "  biaya apapun. Dilarang keras memperjualbelikan/mengambil \n"
                                + "  keuntungan dari Software ini dalam bentuk apapun tanpa seijin pembuat \n"
                                + "  software (Khanza.Soft Media).\n\n"
                                + "  #    ____  ___  __  __  ____   ____    _  __ _                              \n"
                                + "  #   / ___||_ _||  \\/  ||  _ \\ / ___|  | |/ /| |__    __ _  _ __   ____ __ _ \n"
                                + "  #   \\___ \\ | | | |\\/| || |_) |\\___ \\  | ' / | '_ \\  / _` || '_ \\ |_  // _` |\n"
                                + "  #    ___) || | | |  | ||  _ <  ___) | | . \\ | | | || (_| || | | | / /| (_| |\n"
                                + "  #   |____/|___||_|  |_||_| \\_\\|____/  |_|\\_\\|_| |_| \\__,_||_| |_|/___|\\__,_|\n"
                                + "  #                                                                           \n\n"
                                + "  Lisensi yang dianut di software ini https://en.wikipedia.org/wiki/Aladdin_Free_Public_License \n"
                                + "  Informasi dan panduan bisa dicek di halaman https://github.com/mas-elkhanza/SIMRS-Khanza/wiki \n"
                                + "  Bagi yang ingin berdonasi untuk pengembangan aplikasi ini bisa ke BSI 1015369872 atas nama Windiarto");
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
    
    public static boolean ANTRIANPREFIXHURUF() {
        try (FileInputStream fs = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fs);
            return prop.getProperty("ANTRIANPREFIXHURUF", "no").trim().equalsIgnoreCase("yes");
        } catch (Exception e) {
            return false;
        }
    }
    
    public static String[] PREFIXHURUFAKTIF() {
        if (!ANTRIANPREFIXHURUF()) {
            return null;
        }
        try (FileInputStream fs = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fs);
            return prop.getProperty("PREFIXHURUFAKTIF", "").trim().replaceAll("\\s+", "").split(",");
        } catch (Exception e) {
            return null;
        }
    }
    
    public static String ANTRIAN() {
        try (FileInputStream fs = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fs);
            return prop.getProperty("ANTRIAN", "").trim().toLowerCase();
        } catch (Exception e) {
            return "";
        }
    }
    
    public static String cariCepat(){
        try{
            prop.loadFromXML(new FileInputStream("setting/database.xml"));
            caricepat=prop.getProperty("CARICEPAT");
        }catch(Exception e){
            caricepat="tidak aktif"; 
        }
        return caricepat;
    }
    
    public static String HOST(){
        try{
            prop.loadFromXML(new FileInputStream("setting/database.xml"));
            var=EnkripsiAES.decrypt(prop.getProperty("HOSTHYBRIDWEB", EnkripsiAES.encrypt("localhost")));
        }catch(Exception e){
            var="localhost"; 
        }
        return var;
    }
    
    public static String PORT(){
        try{
            prop.loadFromXML(new FileInputStream("setting/database.xml"));
            var=EnkripsiAES.decrypt(prop.getProperty("PORT", EnkripsiAES.encrypt("3306")));
        }catch(Exception e){
            var="3306"; 
        }
        return var;
    }
    
    public static String DATABASE(){
        try{
            prop.loadFromXML(new FileInputStream("setting/database.xml"));
            var=EnkripsiAES.decrypt(prop.getProperty("DATABASE", EnkripsiAES.encrypt("sik")));
        }catch(Exception e){
            var="sik"; 
        }
        return var;
    }
    
    public static String SMCINTERNALAPPAPIURL() {
        try (FileInputStream fs = new FileInputStream("setting/database.xml")) {
            prop.loadFromXML(fs);
            var=EnkripsiAES.decrypt(prop.getProperty("SMCINTERNALAPPAPIURL", EnkripsiAES.encrypt("http://smc-internal-app.test/api")));
        } catch (Exception e) {
            var="http://smc-internal-app.test/api";
        }
        return var;
    }
    
}
