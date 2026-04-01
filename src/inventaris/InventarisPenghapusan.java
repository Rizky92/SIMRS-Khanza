package inventaris;

import fungsi.WarnaTable;
import fungsi.batasInput;
import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.event.DocumentEvent;
import javax.swing.table.DefaultTableModel;
import kepegawaian.DlgCariPetugas;
import keuangan.Jurnal;

public class InventarisPenghapusan extends javax.swing.JDialog {
    private final DefaultTableModel tabMode;
    private sekuel Sequel = new sekuel();
    private validasi Valid = new validasi();
    private Jurnal jur = new Jurnal();
    private Connection koneksi = koneksiDB.condb();
    private PreparedStatement ps;
    private ResultSet rs;
    private volatile boolean ceksukses = false;
    private double harga = 0, akm = 0, nilai = 0;

    public InventarisPenghapusan(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.setLocation(8, 1);
        setSize(990, 680);

        tabMode = new DefaultTableModel(null, new Object[] {
            "No.Penghapusan", "No.Inventaris", "Nama Barang", "Tanggal", "Alasan", "Nilai Jual", "Nilai Buku", "Akm.Penyusutan", "Untung/Rugi", "Keterangan"
        }) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }

            @Override
            public Class getColumnClass(int columnIndex) {
                if (columnIndex == 5 || columnIndex == 6 || columnIndex == 7 || columnIndex == 8) {
                    return Double.class;
                }

                return String.class;
            }
        };
        tbHapus.setModel(tabMode);
        tbHapus.setPreferredScrollableViewportSize(new Dimension(900, 300));
        tbHapus.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tbHapus.setDefaultRenderer(Object.class, new WarnaTable());
        int[] widths = {120, 110, 200, 100, 100, 110, 110, 110, 100, 200};
        for (int i = 0; i < widths.length; i++) {
            tbHapus.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        noPembebasan.setDocument(new batasInput((byte) 20).getKata(noPembebasan));
        noInventaris.setDocument(new batasInput((byte) 30).getKata(noInventaris));
        nilaiJual.setDocument(new batasInput((byte) 20).getOnlyAngka(nilaiJual));
        nip.setDocument(new batasInput((byte) 20).getKata(nip));
        keterangan.setDocument(new batasInput((byte) 200).getKata(keterangan));
        TCari.setDocument(new batasInput((byte) 100).getKata(TCari));

        noPembebasan.setEditable(false);
        namaInventaris.setText("-");
        hargaPerolehan.setText("0");
        akumulasiPenyusutan.setText("0");
        nilaiBuku.setText("0");
        untungRugi.setText("0");
        namaPetugas.setEditable(false);

        nip.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                isCekPetugas();
            }

            public void removeUpdate(DocumentEvent e) {
                isCekPetugas();
            }

            public void changedUpdate(DocumentEvent e) {
                isCekPetugas();
            }
        });

        noInventaris.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent evt) {
                if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
                    isiBarang();
                }
            }
        });

        nilaiJual.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                hitungUntungRugi();
            }

            public void removeUpdate(DocumentEvent e) {
                hitungUntungRugi();
            }

            public void changedUpdate(DocumentEvent e) {
                hitungUntungRugi();
            }
        });
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        internalFrame1 = new widget.InternalFrame();
        PanelInput = new javax.swing.JPanel();
        FormInput = new widget.PanelBiasa();
        label1 = new widget.Label();
        noPembebasan = new widget.TextBox();
        label2 = new widget.Label();
        tglPembebasan = new widget.Tanggal();
        label3 = new widget.Label();
        noInventaris = new widget.TextBox();
        label4 = new widget.Label();
        label5 = new widget.Label();
        label6 = new widget.Label();
        label7 = new widget.Label();
        alasan = new widget.ComboBox();
        label8 = new widget.Label();
        nilaiJual = new widget.TextBox();
        label9 = new widget.Label();
        label10 = new widget.Label();
        akunKas = new widget.ComboBox();
        label11 = new widget.Label();
        nip = new widget.TextBox();
        pilihPetugas = new widget.Button();
        namaPetugas = new widget.TextBox();
        label12 = new widget.Label();
        keterangan = new widget.TextBox();
        pilihInventaris = new widget.Button();
        merkInventaris = new widget.TextBox();
        namaInventaris = new widget.TextBox();
        hargaPerolehan = new widget.TextBox();
        akumulasiPenyusutan = new widget.TextBox();
        nilaiBuku = new widget.TextBox();
        untungRugi = new widget.TextBox();
        ChkInput = new widget.CekBox();
        Scroll = new widget.ScrollPane();
        tbHapus = new widget.Table();
        jPanel3 = new javax.swing.JPanel();
        panelisi1 = new widget.panelisi();
        BtnSimpan = new widget.Button();
        BtnBatal = new widget.Button();
        BtnHapus = new widget.Button();
        BtnPrint = new widget.Button();
        BtnAll = new widget.Button();
        BtnKeluar = new widget.Button();
        panelisi2 = new widget.panelisi();
        label13 = new widget.Label();
        TCari = new widget.TextBox();
        BtnCari = new widget.Button();
        label14 = new widget.Label();
        LCount = new widget.Label();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)), "::[ Pembebasan Inventaris ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50))); // NOI18N
        internalFrame1.setName("internalFrame1"); // NOI18N
        internalFrame1.setLayout(new java.awt.BorderLayout(1, 1));

        PanelInput.setName("PanelInput"); // NOI18N
        PanelInput.setOpaque(false);
        PanelInput.setPreferredSize(new java.awt.Dimension(960, 188));
        PanelInput.setLayout(new java.awt.BorderLayout(1, 1));

        FormInput.setName("FormInput"); // NOI18N
        FormInput.setPreferredSize(new java.awt.Dimension(960, 165));
        FormInput.setLayout(null);

        label1.setText("No. Pembebasan :");
        label1.setName("label1"); // NOI18N
        label1.setPreferredSize(new java.awt.Dimension(100, 23));
        FormInput.add(label1);
        label1.setBounds(0, 10, 100, 23);

        noPembebasan.setName("noPembebasan"); // NOI18N
        noPembebasan.setPreferredSize(new java.awt.Dimension(110, 23));
        FormInput.add(noPembebasan);
        noPembebasan.setBounds(103, 10, 110, 23);

        label2.setText("Tanggal :");
        label2.setName("label2"); // NOI18N
        label2.setPreferredSize(new java.awt.Dimension(65, 23));
        FormInput.add(label2);
        label2.setBounds(216, 10, 65, 23);

        tglPembebasan.setDisplayFormat("dd-MM-yyyy");
        tglPembebasan.setName("tglPembebasan"); // NOI18N
        tglPembebasan.setPreferredSize(new java.awt.Dimension(90, 23));
        tglPembebasan.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                tglPembebasanItemStateChanged(evt);
            }
        });
        FormInput.add(tglPembebasan);
        tglPembebasan.setBounds(284, 10, 90, 23);

        label3.setText("No. Inventaris :");
        label3.setName("label3"); // NOI18N
        label3.setPreferredSize(new java.awt.Dimension(100, 23));
        FormInput.add(label3);
        label3.setBounds(0, 40, 100, 23);

        noInventaris.setName("noInventaris"); // NOI18N
        noInventaris.setPreferredSize(new java.awt.Dimension(110, 23));
        FormInput.add(noInventaris);
        noInventaris.setBounds(103, 40, 110, 23);

        label4.setText("Harga Perolehan :");
        label4.setName("label4"); // NOI18N
        label4.setPreferredSize(new java.awt.Dimension(100, 23));
        FormInput.add(label4);
        label4.setBounds(0, 70, 100, 23);

        label5.setText("Akm. Penyusutan :");
        label5.setName("label5"); // NOI18N
        label5.setPreferredSize(new java.awt.Dimension(100, 23));
        FormInput.add(label5);
        label5.setBounds(256, 70, 100, 23);

        label6.setText("Nilai Buku :");
        label6.setName("label6"); // NOI18N
        label6.setPreferredSize(new java.awt.Dimension(55, 23));
        FormInput.add(label6);
        label6.setBounds(517, 70, 55, 23);

        label7.setText("Alasan :");
        label7.setName("label7"); // NOI18N
        label7.setPreferredSize(new java.awt.Dimension(100, 23));
        FormInput.add(label7);
        label7.setBounds(0, 130, 100, 23);

        alasan.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Rusak Berat", "Hilang", "Dijual", "Hibah Keluar", "Lainnya" }));
        alasan.setName("alasan"); // NOI18N
        alasan.setPreferredSize(new java.awt.Dimension(120, 23));
        FormInput.add(alasan);
        alasan.setBounds(103, 130, 120, 23);

        label8.setText("Nilai Jual :");
        label8.setName("label8"); // NOI18N
        label8.setPreferredSize(new java.awt.Dimension(64, 23));
        FormInput.add(label8);
        label8.setBounds(276, 100, 64, 23);

        nilaiJual.setName("nilaiJual"); // NOI18N
        nilaiJual.setPreferredSize(new java.awt.Dimension(149, 23));
        FormInput.add(nilaiJual);
        nilaiJual.setBounds(343, 100, 149, 23);

        label9.setText("Untung/Rugi :");
        label9.setName("label9"); // NOI18N
        FormInput.add(label9);
        label9.setBounds(495, 100, 77, 23);

        label10.setText("Akun Pembebasan:");
        label10.setName("label10"); // NOI18N
        label10.setPreferredSize(new java.awt.Dimension(100, 23));
        FormInput.add(label10);
        label10.setBounds(0, 100, 100, 23);

        akunKas.setName("akunKas"); // NOI18N
        akunKas.setPreferredSize(new java.awt.Dimension(170, 23));
        FormInput.add(akunKas);
        akunKas.setBounds(103, 100, 170, 23);

        label11.setText("NIP :");
        label11.setName("label11"); // NOI18N
        label11.setPreferredSize(new java.awt.Dimension(40, 23));
        FormInput.add(label11);
        label11.setBounds(377, 10, 40, 23);

        nip.setEditable(false);
        nip.setName("nip"); // NOI18N
        nip.setPreferredSize(new java.awt.Dimension(72, 23));
        FormInput.add(nip);
        nip.setBounds(420, 10, 72, 23);

        pilihPetugas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        pilihPetugas.setName("pilihPetugas"); // NOI18N
        pilihPetugas.setPreferredSize(new java.awt.Dimension(28, 23));
        pilihPetugas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pilihPetugasActionPerformed(evt);
            }
        });
        FormInput.add(pilihPetugas);
        pilihPetugas.setBounds(718, 10, 28, 23);

        namaPetugas.setEditable(false);
        namaPetugas.setName("namaPetugas"); // NOI18N
        namaPetugas.setPreferredSize(new java.awt.Dimension(220, 23));
        FormInput.add(namaPetugas);
        namaPetugas.setBounds(495, 10, 220, 23);

        label12.setText("Keterangan :");
        label12.setName("label12"); // NOI18N
        label12.setPreferredSize(new java.awt.Dimension(85, 23));
        FormInput.add(label12);
        label12.setBounds(226, 130, 85, 23);

        keterangan.setName("keterangan"); // NOI18N
        keterangan.setPreferredSize(new java.awt.Dimension(432, 23));
        FormInput.add(keterangan);
        keterangan.setBounds(314, 130, 432, 23);

        pilihInventaris.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        pilihInventaris.setName("pilihInventaris"); // NOI18N
        pilihInventaris.setPreferredSize(new java.awt.Dimension(28, 23));
        pilihInventaris.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pilihInventarisActionPerformed(evt);
            }
        });
        FormInput.add(pilihInventaris);
        pilihInventaris.setBounds(718, 40, 28, 23);

        merkInventaris.setEditable(false);
        merkInventaris.setName("merkInventaris"); // NOI18N
        merkInventaris.setPreferredSize(new java.awt.Dimension(130, 23));
        FormInput.add(merkInventaris);
        merkInventaris.setBounds(216, 40, 130, 23);

        namaInventaris.setEditable(false);
        namaInventaris.setName("namaInventaris"); // NOI18N
        namaInventaris.setPreferredSize(new java.awt.Dimension(366, 23));
        FormInput.add(namaInventaris);
        namaInventaris.setBounds(349, 40, 366, 23);

        hargaPerolehan.setEditable(false);
        hargaPerolehan.setName("hargaPerolehan"); // NOI18N
        hargaPerolehan.setPreferredSize(new java.awt.Dimension(150, 23));
        FormInput.add(hargaPerolehan);
        hargaPerolehan.setBounds(103, 70, 150, 23);

        akumulasiPenyusutan.setEditable(false);
        akumulasiPenyusutan.setName("akumulasiPenyusutan"); // NOI18N
        akumulasiPenyusutan.setPreferredSize(new java.awt.Dimension(150, 23));
        FormInput.add(akumulasiPenyusutan);
        akumulasiPenyusutan.setBounds(359, 70, 150, 23);

        nilaiBuku.setEditable(false);
        nilaiBuku.setName("nilaiBuku"); // NOI18N
        nilaiBuku.setPreferredSize(new java.awt.Dimension(171, 23));
        FormInput.add(nilaiBuku);
        nilaiBuku.setBounds(575, 70, 171, 23);

        untungRugi.setEditable(false);
        untungRugi.setName("untungRugi"); // NOI18N
        untungRugi.setPreferredSize(new java.awt.Dimension(171, 23));
        FormInput.add(untungRugi);
        untungRugi.setBounds(575, 100, 171, 23);

        PanelInput.add(FormInput, java.awt.BorderLayout.CENTER);

        ChkInput.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/143.png"))); // NOI18N
        ChkInput.setText(".: Input Data");
        ChkInput.setName("ChkInput"); // NOI18N
        ChkInput.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/145.png"))); // NOI18N
        ChkInput.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                ChkInputItemStateChanged(evt);
            }
        });
        PanelInput.add(ChkInput, java.awt.BorderLayout.PAGE_END);

        internalFrame1.add(PanelInput, java.awt.BorderLayout.PAGE_START);

        Scroll.setName("Scroll"); // NOI18N

        tbHapus.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tbHapus.setName("tbHapus"); // NOI18N
        Scroll.setViewportView(tbHapus);

        internalFrame1.add(Scroll, java.awt.BorderLayout.CENTER);

        jPanel3.setName("jPanel3"); // NOI18N
        jPanel3.setOpaque(false);
        jPanel3.setPreferredSize(new java.awt.Dimension(960, 100));
        jPanel3.setLayout(new java.awt.BorderLayout(1, 1));

        panelisi1.setName("panelisi1"); // NOI18N
        panelisi1.setPreferredSize(new java.awt.Dimension(960, 50));
        panelisi1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 9));

        BtnSimpan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/save-16x16.png"))); // NOI18N
        BtnSimpan.setText("Simpan");
        BtnSimpan.setName("BtnSimpan"); // NOI18N
        BtnSimpan.setPreferredSize(new java.awt.Dimension(90, 30));
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
        panelisi1.add(BtnSimpan);

        BtnBatal.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Cancel-2-16x16.png"))); // NOI18N
        BtnBatal.setText("Baru");
        BtnBatal.setName("BtnBatal"); // NOI18N
        BtnBatal.setPreferredSize(new java.awt.Dimension(110, 30));
        BtnBatal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnBatalActionPerformed(evt);
            }
        });
        BtnBatal.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnBatalKeyPressed(evt);
            }
        });
        panelisi1.add(BtnBatal);

        BtnHapus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/stop_f2.png"))); // NOI18N
        BtnHapus.setMnemonic('H');
        BtnHapus.setText("Hapus");
        BtnHapus.setToolTipText("Alt+H");
        BtnHapus.setName("BtnHapus"); // NOI18N
        BtnHapus.setPreferredSize(new java.awt.Dimension(100, 30));
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
        panelisi1.add(BtnHapus);

        BtnPrint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/b_print.png"))); // NOI18N
        BtnPrint.setMnemonic('T');
        BtnPrint.setText("Cetak");
        BtnPrint.setToolTipText("Alt+T");
        BtnPrint.setName("BtnPrint"); // NOI18N
        BtnPrint.setPreferredSize(new java.awt.Dimension(100, 30));
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
        panelisi1.add(BtnPrint);

        BtnAll.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Search-16x16.png"))); // NOI18N
        BtnAll.setMnemonic('M');
        BtnAll.setText("Semua");
        BtnAll.setToolTipText("Alt+M");
        BtnAll.setName("BtnAll"); // NOI18N
        BtnAll.setPreferredSize(new java.awt.Dimension(100, 30));
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
        panelisi1.add(BtnAll);

        BtnKeluar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/exit.png"))); // NOI18N
        BtnKeluar.setText("Keluar");
        BtnKeluar.setName("BtnKeluar"); // NOI18N
        BtnKeluar.setPreferredSize(new java.awt.Dimension(95, 30));
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
        panelisi1.add(BtnKeluar);

        jPanel3.add(panelisi1, java.awt.BorderLayout.CENTER);

        panelisi2.setName("panelisi2"); // NOI18N
        panelisi2.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 9));

        label13.setText("Key Word :");
        label13.setName("label13"); // NOI18N
        label13.setPreferredSize(new java.awt.Dimension(70, 23));
        panelisi2.add(label13);

        TCari.setName("TCari"); // NOI18N
        TCari.setPreferredSize(new java.awt.Dimension(450, 23));
        TCari.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TCariKeyPressed(evt);
            }
        });
        panelisi2.add(TCari);

        BtnCari.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png"))); // NOI18N
        BtnCari.setName("BtnCari"); // NOI18N
        BtnCari.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnCari.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnCariActionPerformed(evt);
            }
        });
        panelisi2.add(BtnCari);

        label14.setText("Record :");
        label14.setName("label14"); // NOI18N
        label14.setPreferredSize(new java.awt.Dimension(70, 23));
        panelisi2.add(label14);

        LCount.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        LCount.setText("0");
        LCount.setName("LCount"); // NOI18N
        LCount.setPreferredSize(new java.awt.Dimension(90, 23));
        panelisi2.add(LCount);

        jPanel3.add(panelisi2, java.awt.BorderLayout.PAGE_START);

        internalFrame1.add(jPanel3, java.awt.BorderLayout.PAGE_END);

        getContentPane().add(internalFrame1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        autoNomor();
        tampil();
    }//GEN-LAST:event_formWindowOpened

    private void BtnSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnSimpanActionPerformed
        if (noPembebasan.getText().trim().isEmpty()) {
            Valid.textKosong(noPembebasan, "No.Penghapusan");
            return;
        }
        if (noInventaris.getText().trim().isEmpty()) {
            Valid.textKosong(noInventaris, "No.Inventaris");
            return;
        }
        if (namaInventaris.getText().equals("-") || namaInventaris.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Pilih No. Inventaris yang valid terlebih dahulu.");
            return;
        }
        if (alasan.getSelectedItem() == null || alasan.getSelectedItem().toString().trim().isEmpty()) {
            Valid.textKosong(alasan, "Alasan");
            return;
        }
        if (nip.getText().trim().isEmpty()) {
            Valid.textKosong(nip, "NIP");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(rootPane,
            "Penghapusan inventaris ini akan bersifat permanen dan tidak dapat dibatalkan.\nLanjutkan?",
            "Konfirmasi Penghapusan", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        // Resolve account codes
        String noInvVal = noInventaris.getText().trim();
        String kdAkmPenyusutan = Sequel.cariIsiSmc(
            "SELECT aa.kd_rek_akm_penyusutan FROM akun_aset_inventaris aa " +
            "INNER JOIN inventaris_barang ib ON ib.id_jenis = aa.id_jenis " +
            "INNER JOIN inventaris i ON i.kode_barang = ib.kode_barang " +
            "WHERE i.no_inventaris = ? LIMIT 1", noInvVal);
        String kdAkunAset = Sequel.cariIsiSmc(
            "SELECT aa.kd_rek FROM akun_aset_inventaris aa " +
            "INNER JOIN inventaris_barang ib ON ib.id_jenis = aa.id_jenis " +
            "INNER JOIN inventaris i ON i.kode_barang = ib.kode_barang " +
            "WHERE i.no_inventaris = ? LIMIT 1", noInvVal);
        String kdKeuntungan = Sequel.cariIsiSmc(
            "SELECT IFNULL(aa.kd_rek_keuntungan, (SELECT sa.Keuntungan_Penghapusan_Aset FROM set_akun sa LIMIT 1)) " +
            "FROM akun_aset_inventaris aa " +
            "INNER JOIN inventaris_barang ib ON ib.id_jenis = aa.id_jenis " +
            "INNER JOIN inventaris i ON i.kode_barang = ib.kode_barang " +
            "WHERE i.no_inventaris = ? LIMIT 1", noInvVal);
        String kdKerugian = Sequel.cariIsiSmc(
            "SELECT IFNULL(aa.kd_rek_kerugian, (SELECT sa.Kerugian_Penghapusan_Aset FROM set_akun sa LIMIT 1)) " +
            "FROM akun_aset_inventaris aa " +
            "INNER JOIN inventaris_barang ib ON ib.id_jenis = aa.id_jenis " +
            "INNER JOIN inventaris i ON i.kode_barang = ib.kode_barang " +
            "WHERE i.no_inventaris = ? LIMIT 1", noInvVal);

        if (kdAkmPenyusutan == null || kdAkmPenyusutan.isEmpty() || kdAkunAset == null || kdAkunAset.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Akun aset/akumulasi penyusutan belum dikonfigurasi untuk jenis barang ini.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (kdKeuntungan == null || kdKeuntungan.isEmpty() || kdKerugian == null || kdKerugian.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Akun keuntungan/kerugian penghapusan aset belum dikonfigurasi.\nSilakan isi di Akun Aset Inventaris atau Set Akun.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        double nilaiJualVal = Valid.SetAngka(nilaiJual.getText());
        double untungRugiVal = nilaiJualVal - nilai;
        String tglVal = Valid.SetTgl(tglPembebasan.getSelectedItem() + "");
        String noPenghapusanVal = noPembebasan.getText().trim();
        String alasanVal = alasan.getSelectedItem().toString();
        String nipVal = nip.getText().trim();
        String ketVal = keterangan.getText().trim();

        String kdKasVal = "";
        if (nilaiJualVal > 0) {
            if (akunKas.getSelectedItem() == null || akunKas.getSelectedItem().toString().trim().isEmpty()) {
                Valid.textKosong(akunKas, "Akun Kas");
                return;
            }
            kdKasVal = Sequel.cariIsiSmc("SELECT kd_rek FROM akun_bayar WHERE nama_bayar = ? LIMIT 1",
                akunKas.getSelectedItem().toString());
            if (kdKasVal == null || kdKasVal.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Akun Kas tidak ditemukan.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        final String kdKasFinal = kdKasVal;
        Sequel.AutoComitFalse();
        boolean sukses = Sequel.menyimpantf2("penghapusan_inventaris",
            "?,?,?,?,?,?,?,?,?,?,?", "No.Penghapusan", 11, new String[] {
                noPenghapusanVal, noInvVal, tglVal, alasanVal,
                "" + nilaiJualVal, "" + akm, "" + nilai, "" + untungRugiVal,
                nipVal, ketVal.isEmpty() ? null : ketVal, null
            });

        if (sukses) {
            sukses = Sequel.executeRawSmc(
                "UPDATE inventaris SET status_barang = 'Dihapus' WHERE no_inventaris = ?", noInvVal);
        }

        if (sukses) {
            Sequel.deleteTampJurnal();
            // Dr. Akumulasi Penyusutan
            if (akm > 0) {
                sukses = Sequel.insertTampJurnal(kdAkmPenyusutan, "AKUMULASI PENYUSUTAN INVENTARIS " + noInvVal, akm, 0);
            }
            // Dr. Kas (if sold)
            if (sukses && nilaiJualVal > 0) {
                sukses = Sequel.insertTampJurnal(kdKasFinal, "PENERIMAAN PENGHAPUSAN INVENTARIS " + noInvVal, nilaiJualVal, 0);
            }
            // Dr. Kerugian (if loss)
            if (sukses && untungRugiVal < 0) {
                sukses = Sequel.insertTampJurnal(kdKerugian, "KERUGIAN PENGHAPUSAN INVENTARIS " + noInvVal, Math.abs(untungRugiVal), 0);
            }
            // Cr. Keuntungan (if gain)
            if (sukses && untungRugiVal > 0) {
                sukses = Sequel.insertTampJurnal(kdKeuntungan, "KEUNTUNGAN PENGHAPUSAN INVENTARIS " + noInvVal, 0, untungRugiVal);
            }
            // Cr. Aset Inventaris
            if (sukses) {
                sukses = Sequel.insertTampJurnal(kdAkunAset, "PENGHAPUSAN ASET INVENTARIS " + noInvVal, 0, harga);
            }

            if (sukses) {
                sukses = jur.simpanJurnal(noPenghapusanVal, "U", "PENGHAPUSAN INVENTARIS " + noInvVal);
            }

            if (sukses) {
                String noJurnal = Sequel.cariIsiSmc("SELECT no_jurnal FROM jurnal WHERE no_ref = ?", noPenghapusanVal);
                if (noJurnal != null && !noJurnal.isEmpty()) {
                    Sequel.executeRawSmc("UPDATE penghapusan_inventaris SET no_jurnal = ? WHERE no_penghapusan = ?",
                        noJurnal, noPenghapusanVal);
                }
            }
        }

        if (sukses) {
            Sequel.Commit();
            JOptionPane.showMessageDialog(null, "Penghapusan inventaris berhasil disimpan.");
            emptTeks();
            tampil();
        } else {
            JOptionPane.showMessageDialog(null, "Terjadi kesalahan saat menyimpan data.", "Error", JOptionPane.ERROR_MESSAGE);
            Sequel.RollBack();
        }
        Sequel.AutoComitTrue();
        autoNomor();
    }//GEN-LAST:event_BtnSimpanActionPerformed

    private void BtnBatalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnBatalActionPerformed
        emptTeks();
        autoNomor();
    }//GEN-LAST:event_BtnBatalActionPerformed

    private void BtnKeluarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnKeluarActionPerformed
        dispose();
    }//GEN-LAST:event_BtnKeluarActionPerformed

    private void BtnCariActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnCariActionPerformed
        tampil();
    }//GEN-LAST:event_BtnCariActionPerformed

    private void TCariKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TCariKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
            tampil();
    }//GEN-LAST:event_TCariKeyPressed

    private void pilihInventarisActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pilihInventarisActionPerformed
        InventarisCariKoleksi inv = new InventarisCariKoleksi(null, true);
        inv.isCek();
        inv.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
        inv.setLocationRelativeTo(internalFrame1);
        inv.setAlwaysOnTop(false);
        inv.setVisible(true);
        if (inv.getTable() != null && inv.getTable().getSelectedRow() >= 0) {
            JTable t = inv.getTable();
            noInventaris.setText(t.getValueAt(t.getSelectedRow(), 0).toString());
            isiBarang();
        }
    }//GEN-LAST:event_pilihInventarisActionPerformed

    private void pilihPetugasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pilihPetugasActionPerformed
        DlgCariPetugas ptg = new DlgCariPetugas(null, true);
        ptg.isCek();
        ptg.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
        ptg.setLocationRelativeTo(internalFrame1);
        ptg.setAlwaysOnTop(false);
        ptg.setVisible(true);
        if (ptg.getTable() != null && ptg.getTable().getSelectedRow() >= 0) {
            JTable t = ptg.getTable();
            nip.setText(t.getValueAt(t.getSelectedRow(), 0).toString());
            namaPetugas.setText(t.getValueAt(t.getSelectedRow(), 1).toString());
        }
    }//GEN-LAST:event_pilihPetugasActionPerformed

    private void tglPembebasanItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_tglPembebasanItemStateChanged
        autoNomor();
    }//GEN-LAST:event_tglPembebasanItemStateChanged

    private void ChkInputItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_ChkInputItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_ChkInputItemStateChanged

    private void BtnSimpanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnSimpanKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_BtnSimpanKeyPressed

    private void BtnBatalKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnBatalKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_BtnBatalKeyPressed

    private void BtnHapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnHapusActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_BtnHapusActionPerformed

    private void BtnHapusKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnHapusKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_BtnHapusKeyPressed

    private void BtnPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnPrintActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_BtnPrintActionPerformed

    private void BtnPrintKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnPrintKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_BtnPrintKeyPressed

    private void BtnAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnAllActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_BtnAllActionPerformed

    private void BtnAllKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnAllKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_BtnAllKeyPressed

    private void BtnKeluarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnKeluarKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_BtnKeluarKeyPressed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private widget.Button BtnAll;
    private widget.Button BtnBatal;
    private widget.Button BtnCari;
    private widget.Button BtnHapus;
    private widget.Button BtnKeluar;
    private widget.Button BtnPrint;
    private widget.Button BtnSimpan;
    private widget.CekBox ChkInput;
    private widget.PanelBiasa FormInput;
    private widget.Label LCount;
    private javax.swing.JPanel PanelInput;
    private widget.ScrollPane Scroll;
    private widget.TextBox TCari;
    private widget.TextBox akumulasiPenyusutan;
    private widget.ComboBox akunKas;
    private widget.ComboBox alasan;
    private widget.TextBox hargaPerolehan;
    private widget.InternalFrame internalFrame1;
    private javax.swing.JPanel jPanel3;
    private widget.TextBox keterangan;
    private widget.Label label1;
    private widget.Label label10;
    private widget.Label label11;
    private widget.Label label12;
    private widget.Label label13;
    private widget.Label label14;
    private widget.Label label2;
    private widget.Label label3;
    private widget.Label label4;
    private widget.Label label5;
    private widget.Label label6;
    private widget.Label label7;
    private widget.Label label8;
    private widget.Label label9;
    private widget.TextBox merkInventaris;
    private widget.TextBox namaInventaris;
    private widget.TextBox namaPetugas;
    private widget.TextBox nilaiBuku;
    private widget.TextBox nilaiJual;
    private widget.TextBox nip;
    private widget.TextBox noInventaris;
    private widget.TextBox noPembebasan;
    private widget.panelisi panelisi1;
    private widget.panelisi panelisi2;
    private widget.Button pilihInventaris;
    private widget.Button pilihPetugas;
    private widget.Table tbHapus;
    private widget.Tanggal tglPembebasan;
    private widget.TextBox untungRugi;
    // End of variables declaration//GEN-END:variables

    private void tampil() {
        if (!ceksukses) {
            ceksukses = true;
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            Valid.tabelKosongSmc(tabMode);
            new SwingWorker<Void, Object[]>() {
                final String cari = TCari.getText().trim();

                @Override
                protected Void doInBackground() throws Exception {
                    try (PreparedStatement ps = koneksi.prepareStatement(
                        "select p.no_penghapusan, p.no_inventaris, ib.nm_barang, p.tanggal, p.alasan, p.nilai_jual, p.nilai_buku, p.akm_penyusutan, p.untung_rugi, " +
                        "p.keterangan from penghapusan_inventaris p inner join inventaris i on i.no_inventaris = p.no_inventaris inner join inventaris_barang ib on " +
                        "ib.kode_barang = i.kode_barang where p.tanggal between ? and ? " + (cari.isBlank() ? "" : "and (p.no_penghapusan like ? or p.no_inventaris " +
                        "like ? or ib.nm_barang like ?) ") + "order by p.tanggal desc, p.no_penghapusan desc"
                    )) {
                        int p = 0;
                        // ps.setString(++p, Valid.getTglSmc(Tgl1));
                        // ps.setString(++p, Valid.getTglSmc(Tgl2));
                        if (!cari.isBlank()) {
                            ps.setString(++p, "%" + cari + "%");
                            ps.setString(++p, "%" + cari + "%");
                            ps.setString(++p, "%" + cari + "%");
                        }
                        try (ResultSet rs = ps.executeQuery()) {
                            while (rs.next()) {
                                publish(new Object[] {
                                    rs.getString("no_penghapusan"), rs.getString("no_inventaris"), rs.getString("nm_barang"), rs.getString("tanggal"),
                                    rs.getString("alasan"), rs.getDouble("nilai_jual"), rs.getDouble("nilai_buku"), rs.getDouble("akm_penyusutan"),
                                    rs.getDouble("untung_rugi"), rs.getString("keterangan")
                                });
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
                    InventarisPenghapusan.this.setCursor(Cursor.getDefaultCursor());
                    ceksukses = false;
                }
            }.execute();
        }
    }

    private void emptTeks() {
        noInventaris.setText("");
        namaInventaris.setText("-");
        hargaPerolehan.setText("0");
        akumulasiPenyusutan.setText("0");
        nilaiBuku.setText("0");
        untungRugi.setText("0");
        nilaiJual.setText("");
        alasan.setSelectedIndex(0);
        akunKas.setSelectedIndex(0);
        nip.setText("");
        namaPetugas.setText("");
        keterangan.setText("");
        harga = 0;
        akm = 0;
        nilai = 0;
    }

    private void autoNomor() {
        Valid.autonomorSmc(noPembebasan, "IPB", "", "penghapusan_inventaris", "no_penghapusan", 3, "0", tglPembebasan);
    }

    private void isCekPetugas() {
        String nama = Sequel.CariPetugas(nip.getText().trim());
        namaPetugas.setText(nama);
    }

    private void isiBarang() {
        String noInv = noInventaris.getText().trim();
        if (noInv.isEmpty()) {
            return;
        }
        /*
        runBackground(() -> {
            try {
                ps = koneksi.prepareStatement(
                    "SELECT ib.nm_barang, i.harga, i.status_barang " +
                    "FROM inventaris i " +
                    "INNER JOIN inventaris_barang ib ON ib.kode_barang = i.kode_barang " +
                    "WHERE i.no_inventaris = ? LIMIT 1"
                );
                ps.setString(1, noInv);
                rs = ps.executeQuery();
                if (rs.next()) {
                    final String nama = rs.getString("nm_barang");
                    final double hargaDb = rs.getDouble("harga");
                    final String statusDb = rs.getString("status_barang");
                    if ("Dihapus".equals(statusDb)) {
                        SwingUtilities.invokeLater(() -> {
                            JOptionPane.showMessageDialog(null, "Inventaris ini sudah dihapus/dipensiunkan.", "Peringatan", JOptionPane.WARNING_MESSAGE);
                            noInventaris.setText("");
                            namaInventaris.setText("-");
                            hargaPerolehan.setText("0");
                            akumulasiPenyusutan.setText("0");
                            nilaiBuku.setText("0");
                            untungRugi.setText("0");
                            harga = 0;
                            akm = 0;
                            nilai = 0;
                        });
                        return;
                    }
                    // Get accumulated depreciation
                    ps = koneksi.prepareStatement(
                        "SELECT IFNULL(SUM(beban_penyusutan),0) FROM penyusutan_inventaris WHERE no_inventaris = ?"
                    );
                    ps.setString(1, noInv);
                    rs = ps.executeQuery();
                    double akmDb = rs.next() ? rs.getDouble(1) : 0;
                    double nbDb = hargaDb - akmDb;
                    harga = hargaDb;
                    akm = akmDb;
                    nilai = nbDb;
                    SwingUtilities.invokeLater(() -> {
                        namaInventaris.setText(nama);
                        hargaPerolehan.setText(Valid.SetAngka(hargaDb));
                        akumulasiPenyusutan.setText(Valid.SetAngka(akmDb));
                        nilaiBuku.setText(Valid.SetAngka(nbDb));
                        hitungUntungRugi();
                    });
                } else {
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(null, "No. Inventaris tidak ditemukan.", "Peringatan", JOptionPane.WARNING_MESSAGE);
                        namaInventaris.setText("-");
                        hargaPerolehan.setText("0");
                        akumulasiPenyusutan.setText("0");
                        nilaiBuku.setText("0");
                        untungRugi.setText("0");
                        harga = 0;
                        akm = 0;
                        nilai = 0;
                    });
                }
            } catch (Exception e) {
                System.out.println("InventarisPenghapusan.isiBarang: " + e);
            }
        });
        */
    }

    private void hitungUntungRugi() {
        double nj = Valid.SetAngka(nilaiJual.getText());
        double ur = nj - nilai;
        untungRugi.setText(Valid.SetAngka(ur));
        untungRugi.setForeground(ur >= 0 ? new java.awt.Color(0, 120, 0) : new java.awt.Color(180, 0, 0));
    }

    private void tampilAkun() {
        //
    }

    private void tampilAkun2() {
        new SwingWorker<Void, Object[]>() {
            @Override
            protected Void doInBackground() throws Exception {
                try (FileReader fr = new FileReader(new File("./cache/akunbayar.iyem"))) {

                }
                return null;
            }

            @Override
            protected void process(List<Object[]> chunks) {
                super.process(chunks); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/OverriddenMethodBody
            }

            @Override
            protected void done() {
                try {
                    get();
                } catch (Exception e) {
                    System.out.println("Notif : " + e);
                }
            }
        }.execute();
    }
}
