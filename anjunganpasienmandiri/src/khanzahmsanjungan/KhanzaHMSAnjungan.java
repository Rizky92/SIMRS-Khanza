/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package khanzahmsanjungan;

import AESsecurity.EnkripsiAES;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.util.ColorFunctions;
import fungsi.koneksiDB;
import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 *
 * @author khanzasoft
 */
public class KhanzaHMSAnjungan {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, ClassLoader.getSystemClassLoader().getResourceAsStream("font/Inter-Regular.ttf")));
            ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, ClassLoader.getSystemClassLoader().getResourceAsStream("font/Inter-Medium.ttf")));
            ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, ClassLoader.getSystemClassLoader().getResourceAsStream("font/Inter-Bold.ttf")));

            Color foreground = new Color(0, 131, 62);
            Color panelBackground = new Color(240, 249, 255);
            Color disableEditBackground = new Color(255, 255, 153);
            Font main = new Font("Inter Medium", Font.PLAIN, 18);

            UIManager.setLookAndFeel(new FlatLightLaf());
            System.setProperty("flatlaf.animation", "true");
            UIManager.put("TitlePane.background", panelBackground);
            UIManager.put("CheckBox.icon.style", "filled");
            UIManager.put("CheckBox.icon[filled].selectedBackground", foreground);
            UIManager.put("ComboBox.background", Color.WHITE);
            UIManager.put("ComboBox.editableBackground", Color.WHITE);
            UIManager.put("ComboBox.foreground", foreground);
            UIManager.put("ComboBox.buttonBackground", Color.WHITE);
            UIManager.put("ComboBox.buttonEditableBackground", Color.WHITE);
            UIManager.put("ComboBox.buttonArrowColor", foreground);
            UIManager.put("ComboBox.buttonHoverArrowColor", ColorFunctions.lighten(foreground, 0.1f));
            UIManager.put("ComboBox.buttonPressedArrowColor", ColorFunctions.darken(foreground, 0.4f));
            UIManager.put("ComboBox.popupBackground", Color.WHITE);
            UIManager.put("ComboBox.selectionBackground", foreground);
            UIManager.put("ComboBox.selectionForeground", Color.WHITE);
            UIManager.put("ComboBox.font", main);
            UIManager.put("Panel.background", panelBackground);
            UIManager.put("Table.background", panelBackground);
            UIManager.put("Table.foreground", foreground);
            UIManager.put("Table.alternateRowColor", Color.WHITE);
            UIManager.put("Table.selectionBackground", foreground);
            UIManager.put("Table.selectionForeground", Color.WHITE);
            UIManager.put("Table.cellMargins", new Insets(2, 14, 2, 14));
            UIManager.put("Table.rowHeight", 50);
            UIManager.put("Table.font", main);
            UIManager.put("TextField.foreground", foreground);
            UIManager.put("TextField.background", Color.WHITE);
            UIManager.put("TextField.selectionBackground", foreground);
            UIManager.put("TextField.selectionForeground", Color.WHITE);
            UIManager.put("TextField.inactiveBackground", disableEditBackground);
            UIManager.put("TextField.font", main);
            UIManager.put("TableHeader.background", Color.WHITE);
            UIManager.put("TableHeader.foreground", foreground);
            UIManager.put("TableHeader.font", new Font("Inter", Font.BOLD, 14));
            UIManager.put("ScrollBar.showButtons", true);
            UIManager.put("ScrollBar.width", 16);
            UIManager.put("ScrollPane.smoothScrolling", true);
            UIManager.put("Button.arc", 16);
            UIManager.put("Component.arc", 8);
            UIManager.put("CheckBox.arc", 8);
            UIManager.put("ProgressBar.arc", 16);
            UIManager.put("TextComponent.arc", 8);
        } catch (Exception e) {
            System.err.println("Failed to initialize LaF");
        }

        try {
            File iyem = new File("./cache/pengaturanapmsmc.iyem");

            if (iyem.isFile()) {
                final ObjectMapper mapper = new ObjectMapper();
                try (FileReader fr = new FileReader("./cache/pengaturanapmsmc.iyem")) {
                    final JsonNode root = mapper.readTree(fr).path("pengaturanapmsmc");
                    final JsonNode decrypted = mapper.readTree(EnkripsiAES.decrypt(root.asText()));

                    String printerBarcode = null, printerRegistrasi = null, printerAntrian = null;

                    for (PrintService ps : PrintServiceLookup.lookupPrintServices(null, null)) {
                        System.out.println("Printer ditemukan: " + ps.getName());

                        if (ps.getName().equals(decrypted.path("printerRegist").asText(koneksiDB.PRINTER_REGISTRASI()))) {
                            printerRegistrasi = ps.getName();
                        }

                        if (ps.getName().equals(decrypted.path("printerBarcode").asText(koneksiDB.PRINTER_BARCODE()))) {
                            printerBarcode = ps.getName();
                        }

                        if (ps.getName().equals(decrypted.path("printerAntrian").asText(koneksiDB.PRINTER_ANTRIAN()))) {
                            printerAntrian = ps.getName();
                        }
                    }

                    if (printerBarcode != null) {
                        System.out.println("Setting PRINTER_BARCODE menggunakan printer: " + printerBarcode);
                    }

                    if (printerRegistrasi != null) {
                        System.out.println("Setting PRINTER_REGISTRASI menggunakan printer: " + printerRegistrasi);
                    }

                    if (printerAntrian != null) {
                        System.out.println("Setting PRINTER_ANTRIAN menggunakan printer: " + printerAntrian);
                    }
                }
            } else {
                iyem.createNewFile();

                try (FileWriter fw = new FileWriter(iyem)) {
                    final ObjectMapper mapper = new ObjectMapper();
                    final ObjectNode root = mapper.createObjectNode();

                    String printerBarcode = null, printerRegistrasi = null, printerAntrian = null;

                    for (PrintService ps : PrintServiceLookup.lookupPrintServices(null, null)) {
                        System.out.println("Printer ditemukan: " + ps.getName());

                        if (ps.getName().equals(koneksiDB.PRINTER_REGISTRASI())) {
                            printerRegistrasi = ps.getName();
                        }

                        if (ps.getName().equals(koneksiDB.PRINTER_BARCODE())) {
                            printerBarcode = ps.getName();
                        }

                        if (ps.getName().equals(koneksiDB.PRINTER_ANTRIAN())) {
                            printerAntrian = ps.getName();
                        }
                    }

                    if (printerBarcode != null) {
                        System.out.println("Setting PRINTER_BARCODE menggunakan printer: " + printerBarcode);
                    }

                    if (printerRegistrasi != null) {
                        System.out.println("Setting PRINTER_REGISTRASI menggunakan printer: " + printerRegistrasi);
                    }

                    if (printerAntrian != null) {
                        System.out.println("Setting PRINTER_ANTRIAN menggunakan printer: " + printerAntrian);
                    }

                    root.put("printerRegist", printerRegistrasi);
                    root.put("printerBarcode", printerBarcode);
                    root.put("printerAntrian", printerAntrian);
                    root.put("printJumlahBarcode", koneksiDB.PRINTJUMLAHBARCODE());
                    root.put("printJumlahAntrianFarmasi", koneksiDB.PRINTJUMLAHANTRIANFARMASI());
                    Set<String> valbiomAktif = new HashSet(Arrays.asList(koneksiDB.VALIDASIBIOMETRIKAKTIF()));
                    root.withObject("fingerprint").put("path", koneksiDB.URLAPLIKASIFINGERPRINTBPJS());
                    root.withObject("fingerprint").put("aktifkan", valbiomAktif.contains("fingerprint"));
                    root.withObject("frista").put("path", koneksiDB.URLAPLIKASIFRISTABPJS());
                    root.withObject("frista").put("aktifkan", valbiomAktif.contains("frista"));
                    root.put("kodePoliEksekutif", koneksiDB.KODEPOLIEKSEKUTIF());
                    root.put("userFP", EnkripsiAES.encrypt(koneksiDB.USERFINGERPRINTBPJS()));
                    root.put("passFP", EnkripsiAES.encrypt(koneksiDB.PASSWORDFINGERPRINTBPJS()));

                    final Set<String> tm = new HashSet(Arrays.asList(koneksiDB.TOMBOLDIMATIKAN()));
                    final ArrayNode ta = mapper.createArrayNode();

                    if (!tm.contains("antrian")) {
                        ta.add("antrian");
                    }
                    if (!tm.contains("antrianfarmasi")) {
                        ta.add("antrianfarmasi");
                    }
                    if (!tm.contains("cekin")) {
                        ta.add("cekin");
                    }
                    if (!tm.contains("daftarpoli")) {
                        ta.add("daftarpoli");
                    }
                    if (!tm.contains("seppertama")) {
                        ta.add("seppertama");
                    }
                    if (!tm.contains("sepkontrol")) {
                        ta.add("sepkontrol");
                    }
                    if (!tm.contains("sepbedapoli")) {
                        ta.add("sepbedapoli");
                    }
                    if (!tm.contains("mobilejkn")) {
                        ta.add("mobilejkn");
                    }
                    if (!tm.contains("satusehat")) {
                        ta.add("satusehat");
                    }
                    root.set("tombolDiaktifkan", ta);
                    root.put("batasRegistrasiSatuJam", koneksiDB.REGISTRASISATUJAMSEBELUMJAMPRAKTEK());

                    String encrypted = EnkripsiAES.encrypt(mapper.writeValueAsString(root));
                    fw.write(mapper.writeValueAsString(mapper.createObjectNode().put("pengaturanapmsmc", encrypted)));
                    fw.flush();
                }
            }
        } catch (Exception e) {
            System.err.println("Gagal membaca pengaturan awal");
        }
        SwingUtilities.invokeLater(() -> new HalamanUtama().setVisible(true));
    }
}
