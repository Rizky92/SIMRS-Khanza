/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

 /*
 * DlgJadwal.java
 *
 * Created on May 22, 2010, 10:25:16 PM
 */
package kepegawaian;

import fungsi.WarnaTable;
import fungsi.akses;
import fungsi.batasInput;
import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.SwingWorker;
import javax.swing.event.DocumentEvent;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class DlgJadwalPegawaiSMC extends javax.swing.JDialog {
    private static final int KOLOM_ID = 1;
    private static final int KOLOM_PIN = 3;
    private static final int KOLOM_HARI_AWAL = 7;
    private static final int IMPOR_BARIS_BULAN = 1;
    private static final int IMPOR_BARIS_TAHUN = 2;
    private static final int IMPOR_BARIS_JUDUL = 4;
    private static final int IMPOR_BARIS_AWAL = 5;
    private static final int IMPOR_KOLOM_NAMA = 1;
    private static final int IMPOR_KOLOM_PIN = 2;
    private static final int IMPOR_KOLOM_HARI_AWAL = 3;
    private static final String SHIFT_LEGACY_DEFAULT = "Pagi";
    private static final String JENIS_TAMBAHAN = "Jadwal Tambahan";

    private final DefaultTableModel tabMode;
    private final Connection koneksi = koneksiDB.condb();
    private final sekuel Sequel = new sekuel();
    private final validasi Valid = new validasi();
    private volatile boolean ceksukses = false;
    private int i = 0;
    private YearMonth ym = YearMonth.now();
    private YearMonth bulanSalin = null;
    private String isiSalin = "";

    /**
     * Creates new form DlgJadwal
     *
     * @param parent
     * @param modal
     */
    public DlgJadwalPegawaiSMC(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();

        tabMode = new DefaultTableModel(null, new Object[] {
            "No", "ID", "NIK", "PIN", "Nama", "Pendidikan", "Departemen", "01", "02", "03", "04", "05",
            "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18",
            "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31"
        }) {
            @Override
            public Class getColumnClass(int columnIndex) {
                return String.class;
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return KOLOM_PIN == column || (column >= KOLOM_HARI_AWAL && column < KOLOM_HARI_AWAL + ym.lengthOfMonth());
            }
        };
        tbJadwal.setModel(tabMode);

        for (int i = 0; i < tabMode.getColumnCount(); i++) {
            TableColumn column = tbJadwal.getColumnModel().getColumn(i);
            if (i == 0) {
                column.setPreferredWidth(35);
            } else if (i == KOLOM_ID) {
                column.setMinWidth(0);
                column.setMaxWidth(0);
                column.setPreferredWidth(0);
            } else if (i == 2) {
                column.setPreferredWidth(60);
            } else if (i == KOLOM_PIN) {
                column.setPreferredWidth(70);
            } else if (i == 4) {
                column.setPreferredWidth(250);
            } else if (i == 5 || i == 6) {
                column.setPreferredWidth(120);
            } else {
                column.setMinWidth(0);
                column.setMaxWidth(0);
            }
        }

        tbJadwal.setDefaultRenderer(Object.class, new WarnaTable());
        tbJadwal.setPreferredScrollableViewportSize(new Dimension(500, 500));
        tbJadwal.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        Valid.LoadTahun(ThnCari);
        Valid.loadCombo(Departemen, "nama", "departemen");
        Departemen.addItem("Semua");
        Departemen.setSelectedItem("Semua");

        TCari.setDocument(new batasInput((byte) 100).getKata(TCari));

        int pintasan = Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx();
        InputMap petaTombol = tbJadwal.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        ActionMap petaAksi = tbJadwal.getActionMap();
        petaTombol.put(KeyStroke.getKeyStroke(KeyEvent.VK_C, pintasan), "salinJadwalSmc");
        petaTombol.put(KeyStroke.getKeyStroke(KeyEvent.VK_INSERT, pintasan), "salinJadwalSmc");
        petaTombol.put(KeyStroke.getKeyStroke(KeyEvent.VK_V, pintasan), "tempelJadwalSmc");
        petaTombol.put(KeyStroke.getKeyStroke(KeyEvent.VK_INSERT, KeyEvent.SHIFT_DOWN_MASK), "tempelJadwalSmc");
        petaAksi.put("salinJadwalSmc", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                salinJadwal();
            }
        });
        petaAksi.put("tempelJadwalSmc", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tempelJadwal();
            }
        });
        petaTombol.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "hariBerikutnyaSmc");
        petaTombol.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, KeyEvent.SHIFT_DOWN_MASK), "hariSebelumnyaSmc");
        petaAksi.put("hariBerikutnyaSmc", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pindahFokusSmc(1);
            }
        });
        petaAksi.put("hariSebelumnyaSmc", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pindahFokusSmc(-1);
            }
        });
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        internalFrame1 = new widget.InternalFrame();
        Scroll = new widget.ScrollPane();
        tbJadwal = new widget.Table();
        jPanel3 = new javax.swing.JPanel();
        panelGlass8 = new widget.panelisi();
        BtnSimpan = new widget.Button();
        BtnHapus = new widget.Button();
        BtnImpor = new widget.Button();
        BtnEkspor = new widget.Button();
        BtnPrint = new widget.Button();
        BtnKeluar = new widget.Button();
        panelGlass9 = new widget.panelisi();
        jLabel6 = new widget.Label();
        TCari = new widget.TextBox();
        BtnCari = new widget.Button();
        BtnAll = new widget.Button();
        jLabel7 = new widget.Label();
        LCount = new widget.Label();
        panelBiasa1 = new widget.PanelBiasa();
        label13 = new widget.Label();
        cmbJenis = new widget.ComboBox();
        label11 = new widget.Label();
        ThnCari = new widget.ComboBox();
        BlnCari = new widget.ComboBox();
        label12 = new widget.Label();
        Departemen = new widget.ComboBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)), "::[ Jadwal Dinas Pegawai ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50))); // NOI18N
        internalFrame1.setName("internalFrame1"); // NOI18N
        internalFrame1.setLayout(new java.awt.BorderLayout(1, 1));

        Scroll.setName("Scroll"); // NOI18N
        Scroll.setOpaque(true);

        tbJadwal.setToolTipText("Spasi untuk memilih shift, Delete untuk mengosongkan, Ctrl+C dan Ctrl+V untuk menyalin jadwal antar pegawai");
        tbJadwal.setName("tbJadwal"); // NOI18N
        tbJadwal.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tbJadwalKeyPressed(evt);
            }
        });
        Scroll.setViewportView(tbJadwal);

        internalFrame1.add(Scroll, java.awt.BorderLayout.CENTER);

        jPanel3.setName("jPanel3"); // NOI18N
        jPanel3.setOpaque(false);
        jPanel3.setPreferredSize(new java.awt.Dimension(44, 100));
        jPanel3.setLayout(new java.awt.BorderLayout(1, 1));

        panelGlass8.setName("panelGlass8"); // NOI18N
        panelGlass8.setPreferredSize(new java.awt.Dimension(44, 44));
        panelGlass8.setLayout(null);

        BtnSimpan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/save-16x16.png"))); // NOI18N
        BtnSimpan.setMnemonic('U');
        BtnSimpan.setText("Update");
        BtnSimpan.setToolTipText("Alt+U");
        BtnSimpan.setName("BtnSimpan"); // NOI18N
        BtnSimpan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnSimpanActionPerformed(evt);
            }
        });
        BtnSimpan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnSimpanKeyPressed(evt);
            }
        });
        panelGlass8.add(BtnSimpan);
        BtnSimpan.setBounds(6, 10, 100, 30);

        BtnHapus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/stop_f2.png"))); // NOI18N
        BtnHapus.setMnemonic('H');
        BtnHapus.setText("Hapus");
        BtnHapus.setToolTipText("Alt+H");
        BtnHapus.setName("BtnHapus"); // NOI18N
        BtnHapus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnHapusActionPerformed(evt);
            }
        });
        BtnHapus.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnHapusKeyPressed(evt);
            }
        });
        panelGlass8.add(BtnHapus);
        BtnHapus.setBounds(108, 10, 100, 30);

        BtnImpor.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/file-edit-16x16.png"))); // NOI18N
        BtnImpor.setMnemonic('I');
        BtnImpor.setText("Impor");
        BtnImpor.setToolTipText("Alt+I");
        BtnImpor.setName("BtnImpor"); // NOI18N
        BtnImpor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnImporActionPerformed(evt);
            }
        });
        BtnImpor.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnImporKeyPressed(evt);
            }
        });
        panelGlass8.add(BtnImpor);
        BtnImpor.setBounds(210, 10, 100, 30);

        BtnEkspor.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Export20.png"))); // NOI18N
        BtnEkspor.setMnemonic('E');
        BtnEkspor.setText("Ekspor");
        BtnEkspor.setToolTipText("Alt+E");
        BtnEkspor.setName("BtnEkspor"); // NOI18N
        BtnEkspor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnEksporActionPerformed(evt);
            }
        });
        BtnEkspor.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnEksporKeyPressed(evt);
            }
        });
        panelGlass8.add(BtnEkspor);
        BtnEkspor.setBounds(312, 10, 100, 30);

        BtnPrint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/b_print.png"))); // NOI18N
        BtnPrint.setMnemonic('T');
        BtnPrint.setText("Cetak");
        BtnPrint.setToolTipText("Alt+T");
        BtnPrint.setName("BtnPrint"); // NOI18N
        BtnPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnPrintActionPerformed(evt);
            }
        });
        BtnPrint.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnPrintKeyPressed(evt);
            }
        });
        panelGlass8.add(BtnPrint);
        BtnPrint.setBounds(414, 10, 100, 30);

        BtnKeluar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/exit.png"))); // NOI18N
        BtnKeluar.setMnemonic('K');
        BtnKeluar.setText("Keluar");
        BtnKeluar.setToolTipText("Alt+K");
        BtnKeluar.setName("BtnKeluar"); // NOI18N
        BtnKeluar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnKeluarActionPerformed(evt);
            }
        });
        BtnKeluar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnKeluarKeyPressed(evt);
            }
        });
        panelGlass8.add(BtnKeluar);
        BtnKeluar.setBounds(518, 10, 100, 30);

        jPanel3.add(panelGlass8, java.awt.BorderLayout.CENTER);

        panelGlass9.setName("panelGlass9"); // NOI18N
        panelGlass9.setPreferredSize(new java.awt.Dimension(44, 44));
        panelGlass9.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 9));

        jLabel6.setText("Key Word :");
        jLabel6.setName("jLabel6"); // NOI18N
        jLabel6.setPreferredSize(new java.awt.Dimension(70, 23));
        panelGlass9.add(jLabel6);

        TCari.setName("TCari"); // NOI18N
        TCari.setPreferredSize(new java.awt.Dimension(450, 23));
        TCari.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TCariKeyPressed(evt);
            }
        });
        panelGlass9.add(TCari);

        BtnCari.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png"))); // NOI18N
        BtnCari.setMnemonic('3');
        BtnCari.setToolTipText("Alt+3");
        BtnCari.setName("BtnCari"); // NOI18N
        BtnCari.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnCari.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnCariActionPerformed(evt);
            }
        });
        BtnCari.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnCariKeyPressed(evt);
            }
        });
        panelGlass9.add(BtnCari);

        BtnAll.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Search-16x16.png"))); // NOI18N
        BtnAll.setMnemonic('4');
        BtnAll.setToolTipText("Alt+4");
        BtnAll.setName("BtnAll"); // NOI18N
        BtnAll.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnAllActionPerformed(evt);
            }
        });
        BtnAll.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnAllKeyPressed(evt);
            }
        });
        panelGlass9.add(BtnAll);

        jLabel7.setText("Record :");
        jLabel7.setName("jLabel7"); // NOI18N
        jLabel7.setPreferredSize(new java.awt.Dimension(65, 23));
        panelGlass9.add(jLabel7);

        LCount.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        LCount.setText("0");
        LCount.setName("LCount"); // NOI18N
        LCount.setPreferredSize(new java.awt.Dimension(50, 23));
        panelGlass9.add(LCount);

        jPanel3.add(panelGlass9, java.awt.BorderLayout.PAGE_START);

        internalFrame1.add(jPanel3, java.awt.BorderLayout.PAGE_END);

        panelBiasa1.setName("panelBiasa1"); // NOI18N
        panelBiasa1.setPreferredSize(new java.awt.Dimension(1023, 47));
        panelBiasa1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 3, 10));

        label13.setText("Jenis Jadwal :");
        label13.setName("label13"); // NOI18N
        label13.setPreferredSize(new java.awt.Dimension(90, 23));
        panelBiasa1.add(label13);

        cmbJenis.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Jadwal Dinas", "Jadwal Tambahan" }));
        cmbJenis.setName("cmbJenis"); // NOI18N
        cmbJenis.setPreferredSize(new java.awt.Dimension(140, 23));
        cmbJenis.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbJenisItemStateChanged(evt);
            }
        });
        panelBiasa1.add(cmbJenis);

        label11.setText("Tahun & Bulan :");
        label11.setName("label11"); // NOI18N
        label11.setPreferredSize(new java.awt.Dimension(90, 23));
        panelBiasa1.add(label11);

        ThnCari.setName("ThnCari"); // NOI18N
        ThnCari.setPreferredSize(new java.awt.Dimension(85, 23));
        ThnCari.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                ThnCariItemStateChanged(evt);
            }
        });
        panelBiasa1.add(ThnCari);

        BlnCari.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12" }));
        BlnCari.setName("BlnCari"); // NOI18N
        BlnCari.setPreferredSize(new java.awt.Dimension(62, 23));
        BlnCari.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                BlnCariItemStateChanged(evt);
            }
        });
        panelBiasa1.add(BlnCari);

        label12.setText("Departemen :");
        label12.setName("label12"); // NOI18N
        label12.setPreferredSize(new java.awt.Dimension(120, 23));
        panelBiasa1.add(label12);

        Departemen.setName("Departemen"); // NOI18N
        Departemen.setPreferredSize(new java.awt.Dimension(230, 23));
        Departemen.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                DepartemenItemStateChanged(evt);
            }
        });
        panelBiasa1.add(Departemen);

        internalFrame1.add(panelBiasa1, java.awt.BorderLayout.PAGE_START);

        getContentPane().add(internalFrame1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void BtnSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnSimpanActionPerformed
        if (ceksukses) {
            JOptionPane.showMessageDialog(null, "Proses loading data belum selesai, silahkan tunggu hingga proses loading selesai...!!!!");
            return;
        }

        Set<String> kodeSah = getKodeShift();
        Set<String> kodeAsing = new LinkedHashSet<>();
        for (i = 0; i < tabMode.getRowCount(); i++) {
            for (int hari = 0; hari < ym.lengthOfMonth(); hari++) {
                String kode = getKode(i, hari);
                if (!kode.isEmpty() && !kodeSah.contains(kode)) {
                    kodeAsing.add(kode);
                }
            }
        }
        if (!kodeAsing.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Kode shift berikut belum terdaftar di jadwal dinas : " + String.join(", ", kodeAsing));
            return;
        }

        Map<String, String> pemilikPin = getPegawaiPerPin();
        Set<String> idTampil = new LinkedHashSet<>();
        Set<String> pinTerpakai = new LinkedHashSet<>();
        Set<String> pinKembar = new LinkedHashSet<>();
        for (i = 0; i < tabMode.getRowCount(); i++) {
            idTampil.add(tabMode.getValueAt(i, KOLOM_ID).toString());
            String pin = getPin(i);
            if (!pin.isEmpty() && !pinTerpakai.add(pin)) {
                pinKembar.add(pin);
            }
        }
        if (!pinKembar.isEmpty()) {
            JOptionPane.showMessageDialog(null, "PIN berikut dipakai lebih dari satu pegawai : " + String.join(", ", pinKembar));
            return;
        }

        Set<String> pinBentrok = new LinkedHashSet<>();
        for (i = 0; i < tabMode.getRowCount(); i++) {
            String pin = getPin(i);
            String pemilik = pemilikPin.get(pin);
            if (!pin.isEmpty() && null != pemilik && !pemilik.equals(tabMode.getValueAt(i, KOLOM_ID).toString()) && !idTampil.contains(pemilik)) {
                pinBentrok.add(pin);
            }
        }
        if (!pinBentrok.isEmpty()) {
            JOptionPane.showMessageDialog(null, "PIN berikut sudah dipakai pegawai lain di luar tampilan ini : " + String.join(", ", pinBentrok));
            return;
        }

        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        Map<String, String> legacy = getShiftLegacy();
        Map<String, String> pinLama = getPinPerPegawai(pemilikPin);
        int pinBerubah = 0;
        boolean sukses = true;
        try {
            Sequel.AutoComitFalse();
            for (i = 0; i < tabMode.getRowCount(); i++) {
                String id = tabMode.getValueAt(i, KOLOM_ID).toString();
                if (simpanPin(id, getPin(i), pinLama.getOrDefault(id, ""))) {
                    pinBerubah++;
                }
                if (!simpanJadwal(id, i, legacy)) {
                    sukses = false;
                }
            }

            if (sukses) {
                Sequel.Commit();
            } else {
                Sequel.RollBack();
            }
        } catch (Exception e) {
            sukses = false;
            Sequel.RollBack();
            System.out.println("Notif : " + e);
        } finally {
            Sequel.AutoComitTrue();
            this.setCursor(Cursor.getDefaultCursor());
        }

        if (!sukses) {
            JOptionPane.showMessageDialog(null, "Sebagian jadwal gagal disimpan, seluruh perubahan dibatalkan...!!!!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JOptionPane.showMessageDialog(null, "Proses selesai...!!!!" + (0 == pinBerubah ? "" : "\nPIN diperbarui untuk " + pinBerubah + " pegawai."));
        tampilSmc();
    }//GEN-LAST:event_BtnSimpanActionPerformed

    private void BtnSimpanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnSimpanKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            BtnSimpanActionPerformed(null);
        } else {
            Valid.pindah(evt, BtnKeluar, BtnHapus);
        }
    }//GEN-LAST:event_BtnSimpanKeyPressed

    private void BtnHapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnHapusActionPerformed
        if (tabMode.getRowCount() != 0) {
            if (tbJadwal.getSelectedRow() != -1) {
                String id = tabMode.getValueAt(tbJadwal.getSelectedRow(), KOLOM_ID).toString();
                Sequel.menghapustfSmc(tabelSmc(), "id=? and tanggal between ? and ?", id,
                    ym.atDay(1).toString(), ym.atEndOfMonth().toString());
                Sequel.menghapustfSmc(tabelLegacy(), "id=? and tahun=? and bulan=?", id,
                    ThnCari.getSelectedItem().toString(), BlnCari.getSelectedItem().toString());
                tampilSmc();
            }
        }
    }//GEN-LAST:event_BtnHapusActionPerformed

    private void BtnHapusKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnHapusKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            BtnHapusActionPerformed(null);
        } else {
            Valid.pindah(evt, BtnSimpan, BtnImpor);
        }
    }//GEN-LAST:event_BtnHapusKeyPressed

    private void BtnImporActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnImporActionPerformed
        if (ceksukses) {
            JOptionPane.showMessageDialog(null, "Proses loading data belum selesai, silahkan tunggu hingga proses loading selesai...!!!!");
            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Pilih berkas jadwal per karyawan");
        chooser.setFileFilter(new FileNameExtensionFilter("Berkas Excel (*.xlsx)", "xlsx"));
        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        YearMonth periode;
        Map<String, String[]> jadwal = new LinkedHashMap<>();
        Set<String> pinAsing = new LinkedHashSet<>();
        Set<String> kodeAsing = new LinkedHashSet<>();
        try (Workbook workbook = WorkbookFactory.create(chooser.getSelectedFile())) {
            Sheet sheet = workbook.getSheetAt(0);
            periode = YearMonth.of(Integer.parseInt(bacaTeks(sheet.getRow(IMPOR_BARIS_TAHUN), 1)),
                Integer.parseInt(bacaTeks(sheet.getRow(IMPOR_BARIS_BULAN), 1)));

            Map<String, Integer> kolomHari = new LinkedHashMap<>();
            Row judul = sheet.getRow(IMPOR_BARIS_JUDUL);
            for (int kolom = IMPOR_KOLOM_HARI_AWAL; kolom < judul.getLastCellNum(); kolom++) {
                String teks = bacaTeks(judul, kolom);
                if (!teks.isEmpty()) {
                    kolomHari.put(teks, kolom);
                }
            }

            Map<String, String> pegawai = getPegawaiPerPin();
            Set<String> kodeSah = getKodeShift();
            for (int nomor = IMPOR_BARIS_AWAL; nomor <= sheet.getLastRowNum(); nomor++) {
                Row baris = sheet.getRow(nomor);
                if (baris == null) {
                    continue;
                }

                String pin = bacaTeks(baris, IMPOR_KOLOM_PIN);
                if (pin.isEmpty() || bacaTeks(baris, IMPOR_KOLOM_NAMA).isEmpty()) {
                    break;
                }
                if (!pegawai.containsKey(pin)) {
                    pinAsing.add(pin + " (" + bacaTeks(baris, IMPOR_KOLOM_NAMA) + ")");
                    continue;
                }

                String[] shift = new String[31];
                for (Map.Entry<String, Integer> hari : kolomHari.entrySet()) {
                    int tanggal = Integer.parseInt(hari.getKey());
                    if (tanggal < 1 || tanggal > periode.lengthOfMonth()) {
                        continue;
                    }

                    String kode = bacaTeks(baris, hari.getValue()).toUpperCase();
                    if (kode.isEmpty()) {
                        continue;
                    }
                    if (!kodeSah.contains(kode)) {
                        kodeAsing.add(kode);
                        continue;
                    }
                    shift[tanggal - 1] = kode;
                }
                jadwal.put(pegawai.get(pin), shift);
            }
        } catch (Exception e) {
            System.out.println("Notifikasi : " + e);
            this.setCursor(Cursor.getDefaultCursor());
            JOptionPane.showMessageDialog(null, "Gagal membaca berkas, pastikan formatnya sesuai jadwal per karyawan...!!!!");
            return;
        }
        this.setCursor(Cursor.getDefaultCursor());

        StringBuilder ringkasan = new StringBuilder();
        ringkasan.append("Periode ").append(periode).append(", ").append(jadwal.size()).append(" pegawai siap diimpor.");
        if (!pinAsing.isEmpty()) {
            ringkasan.append("\n\nPIN tidak dikenal (").append(pinAsing.size()).append(") : ").append(String.join(", ", pinAsing));
        }
        if (!kodeAsing.isEmpty()) {
            ringkasan.append("\n\nKode shift tidak terdaftar (").append(kodeAsing.size()).append(") : ").append(String.join(", ", kodeAsing));
        }
        if (jadwal.isEmpty()) {
            JOptionPane.showMessageDialog(null, ringkasan.toString());
            return;
        }

        ringkasan.append("\n\nData yang tidak dikenal akan dilewati. Lanjutkan impor?");
        if (JOptionPane.showConfirmDialog(null, ringkasan.toString(), "Konfirmasi Impor", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
            return;
        }

        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        Map<String, String> legacy = getShiftLegacy();
        boolean sukses = true;
        try {
            Sequel.AutoComitFalse();
            for (Map.Entry<String, String[]> baris : jadwal.entrySet()) {
                if (!simpanJadwal(baris.getKey(), periode, baris.getValue(), legacy)) {
                    sukses = false;
                }
            }

            if (sukses) {
                Sequel.Commit();
            } else {
                Sequel.RollBack();
            }
        } catch (Exception e) {
            sukses = false;
            Sequel.RollBack();
            System.out.println("Notif : " + e);
        } finally {
            Sequel.AutoComitTrue();
            this.setCursor(Cursor.getDefaultCursor());
        }

        if (!sukses) {
            JOptionPane.showMessageDialog(null, "Sebagian jadwal gagal diimpor, seluruh perubahan dibatalkan...!!!!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        ThnCari.setSelectedItem(String.valueOf(periode.getYear()));
        BlnCari.setSelectedItem(String.format("%02d", periode.getMonthValue()));
        JOptionPane.showMessageDialog(null, "Impor selesai untuk " + jadwal.size() + " pegawai.");
        tampilSmc();
    }//GEN-LAST:event_BtnImporActionPerformed

    private void BtnImporKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnImporKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            BtnImporActionPerformed(null);
        } else {
            Valid.pindah(evt, BtnHapus, BtnEkspor);
        }
    }//GEN-LAST:event_BtnImporKeyPressed

    private void BtnEksporActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnEksporActionPerformed
        if (ceksukses) {
            JOptionPane.showMessageDialog(null, "Proses loading data belum selesai, silahkan tunggu hingga proses loading selesai...!!!!");
            return;
        }
        if (tabMode.getRowCount() == 0) {
            JOptionPane.showMessageDialog(null, "Maaf, data sudah habis. Tidak ada data yang bisa anda ekspor...!!!!");
            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Simpan jadwal per karyawan");
        chooser.setFileFilter(new FileNameExtensionFilter("Berkas Excel (*.xlsx)", "xlsx"));
        chooser.setSelectedFile(new File(namaBerkas() + "PerKaryawan-" + ym + ".xlsx"));
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File berkas = chooser.getSelectedFile();
        if (!berkas.getName().toLowerCase().endsWith(".xlsx")) {
            berkas = new File(berkas.getAbsolutePath() + ".xlsx");
        }

        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        try (XSSFWorkbook workbook = new XSSFWorkbook(); FileOutputStream keluaran = new FileOutputStream(berkas)) {
            Sheet sheet = workbook.createSheet("Sheet1");
            sheet.createRow(0).createCell(0).setCellValue("JADWAL PER KARYAWAN");

            Row barisBulan = sheet.createRow(IMPOR_BARIS_BULAN);
            barisBulan.createCell(0).setCellValue("Bulan: ");
            barisBulan.createCell(1).setCellValue(ym.getMonthValue());

            Row barisTahun = sheet.createRow(IMPOR_BARIS_TAHUN);
            barisTahun.createCell(0).setCellValue("Tahun: ");
            barisTahun.createCell(1).setCellValue(ym.getYear());

            Row judul = sheet.createRow(IMPOR_BARIS_JUDUL);
            judul.createCell(IMPOR_KOLOM_NAMA).setCellValue("Nama Lengkap");
            judul.createCell(IMPOR_KOLOM_PIN).setCellValue("PIN");
            for (int hari = 1; hari <= ym.lengthOfMonth(); hari++) {
                judul.createCell(IMPOR_KOLOM_HARI_AWAL + hari - 1).setCellValue(String.format("%02d", hari));
            }

            int nomor = IMPOR_BARIS_AWAL;
            for (int baris = 0; baris < tabMode.getRowCount(); baris++) {
                Row keluar = sheet.createRow(nomor++);
                keluar.createCell(IMPOR_KOLOM_NAMA).setCellValue(tabMode.getValueAt(baris, 4).toString());
                keluar.createCell(IMPOR_KOLOM_PIN).setCellValue(tabMode.getValueAt(baris, KOLOM_PIN).toString());
                for (int hari = 0; hari < ym.lengthOfMonth(); hari++) {
                    String kode = getKode(baris, hari);
                    if (!kode.isEmpty()) {
                        keluar.createCell(IMPOR_KOLOM_HARI_AWAL + hari).setCellValue(kode);
                    }
                }
            }

            nomor++;
            sheet.createRow(nomor++).createCell(0).setCellValue("Keterangan:");
            for (Map.Entry<String, String> shift : getNamaShift().entrySet()) {
                Row keterangan = sheet.createRow(nomor++);
                keterangan.createCell(0).setCellValue(shift.getKey());
                keterangan.createCell(1).setCellValue(shift.getValue());
            }

            workbook.write(keluaran);
        } catch (Exception e) {
            System.out.println("Notifikasi : " + e);
            this.setCursor(Cursor.getDefaultCursor());
            JOptionPane.showMessageDialog(null, "Gagal menulis berkas...!!!!");
            return;
        }
        this.setCursor(Cursor.getDefaultCursor());

        JOptionPane.showMessageDialog(null, "Ekspor selesai ke " + berkas.getAbsolutePath());
    }//GEN-LAST:event_BtnEksporActionPerformed

    private void BtnEksporKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnEksporKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            BtnEksporActionPerformed(null);
        } else {
            Valid.pindah(evt, BtnImpor, BtnPrint);
        }
    }//GEN-LAST:event_BtnEksporKeyPressed

    private void BtnKeluarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnKeluarActionPerformed
        dispose();
    }//GEN-LAST:event_BtnKeluarActionPerformed

    private void BtnKeluarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnKeluarKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            dispose();
        } else {
            Valid.pindah(evt, BtnPrint, TCari);
        }
    }//GEN-LAST:event_BtnKeluarKeyPressed

    private void BtnPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnPrintActionPerformed
        if (ceksukses) {
            JOptionPane.showMessageDialog(null, "Proses loading data belum selesai, silahkan tunggu hingga proses loading selesai...!!!!");
            return;
        }
        if (tabMode.getRowCount() == 0) {
            JOptionPane.showMessageDialog(null, "Maaf, data sudah habis. Tidak ada data yang bisa anda print...!!!!");
        } else if (tabMode.getRowCount() != 0) {
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            try {
                try (BufferedWriter bw = new BufferedWriter(new FileWriter(new File("file2.css")))) {
                    bw.write(".isi td{border-right: 1px solid #e2e7dd;font: 8.5px tahoma;height:12px;border-bottom: 1px solid #e2e7dd;background: #ffffff;color:#323232;}.head td{border-right: 1px solid #777777;font: 8.5px tahoma;height:10px;border-bottom: 1px solid #e2e7dd;background: #ffffff;color:#323232;}.isi a{text-decoration:none;color:#8b9b95;padding:0 0 0 0px;font-family: Tahoma;font-size: 8.5px;}.isi2 td{font: 8.5px tahoma;height:12px;background: #ffffff;color:#323232;}.isi3 td{border-right: 1px solid #e2e7dd;font: 8.5px tahoma;height:12px;border-top: 1px solid #e2e7dd;background: #ffffff;color:#323232;}.isi4 td{font: 11px tahoma;height:12px;border-top: 1px solid #e2e7dd;background: #ffffff;color:#323232;}");
                    bw.flush();
                }
                String pilihan = (String) JOptionPane.showInputDialog(null, "Silahkan pilih laporan..!", "Pilihan Cetak", JOptionPane.QUESTION_MESSAGE, null, new Object[] {
                    "Laporan 1 (HTML)", "Laporan 2 (WPS)", "Laporan 3 (CSV)", "Laporan 4 (XLSX)"
                }, "Laporan 4 (XLSX)");
                switch (pilihan) {
                    case "Laporan 1 (HTML)":
                        Valid.exportHtmlSmc(namaBerkas() + ".html", judulJenis(), tbJadwal);
                        break;
                    case "Laporan 2 (WPS)":
                        Valid.exportWPSSmc(namaBerkas() + ".wps", judulJenis(), tbJadwal);
                        break;
                    case "Laporan 3 (CSV)":
                        Valid.exportCSVSmc(namaBerkas() + ".csv", tbJadwal);
                        break;
                    case "Laporan 4 (XLSX)":
                        Valid.exportXlsxSmc(namaBerkas() + ".xlsx", tbJadwal);
                        break;
                }
            } catch (Exception e) {
                System.out.println("Notifikasi : " + e);
            }
            this.setCursor(Cursor.getDefaultCursor());
        }
    }//GEN-LAST:event_BtnPrintActionPerformed

    private void BtnPrintKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnPrintKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            BtnPrintActionPerformed(null);
        } else {
            Valid.pindah(evt, BtnEkspor, BtnKeluar);
        }
    }//GEN-LAST:event_BtnPrintKeyPressed

    private void TCariKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TCariKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            BtnCariActionPerformed(null);
        } else if (evt.getKeyCode() == KeyEvent.VK_PAGE_DOWN) {
            BtnCari.requestFocus();
        } else if (evt.getKeyCode() == KeyEvent.VK_PAGE_UP) {
            BtnKeluar.requestFocus();
        }
    }//GEN-LAST:event_TCariKeyPressed

    private void BtnCariActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnCariActionPerformed
        tampilSmc();
    }//GEN-LAST:event_BtnCariActionPerformed

    private void BtnCariKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnCariKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            BtnCariActionPerformed(null);
        } else {
            Valid.pindah(evt, TCari, BtnAll);
        }
    }//GEN-LAST:event_BtnCariKeyPressed

    private void BtnAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnAllActionPerformed
        TCari.setText("");
        tampilSmc();
    }//GEN-LAST:event_BtnAllActionPerformed

    private void BtnAllKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnAllKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            TCari.setText("");
            tampilSmc();
        } else {
            Valid.pindah(evt, BtnCari, BtnKeluar);
        }
    }//GEN-LAST:event_BtnAllKeyPressed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        tampilSmc();
        if (koneksiDB.CARICEPAT().equals("aktif")) {
            TCari.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    if (TCari.getText().length() > 2) {
                        tampilSmc();
                    }
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    if (TCari.getText().length() > 2) {
                        tampilSmc();
                    }
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    if (TCari.getText().length() > 2) {
                        tampilSmc();
                    }
                }
            });
        }
    }//GEN-LAST:event_formWindowOpened

    private void tbJadwalKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tbJadwalKeyPressed
        if (tabMode.getRowCount() != 0) {
            if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
                int baris = tbJadwal.getSelectedRow();
                int kolom = tbJadwal.getSelectedColumn();
                if (0 > baris || KOLOM_HARI_AWAL > kolom || KOLOM_HARI_AWAL + ym.lengthOfMonth() <= kolom) {
                    return;
                }

                evt.consume();
                batalkanEditSmc();

                DlgJamMasukSMC jammasuk = new DlgJamMasukSMC(null, false);
                jammasuk.addWindowListener(new WindowListener() {
                    @Override
                    public void windowOpened(WindowEvent e) {
                    }

                    @Override
                    public void windowClosing(WindowEvent e) {
                    }

                    @Override
                    public void windowClosed(WindowEvent e) {
                        int terpilih = jammasuk.getTable().getSelectedRow();
                        if (-1 != terpilih) {
                            Object kode = jammasuk.getTable().getValueAt(terpilih, 1);
                            batalkanEditSmc();
                            tabMode.setValueAt(null == kode ? "" : kode.toString(), baris, kolom);
                        }
                        tbJadwal.changeSelection(baris, kolom, false, false);
                        tbJadwal.requestFocus();
                    }

                    @Override
                    public void windowIconified(WindowEvent e) {
                    }

                    @Override
                    public void windowDeiconified(WindowEvent e) {
                    }

                    @Override
                    public void windowActivated(WindowEvent e) {
                    }

                    @Override
                    public void windowDeactivated(WindowEvent e) {
                    }
                });

                jammasuk.getTable().addKeyListener(new KeyListener() {
                    @Override
                    public void keyTyped(KeyEvent e) {
                    }

                    @Override
                    public void keyPressed(KeyEvent e) {
                        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                            e.consume();
                            jammasuk.dispose();
                        }
                    }

                    @Override
                    public void keyReleased(KeyEvent e) {
                    }
                });
                jammasuk.isCek();
                jammasuk.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight());
                jammasuk.setLocationRelativeTo(internalFrame1);
                jammasuk.setVisible(true);
            } else if (evt.getKeyCode() == KeyEvent.VK_DELETE) {
                if (-1 != tbJadwal.getSelectedRow() && -1 != tbJadwal.getSelectedColumn()) {
                    evt.consume();
                    batalkanEditSmc();
                    tabMode.setValueAt("", tbJadwal.getSelectedRow(), tbJadwal.getSelectedColumn());
                }
            }
        }
    }//GEN-LAST:event_tbJadwalKeyPressed

    private void cmbJenisItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbJenisItemStateChanged
        if (this.isActive() == true) {
            tampilSmc();
        }
    }//GEN-LAST:event_cmbJenisItemStateChanged

    private void BlnCariItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_BlnCariItemStateChanged
        if (this.isActive() == true) {
            tampilSmc();
        }
    }//GEN-LAST:event_BlnCariItemStateChanged

    private void ThnCariItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_ThnCariItemStateChanged
        if (this.isActive() == true) {
            tampilSmc();
        }
    }//GEN-LAST:event_ThnCariItemStateChanged

    private void DepartemenItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_DepartemenItemStateChanged
        if (this.isActive() == true) {
            tampilSmc();
        }
    }//GEN-LAST:event_DepartemenItemStateChanged

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            DlgJadwalPegawaiSMC dialog = new DlgJadwalPegawaiSMC(new javax.swing.JFrame(), true);
            dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent e) {
                    System.exit(0);
                }
            });
            dialog.setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private widget.ComboBox BlnCari;
    private widget.Button BtnAll;
    private widget.Button BtnCari;
    private widget.Button BtnEkspor;
    private widget.Button BtnHapus;
    private widget.Button BtnImpor;
    private widget.Button BtnKeluar;
    private widget.Button BtnPrint;
    private widget.Button BtnSimpan;
    private widget.ComboBox Departemen;
    private widget.Label LCount;
    private widget.ScrollPane Scroll;
    private widget.TextBox TCari;
    private widget.ComboBox ThnCari;
    private widget.InternalFrame internalFrame1;
    private widget.Label jLabel6;
    private widget.Label jLabel7;
    private javax.swing.JPanel jPanel3;
    private widget.ComboBox cmbJenis;
    private widget.Label label11;
    private widget.Label label12;
    private widget.Label label13;
    private widget.PanelBiasa panelBiasa1;
    private widget.panelisi panelGlass8;
    private widget.panelisi panelGlass9;
    private widget.Table tbJadwal;
    // End of variables declaration//GEN-END:variables

    public void isCek() {
        BtnSimpan.setEnabled(akses.getjadwal_pegawai_smc());
        BtnHapus.setEnabled(akses.getjadwal_pegawai_smc());
        BtnImpor.setEnabled(akses.getjadwal_pegawai_smc());
    }

    public JTable getTable() {
        return tbJadwal;
    }

    private void batalkanEditSmc() {
        if (tbJadwal.isEditing()) {
            TableCellEditor editor = tbJadwal.getCellEditor();
            if (null != editor) {
                editor.cancelCellEditing();
            }
        }
    }

    private String getKode(int baris, int hari) {
        Object nilai = tabMode.getValueAt(baris, KOLOM_HARI_AWAL + hari);
        if (nilai == null) {
            return "";
        }
        return nilai.toString().trim().toUpperCase();
    }

    private static class WarnaMingguSMC extends WarnaTable {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (!isSelected) {
                component.setForeground(new Color(200, 0, 0));
            }
            component.setBackground(1 == row % 2 ? new Color(255, 228, 228) : new Color(255, 240, 240));
            return component;
        }
    }

    private static class JudulMingguSMC implements TableCellRenderer {
        private final TableCellRenderer asli;

        JudulMingguSMC(TableCellRenderer asli) {
            this.asli = asli;
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component component = asli.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            component.setForeground(new Color(200, 0, 0));
            return component;
        }
    }

    private void pindahFokusSmc(int langkah) {
        if (KOLOM_PIN == tbJadwal.getSelectedColumn()) {
            pindahBarisSmc(langkah);
        } else {
            pindahHari(langkah);
        }
    }

    private void pindahBarisSmc(int langkah) {
        if (0 == tbJadwal.getRowCount()) {
            return;
        }

        if (tbJadwal.isEditing()) {
            TableCellEditor editor = tbJadwal.getCellEditor();
            if (null != editor && !editor.stopCellEditing()) {
                return;
            }
        }

        int baris = tbJadwal.getSelectedRow();
        if (0 > baris) {
            return;
        }

        int kolom = tbJadwal.getSelectedColumn();
        baris = baris + langkah;
        if (0 > baris) {
            baris = 0;
        } else if (tbJadwal.getRowCount() <= baris) {
            baris = tbJadwal.getRowCount() - 1;
        }

        tbJadwal.changeSelection(baris, kolom, false, false);
    }

    private void pindahHari(int langkah) {
        if (0 == tbJadwal.getRowCount()) {
            return;
        }

        if (tbJadwal.isEditing()) {
            TableCellEditor editor = tbJadwal.getCellEditor();
            if (null != editor && !editor.stopCellEditing()) {
                return;
            }
        }

        int baris = tbJadwal.getSelectedRow();
        if (0 > baris) {
            return;
        }

        int awal = KOLOM_HARI_AWAL;
        int akhir = KOLOM_HARI_AWAL + ym.lengthOfMonth() - 1;
        int kolom = tbJadwal.getSelectedColumn();
        if (awal > kolom) {
            kolom = awal;
        } else if (akhir < kolom) {
            kolom = akhir;
        } else {
            kolom = kolom + langkah;
        }

        if (akhir < kolom) {
            kolom = awal;
            baris = baris + 1 < tbJadwal.getRowCount() ? baris + 1 : baris;
        } else if (awal > kolom) {
            kolom = akhir;
            baris = 0 < baris ? baris - 1 : baris;
        }

        tbJadwal.changeSelection(baris, kolom, false, false);
    }

    private void salinJadwal() {
        int[] terpilih = tbJadwal.getSelectedRows();
        if (0 == terpilih.length) {
            JOptionPane.showMessageDialog(null, "Silahkan pilih dulu pegawai yang jadwalnya mau disalin...!!!!");
            return;
        }

        StringBuilder isi = new StringBuilder();
        for (int i = 0; i < terpilih.length; i++) {
            if (0 < i) {
                isi.append("\n");
            }

            int baris = tbJadwal.convertRowIndexToModel(terpilih[i]);
            for (int hari = 0; hari < ym.lengthOfMonth(); hari++) {
                if (0 < hari) {
                    isi.append("\t");
                }
                isi.append(getKode(baris, hari));
            }
        }

        try {
            Clipboard papan = Toolkit.getDefaultToolkit().getSystemClipboard();
            papan.setContents(new StringSelection(isi.toString()), null);
            bulanSalin = ym;
            isiSalin = isi.toString();
        } catch (Exception e) {
            System.out.println("Notif : " + e);
            JOptionPane.showMessageDialog(null, "Gagal menyalin jadwal ke clipboard...!!!!");
        }
    }

    private void tempelJadwal() {
        if (!akses.getjadwal_pegawai_smc()) {
            JOptionPane.showMessageDialog(null, "Maaf, anda tidak punya akses untuk mengubah jadwal pegawai...!!!!");
            return;
        }

        int[] terpilih = tbJadwal.getSelectedRows();
        if (0 == terpilih.length) {
            JOptionPane.showMessageDialog(null, "Silahkan pilih dulu pegawai tujuan...!!!!");
            return;
        }

        String data;
        try {
            Clipboard papan = Toolkit.getDefaultToolkit().getSystemClipboard();
            if (!papan.isDataFlavorAvailable(DataFlavor.stringFlavor)) {
                return;
            }
            data = papan.getData(DataFlavor.stringFlavor).toString();
        } catch (Exception e) {
            System.out.println("Notif : " + e);
            return;
        }

        if (null == bulanSalin || !data.equals(isiSalin)) {
            return;
        }
        if (!bulanSalin.equals(ym)) {
            JOptionPane.showMessageDialog(null, "Jadwal ini disalin dari periode " + bulanSalin + ", sedangkan hari dalam sepekan tidak sama dengan periode " + ym +
                ". Silahkan salin ulang dari periode " + ym + "...!!!!");
            return;
        }

        List<String[]> jadwal = new LinkedList<>();
        for (String baris : data.replace("\r\n", "\n").replace("\r", "\n").split("\n", -1)) {
            if (!baris.isEmpty()) {
                jadwal.add(baris.split("\t", -1));
            }
        }
        if (jadwal.isEmpty()) {
            return;
        }

        Set<String> kodeSah = getKodeShift();
        Set<String> kodeAsing = new LinkedHashSet<>();
        for (String[] baris : jadwal) {
            for (String kode : baris) {
                String bersih = kode.trim().toUpperCase();
                if (!bersih.isEmpty() && !kodeSah.contains(bersih)) {
                    kodeAsing.add(bersih);
                }
            }
        }
        if (!kodeAsing.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Kode shift berikut belum terdaftar di jadwal dinas : " + String.join(", ", kodeAsing));
            return;
        }

        int ditempel = 0;
        for (int i = 0; i < terpilih.length; i++) {
            if (1 < jadwal.size() && i >= jadwal.size()) {
                break;
            }

            String[] sumber = 1 == jadwal.size() ? jadwal.get(0) : jadwal.get(i);
            int baris = tbJadwal.convertRowIndexToModel(terpilih[i]);
            for (int hari = 0; hari < ym.lengthOfMonth(); hari++) {
                tabMode.setValueAt(hari < sumber.length ? sumber[hari].trim().toUpperCase() : "", baris, KOLOM_HARI_AWAL + hari);
            }
            ditempel++;
        }

        if (ditempel != terpilih.length) {
            JOptionPane.showMessageDialog(null, "Jadwal di clipboard hanya " + jadwal.size() + " baris, ditempel ke " + ditempel + " dari " + terpilih.length + " pegawai yang dipilih.");
        }
    }

    private String getPin(int baris) {
        Object nilai = tabMode.getValueAt(baris, KOLOM_PIN);
        if (nilai == null) {
            return "";
        }
        return nilai.toString().trim();
    }

    private Map<String, String> getPinPerPegawai(Map<String, String> pemilikPin) {
        Map<String, String> pin = new LinkedHashMap<>();
        for (Map.Entry<String, String> baris : pemilikPin.entrySet()) {
            pin.put(baris.getValue(), baris.getKey());
        }
        return pin;
    }

    private boolean simpanPin(String id, String pinBaru, String pinLama) {
        if (pinBaru.equals(pinLama)) {
            return false;
        }

        Sequel.menghapustfSmc("mapping_pin_pegawai_smc", "id=?", id);
        if (!pinBaru.isEmpty()) {
            Sequel.executeRawSmc("insert into mapping_pin_pegawai_smc (pin, id) values (?, ?) on duplicate key update id=values(id)", pinBaru, id);
        }
        return true;
    }

    private Set<String> getKodeShift() {
        Set<String> kode = new LinkedHashSet<>();
        try (PreparedStatement ps = koneksi.prepareStatement("select kode_shift from jam_masuk_smc")) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    kode.add(rs.getString("kode_shift"));
                }
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
        }
        return kode;
    }

    private Map<String, String> getNamaShift() {
        Map<String, String> shift = new LinkedHashMap<>();
        try (PreparedStatement ps = koneksi.prepareStatement("select kode_shift, nama_shift from jam_masuk_smc order by nama_shift")) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    shift.put(rs.getString("kode_shift"), rs.getString("nama_shift"));
                }
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
        }
        return shift;
    }

    private boolean isTambahan() {
        return JENIS_TAMBAHAN.equals(cmbJenis.getSelectedItem().toString());
    }

    private String tabelSmc() {
        return isTambahan() ? "jadwal_tambahan_smc" : "jadwal_pegawai_smc";
    }

    private String tabelLegacy() {
        return isTambahan() ? "jadwal_tambahan" : "jadwal_pegawai";
    }

    private String namaBerkas() {
        return isTambahan() ? "JadwalTambahan" : "JadwalDinas";
    }

    private String judulJenis() {
        return cmbJenis.getSelectedItem().toString() + " Pegawai";
    }

    private Map<String, String> getShiftLegacy() {
        Map<String, String> legacy = new LinkedHashMap<>();
        try (PreparedStatement ps = koneksi.prepareStatement("select kode_shift, shift from set_kode_shift_smc")) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    legacy.put(rs.getString("kode_shift"), rs.getString("shift"));
                }
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
        }
        return legacy;
    }

    private Map<String, String> getPegawaiPerPin() {
        Map<String, String> pegawai = new LinkedHashMap<>();
        try (PreparedStatement ps = koneksi.prepareStatement("select pin, id from mapping_pin_pegawai_smc")) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    pegawai.put(rs.getString("pin"), rs.getString("id"));
                }
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
        }
        return pegawai;
    }

    private boolean simpanJadwal(String id, int baris, Map<String, String> legacy) {
        String[] shift = new String[31];
        for (int hari = 0; hari < ym.lengthOfMonth(); hari++) {
            String kode = getKode(baris, hari);
            if (!kode.isEmpty()) {
                shift[hari] = kode;
            }
        }
        return simpanJadwal(id, ym, shift, legacy);
    }

    private boolean simpanJadwal(String id, YearMonth periode, String[] shift, Map<String, String> legacy) {
        Sequel.menghapustfSmc(tabelSmc(), "id=? and tanggal between ? and ?", id,
            periode.atDay(1).toString(), periode.atEndOfMonth().toString());

        boolean sukses = true;
        String[] mirror = new String[31];
        for (int hari = 0; hari < 31; hari++) {
            mirror[hari] = "";
            if (hari >= periode.lengthOfMonth() || null == shift[hari] || shift[hari].isEmpty()) {
                continue;
            }

            if (!Sequel.menyimpantfSmc(tabelSmc(), "id, tanggal, kode_shift", id,
                periode.atDay(hari + 1).toString(), shift[hari])) {
                sukses = false;
            }
            mirror[hari] = legacy.getOrDefault(shift[hari], SHIFT_LEGACY_DEFAULT);
        }

        return simpanJadwalLegacy(id, periode, mirror) && sukses;
    }

    private boolean simpanJadwalLegacy(String id, YearMonth periode, String[] mirror) {
        StringBuilder kolom = new StringBuilder("id, tahun, bulan");
        for (int hari = 1; hari <= 31; hari++) {
            kolom.append(", h").append(hari);
        }

        String tahun = String.valueOf(periode.getYear());
        String bulan = String.format("%02d", periode.getMonthValue());

        String[] nilai = new String[34];
        nilai[0] = id;
        nilai[1] = tahun;
        nilai[2] = bulan;
        System.arraycopy(mirror, 0, nilai, 3, 31);

        Sequel.menghapustfSmc(tabelLegacy(), "id=? and tahun=? and bulan=?", id, tahun, bulan);
        return Sequel.menyimpantfSmc(tabelLegacy(), kolom.toString(), nilai);
    }

    private String bacaTeks(Row baris, int kolom) {
        if (null == baris) {
            return "";
        }

        Cell sel = baris.getCell(kolom);
        if (null == sel) {
            return "";
        }
        if (CellType.NUMERIC.equals(sel.getCellType())) {
            return String.valueOf((long) sel.getNumericCellValue());
        }
        if (CellType.STRING.equals(sel.getCellType())) {
            return sel.getStringCellValue().trim();
        }
        return "";
    }

    private void tampilSmc() {
        if (!ceksukses) {
            ceksukses = true;
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            Valid.tabelKosongSmc(tabMode);

            ym = YearMonth.parse(ThnCari.getSelectedItem().toString() + "-" + BlnCari.getSelectedItem().toString());
            final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd (EEEE)", new Locale("id", "ID"));
            final LinkedList<LocalDate> days = IntStream.rangeClosed(1, ym.lengthOfMonth())
                .collect(LinkedList::new, (list, day) -> list.add(ym.atDay(day)), LinkedList::addAll);

            int i = KOLOM_HARI_AWAL;
            for (LocalDate day : days) {
                TableColumn column = tbJadwal.getColumnModel().getColumn(i++);
                column.setHeaderValue(dtf.format(day).toUpperCase());
                column.setMinWidth(0);
                column.setMaxWidth(Integer.MAX_VALUE);
                column.setPreferredWidth(75);
                if (DayOfWeek.SUNDAY.equals(day.getDayOfWeek())) {
                    column.setCellRenderer(new WarnaMingguSMC());
                    column.setHeaderRenderer(new JudulMingguSMC(tbJadwal.getTableHeader().getDefaultRenderer()));
                } else {
                    column.setCellRenderer(null);
                    column.setHeaderRenderer(null);
                }
            }

            for (; i < tabMode.getColumnCount(); i++) {
                TableColumn column = tbJadwal.getColumnModel().getColumn(i);
                column.setCellRenderer(null);
                column.setHeaderRenderer(null);
                column.setHeaderValue("");
                column.setMinWidth(0);
                column.setMaxWidth(0);
                column.setPreferredWidth(0);
            }

            tbJadwal.getTableHeader().repaint();
            tbJadwal.repaint();

            new SwingWorker<Void, Object[]>() {
                final String departemen = Departemen.getSelectedItem().toString();
                final String cari = TCari.getText().trim();
                final YearMonth periode = ym;

                @Override
                protected Void doInBackground() throws Exception {
                    Map<String, String[]> jadwal = new LinkedHashMap<>();
                    try (PreparedStatement ps = koneksi.prepareStatement(
                        "select id, dayofmonth(tanggal) as hari, kode_shift from " + tabelSmc() + " where tanggal between ? and ?"
                    )) {
                        ps.setString(1, periode.atDay(1).toString());
                        ps.setString(2, periode.atEndOfMonth().toString());
                        try (ResultSet rs = ps.executeQuery()) {
                            while (rs.next()) {
                                jadwal.computeIfAbsent(rs.getString("id"), key -> new String[31])[rs.getInt("hari") - 1] = rs.getString("kode_shift");
                            }
                        }
                    }

                    try (PreparedStatement ps = koneksi.prepareStatement(
                        "select pegawai.id, pegawai.nik, ifnull(mapping_pin_pegawai_smc.pin, '') as pin, pegawai.nama, pegawai.pendidikan, departemen.nama as departemen " +
                        "from pegawai inner join departemen on pegawai.departemen = departemen.dep_id left join mapping_pin_pegawai_smc on " +
                        "pegawai.id = mapping_pin_pegawai_smc.id where pegawai.stts_aktif != 'KELUAR' " +
                        (departemen.equals("Semua") ? "" : "and departemen.nama = ? ") +
                        (cari.isBlank() ? "" : "and (pegawai.nik like ? or pegawai.nama like ? or mapping_pin_pegawai_smc.pin like ? or departemen.nama like ?) ") +
                        "order by pegawai.nama"
                    )) {
                        int p = 0;
                        if (!departemen.equals("Semua")) {
                            ps.setString(++p, departemen);
                        }
                        if (!cari.isBlank()) {
                            ps.setString(++p, "%" + cari + "%");
                            ps.setString(++p, "%" + cari + "%");
                            ps.setString(++p, "%" + cari + "%");
                            ps.setString(++p, "%" + cari + "%");
                        }
                        try (ResultSet rs = ps.executeQuery()) {
                            int nomor = 1;
                            while (rs.next()) {
                                String[] shift = jadwal.getOrDefault(rs.getString("id"), new String[31]);
                                Object[] baris = new Object[tabMode.getColumnCount()];
                                baris[0] = String.valueOf(nomor++);
                                baris[KOLOM_ID] = rs.getString("id");
                                baris[2] = rs.getString("nik");
                                baris[KOLOM_PIN] = rs.getString("pin");
                                baris[4] = rs.getString("nama");
                                baris[5] = rs.getString("pendidikan");
                                baris[6] = rs.getString("departemen");
                                for (int hari = 0; hari < 31; hari++) {
                                    baris[KOLOM_HARI_AWAL + hari] = null == shift[hari] ? "" : shift[hari];
                                }
                                publish(baris);
                            }
                        }
                    }
                    return null;
                }

                @Override
                protected void process(List<Object[]> chunks) {
                    chunks.forEach(tabMode::addRow);
                }

                @Override
                protected void done() {
                    try {
                        get();
                    } catch (Exception e) {
                        System.out.println("Notif : " + e);
                    }
                    tabMode.fireTableDataChanged();
                    LCount.setText(tabMode.getRowCount() + "");
                    DlgJadwalPegawaiSMC.this.setCursor(Cursor.getDefaultCursor());
                    ceksukses = false;
                }
            }.execute();
        }
    }
}
