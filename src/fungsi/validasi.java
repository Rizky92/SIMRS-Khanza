/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fungsi;


import java.awt.Desktop;
import java.awt.Dialog.ModalExclusionType;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CodingErrorAction;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Properties;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.JTextComponent;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import net.sf.jasperreports.engine.JRResultSetDataSource;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;
import uz.ncipro.calendar.JDateTimePicker;
import widget.Button;
import widget.ComboBox;
import widget.Tanggal;
import widget.TextArea;
import widget.TextBox;
/**
 *
 * @author Owner
 */
public final class validasi {
    private int a,j,i,result=0;
    private String s,s1,auto,PEMBULATANHARGAOBAT=koneksiDB.PEMBULATANHARGAOBAT();
    private final Connection connect=koneksiDB.condb();
    private final sekuel sek=new sekuel();
    private final java.awt.Desktop desktop = java.awt.Desktop.getDesktop();
    private final DecimalFormat df2 = new DecimalFormat("###,###,###,###,###,###,###");  
    private final DecimalFormat df4 = new DecimalFormat("###,###,###,###,###,###,###.#################");  
    private final DecimalFormat df5 = new DecimalFormat("###,###,###,###,###,###,###.##");  
    private final DecimalFormat df3 = new DecimalFormat("######"); 
    private final DecimalFormat df6 = new DecimalFormat("######.###"); 
    private final DecimalFormat df7 = new DecimalFormat("######.#"); 
    private PreparedStatement ps;
    private ResultSet rs;
    private File file;
    private boolean status=true;
    private final Calendar now = Calendar.getInstance();
    private final int year=(now.get(Calendar.YEAR));
    private String[] nomina={"","satu","dua","tiga","empat","lima","enam",
                         "tujuh","delapan","sembilan","sepuluh","sebelas"};
    private String[] nominal2 = {
        "nol","satu","dua","tiga","empat","lima","enam",
        "tujuh","delapan","sembilan","sepuluh","sebelas"
    };
    
    public validasi(){
        super();
    };
    
    public String fingerSmc(String kodedokter, String tanggal) {
        String finger = sek.cariIsiSmc("select sha1(sidikjari.sidikjari) from sidikjari join pegawai on pegawai.id = sidikjari.id where pegawai.nik = ?", kodedokter);
        return String.format("Dikeluarkan di %s, Kabupaten/Kota %s\nDitandatangani secara elektronik oleh %s\nID %s\n%s",
            akses.getnamars(), akses.getkabupatenrs(), sek.cariIsiSmc("select dokter.nm_dokter from dokter where dokter.kd_dokter = ?", kodedokter),
            finger.isBlank() ? kodedokter : finger, tanggal
        );
    }
    
    public void autonomorSmc(JTextComponent component, String prefix, String table, int panjang, String pad) {
        component.setText(sek.autonomorSmc(prefix, table, panjang, pad));
    }
    
    public void autonomorSmc(JTextComponent component, String prefix, String separator, String table, String kolom, int panjang, String pad, String tanggal, int next) {
        component.setText(sek.autonomorSmc(prefix, separator, table, kolom, panjang, pad, tanggal, next));
    }
    
    public void autonomorSmc(JTextComponent component, String prefix, String separator, String table, String kolom, int panjang, String pad, Tanggal tanggal, int next) {
        autonomorSmc(component, prefix, separator, table, kolom, panjang, pad, getTglSmc(tanggal), next);
    }
    
    public void autonomorSmc(JTextComponent component, String prefix, String separator, String table, String kolom, int panjang, String pad, String tanggal) {
        component.setText(sek.autonomorSmc(prefix, separator, table, kolom, panjang, pad, tanggal));
    }
    
    public void autonomorSmc(JTextComponent component, String prefix, String separator, String table, String kolom, int panjang, String pad, Tanggal tanggal) {
        autonomorSmc(component, prefix, separator, table, kolom, panjang, pad, getTglSmc(tanggal));
    }
    
    public void autonomor1Smc(JTextComponent component, String prefix, String table, String kolom, int panjang, String pad, Tanggal tanggal) {
        autonomorSmc(component, prefix, "", table, kolom, panjang, pad, getTglSmc(tanggal));
    }
    
    public String getTglSmc(Tanggal tgl) {
        return new SimpleDateFormat("yyyy-MM-dd").format(tgl.getDate());
    }
    
    public String getJamSmc(ComboBox jam, ComboBox menit, ComboBox detik) {
        return jam.getSelectedItem() + ":" + menit.getSelectedItem() + ":" + detik.getSelectedItem();
    }
    
    public String getJamSmc(Tanggal tgl) {
        return new SimpleDateFormat("HH:mm:ss").format(tgl.getDate());
    }
    
    public String getTglJamSmc(Tanggal tgl, ComboBox jam, ComboBox menit, ComboBox detik) {
        return getTglSmc(tgl) + " " + getJamSmc(jam, menit, detik);
    }
    
