/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * DlgBiling.java
 *
 * Created on 07 Jun 10, 19:07:06
 */

package khanzaantrianloket;

import fungsi.BackgroundMusic;
import fungsi.koneksiDB;
import fungsi.sekuel;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import javax.swing.ImageIcon;
import javax.swing.Timer;

/**
 *
 * @author perpustakaan
 */
public class DlgAntrian extends javax.swing.JFrame implements ActionListener {
    private final Connection koneksi = koneksiDB.condb();
    private final sekuel Sequel = new sekuel();
    private final boolean ANTRIANPREFIXHURUF = koneksiDB.ANTRIANPREFIXHURUF();
    private final String[] PREFIXHURUFAKTIF = koneksiDB.PREFIXHURUFAKTIF();
    private final String ANTRIAN = koneksiDB.ANTRIAN();
    private final SimpleDateFormat df = new SimpleDateFormat("ss");
    private BackgroundMusic music;
    private int i;
    private String antri = "", loket = "";
    private String[] urut = {"", "./suara/satu.mp3", "./suara/dua.mp3", "./suara/tiga.mp3", "./suara/empat.mp3", "./suara/lima.mp3", "./suara/enam.mp3", "./suara/tujuh.mp3", "./suara/delapan.mp3", "./suara/sembilan.mp3", "./suara/sepuluh.mp3", "./suara/sebelas.mp3"},
                     urutsmc = {"./suarasmc/0.mp3", "./suarasmc/1.mp3", "./suarasmc/2.mp3", "./suarasmc/3.mp3", "./suarasmc/4.mp3", "./suarasmc/5.mp3", "./suarasmc/6.mp3", "./suarasmc/7.mp3", "./suarasmc/8.mp3", "./suarasmc/9.mp3"};

    /** Creates new form DlgBiling
     * @param parent
     * @param modal */
    public DlgAntrian() {
        initComponents();
        setIconImage(new ImageIcon(super.getClass().getResource("/picture/addressbook-edit24.png")).getImage());
        
        this.setSize(350, 400);
        
        panelBiasa1.setVisible(ANTRIANPREFIXHURUF);
        label3.setVisible(ANTRIANPREFIXHURUF);
        cmbhuruf.setVisible(ANTRIANPREFIXHURUF);
        
        if (ANTRIANPREFIXHURUF) {
            panelBiasa1.remove(AntrianA);
            panelBiasa1.remove(AntrianB);
            panelBiasa1.remove(AntrianC);
            panelBiasa1.remove(AntrianD);
            panelBiasa1.remove(AntrianE);
            panelBiasa1.remove(AntrianF);
            cmbhuruf.removeAllItems();
            int col = 0;
            java.awt.GridBagConstraints gbc;
            for (String huruf : PREFIXHURUFAKTIF) {
                switch (huruf) {
                    case "A":
                        cmbhuruf.addItem(huruf);
                        gbc = new java.awt.GridBagConstraints();
                        gbc.gridx = 0;
                        gbc.gridy = 0;
                        gbc.fill = java.awt.GridBagConstraints.BOTH;
                        gbc.weightx = 1.0;
                        gbc.weighty = 1.0;
                        panelBiasa1.add(AntrianA, gbc);
                        ++col;
                        break;
                    case "B":
                        cmbhuruf.addItem(huruf);
                        gbc = new java.awt.GridBagConstraints();
                        gbc.gridx = 1;
                        gbc.gridy = 0;
                        gbc.fill = java.awt.GridBagConstraints.BOTH;
                        gbc.weightx = 1.0;
                        gbc.weighty = 1.0;
                        panelBiasa1.add(AntrianB, gbc);
                        ++col;
                        break;
                    case "C":
                        cmbhuruf.addItem(huruf);
                        gbc = new java.awt.GridBagConstraints();
                        gbc.gridx = 2;
                        gbc.gridy = 0;
                        gbc.fill = java.awt.GridBagConstraints.BOTH;
                        gbc.weightx = 1.0;
                        gbc.weighty = 1.0;
                        panelBiasa1.add(AntrianC, gbc);
                        ++col;
                        break;
                    case "D":
                        cmbhuruf.addItem(huruf);
                        gbc = new java.awt.GridBagConstraints();
                        gbc.gridx = 0;
                        gbc.gridy = 1;
                        gbc.fill = java.awt.GridBagConstraints.BOTH;
                        gbc.weightx = 1.0;
                        gbc.weighty = 1.0;
                        panelBiasa1.add(AntrianD, gbc);
                        ++col;
                        break;
                    case "E":
                        cmbhuruf.addItem(huruf);
                        gbc = new java.awt.GridBagConstraints();
                        gbc.gridx = 1;
                        gbc.gridy = 1;
                        gbc.fill = java.awt.GridBagConstraints.BOTH;
                        gbc.weightx = 1.0;
                        gbc.weighty = 1.0;
                        panelBiasa1.add(AntrianE, gbc);
                        ++col;
                        break;
                    case "F":
                        cmbhuruf.addItem(huruf);
                        gbc = new java.awt.GridBagConstraints();
                        gbc.gridx = 2;
                        gbc.gridy = 1;
                        gbc.fill = java.awt.GridBagConstraints.BOTH;
                        gbc.weightx = 1.0;
                        gbc.weighty = 1.0;
                        panelBiasa1.add(AntrianF, gbc);
                        ++col;
                        break;
                }
            }
            if (col > 3) {
                panelBiasa1.setPreferredSize(new Dimension(panelBiasa1.getWidth(), 240));
            }
            repaint();
        }
        jam();
        Timer timer = new Timer(100, this);
        timer.start();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        DlgDisplay = new javax.swing.JDialog();
        internalFrame5 = new widget.InternalFrame();
        paneliklan = new usu.widget.glass.PanelGlass();
        panelruntext = new javax.swing.JPanel();
        labelruntext = new widget.Label();
        form1 = new widget.InternalFrame();
        labelantri1 = new widget.Label();
        labelLoket = new widget.Label();
        DlgDisplaySMC = new javax.swing.JDialog();
        internalFrame6 = new widget.InternalFrame();
        paneliklan1 = new usu.widget.glass.PanelGlass();
        internalFrame2 = new widget.InternalFrame();
        DisplayAntrian = new widget.Label();
        DisplayLoket = new widget.Label();
        panelBiasa1 = new widget.PanelBiasa();
        AntrianA = new widget.Label();
        AntrianB = new widget.Label();
        AntrianC = new widget.Label();
        AntrianD = new widget.Label();
        AntrianE = new widget.Label();
        AntrianF = new widget.Label();
        internalFrame1 = new widget.InternalFrame();
        panelisi1 = new widget.panelisi();
        BtnDisplay = new widget.Button();
        BtnKeluar = new widget.Button();
        panelisi5 = new widget.panelisi();
        BtnAntri = new widget.Button();
        BtnReset = new widget.Button();
        label1 = new widget.Label();
        cmbloket = new widget.ComboBox();
        label2 = new widget.Label();
        Antrian = new widget.TextBox();
        BtnStop = new widget.Button();
        label3 = new widget.Label();
        cmbhuruf = new widget.ComboBox();

        DlgDisplay.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        DlgDisplay.setModalExclusionType(java.awt.Dialog.ModalExclusionType.APPLICATION_EXCLUDE);

        internalFrame5.setBackground(new java.awt.Color(250, 255, 250));
        internalFrame5.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(100, 200, 100)), "::[ Informasi ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 32), new java.awt.Color(50, 100, 50))); // NOI18N
        internalFrame5.setPreferredSize(new java.awt.Dimension(500, 110));
        internalFrame5.setWarnaBawah(new java.awt.Color(250, 255, 250));
        internalFrame5.setLayout(new java.awt.BorderLayout());