    public String getTglJamSmc(Tanggal tgljam) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(tgljam.getDate());
    }
    
    public String setTglSmc(Date tgl) {
        return new SimpleDateFormat("dd-MM-yyyy").format(tgl);
    }
    
    public String setTglSmc(String tgl) {
        return tgl.substring(8, 10) + "-" + tgl.substring(5, 7) + "-" + tgl.substring(0, 4);
    }
    
    public String setTglJamSmc(Date tgljam) {
        return new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(tgljam);
    }
    
    public void reportTempSmc(String reportName, String reportDirName, String judul, Map reportParams) {
        reportSmc(reportName, reportDirName, judul, reportParams, "select * from temporary where temp37 = ? order by temporary.no", akses.getalamatip());
    }
    
    public void reportSmc(String reportName, String reportDirName, String judul, Map reportParams, String sql, String... values) {
        try (PreparedStatement ps = connect.prepareStatement(sql)) {
            for (int i = 0; i < values.length; i++) {
                ps.setString(i + 1, values[i]);
            }
            JasperViewer jasperViewer = new JasperViewer(JasperFillManager.fillReport("./" + reportDirName + "/" + reportName, reportParams, new JRResultSetDataSource(ps.executeQuery())), false);
            jasperViewer.setTitle(judul);
            Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
            jasperViewer.setSize(screen.width - 50, screen.height - 50);
            jasperViewer.setModalExclusionType(ModalExclusionType.TOOLKIT_EXCLUDE);
            jasperViewer.setLocationRelativeTo(null);
            jasperViewer.setVisible(true);
        } catch (Exception e) {
            System.out.println("Notif : " + e);
            JOptionPane.showMessageDialog(null, "Report can't view because : " + e);
        }
    }
    
    public void reportSmc(String reportName, String reportDirName, String judul, Map reportParams) {
        try {
            JasperViewer jv = new JasperViewer(JasperFillManager.fillReport("./" + reportDirName + "/" + reportName, reportParams, connect), false);
            jv.setTitle(judul);
            Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
            jv.setSize(screen.width - 50, screen.height - 50);
            jv.setModalExclusionType(ModalExclusionType.TOOLKIT_EXCLUDE);
            jv.setLocationRelativeTo(null);
            jv.setVisible(true);
        } catch (Exception e) {
            System.out.println("Notif : " + e);
            JOptionPane.showMessageDialog(null, "Report can't view because : " + e);
        }
    }
    
    public String capitalizeSmc(String judul) {
        return judul.substring(0, 1).toUpperCase() + judul.substring(1);
    }
    
    public String terbilangSmc(double nilai) {
        if (nilai < 12) {
            return nominal2[(int) nilai];
        }

        if (nilai >= 12 && nilai <= 19) {
            return nominal2[(int) nilai % 10] + " belas ";
        }

        if (nilai >= 20 && nilai <= 99) {
            return nominal2[(int) nilai / 10] + " puluh " + nominal2[(int) nilai % 10];
        }

        if (nilai >= 100 && nilai <= 199) {
            return "seratus " + terbilangSmc(nilai % 100);
        }

        if (nilai >= 200 && nilai <= 999) {
            return nominal2[(int) nilai / 100] + " ratus " + terbilangSmc(nilai % 100);
        }

        if (nilai >= 1_000 && nilai <= 1_999) {
            return "seribu " + terbilangSmc(nilai % 1_000);
        }

        if (nilai >= 2_000 && nilai <= 999_999) {
            return terbilangSmc((int) nilai / 1_000) + " ribu " + terbilangSmc(nilai % 1_000);
        }

        if (nilai >= 1_000_000 && nilai <= 999_999_999) {
            return terbilangSmc((int) nilai / 1_000_000) + " juta " + terbilangSmc(nilai % 1_000_000);
        }
        
        return "";
    }
    
    public void panggilUrlSmc(String app, String url) {
        if (app == null || app.isBlank()) {
            panggilUrl2(url);
        } else if (app.equalsIgnoreCase("disable")) {
            //
        } else {
            String os = System.getProperty("os.name").toLowerCase();
            String realpath = "";
            try {
                if (os.contains("windows")) {
                    if (app.equalsIgnoreCase("chrome")) {
                        realpath = "C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe";
                    } else if (app.equalsIgnoreCase("firefox")) {
                        realpath = "C:\\Program Files\\Mozilla Firefox\\firefox.exe";
                    } else if (app.equalsIgnoreCase("msedge")) {
                        realpath = "C:\\Program Files (x86)\\Microsoft\\Edge\\Application\\msedge.exe";
                    } else {
                        realpath = app;
                    }
                    if (realpath.contains(".lnk")) {
                        ProcessBuilder pb = new ProcessBuilder(new String[]{"cmd", "/c", realpath, url});
                        pb.start();
                    } else {
                        Runtime.getRuntime().exec(realpath + " " + url);
                    }
                } else if (os.contains("mac") || os.contains("nix") || os.contains("nux")) {
                    System.out.println("Notif : Sistem operasi belum disupport, menggunakan proses default...");
                    panggilUrl2(url);
                }
            } catch (Exception e) {
                System.out.println("Notif : " + e);
            }
        }
    }
    
    public void cleanupTextSmc(JTextComponent txt) {
        try {
            CharsetEncoder latin1Encoder = Charset.forName("ISO-8859-1").newEncoder();
            CharsetDecoder latin1Decoder = Charset.forName("ISO-8859-1").newDecoder();
            latin1Encoder.onMalformedInput(CodingErrorAction.REPLACE);
            latin1Encoder.onUnmappableCharacter(CodingErrorAction.REPLACE);
            latin1Encoder.replaceWith(" ".getBytes());
            
            ByteBuffer input = latin1Encoder.encode(CharBuffer.wrap(txt.getText()));
            
            CharBuffer cb = latin1Decoder.decode(input);
            txt.setText(cb.toString().trim());
            
            latin1Encoder.flush(input);
            latin1Decoder.flush(cb);
        } catch (Exception e) {
            System.out.println("Notif : " + e);
        }
    }
    
    public boolean exists(String path) {
        try {
            return new File(path).isFile();
        } catch (Exception e) {
            System.out.println("Notif : " + e);
            return false;
        }
    }
    
    public String joinSmc(String separator, String... values) {
        if (values.length == 0) {
            return "";
        }
        
        String base = values[0];
        
        for (int i = 1; i < values.length; i++) {
            base = base.concat(separator + values[i]);
        }
        
        return base;
    }
    
    public void autoNomer(DefaultTableModel tabMode,String strAwal,Integer pnj,javax.swing.JTextField teks){        
        s=Integer.toString(tabMode.getRowCount()+1);
        j=s.length();
        s1="";
        for(i = 1;i<=pnj-j;i++){
            s1=s1+"0";
        }
        teks.setText(strAwal+s1+s);
    }

    public void autoNomer(String tabel,String strAwal,Integer pnj,javax.swing.JTextField teks){
        try {
            ps=connect.prepareStatement("select * from "+tabel);
            try{
               rs=ps.executeQuery();
               rs.last();
               s=Integer.toString(rs.getRow()+1);
               j=s.length();
               s1="";
               for(i = 1;i<=pnj-j;i++){
                   s1=s1+"0";
               }
               teks.setText(strAwal+s1+s);      
            }catch(Exception e){
               System.out.println("Notifikasi : "+e);
            }finally{
                if(rs != null){
                    rs.close();
                }
                
                if(ps != null){
                    ps.close();
                }
            }
        } catch (Exception e) {
            System.out.println("Notifikasi : "+e);
        }
    }
    
    public void autoNomer2(String sql,String strAwal,Integer pnj,javax.swing.JTextField teks){
        try {
            ps=connect.prepareStatement(sql);
            try{
                rs=ps.executeQuery();
                rs.last();
                s=Integer.toString(rs.getRow()+1);
                j=s.length();
                s1="";
                for(i = 1;i<=pnj-j;i++){
                    s1=s1+"0";
                }
                teks.setText(strAwal+s1+s);
             }catch(Exception e){
                System.out.println("Notifikasi : "+e);
             }finally{
                if(rs != null){
                    rs.close();
                }
                
                if(ps != null){
                    ps.close();
                }
            }
        } catch (Exception e) {
            System.out.println("Notifikasi : "+e);
        }
    }
    
    public void autoNomer3(String sql,String strAwal,Integer pnj,javax.swing.JTextField teks){
        try {
            ps=connect.prepareStatement(sql);
            try{   
                rs=ps.executeQuery();
                s="1";
                while(rs.next()){
                    s=Integer.toString(Integer.parseInt(rs.getString(1))+1);
                }            

                j=s.length();
                s1="";
                for(i = 1;i<=pnj-j;i++){
                    s1=s1+"0";
                }
                teks.setText(strAwal+s1+s);
             }catch(Exception e){
                System.out.println("Notifikasi : "+e);
                JOptionPane.showMessageDialog(null,"Maaf, Query tidak bisa dijalankan...!!!!");
             }finally{
                if(rs != null){
                    rs.close();
                }
                
                if(ps != null){
                    ps.close();
                }
            }
        } catch (Exception e) {
            System.out.println("Notifikasi : "+e);
        }
    }
    
    public void autoNomer4(String sql,String strAwal,Integer pnj,javax.swing.JTextField teks){
        try {
            ps=connect.prepareStatement(sql);
            try{   
                rs=ps.executeQuery();
                s="1";
                while(rs.next()){
                    s=Integer.toString(Integer.parseInt(rs.getString(1))+1);
                }            

                j=s.length();
                s1="";
                for(i = 1;i<=pnj-j;i++){
                    s1=s1+"0";
                }
                teks.setText((strAwal+s1+s).substring(4,6)+(strAwal+s1+s).substring(2,4)+(strAwal+s1+s).substring(0,2));
             }catch(Exception e){
                System.out.println("Notifikasi : "+e);
                JOptionPane.showMessageDialog(null,"Maaf, Query tidak bisa dijalankan...!!!!");
             }finally{
                if(rs != null){
                    rs.close();
                }
                
                if(ps != null){
                    ps.close();
                }
            }
        } catch (Exception e) {
            System.out.println("Notifikasi : "+e);
        }
    }
    
    public void autoNomer5(String sql,String strAwal,Integer pnj,javax.swing.JTextField teks){
        try {
            ps=connect.prepareStatement(sql);
            try{   
                rs=ps.executeQuery();
                s="1";
                while(rs.next()){
                    s=Integer.toString(Integer.parseInt(rs.getString(1))+1);
                }            

                j=s.length();
                s1="";
                for(i = 1;i<=pnj-j;i++){
                    s1=s1+"0";
                }
                teks.setText((strAwal+s1+s).substring(2,4)+(strAwal+s1+s).substring(0,2)+(strAwal+s1+s).substring(4,6));
             }catch(Exception e){
                System.out.println("Notifikasi : "+e);
                JOptionPane.showMessageDialog(null,"Maaf, Query tidak bisa dijalankan...!!!!");
             }finally{
                if(rs != null){
                    rs.close();
                }
                
                if(ps != null){
                    ps.close();
                }
            }
        } catch (Exception e) {
            System.out.println("Notifikasi : "+e);
        }
    }
    
    public void autoNomer6(String sql,String strAwal,Integer pnj,javax.swing.JTextField teks){
        try {
            ps=connect.prepareStatement(sql);
            try{   
                rs=ps.executeQuery();
                s="1";
                while(rs.next()){
                    s=Integer.toString(Integer.parseInt(rs.getString(1))+1);
                }            

                j=s.length();
                s1="";
                for(i = 1;i<=pnj-j;i++){
                    s1=s1+"0";
                }
                teks.setText(s1+s+strAwal);
             }catch(Exception e){
                System.out.println("Notifikasi : "+e);
                JOptionPane.showMessageDialog(null,"Maaf, Query tidak bisa dijalankan...!!!!");
             }finally{
                if(rs != null){
                    rs.close();
                }
                
                if(ps != null){
                    ps.close();
                }
            }
        } catch (Exception e) {
            System.out.println("Notifikasi : "+e);
        }
    }
    
    public String autoNomer(String tabel,String strAwal,Integer pnj){
        try {
            auto="";
            ps=connect.prepareStatement("select * from "+tabel);
            try{        
                rs=ps.executeQuery();
                rs.last();
                s=Integer.toString(rs.getRow()+1);
                j=s.length();
                s1="";
                for(i = 1;i<=pnj-j;i++){
                    s1=s1+"0";
                }
                auto=strAwal+s1+s;             
             }catch(Exception e){
                System.out.println("Notifikasi : "+e);
                JOptionPane.showMessageDialog(null,"Maaf, Query tidak bisa dijalankan...!!!!");
             }finally{
                if(rs != null){
                    rs.close();
                }
                
                if(ps != null){
                    ps.close();
                }
            }
        } catch (Exception e) {
            System.out.println("Notifikasi : "+e);
        }
            
        return auto;        
    }
    
    public String autoNomer3(String sql,String strAwal,Integer pnj){
        try {
            auto="";
            ps=connect.prepareStatement(sql);
            try{
                rs=ps.executeQuery();
                s="1";
                while(rs.next()){
                    s=Integer.toString(Integer.parseInt(rs.getString(1))+1);
                }            

                j=s.length();
                s1="";
                for(i = 1;i<=pnj-j;i++){
                    s1=s1+"0";
                }
                auto=strAwal+s1+s;
             }catch(Exception e){
                System.out.println("Notifikasi : "+e);
                JOptionPane.showMessageDialog(null,"Maaf, Query tidak bisa dijalankan...!!!!");
             }finally{
                if(rs != null){
                    rs.close();
                }
                
                if(ps != null){
                    ps.close();
                }
            }
        } catch (Exception e) {
            System.out.println("Notifikasi : "+e);
        }
            
        return auto;
    }

    public void editTable(DefaultTableModel tabMode,String table,String field_acuan,JTextField nilai_field,String update) {
        if(tabMode.getRowCount()==0){
            JOptionPane.showMessageDialog(null,"Maaf, data sudah habis...!!!!");
            nilai_field.requestFocus();
        }else if(nilai_field.getText().trim().equals("")){
            JOptionPane.showMessageDialog(null,"Maaf, Gagal mengedit. Pilih dulu data yang mau diedit.\nKlik data pada table untuk memilih...!!!!");
        }else if(! nilai_field.getText().trim().equals("")){            
            sek.mengedit(table,field_acuan+"='"+nilai_field.getText()+"'", update);                 
        }
    }
    
    public boolean editTabletf(DefaultTableModel tabMode,String table,String field_acuan,JTextField nilai_field,String update) {
        status=true;
        if(tabMode.getRowCount()==0){
            status=false;
            JOptionPane.showMessageDialog(null,"Maaf, data sudah habis...!!!!");
            nilai_field.requestFocus();
        }else if(nilai_field.getText().trim().equals("")){
            status=false;
            JOptionPane.showMessageDialog(null,"Maaf, Gagal mengedit. Pilih dulu data yang mau diedit.\nKlik data pada table untuk memilih...!!!!");
        }else if(! nilai_field.getText().trim().equals("")){            
            status=sek.mengedittf(table,field_acuan+"='"+nilai_field.getText()+"'", update);                 
        }
        return status;
    }
    
    public void editTable(String table,String field_acuan,JTextField nilai_field,String update) {
        if(nilai_field.getText().trim().equals("")){
            JOptionPane.showMessageDialog(null,"Maaf, Gagal mengedit. Pilih dulu data yang mau diedit.\nKlik data pada table untuk memilih...!!!!");
        }else if(! nilai_field.getText().trim().equals("")){            
            sek.mengedit(table,field_acuan+"='"+nilai_field.getText()+"'", update);                 
        }
    }
    
    public void editTable(DefaultTableModel tabMode,String table,String field_acuan,String nilai_field,String update,int i, String[] a) {
        if(tabMode.getRowCount()==0){
            JOptionPane.showMessageDialog(null,"Maaf, data sudah habis...!!!!");
        }else if(nilai_field.trim().equals("")){
            JOptionPane.showMessageDialog(null,"Maaf, Gagal mengedit. Pilih dulu data yang mau diedit.\nKlik data pada table untuk memilih...!!!!");
        }else if(! nilai_field.trim().equals("")){            
            sek.mengedit(table,field_acuan+"="+nilai_field, update,i,a);                 
        }
    }
    
    public boolean editTabletf(DefaultTableModel tabMode,String table,String field_acuan,String nilai_field,String update,int i, String[] a) {
        status=true;
        if(tabMode.getRowCount()==0){
            status=false;
            JOptionPane.showMessageDialog(null,"Maaf, data sudah habis...!!!!");
        }else if(nilai_field.trim().equals("")){
            status=false;
            JOptionPane.showMessageDialog(null,"Maaf, Gagal mengedit. Pilih dulu data yang mau diedit.\nKlik data pada table untuk memilih...!!!!");
        }else if(! nilai_field.trim().equals("")){            
            status=sek.mengedittf(table,field_acuan+"="+nilai_field, update,i,a);                 
        }
        return status;
    }

    public void editTable(DefaultTableModel tabMode,String table,String field_acuan,JComboBox nilai_field,String update) {
        if(tabMode.getRowCount()==0){
            JOptionPane.showMessageDialog(null,"Maaf, data sudah habis...!!!!");
            nilai_field.requestFocus();
        }else if(nilai_field.getSelectedItem().equals("")){
            JOptionPane.showMessageDialog(null,"Maaf, Gagal mengedit. Pilih dulu data yang mau diedit.\nKlik data pada table untuk memilih...!!!!");
        }else if(nilai_field.getSelectedItem()!=""){            
            sek.mengedit(table,field_acuan+"='"+nilai_field.getSelectedItem()+"'", update);            
            
        }
    }

    public void editTable(DefaultTableModel tabMode,String table,String field_acuan,JLabel nilai_field,String update) {
        if(tabMode.getRowCount()==0){
            JOptionPane.showMessageDialog(null,"Maaf, data sudah habis...!!!!");
            nilai_field.requestFocus();
        }else if(nilai_field.getText().trim().equals("")){
            JOptionPane.showMessageDialog(null,"Maaf, Gagal mengedit. Pilih dulu data yang mau diedit.\nKlik data pada table untuk memilih...!!!!");
        }else if(! nilai_field.getText().trim().equals("")){            
            sek.mengedit(table,field_acuan+"='"+nilai_field.getText()+"'", update);            
            
        }
    }
    
    public void fillData(DefaultTableModel model,JTable table, File file) {
        try {
            WritableWorkbook workbook1 = Workbook.createWorkbook(file);
            WritableSheet sheet1 = workbook1.createSheet("First Sheet", 0); 
            model = (DefaultTableModel) table.getModel();

            for (i = 0; i < model.getColumnCount(); i++) {
                Label column = new Label(i, 0, model.getColumnName(i));
                sheet1.addCell(column);
            }
            for (i = 0; i < model.getRowCount(); i++) {
                for (j = 0; j < model.getColumnCount(); j++) {
                    Label row = new Label(j, i + 1, 
                            model.getValueAt(i, j).toString());
                    sheet1.addCell(row);
                }
            }
            workbook1.write();
            workbook1.close();
        } catch (IOException | WriteException ex) {
            System.out.println("Notifikasi : "+ex);
        }
    }

    public void hapusTable(DefaultTableModel tabMode,JTextField nilai_field,String table,String field) {
        if(tabMode.getRowCount()==0){
            JOptionPane.showMessageDialog(null,"Maaf, data sudah habis...!!!!");
            nilai_field.requestFocus();
        }else if(nilai_field.getText().trim().equals("")){
            JOptionPane.showMessageDialog(null,"Maaf, Gagal menghapus. Pilih dulu data yang mau dihapus.\nKlik data pada table untuk memilih...!!!!");
        }else if(! nilai_field.getText().trim().equals("")){            
            sek.meghapus(table,field,nilai_field.getText());   
        }
    }
    
    public boolean hapusTabletf(DefaultTableModel tabMode,JTextField nilai_field,String table,String field) {
        status=true;
        if(tabMode.getRowCount()==0){
            status=false;
            JOptionPane.showMessageDialog(null,"Maaf, data sudah habis...!!!!");
            nilai_field.requestFocus();
        }else if(nilai_field.getText().trim().equals("")){
            status=false;
            JOptionPane.showMessageDialog(null,"Maaf, Gagal menghapus. Pilih dulu data yang mau dihapus.\nKlik data pada table untuk memilih...!!!!");
        }else if(! nilai_field.getText().trim().equals("")){            
            status=sek.meghapustf(table,field,nilai_field.getText());   
        }
        return status;
    }

    public void hapusTable(DefaultTableModel tabMode,JComboBox nilai_field,String table,String field) {
        if(tabMode.getRowCount()==0){
            JOptionPane.showMessageDialog(null,"Maaf, data sudah habis...!!!!");
            nilai_field.requestFocus();
        }else if(nilai_field.getSelectedItem().equals("")){
            JOptionPane.showMessageDialog(null,"Maaf, Gagal menghapus. Pilih dulu data yang mau dihapus.\nKlik data pada table untuk memilih...!!!!");
        }else if(nilai_field.getSelectedItem()!=""){            
            String data=nilai_field.getSelectedItem().toString();
            sek.meghapus(table,field,data);     
        }
    }

    public void loadCombo(JComboBox cmb,String field,String table){
        cmb.removeAllItems();
        try {
            ps=connect.prepareStatement("select "+field+" from "+table+" order by "+field);
            try{
                rs=ps.executeQuery();
                while(rs.next()){
                    String item=rs.getString(1);
                    cmb.addItem(item);
                    a++;
                }          
            }catch(Exception e){
                System.out.println("Notifikasi : "+e);
            }finally{
                if(rs != null){
                    rs.close();
                }
                
                if(ps != null){
                    ps.close();
                }
            }
        } catch (Exception e) {
            System.out.println("Notifikasi : "+e);
        }
    }
    
    public int hariAkhad(int month,int year){ 
        j=0; 
        Calendar calendar=Calendar.getInstance(); 
        calendar.set(year, month-1,1); 
        int days = calendar.getActualMaximum(Calendar.DAY_OF_MONTH); 
        
        for(i=1;i<=days;i++) { 
            calendar.set(year, month-1, i); 
            int day=calendar.get(Calendar.DAY_OF_WEEK); 
            if(day==1){
                j++ ;
            }  
        } 
        
        return j; 
    } 
    
    public int jumlahHari(int month,int year){ 
        Calendar calendar=Calendar.getInstance(); 
        calendar.set(year, month-1,1); 
        int days = calendar.getActualMaximum(Calendar.DAY_OF_MONTH); 
        return days; 
    } 
    
    public void loadCombo(JComboBox cmb,String query){
        cmb.removeAllItems();
        try {
            ps=connect.prepareStatement(query);
            try{
                rs=ps.executeQuery();
                while(rs.next()){
                    String item=rs.getString(1);
                    cmb.addItem(item);
                    a++;
                }          
            }catch(Exception e){
                System.out.println("Notifikasi : "+e);
            }finally{
                if(rs != null){
                    rs.close();
                }
                
                if(ps != null){
                    ps.close();
                }
            }
        } catch (Exception e) {
            System.out.println("Notifikasi : "+e);
        }
    }

    public void LoadTahun(JComboBox cmb){        
        cmb.removeAllItems();
        for(i =(year+1);i>=1980;i--){
            cmb.addItem(i);
        }
        cmb.setSelectedItem(year);
    }

    public void LoadTahunAkd(JComboBox cmb){
        cmb.removeAllItems();
        for(i = 1950;i<=year;i++){
            cmb.addItem(i+"1");
            cmb.addItem(i+"2");
        }
        cmb.setSelectedItem(year+"1");
    }

    @SuppressWarnings("empty-statement")
    public void MyReport(String reportName,String reportDirName,String judul,Map parameters){
        Properties systemProp = System.getProperties();

        // Ambil current dir
        String currentDir = systemProp.getProperty("user.dir");

        File dir = new File(currentDir);

        File fileRpt;
        String fullPath = "";
        if (dir.isDirectory()) {
            String[] isiDir = dir.list();
            for (String iDir : isiDir) {
                fileRpt = new File(currentDir + File.separatorChar + iDir + File.separatorChar + reportDirName + File.separatorChar + reportName);
                if (fileRpt.isFile()) { // Cek apakah file RptMaster.jasper ada
                    fullPath = fileRpt.toString();
                    System.out.println("Found Report File at : " + fullPath);
                } // end if
            } // end for i
        } // end if

        try {
            try (Statement stm = connect.createStatement()) {
                try {
                    
                    String namafile="./"+reportDirName+"/"+reportName;
                    JasperPrint jasperPrint = JasperFillManager.fillReport(namafile, parameters, connect);
                    
                    JasperViewer jasperViewer = new JasperViewer(jasperPrint, false);
                    jasperViewer.setTitle(judul);
                    Dimension screen=Toolkit.getDefaultToolkit().getScreenSize();
                    jasperViewer.setSize(screen.width-50,screen.height-50);
                    jasperViewer.setModalExclusionType(ModalExclusionType.TOOLKIT_EXCLUDE);
                    jasperViewer.setLocationRelativeTo(null);
                    jasperViewer.setVisible(true);
                } catch (Exception rptexcpt) {
                    System.out.println("Report Can't view because : " + rptexcpt);
                    JOptionPane.showMessageDialog(null,"Report Can't view because : "+ rptexcpt);
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    
    @SuppressWarnings("empty-statement")
    public void MyReportPDF(String reportName,String reportDirName,String judul,Map parameters){
        Properties systemProp = System.getProperties();

        // Ambil current dir
        String currentDir = systemProp.getProperty("user.dir");

        File dir = new File(currentDir);

        File fileRpt;
        String fullPath = "";
        if (dir.isDirectory()) {
            String[] isiDir = dir.list();
            for (String iDir : isiDir) {
                fileRpt = new File(currentDir + File.separatorChar + iDir + File.separatorChar + reportDirName + File.separatorChar + reportName);
                if (fileRpt.isFile()) { // Cek apakah file RptMaster.jasper ada
                    fullPath = fileRpt.toString();
                    System.out.println("Found Report File at : " + fullPath);
                } // end if
            } // end for i
        } // end if

        try {
            try (Statement stm = connect.createStatement()) {
                try {
                    File f = new File("./"+reportDirName+"/"+reportName.replaceAll("jasper","pdf")); 
                    String namafile="./"+reportDirName+"/"+reportName;
                    JasperPrint jasperPrint = JasperFillManager.fillReport(namafile, parameters, connect);
                    JasperExportManager.exportReportToPdfFile(jasperPrint,"./"+reportDirName+"/"+reportName.replaceAll("jasper","pdf"));
                    Desktop.getDesktop().open(f);
                } catch (Exception rptexcpt) {
                    System.out.println("Report Can't view because : " + rptexcpt);
                    JOptionPane.showMessageDialog(null,"Report Can't view because : "+ rptexcpt);
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    
    @SuppressWarnings("empty-statement")
    public void MyReportPDF2(String reportName,String reportDirName,String judul,Map parameters){
        Properties systemProp = System.getProperties();

        // Ambil current dir
        String currentDir = systemProp.getProperty("user.dir");

        File dir = new File(currentDir);

        File fileRpt;
        String fullPath = "";
        if (dir.isDirectory()) {
            String[] isiDir = dir.list();
            for (String iDir : isiDir) {
                fileRpt = new File(currentDir + File.separatorChar + iDir + File.separatorChar + reportDirName + File.separatorChar + reportName);
                if (fileRpt.isFile()) { // Cek apakah file RptMaster.jasper ada
                    fullPath = fileRpt.toString();
                    System.out.println("Found Report File at : " + fullPath);
                } // end if
            } // end for i
        } // end if

        try {
            try (Statement stm = connect.createStatement()) {
                try {
                    File f = new File("./"+reportDirName+"/"+reportName.replaceAll("jasper","pdf")); 
                    String namafile="./"+reportDirName+"/"+reportName;
                    JasperPrint jasperPrint = JasperFillManager.fillReport(namafile, parameters, connect);
                    JasperExportManager.exportReportToPdfFile(jasperPrint,"./"+reportDirName+"/"+reportName.replaceAll("jasper","pdf"));
                } catch (Exception rptexcpt) {
                    System.out.println("Report Can't view because : " + rptexcpt);
                    JOptionPane.showMessageDialog(null,"Report Can't view because : "+ rptexcpt);
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    
    @SuppressWarnings("empty-statement")
    public void MyReport2(String reportName,String reportDirName,String judul,Map parameters){
        Properties systemProp = System.getProperties();

        // Ambil current dir
        String currentDir = systemProp.getProperty("user.dir");

        File dir = new File(currentDir);

        File fileRpt;
        String fullPath = "";
        if (dir.isDirectory()) {
            String[] isiDir = dir.list();
            for (String iDir : isiDir) {
                fileRpt = new File(currentDir + File.separatorChar + iDir + File.separatorChar + reportDirName + File.separatorChar + reportName);
                if (fileRpt.isFile()) { // Cek apakah file RptMaster.jasper ada
                    fullPath = fileRpt.toString();
                    System.out.println("Found Report File at : " + fullPath);
                } // end if
            } // end for i
        } // end if

        try {
            try (Statement stm = connect.createStatement()) {
                try {
                    
                    String namafile="./"+reportDirName+"/"+reportName;
                    JasperPrint jasperPrint = JasperFillManager.fillReport(namafile, parameters, connect);
                    
                    JasperViewer jasperViewer = new JasperViewer(jasperPrint, false);
                    jasperViewer.setTitle(judul);
                    Dimension screen=Toolkit.getDefaultToolkit().getScreenSize();
                    jasperViewer.setSize(screen.width-50,screen.height-50);
                    jasperViewer.setModalExclusionType(ModalExclusionType.TOOLKIT_EXCLUDE);
                    jasperViewer.setLocationRelativeTo(null);
                    jasperViewer.setVisible(true);
                } catch (Exception rptexcpt) {
                    System.out.println("Report Can't view because : " + rptexcpt);
                    JOptionPane.showMessageDialog(null,"Report Can't view because : "+ rptexcpt);
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    
    public void MyReportqry(String reportName,String reportDirName,String judul,String qry,Map parameters){
        Properties systemProp = System.getProperties();

        // Ambil current dir
        String currentDir = systemProp.getProperty("user.dir");

        File dir = new File(currentDir);

        File fileRpt;
        String fullPath = "";
        if (dir.isDirectory()) {
            String[] isiDir = dir.list();
            for (String iDir : isiDir) {
                fileRpt = new File(currentDir + File.separatorChar + iDir + File.separatorChar + reportDirName + File.separatorChar + reportName);
                if (fileRpt.isFile()) { // Cek apakah file RptMaster.jrxml ada
                    fullPath = fileRpt.toString();
                    System.out.println("Found Report File at : " + fullPath);
                } // end if
            } // end for i
        } // end if

        try {
            ps=connect.prepareStatement(qry);
            try {
                String namafile="./"+reportDirName+"/"+reportName;
                rs=ps.executeQuery();
                JRResultSetDataSource rsdt = new JRResultSetDataSource(rs);
                
                JasperPrint jasperPrint = JasperFillManager.fillReport(namafile, parameters,rsdt);

                JasperViewer jasperViewer = new JasperViewer(jasperPrint, false);
                jasperViewer.setTitle(judul);
                Dimension screen=Toolkit.getDefaultToolkit().getScreenSize();
                jasperViewer.setSize(screen.width-50,screen.height-50);
                jasperViewer.setModalExclusionType(ModalExclusionType.TOOLKIT_EXCLUDE);
                jasperViewer.setLocationRelativeTo(null);
                jasperViewer.setVisible(true);
            } catch (Exception rptexcpt) {
                System.out.println("Report Can't view because : " + rptexcpt);
                JOptionPane.showMessageDialog(null,"Report Can't view because : "+ rptexcpt);
            } finally{
                if(rs!=null){
                    rs.close();
                }
                if(ps!=null){
                    ps.close();
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    
    public void viewReport(String namaReport, String judul, Map params) {
        String currentDir = System.getProperties().getProperty("user.dir");

        File dir = new File(currentDir);

        File report = null;
        
        if (dir.isDirectory()) {
            for (String file: dir.list()) {
                report = new File(currentDir + File.separatorChar + file + File.separatorChar + namaReport);
                
                if (report.isFile()) {
                    System.out.println("Found report file at: " + report.toString());
                    break;
                }
            }
        }
        
        if (report == null) {
            JOptionPane.showMessageDialog(null, "File tidak ditemukan!");
            return;
        }
        
        try {
            JasperReport jr = (JasperReport) JRLoader.loadObject(report);            
            JasperPrint jp = JasperFillManager.fillReport(jr, params, connect);
            JasperViewer jv = new JasperViewer(jp, false);
            
            Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
            
            jv.setTitle(judul);
            jv.setSize(screen.width - 50, screen.height - 50);
            jv.setModalExclusionType(ModalExclusionType.TOOLKIT_EXCLUDE);
            jv.setLocationRelativeTo(null);
            jv.setVisible(true);
        } catch (Exception e) {
            System.out.println("Report can't view because: " + e);
            e.printStackTrace();
            
            JOptionPane.showMessageDialog(null, "Tidak bisa menampilkan hasil cetak!");
        }
    }
    
    public void MyReportqrypdf(String reportName,String reportDirName,String judul,String qry,Map parameters){
        Properties systemProp = System.getProperties();

        // Ambil current dir
        String currentDir = systemProp.getProperty("user.dir");

        File dir = new File(currentDir);

        File fileRpt;
        String fullPath = "";
        if (dir.isDirectory()) {
            String[] isiDir = dir.list();
            for (String iDir : isiDir) {
                fileRpt = new File(currentDir + File.separatorChar + iDir + File.separatorChar + reportDirName + File.separatorChar + reportName);
                if (fileRpt.isFile()) { // Cek apakah file RptMaster.jrxml ada
                    fullPath = fileRpt.toString();
                    System.out.println("Found Report File at : " + fullPath);
                } // end if
            } // end for i
        } // end if

        try {
            ps=connect.prepareStatement(qry);
            try {
                String namafile="./"+reportDirName+"/"+reportName;
                File f = new File("./"+reportDirName+"/"+reportName.replaceAll("jasper","pdf")); 
                rs=ps.executeQuery();
                JRResultSetDataSource rsdt = new JRResultSetDataSource(rs);
                JasperPrint jasperPrint = JasperFillManager.fillReport(namafile, parameters,rsdt);
                JasperExportManager.exportReportToPdfFile(jasperPrint,"./"+reportDirName+"/"+reportName.replaceAll("jasper","pdf"));
                Desktop.getDesktop().open(f);
            } catch (Exception rptexcpt) {
                System.out.println("Report Can't view because : " + rptexcpt);
                JOptionPane.showMessageDialog(null,"Report Can't view because : "+ rptexcpt);
            } finally{
                if(rs!=null){
                    rs.close();
                }
                if(ps!=null){
                    ps.close();
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    
    public void MyReport(String reportName,Map parameters,String title){
        try {
                JasperViewer jasperViewer =new JasperViewer(JasperFillManager.fillReport("./report/"+reportName,parameters,connect), false);
                jasperViewer.setTitle(title);
                jasperViewer.setLocationRelativeTo(null);
                jasperViewer.setVisible(true);
                //JasperViewer.viewReport(JasperFillManager.fillReport(JasperCompileManager.compileReport("./report/"+reportName),parameters,connect),false);
        } catch (Exception ex) {
           System.out.println("Notifikasi : "+ex);
        } 
    }


    public void pindah(java.awt.event.KeyEvent evt,JTextField kiri,JTextField kanan){
        if(evt.getKeyCode()==KeyEvent.VK_ENTER){
            kanan.requestFocus();
        }else if(evt.getKeyCode()==KeyEvent.VK_PAGE_UP){
            kiri.requestFocus();
        }
    }
    
    public void pindah(java.awt.event.KeyEvent evt,JTextField kiri,JCheckBox kanan){
        if(evt.getKeyCode()==KeyEvent.VK_ENTER){
            kanan.requestFocus();
        }else if(evt.getKeyCode()==KeyEvent.VK_PAGE_UP){
            kiri.requestFocus();
        }
    }
    
    public void pindah(java.awt.event.KeyEvent evt,JTextField kiri,JTextField kanan,JTextField bawah){
        if(evt.getKeyCode()==KeyEvent.VK_ENTER){
            kanan.requestFocus();
        }else if(evt.getKeyCode()==KeyEvent.VK_PAGE_UP){
            kiri.requestFocus();
        }else if(evt.getKeyCode()==KeyEvent.VK_DOWN){
            bawah.requestFocus();
        }
    }
    
    public void pindah2(java.awt.event.KeyEvent evt,JTextField kiri,JTextField kanan){
        if(evt.getKeyCode()==KeyEvent.VK_TAB){
            kanan.requestFocus();
        }else if(evt.getKeyCode()==KeyEvent.VK_PAGE_UP){
            kiri.requestFocus();
        }
    }
    
    public void pindah2(KeyEvent evt, Button kiri, Button kanan) {
        if(evt.getKeyCode()==KeyEvent.VK_TAB){
            kanan.requestFocus();
        }else if(evt.getKeyCode()==KeyEvent.VK_PAGE_UP){
            kiri.requestFocus();
        }
    }
    
    public void pindah2(java.awt.event.KeyEvent evt,JTextField kiri,JTextArea kanan){
        if(evt.getKeyCode()==KeyEvent.VK_TAB){
            kanan.requestFocus();
        }else if(evt.getKeyCode()==KeyEvent.VK_PAGE_UP){
            kiri.requestFocus();
        }
    }
    
    public void pindah2(java.awt.event.KeyEvent evt,JTextArea kiri,JTextArea kanan){
        if(evt.getKeyCode()==KeyEvent.VK_TAB){
            kanan.requestFocus();
        }else if(evt.getKeyCode()==KeyEvent.VK_PAGE_UP){
            kiri.requestFocus();
        }
    }
    
    public void pindah2(java.awt.event.KeyEvent evt,JTextArea kiri,JTextField kanan){
        if(evt.getKeyCode()==KeyEvent.VK_TAB){
            kanan.requestFocus();
        }else if(evt.getKeyCode()==KeyEvent.VK_PAGE_UP){
            kiri.requestFocus();
        }
    }
    
    public void pindah2(java.awt.event.KeyEvent evt,JTextArea kiri,JButton kanan){
        if(evt.getKeyCode()==KeyEvent.VK_TAB){
            kanan.requestFocus();
        }else if(evt.getKeyCode()==KeyEvent.VK_PAGE_UP){
            kiri.requestFocus();
        }
    }
    
    public void pindah2(java.awt.event.KeyEvent evt,JTextField kiri,JComboBox kanan){
        if(evt.getKeyCode()==KeyEvent.VK_TAB){
            kanan.requestFocus();
        }else if(evt.getKeyCode()==KeyEvent.VK_PAGE_UP){
            kiri.requestFocus();
        }
    }
    
    public void pindah2(java.awt.event.KeyEvent evt,JComboBox kiri,JComboBox kanan){
        if(evt.getKeyCode()==KeyEvent.VK_TAB){
            kanan.requestFocus();
        }else if(evt.getKeyCode()==KeyEvent.VK_PAGE_UP){
            kiri.requestFocus();
        }
    }
    
    public void pindah2(java.awt.event.KeyEvent evt,JComboBox kiri,JTextField kanan){
        if(evt.getKeyCode()==KeyEvent.VK_TAB){
            kanan.requestFocus();
        }else if(evt.getKeyCode()==KeyEvent.VK_PAGE_UP){
            kiri.requestFocus();
        }
    }
    
    public void pindah2(java.awt.event.KeyEvent evt,JComboBox kiri,JTextArea kanan){
        if(evt.getKeyCode()==KeyEvent.VK_TAB){
            kanan.requestFocus();
        }else if(evt.getKeyCode()==KeyEvent.VK_PAGE_UP){
            kiri.requestFocus();
        }
    }
    
    public void pindah2(java.awt.event.KeyEvent evt,JButton kiri,JTextArea kanan){
        if(evt.getKeyCode()==KeyEvent.VK_TAB){
            kanan.requestFocus();
        }else if(evt.getKeyCode()==KeyEvent.VK_PAGE_UP){
            kiri.requestFocus();
        }
    }

    public void pindah2(KeyEvent evt, TextArea kiri, ComboBox kanan) {
        if(evt.getKeyCode()==KeyEvent.VK_TAB){
            kanan.requestFocus();
        }else if(evt.getKeyCode()==KeyEvent.VK_PAGE_UP){
            kiri.requestFocus();
        }
    }
    
    public void pindah2(KeyEvent evt, Tanggal kiri, Button kanan) {
        if(evt.getKeyCode()==KeyEvent.VK_TAB){
            kanan.requestFocus();
        }else if(evt.getKeyCode()==KeyEvent.VK_PAGE_UP){
            kiri.requestFocus();
        }
    }

    public void pindah2(KeyEvent evt, Button kiri, ComboBox kanan) {
        if(evt.getKeyCode()==KeyEvent.VK_ENTER){
            kanan.requestFocus();
        }else if(evt.getKeyCode()==KeyEvent.VK_PAGE_UP){
            kiri.requestFocus();
        }
    }
    
    public void pindah2(java.awt.event.KeyEvent evt,JTextField kiri,JButton kanan){
        if(evt.getKeyCode()==KeyEvent.VK_TAB){
            kanan.requestFocus();
        }else if(evt.getKeyCode()==KeyEvent.VK_PAGE_UP){
            kiri.requestFocus();
        }
    }
    
    public void pindah2(KeyEvent evt, ComboBox kiri, Button kanan) {
        if(evt.getKeyCode()==KeyEvent.VK_TAB){
            kanan.requestFocus();
        }else if(evt.getKeyCode()==KeyEvent.VK_PAGE_UP){
            kiri.requestFocus();
        }
    }

    public void pindah2(KeyEvent evt, TextArea kiri, Tanggal kanan) {
        if(evt.getKeyCode()==KeyEvent.VK_TAB){
            kanan.requestFocus();
        }else if(evt.getKeyCode()==KeyEvent.VK_PAGE_UP){
            kiri.requestFocus();
        }
    }

    public void pindah2(KeyEvent evt, Button kiri, TextBox kanan) {
        if(evt.getKeyCode()==KeyEvent.VK_TAB){
            kanan.requestFocus();
        }else if(evt.getKeyCode()==KeyEvent.VK_PAGE_UP){
            kiri.requestFocus();
        }
    }
    
    public void pindah(java.awt.event.KeyEvent evt,JTextField kiri,JTextArea kanan) {
        if(evt.getKeyCode()==KeyEvent.VK_ENTER){
            kanan.requestFocus();
        }else if(evt.getKeyCode()==KeyEvent.VK_PAGE_UP){
            kiri.requestFocus();
        }
    }
    
    public void pindah(java.awt.event.KeyEvent evt,JTextArea kiri,JTextArea kanan) {
        if(evt.getKeyCode()==KeyEvent.VK_ENTER){
            kanan.requestFocus();
        }else if(evt.getKeyCode()==KeyEvent.VK_PAGE_UP){
            kiri.requestFocus();
        }
    }

    public void pindah(KeyEvent evt, JComboBox kiri, JTextArea kanan) {
        if(evt.getKeyCode()==KeyEvent.VK_ENTER){
            kanan.requestFocus();
        }else if(evt.getKeyCode()==KeyEvent.VK_PAGE_UP){
            kiri.requestFocus();
        }
    }

    public void pindah(java.awt.event.KeyEvent evt,JTextField kiri,JButton kanan){
        if(evt.getKeyCode()==KeyEvent.VK_ENTER){
            kanan.requestFocus();
        }else if(evt.getKeyCode()==KeyEvent.VK_PAGE_UP){
            kiri.requestFocus();
        }
    }
    
    public void pindah(java.awt.event.KeyEvent evt,JTextField kiri,JButton kanan,JTextField bawah){
        if(evt.getKeyCode()==KeyEvent.VK_ENTER){
            kanan.requestFocus();
        }else if(evt.getKeyCode()==KeyEvent.VK_PAGE_UP){
            kiri.requestFocus();
        }else if(evt.getKeyCode()==KeyEvent.VK_DOWN){
            bawah.requestFocus();
        }
    }

    public void pindah(java.awt.event.KeyEvent evt,JButton kiri,JTextField kanan){
        if(evt.getKeyCode()==KeyEvent.VK_ENTER){
            kanan.requestFocus();
        }else if(evt.getKeyCode()==KeyEvent.VK_PAGE_UP){
            kiri.requestFocus();
        }
    }

    public void pindah(java.awt.event.KeyEvent evt,JButton kiri,JButton kanan){
        if(evt.getKeyCode()==KeyEvent.VK_ENTER){
            kanan.requestFocus();
        }else if(evt.getKeyCode()==KeyEvent.VK_PAGE_UP){
            kiri.requestFocus();
        }
    }
    
    public void pindah(java.awt.event.KeyEvent evt,JButton kiri,JCheckBox kanan){
        if(evt.getKeyCode()==KeyEvent.VK_ENTER){
            kanan.requestFocus();
        }else if(evt.getKeyCode()==KeyEvent.VK_PAGE_UP){
            kiri.requestFocus();
        }
    }

    public void pindah(java.awt.event.KeyEvent evt,JTextField kiri,JComboBox kanan){
        if(evt.getKeyCode()==KeyEvent.VK_ENTER){
            kanan.requestFocus();
        }else if(evt.getKeyCode()==KeyEvent.VK_PAGE_UP){
            kiri.requestFocus();
        }
    }

    public void pindah(java.awt.event.KeyEvent evt,JTextArea kiri,JComboBox kanan) {
        if(evt.getKeyCode()==KeyEvent.VK_ENTER){
            kanan.requestFocus();
        }else if(evt.getKeyCode()==KeyEvent.VK_PAGE_UP){
            kiri.requestFocus();
        }
    }

    public void pindah(java.awt.event.KeyEvent evt,JComboBox kiri,JTextField kanan){
        if(evt.getKeyCode()==KeyEvent.VK_ENTER){
            kanan.requestFocus();
        }else if(evt.getKeyCode()==KeyEvent.VK_PAGE_UP){
            kiri.requestFocus();
        }
    }

    public void pindah(java.awt.event.KeyEvent evt,JComboBox kiri,JDateTimePicker kanan){
        if(evt.getKeyCode()==KeyEvent.VK_ENTER){
            kanan.requestFocus();
        }else if(evt.getKeyCode()==KeyEvent.VK_PAGE_UP){
            kiri.requestFocus();
        }
    }
    
    public void pindah(java.awt.event.KeyEvent evt,JCheckBox kiri,JDateTimePicker kanan){
        if(evt.getKeyCode()==KeyEvent.VK_ENTER){
            kanan.requestFocus();
        }else if(evt.getKeyCode()==KeyEvent.VK_PAGE_UP){
            kiri.requestFocus();
        }
    }
    
    public void pindah(KeyEvent evt, Button kiri, TextArea kanan) {
        if(evt.getKeyCode()==KeyEvent.VK_ENTER){
            kanan.requestFocus();
        }else if(evt.getKeyCode()==KeyEvent.VK_PAGE_UP){
            kiri.requestFocus();
        }
    }

    public void pindah(java.awt.event.KeyEvent evt,JDateTimePicker kiri,JTextField kanan){
        if(evt.getKeyCode()==KeyEvent.VK_ENTER){
            kanan.requestFocus();
        }else if(evt.getKeyCode()==KeyEvent.VK_PAGE_UP){
            kiri.requestFocus();
        }
    }

    public void pindah(java.awt.event.KeyEvent evt,JComboBox kiri,JComboBox kanan){
        if(evt.getKeyCode()==KeyEvent.VK_ENTER){
            kanan.requestFocus();
        }else if(evt.getKeyCode()==KeyEvent.VK_PAGE_UP){
            kiri.requestFocus();
        }
    }

    public void pindah(java.awt.event.KeyEvent evt,JComboBox kiri,JButton kanan){
        if(evt.getKeyCode()==KeyEvent.VK_ENTER){
            kanan.requestFocus();
        }else if(evt.getKeyCode()==KeyEvent.VK_PAGE_UP){
            kiri.requestFocus();
        }
    }

    public void pindah(KeyEvent evt, JButton kiri, JComboBox kanan) {
        if(evt.getKeyCode()==KeyEvent.VK_ENTER){
            kanan.requestFocus();
        }else if(evt.getKeyCode()==KeyEvent.VK_PAGE_UP){
            kiri.requestFocus();
        }
    }

    public void pindah(KeyEvent evt, JTextArea kiri, JButton kanan) {
        if(evt.getKeyCode()==KeyEvent.VK_ENTER){
            kanan.requestFocus();
        }else if(evt.getKeyCode()==KeyEvent.VK_PAGE_UP){
            kiri.requestFocus();
        }
    }

    public void pindah(KeyEvent evt, JTextArea kiri, JTextField kanan) {
        if(evt.getKeyCode()==KeyEvent.VK_ENTER){
            kanan.requestFocus();
        }else if(evt.getKeyCode()==KeyEvent.VK_PAGE_UP){
            kiri.requestFocus();
        }
    }
    
    public void panggilUrl(String url){
        String os = System.getProperty("os.name").toLowerCase();
        Runtime rt = Runtime.getRuntime();                                
        try{ 
            if(os.contains("win")) {
                rt.exec( "rundll32 url.dll,FileProtocolHandler " + "http://"+koneksiDB.HOSTHYBRIDWEB()+":"+koneksiDB.PORTWEB()+"/"+koneksiDB.HYBRIDWEB()+"/"+url);
            }else if (os.contains("mac")) {
                rt.exec( "open " + "http://"+koneksiDB.HOSTHYBRIDWEB()+":"+koneksiDB.PORTWEB()+"/"+koneksiDB.HYBRIDWEB()+"/"+url);
            }else if (os.contains("nix") || os.contains("nux")) {
                String[] browsers = {"x-www-browser","epiphany", "firefox", "mozilla", "konqueror","chrome","chromium","netscape","opera","links","lynx","midori"};
                // Build a command string which looks like "browser1 "url" || browser2 "url" ||..."
                StringBuilder cmd = new StringBuilder();
                for(i=0; i<browsers.length; i++) cmd.append(i==0  ? "" : " || ").append(browsers[i]).append(" \"").append("http://").append(koneksiDB.HOSTHYBRIDWEB()+":"+koneksiDB.PORTWEB()).append("/").append(koneksiDB.HYBRIDWEB()).append("/").append(url).append( "\" ");
                rt.exec(new String[] { "sh", "-c", cmd.toString() });
            } 
        }catch (Exception e){
            System.out.println("Notif Browser : "+e);
        } 
    }
    
    public void panggilUrl2(String url){
        String os = System.getProperty("os.name").toLowerCase();
        Runtime rt = Runtime.getRuntime();                                
        try{ 
            if(os.contains("win")) {
                rt.exec( "rundll32 url.dll,FileProtocolHandler "+url);
            }else if (os.contains("mac")) {
                rt.exec( "open " +url);
            }else if (os.contains("nix") || os.contains("nux")) {
                String[] browsers = {"x-www-browser","epiphany", "firefox", "mozilla", "konqueror","chrome","chromium","netscape","opera","links","lynx","midori"};
                // Build a command string which looks like "browser1 "url" || browser2 "url" ||..."
                StringBuilder cmd = new StringBuilder();
                for(i=0; i<browsers.length; i++) cmd.append(i==0  ? "" : " || ").append(browsers[i]).append(" \"").append(url).append( "\" ");
                rt.exec(new String[] { "sh", "-c", cmd.toString() });
            } 
        }catch (Exception e){
            System.out.println("Notif Browser : "+e);
        } 
    }
    
    public void printUrl(String url) throws URISyntaxException{
        try{
           desktop.print(new File(new java.net.URI("http://"+koneksiDB.HOSTHYBRIDWEB()+":"+koneksiDB.PORTWEB()+"/"+url)));  
        }catch (Exception e) {
           System.out.println(e);
        }
    }

    public void SetTgl(DefaultTableModel tabMode,JTable table,JDateTimePicker dtp,int i){
        j=table.getSelectedRow();
        try {
           Date dtpa = new SimpleDateFormat("yyyy-MM-dd").parse(tabMode.getValueAt(j,i).toString().replaceAll("'",""));
           dtp.setDate(dtpa);
        } catch (ParseException ex) {
           dtp.setDate(new Date());
        }
    }
    
    public String SetTgl(String original){
        original=original.replaceAll("'","");
        s = "";
        try {
            s=original.substring(6,10)+"-"+original.substring(3,5)+"-"+original.substring(0,2);
        }catch (Exception e) {
        }   
        return s;
    }
    
    public String SetTglJam(String original, String pattern)
    {
        original = original.replaceAll("'", "");
        String out = "";
        
        SimpleDateFormat dfIn = new SimpleDateFormat(pattern);
        SimpleDateFormat dfOut = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
        try {
            Date date = dfIn.parse(original);
            
            out = dfOut.format(date);
        } catch (ParseException ex) {
            System.out.println("Notif : " + ex);
        }
        
        return out;
    }
    
    public String SetTglJam(String original){
        original=original.replaceAll("'","");
        s = "";
        try {
            s=original.substring(6,10)+"-"+original.substring(3,5)+"-"+original.substring(0,2)+" "+original.substring(11,19);
        }catch (Exception e) {
        }   
        return s;
    }
    
    public String SetTgl3(String original){
        original=original.replaceAll("'","");
        s = "";
        try {
            s=original.substring(8,10)+"-"+original.substring(5,7)+"-"+original.substring(0,4);
        }catch (Exception e) {
        }   
        return s;
    }
    
    public String MaxTeks(String original,int max){
        if(original.length()>=max){
            s=original.substring(0,(max-1));
        }else{
            s=original;
        }
        return original;
    }
    
    public void SetTgl(JDateTimePicker dtp,String tgl){            
        try {
           Date dtpa = new SimpleDateFormat("yyyy-MM-dd").parse(tgl.replaceAll("'",""));
           dtp.setDate(dtpa);
        } catch (ParseException ex) {
           dtp.setDate(new Date());
        }
    }
    
    public void SetTgl2(JDateTimePicker dtp,String tgl){            
        try {
           Date dtpa = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(tgl.replaceAll("'",""));
           dtp.setDate(dtpa);
        } catch (ParseException ex) {
           dtp.setDate(new Date());
        }
    }
    
    public Date SetTgl2(String tgl){
        try {
           Date dtpa = new SimpleDateFormat("yyyy-MM-dd").parse(tgl.replaceAll("'",""));
           return dtpa;
        } catch (ParseException ex) {
           return new Date();
        }
    }
    
    public String SetTgl4(String original){
        original=original.replaceAll("'","");
        s = "";
        try {
            s=original.substring(6,10)+original.substring(3,5)+original.substring(0,2);
        }catch (Exception e) {
        }   
        return s;
    }
    
    public void textKosong(JTextField teks,String pesan){
        JOptionPane.showMessageDialog(null,"Maaf, "+pesan+" tidak boleh kosong...!!!");
        teks.requestFocus();
    }

    public void textKosong(JTextArea teks,String pesan){
        JOptionPane.showMessageDialog(null,"Maaf, "+pesan+" tidak boleh kosong...!!!");
        teks.requestFocus();
    }
    
    public void textKosong(JButton teks,String pesan){
        JOptionPane.showMessageDialog(null,"Maaf, "+pesan+" tidak boleh kosong...!!!");
        teks.requestFocus();
    }

    public void tabelKosong(DefaultTableModel tabMode) {
        j=tabMode.getRowCount();
        for(i=0;i<j;i++){
            tabMode.removeRow(0);
        }
    }

    public void textKosong(JComboBox teks, String pesan) {
        JOptionPane.showMessageDialog(null,"Maaf, "+pesan+" tidak boleh kosong...!!!");
        teks.requestFocus();
    }
    
    public String SetAngka(double nilai){        
       return df2.format(nilai);
    }
    
    public String SetAngka3(double nilai){        
       return df4.format(nilai);
    }
    
    public String SetAngka4(double nilai){        
       return df5.format(nilai);
    }
    
    public String SetAngka2(double nilai){        
       return df3.format(nilai);
    }
    
    public String SetAngka5(double nilai){        
       return df6.format(nilai);
    }
    
    public String SetAngka6(double nilai){        
       return df7.format(nilai);
    }
    
    public double SetAngka7(double nilai){        
       return Double.parseDouble(df7.format(nilai));
    }
    
    public double SetAngka8(double value,int places){      
        return new BigDecimal(value).setScale(places, RoundingMode.HALF_UP).doubleValue();
    }
    
    public double SetAngka(String txt){
        double x;   
        try {
            if(txt.equals("")){
                x=0;
            }else{
                x=Double.parseDouble(txt);
            }
        } catch (Exception e) {
            x=0;
        }
            
        return x;
    }
    
    public double setAngkaSmc(String value) {
        if (value.isBlank()) {
            return 0;
        }
        
        try {
            return Double.parseDouble(value);
        } catch (Exception e) {
            return 0;
        }
    }
    
    public int SetInteger(String txt){
        int x;   
        try {
            if(txt.equals("")){
                x=0;
            }else{
                x=Integer.parseInt(txt);
            }
        } catch (Exception e) {
            x=0;
        }
            
        return x;
    }
    
    public double roundUp(double number, int multiple) {
        if(PEMBULATANHARGAOBAT.equals("yes")){
            result = multiple;
            if (number % multiple == 0) {
                return (int) number;
            }

            if (number % multiple != 0) {
                int division = (int) ((number / multiple) + 1);
                result = division * multiple;
            }
            return result;
        }else{
            return Math.round(number);
        }
    }
    
    public String terbilang(double angka)
    {
        if (angka < 12) {
            return nomina[(int) angka];
        }

        if (angka >= 12 && angka <= 19) {
            return nomina[(int) angka % 10] + " belas ";
        }

        if (angka >= 20 && angka <= 99) {
            return nomina[(int) angka / 10] + " puluh " + nomina[(int) angka % 10];
        }

        if (angka >= 100 && angka <= 199) {
            return "seratus " + terbilang(angka % 100);
        }

        if (angka >= 200 && angka <= 999) {
            return nomina[(int) angka / 100] + " ratus " + terbilang(angka % 100);
        }

        if (angka >= 1_000 && angka <= 1_999) {
            return "seribu " + terbilang(angka % 1_000);
        }

        if (angka >= 2_000 && angka <= 999_999) {
            return terbilang((int) angka / 1_000) + " ribu " + terbilang(angka % 1_000);
        }

        if (angka >= 1_000_000 && angka <= 999_999_999) {
            return terbilang((int) angka / 1000000) + " juta " + terbilang(angka % 1000000);
        }

        return "";
    }
    
    public int daysOld(String path) {
        file=new File(path);
        if (file.lastModified() < 1) return 0;
        return milliToDay(Calendar.getInstance().getTimeInMillis() - file.lastModified());
    }

    /**
     * Converts milliseconds to days
     */
    public static int milliToDay(long milli) {
        return (int) ((double) milli / (1000 * 24 * 60 * 60));
    }
}