        paneliklan.setBackground(new java.awt.Color(250, 255, 250));
        paneliklan.setBackgroundImage(new javax.swing.ImageIcon(getClass().getResource("/picture/coba.gif"))); // NOI18N
        paneliklan.setBackgroundImageType(usu.widget.constan.BackgroundConstan.BACKGROUND_IMAGE_STRECT);
        paneliklan.setPreferredSize(new java.awt.Dimension(200, 140));
        paneliklan.setRound(false);
        paneliklan.setWarna(new java.awt.Color(250, 255, 250));
        paneliklan.setLayout(null);
        internalFrame5.add(paneliklan, java.awt.BorderLayout.CENTER);

        panelruntext.setBackground(new java.awt.Color(250, 255, 250));
        panelruntext.setPreferredSize(new java.awt.Dimension(100, 100));
        panelruntext.setLayout(new java.awt.BorderLayout());

        labelruntext.setBackground(new java.awt.Color(250, 255, 250));
        labelruntext.setForeground(new java.awt.Color(50, 100, 50));
        labelruntext.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        labelruntext.setFont(new java.awt.Font("Tahoma", 0, 35)); // NOI18N
        labelruntext.setPreferredSize(new java.awt.Dimension(853, 50));
        panelruntext.add(labelruntext, java.awt.BorderLayout.CENTER);

        internalFrame5.add(panelruntext, java.awt.BorderLayout.PAGE_END);

        DlgDisplay.getContentPane().add(internalFrame5, java.awt.BorderLayout.CENTER);

        form1.setBackground(new java.awt.Color(250, 255, 250));
        form1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(100, 200, 100)), " Antrian Registrasi", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 45), new java.awt.Color(50, 100, 50))); // NOI18N
        form1.setPreferredSize(new java.awt.Dimension(650, 150));
        form1.setWarnaBawah(new java.awt.Color(250, 255, 250));
        form1.setLayout(new java.awt.GridLayout(2, 0));

        labelantri1.setBackground(new java.awt.Color(250, 255, 250));
        labelantri1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(150, 250, 150)), "No.Antrian :", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 32), new java.awt.Color(50, 100, 50))); // NOI18N
        labelantri1.setForeground(new java.awt.Color(50, 100, 50));
        labelantri1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelantri1.setText("1");
        labelantri1.setFont(new java.awt.Font("Tahoma", 1, 200)); // NOI18N
        labelantri1.setPreferredSize(new java.awt.Dimension(300, 50));
        form1.add(labelantri1);

        labelLoket.setBackground(new java.awt.Color(250, 255, 250));
        labelLoket.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(150, 250, 150)), "Loket :", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 32), new java.awt.Color(50, 100, 50))); // NOI18N
        labelLoket.setForeground(new java.awt.Color(50, 100, 50));
        labelLoket.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelLoket.setText("1");
        labelLoket.setFocusable(false);
        labelLoket.setFont(new java.awt.Font("Tahoma", 1, 200)); // NOI18N
        labelLoket.setPreferredSize(new java.awt.Dimension(150, 50));
        form1.add(labelLoket);

        DlgDisplay.getContentPane().add(form1, java.awt.BorderLayout.LINE_END);

        DlgDisplaySMC.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        DlgDisplaySMC.setMinimumSize(new java.awt.Dimension(1366, 768));
        DlgDisplaySMC.setModalExclusionType(java.awt.Dialog.ModalExclusionType.APPLICATION_EXCLUDE);
        DlgDisplaySMC.setPreferredSize(new java.awt.Dimension(1366, 768));

        internalFrame6.setBackground(new java.awt.Color(250, 255, 250));
        internalFrame6.setBorder(null);
        internalFrame6.setPreferredSize(new java.awt.Dimension(500, 110));
        internalFrame6.setWarnaBawah(new java.awt.Color(250, 255, 250));
        internalFrame6.setLayout(new java.awt.BorderLayout(8, 0));

        paneliklan1.setBackground(new java.awt.Color(250, 255, 250));
        paneliklan1.setBackgroundImage(new javax.swing.ImageIcon(getClass().getResource("/picture/coba.gif"))); // NOI18N
        paneliklan1.setBackgroundImageType(usu.widget.constan.BackgroundConstan.BACKGROUND_IMAGE_STRECT);
        paneliklan1.setPreferredSize(new java.awt.Dimension(200, 140));
        paneliklan1.setRound(false);
        paneliklan1.setWarna(new java.awt.Color(250, 255, 250));
        paneliklan1.setLayout(null);
        internalFrame6.add(paneliklan1, java.awt.BorderLayout.CENTER);

        internalFrame2.setPreferredSize(new java.awt.Dimension(650, 150));
        internalFrame2.setLayout(new java.awt.GridLayout(2, 1));

        DisplayAntrian.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(50, 100, 50), 2), "No. Antrian", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 2, 36), new java.awt.Color(50, 100, 50))); // NOI18N
        DisplayAntrian.setForeground(new java.awt.Color(50, 100, 50));
        DisplayAntrian.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        DisplayAntrian.setFont(new java.awt.Font("Tahoma", 1, 90)); // NOI18N
        DisplayAntrian.setPreferredSize(new java.awt.Dimension(186, 200));
        internalFrame2.add(DisplayAntrian);

        DisplayLoket.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(50, 100, 50), 2), "Loket", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 2, 36), new java.awt.Color(50, 100, 50))); // NOI18N
        DisplayLoket.setForeground(new java.awt.Color(50, 100, 50));
        DisplayLoket.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        DisplayLoket.setFont(new java.awt.Font("Tahoma", 1, 90)); // NOI18N
        DisplayLoket.setPreferredSize(new java.awt.Dimension(186, 200));
        internalFrame2.add(DisplayLoket);

        internalFrame6.add(internalFrame2, java.awt.BorderLayout.EAST);

        panelBiasa1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1), "Antrian Terakhir", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 2, 30), new java.awt.Color(60, 130, 90))); // NOI18N
        panelBiasa1.setPreferredSize(new java.awt.Dimension(120, 160));
        java.awt.GridBagLayout panelBiasa1Layout = new java.awt.GridBagLayout();
        panelBiasa1Layout.columnWidths = new int[] {1, 1, 1};
        panelBiasa1Layout.rowHeights = new int[] {1, 1};
        panelBiasa1.setLayout(panelBiasa1Layout);

        AntrianA.setForeground(new java.awt.Color(60, 130, 90));
        AntrianA.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        AntrianA.setText("A");
        AntrianA.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        AntrianA.setFont(new java.awt.Font("Tahoma", 1, 42)); // NOI18N
        AntrianA.setMaximumSize(null);
        AntrianA.setMinimumSize(null);
        AntrianA.setPreferredSize(new java.awt.Dimension(1, 80));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panelBiasa1.add(AntrianA, gridBagConstraints);

        AntrianB.setForeground(new java.awt.Color(60, 130, 90));
        AntrianB.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        AntrianB.setText("B");
        AntrianB.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        AntrianB.setFont(new java.awt.Font("Tahoma", 1, 42)); // NOI18N
        AntrianB.setMaximumSize(null);
        AntrianB.setMinimumSize(null);
        AntrianB.setPreferredSize(new java.awt.Dimension(1, 80));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panelBiasa1.add(AntrianB, gridBagConstraints);

        AntrianC.setForeground(new java.awt.Color(60, 130, 90));
        AntrianC.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        AntrianC.setText("C");
        AntrianC.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        AntrianC.setFont(new java.awt.Font("Tahoma", 1, 42)); // NOI18N
        AntrianC.setMaximumSize(null);
        AntrianC.setMinimumSize(null);
        AntrianC.setPreferredSize(new java.awt.Dimension(1, 80));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panelBiasa1.add(AntrianC, gridBagConstraints);

        AntrianD.setForeground(new java.awt.Color(60, 130, 90));
        AntrianD.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        AntrianD.setText("D");
        AntrianD.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        AntrianD.setFont(new java.awt.Font("Tahoma", 1, 42)); // NOI18N
        AntrianD.setMaximumSize(null);
        AntrianD.setMinimumSize(null);
        AntrianD.setPreferredSize(new java.awt.Dimension(1, 80));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panelBiasa1.add(AntrianD, gridBagConstraints);

        AntrianE.setForeground(new java.awt.Color(60, 130, 90));
        AntrianE.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        AntrianE.setText("E");
        AntrianE.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        AntrianE.setFont(new java.awt.Font("Tahoma", 1, 42)); // NOI18N
        AntrianE.setMaximumSize(null);
        AntrianE.setMinimumSize(null);
        AntrianE.setPreferredSize(new java.awt.Dimension(1, 80));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panelBiasa1.add(AntrianE, gridBagConstraints);

        AntrianF.setForeground(new java.awt.Color(60, 130, 90));
        AntrianF.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        AntrianF.setText("F");
        AntrianF.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        AntrianF.setFont(new java.awt.Font("Tahoma", 1, 42)); // NOI18N
        AntrianF.setMaximumSize(null);
        AntrianF.setMinimumSize(null);
        AntrianF.setPreferredSize(new java.awt.Dimension(1, 80));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panelBiasa1.add(AntrianF, gridBagConstraints);

        internalFrame6.add(panelBiasa1, java.awt.BorderLayout.PAGE_END);

        DlgDisplaySMC.getContentPane().add(internalFrame6, java.awt.BorderLayout.CENTER);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(350, 400));
        setPreferredSize(new java.awt.Dimension(350, 400));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)), "::[ Antrian Registrasi Pasien ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 70, 40))); // NOI18N
        internalFrame1.setLayout(new java.awt.BorderLayout(1, 1));

        panelisi1.setPreferredSize(new java.awt.Dimension(55, 55));
        panelisi1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 9));

        BtnDisplay.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/editcopy.png"))); // NOI18N
        BtnDisplay.setMnemonic('D');
        BtnDisplay.setText("Display");
        BtnDisplay.setToolTipText("Alt+D");
        BtnDisplay.setIconTextGap(3);
        BtnDisplay.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnDisplay.addActionListener(this::BtnDisplayActionPerformed);
        panelisi1.add(BtnDisplay);

        BtnKeluar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/exit.png"))); // NOI18N
        BtnKeluar.setMnemonic('K');
        BtnKeluar.setText("Keluar");
        BtnKeluar.setToolTipText("Alt+K");
        BtnKeluar.setIconTextGap(3);
        BtnKeluar.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnKeluar.addActionListener(this::BtnKeluarActionPerformed);
        panelisi1.add(BtnKeluar);

        internalFrame1.add(panelisi1, java.awt.BorderLayout.PAGE_END);

        panelisi5.setPreferredSize(new java.awt.Dimension(12, 44));
        panelisi5.setLayout(null);

        BtnAntri.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Agenda-1-16x16.png"))); // NOI18N
        BtnAntri.setMnemonic('7');
        BtnAntri.setText("Antri");
        BtnAntri.setToolTipText("Alt+7");
        BtnAntri.setIconTextGap(3);
        BtnAntri.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnAntri.addActionListener(this::BtnAntriActionPerformed);
        panelisi5.add(BtnAntri);
        BtnAntri.setBounds(20, 90, 100, 30);

        BtnReset.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/cross.png"))); // NOI18N
        BtnReset.setMnemonic('8');
        BtnReset.setText("Reset");
        BtnReset.setToolTipText("Alt+8");
        BtnReset.setIconTextGap(3);
        BtnReset.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnReset.addActionListener(this::BtnResetActionPerformed);
        panelisi5.add(BtnReset);
        BtnReset.setBounds(130, 90, 100, 30);

        label1.setText("Antrian :");
        panelisi5.add(label1);
        label1.setBounds(145, 12, 60, 23);

        cmbloket.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9" }));
        panelisi5.add(cmbloket);
        cmbloket.setBounds(65, 12, 60, 23);

        label2.setText("Loket :");
        panelisi5.add(label2);
        label2.setBounds(0, 12, 60, 23);

        Antrian.setText("1");
        panelisi5.add(Antrian);
        Antrian.setBounds(210, 12, 60, 24);

        BtnStop.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Cancel-2-16x16.png"))); // NOI18N
        BtnStop.setMnemonic('8');
        BtnStop.setText("Stop");
        BtnStop.setToolTipText("Alt+8");
        BtnStop.setIconTextGap(3);
        BtnStop.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnStop.addActionListener(this::BtnStopActionPerformed);
        panelisi5.add(BtnStop);
        BtnStop.setBounds(20, 130, 100, 30);

        label3.setText("Huruf :");
        panelisi5.add(label3);
        label3.setBounds(0, 42, 60, 23);

        cmbhuruf.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "A", "B", "C", "D", "E", "F", " " }));
        panelisi5.add(cmbhuruf);
        cmbhuruf.setBounds(65, 42, 60, 23);

        internalFrame1.add(panelisi5, java.awt.BorderLayout.CENTER);

        getContentPane().add(internalFrame1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void BtnDisplayActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnDisplayActionPerformed
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        if (ANTRIANPREFIXHURUF) {
            isTampilSmc();
            DlgDisplaySMC.setSize(screen.width, screen.height);
            DlgDisplaySMC.setIconImage(new ImageIcon(super.getClass().getResource("/picture/addressbook-edit24.png")).getImage());
            DlgDisplaySMC.setAlwaysOnTop(false);
            DlgDisplaySMC.setVisible(true);
        } else {
            isTampil();
            DlgDisplay.setSize(screen.width, screen.height);
            DlgDisplay.setIconImage(new ImageIcon(super.getClass().getResource("/picture/addressbook-edit24.png")).getImage());
            DlgDisplay.setAlwaysOnTop(false);
            DlgDisplay.setVisible(true);
        }
    }//GEN-LAST:event_BtnDisplayActionPerformed

    private void BtnKeluarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnKeluarActionPerformed
        System.exit(0);
    }//GEN-LAST:event_BtnKeluarActionPerformed

    private void BtnAntriActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnAntriActionPerformed
        Sequel.menghapusSmc("antriloketsmc");
        Sequel.executeRawSmc(
            "insert into antriloketsmc values (?, concat(?, lpad(?, 3, '0')))",
            cmbloket.getSelectedItem().toString(),
            cmbhuruf.isVisible() ? cmbhuruf.getSelectedItem().toString() : "",
            Antrian.getText().trim()
        );
        Sequel.mengupdateSmc("antriloketcetak_smc", "jam_panggil = current_time()", "nomor = concat(?, lpad(?, 3, '0')) and tanggal = current_date()",
            cmbhuruf.isVisible() ? cmbhuruf.getSelectedItem().toString() : "",
            Antrian.getText().trim()
        );
    }//GEN-LAST:event_BtnAntriActionPerformed

    private void BtnResetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnResetActionPerformed
        Antrian.setText("1");
    }//GEN-LAST:event_BtnResetActionPerformed

    private void BtnStopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnStopActionPerformed
        Sequel.menghapusSmc("antriloketsmc");
    }//GEN-LAST:event_BtnStopActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        if (ANTRIAN.equals("player")) {
            BtnDisplayActionPerformed(null);
        }
    }//GEN-LAST:event_formWindowOpened

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            DlgAntrian window = new DlgAntrian();
            window.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    System.exit(0);
                }
            });
            window.setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private widget.TextBox Antrian;
    private widget.Label AntrianA;
    private widget.Label AntrianB;
    private widget.Label AntrianC;
    private widget.Label AntrianD;
    private widget.Label AntrianE;
    private widget.Label AntrianF;
    private widget.Button BtnAntri;
    private widget.Button BtnDisplay;
    private widget.Button BtnKeluar;
    private widget.Button BtnReset;
    private widget.Button BtnStop;
    private widget.Label DisplayAntrian;
    private widget.Label DisplayLoket;
    private javax.swing.JDialog DlgDisplay;
    private javax.swing.JDialog DlgDisplaySMC;
    private widget.ComboBox cmbhuruf;
    private widget.ComboBox cmbloket;
    private widget.InternalFrame form1;
    private widget.InternalFrame internalFrame1;
    private widget.InternalFrame internalFrame2;
    private widget.InternalFrame internalFrame5;
    private widget.InternalFrame internalFrame6;
    private widget.Label label1;
    private widget.Label label2;
    private widget.Label label3;
    private widget.Label labelLoket;
    private widget.Label labelantri1;
    private widget.Label labelruntext;
    private widget.PanelBiasa panelBiasa1;
    private usu.widget.glass.PanelGlass paneliklan;
    private usu.widget.glass.PanelGlass paneliklan1;
    private widget.panelisi panelisi1;
    private widget.panelisi panelisi5;
    private javax.swing.JPanel panelruntext;
    // End of variables declaration//GEN-END:variables

    @Override
    public void actionPerformed(ActionEvent e) {
        if (ANTRIANPREFIXHURUF) {
            paneliklan1.repaint();
        } else {
            paneliklan.repaint();
            String oldText = labelruntext.getText();
            String newText = oldText.substring(1) + oldText.substring(0, 1);
            labelruntext.setText(newText);
        }
    }
    
    private void isTampil() {
        try (ResultSet rs = koneksi.createStatement().executeQuery("select aktifkan, gambar, teks from runtext")) {
            if (rs.next()) {
                if (rs.getString(1).equals("Yes")) {
                    Blob gambar = rs.getBlob(2);
                    paneliklan.setBackgroundImage(new ImageIcon(gambar.getBytes(1, (int) gambar.length())));
                }
                labelruntext.setText(rs.getString(3));
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
        }
    }
    
    private void isTampilSmc() {
        try (ResultSet rs = koneksi.createStatement().executeQuery("select aktifkan, gambar from runtext")) {
            if (rs.next()) {
                if (rs.getString(1).equals("Yes")) {
                    Blob gambar = rs.getBlob(2);
                    paneliklan1.setBackgroundImage(new ImageIcon(gambar.getBytes(1, (int) gambar.length())));
                }
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
        }
    }

    private void panggil(String nomor) {
        int antrian = Integer.parseInt(nomor);
        if (antrian < 12) {
            try {
                music = new BackgroundMusic(urut[antrian]);
                music.start();
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                System.out.println(ex);
            }
        } else if (antrian < 20) {
            try {
                music = new BackgroundMusic(urut[antrian - 10]);
                music.start();
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                System.out.println(ex);
            }
            try {
                music = new BackgroundMusic("./suara/belas.mp3");
                music.start();
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                System.out.println(ex);
            }
        } else if (antrian < 100) {
            try {
                music = new BackgroundMusic(urut[antrian / 10]);
                music.start();
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                System.out.println(ex);
            }
            try {
                music = new BackgroundMusic("./suara/puluh.mp3");
                music.start();
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                System.out.println(ex);
            }
            panggil(String.valueOf(antrian % 10));
        } else if (antrian < 200) {
            try {
                music = new BackgroundMusic("./suara/seratus.mp3");
                music.start();
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                System.out.println(ex);
            }
            panggil(String.valueOf(antrian - 100));
        } else if (antrian < 1000) {
            panggil(String.valueOf(antrian / 100));
            try {
                music = new BackgroundMusic("./suara/ratus.mp3");
                music.start();
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                System.out.println(ex);
            }
            panggil(String.valueOf(antrian % 100));
        }
    }

    private void panggilSmc(String antrian) {
        if (antrian.isBlank()) return;
        try {
            System.out.println(antrian.substring(0, 1));
            music = new BackgroundMusic("./suarasmc/" + antrian.substring(0, 1) + ".mp3");
            music.start();
            Thread.sleep(1000);
            panggilAngkaSmc(antrian.substring(1));
        } catch (Exception e) {
            System.out.println("Notif : " + e);
        }
    }

    private void panggilAngkaSmc(String antrian) {
        if (antrian.isBlank()) return;
        for (int i = 0; i < antrian.length(); i++) {
            try {
                music = new BackgroundMusic(urutsmc[Integer.parseInt(antrian.substring(i, i + 1))]);
                music.start();
                Thread.sleep(1000);
            } catch (Exception e) {
                System.out.println("Notif : " + e);
            }
        }
    }

    private void jam() {
        ActionListener taskPerformer = (ActionEvent event) -> {
            Date now = Calendar.getInstance().getTime();
            String ss = df.format(now);
            int s = Integer.parseInt(ss);
            System.out.println("Detik : " + ss);
            
            if (s % 5 == 0) {
                antri = ""; loket = "";
                try (ResultSet rs = koneksi.createStatement().executeQuery("select antrian, loket from antriloketsmc")) {
                    if (rs.next()) {
                        antri = rs.getString(1);
                        loket = rs.getString(2);
                        if (!antri.isBlank() && !loket.isBlank()) {
                            labelantri1.setText(antri);
                            labelLoket.setText(loket);
                            DisplayAntrian.setText(antri);
                            DisplayLoket.setText(loket);
                            System.out.print("Loket : " + loket + "; ");
                            System.out.println("Antrian : " + antri);
                            i = Integer.parseInt(antri.substring(1)) + 1;
                            Antrian.setText("" + i);
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Notif : " + e);
                }
                
                if (panelBiasa1.isVisible()) {
                    try (ResultSet rs = koneksi.createStatement().executeQuery(
                        "select left(nomor, 1), max(nomor) from antriloketcetak_smc where tanggal = current_date() and jam_panggil is not null group by left(nomor, 1)"
                    )) {
                        while (rs.next()) {
                            switch (rs.getString(1)) {
                                case "A": AntrianA.setText(rs.getString(2)); break;
                                case "B": AntrianB.setText(rs.getString(2)); break;
                                case "C": AntrianC.setText(rs.getString(2)); break;
                                case "D": AntrianD.setText(rs.getString(2)); break;
                                case "E": AntrianE.setText(rs.getString(2)); break;
                                case "F": AntrianF.setText(rs.getString(2)); break;
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("Notif : " + e);
                    }
                }
            }
            
            if (!antri.isBlank() && !loket.isBlank() && s % 5 == 1 && ANTRIAN.equals("player")) {
                try {
                    music = new BackgroundMusic("./suarasmc/nomor-urut.mp3");
                    music.start();
                    Thread.sleep(1500);
                    if (ANTRIANPREFIXHURUF) {
                        panggilSmc(antri);
                    } else {
                        panggil(antri);
                    }
                    music = new BackgroundMusic("./suarasmc/loket.mp3");
                    music.start();
                    Thread.sleep(1500);
                    if (ANTRIANPREFIXHURUF) {
                        panggilAngkaSmc(loket);
                    } else {
                        panggil(loket);
                    }
                } catch (InterruptedException ex) {
                    System.out.println(ex);
                }
            }
        };
        new Timer(1000, taskPerformer).start();
    }
}
