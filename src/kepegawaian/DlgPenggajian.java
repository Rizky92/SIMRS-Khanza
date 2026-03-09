

/*
 * DlgAbout.java
 *
 * Created on 23 Jun 10, 19:03:08
 */

package kepegawaian;

import fungsi.koneksiDB;
import fungsi.validasi;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.FileInputStream;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker.State;
import static javafx.concurrent.Worker.State.FAILED;
import javafx.embed.swing.JFXPanel;
import javafx.print.PageLayout;
import javafx.print.PageOrientation;
import javafx.print.Paper;
import javafx.print.Printer;
import javafx.print.PrinterJob;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.transform.Scale;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebEvent;
import javafx.scene.web.WebView;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

/**
 *
 * @author perpustakaan
 */
public class DlgPenggajian extends javax.swing.JDialog {
    private final JFXPanel jfxPanel = new JFXPanel();
    private WebEngine engine;
    private ProgressBar progressBar;

    // private final JPanel panel = new JPanel(new BorderLayout());
    private final JLabel lblStatus = new JLabel();

    private final JTextField txtURL = new JTextField();
    // private final JProgressBar progressBar = new JProgressBar();
    private final Properties prop = new Properties();
    private final validasi Valid=new validasi();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private volatile boolean ceksukses = false;

    public DlgPenggajian(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        Platform.setImplicitExit(false);
        initComponents();
        initComponents2();
        try {
            prop.loadFromXML(new FileInputStream("setting/database.xml"));
        } catch (Exception e) {
        }
    }

    private void initComponents2() {
        txtURL.addActionListener((ActionEvent e) -> {
            runBackground(() -> loadURL(txtURL.getText()));
        });

        Platform.runLater(() -> {
            WebView view = new WebView();
            engine = view.getEngine();
            engine.setJavaScriptEnabled(true);
            engine.setCreatePopupHandler(p -> view.getEngine());

            engine.titleProperty().addListener((ObservableValue<? extends String> observable, String oldValue, final String newValue) -> {
                SwingUtilities.invokeLater(() -> {
                    if (engine.getLocation().contains("/webapps/penggajian/index.php?act=HomeAdmin")) {
                        try {
                            if (prop.getProperty("MENUTRANSPARAN").equals("yes")) {
                                DlgPenggajian.this.setOpacity(0.77f);
                            }
                        } catch (Exception e) {
                        }
                    } else {
                        try {
                            if (prop.getProperty("MENUTRANSPARAN").equals("yes")) {
                                DlgPenggajian.this.setOpacity(1f);
                            }
                        } catch (Exception e) {
                        }
                    }
                    DlgPenggajian.this.setTitle(newValue);
                });
            });

            engine.setOnStatusChanged((final WebEvent<String> event) -> {
                SwingUtilities.invokeLater(() -> lblStatus.setText(event.getData()));
            });

            engine.getLoadWorker().exceptionProperty().addListener((ObservableValue<? extends Throwable> o, Throwable old, final Throwable value) -> {
                if (engine.getLoadWorker().getState() == FAILED) {
                    SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(null, (value != null)
                        ? engine.getLocation() + "\n" + value.getMessage() : engine.getLocation() + "\nUnexpected Catatan.",
                        "Loading Catatan...", JOptionPane.ERROR_MESSAGE
                    ));
                }
            });

            engine.locationProperty().addListener((ObservableValue<? extends String> ov, String oldValue, final String newValue) -> {
                SwingUtilities.invokeLater(() -> {
                    txtURL.setText(newValue);
                });
            });

            engine.getLoadWorker().stateProperty().addListener((ov, oldState, newState) -> {
                if (newState == State.RUNNING) {
                    if (progressBar != null) {
                        progressBar.setVisible(true);
                    }
                } else if (newState == State.SUCCEEDED || newState == FAILED) {
                    if (progressBar != null) {
                        progressBar.setVisible(false);
                    }
                    if (newState == State.SUCCEEDED) {
                        try {
                            if (engine.getLocation().replaceAll("http://" + koneksiDB.HOSTHYBRIDWEB() + ":" + prop.getProperty("PORTWEB") + "/" + prop.getProperty("HYBRIDWEB") + "/", "").contains("penggajian/pages")) {
                                setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                                Valid.panggilUrl(engine.getLocation().replaceAll("http://" + koneksiDB.HOSTHYBRIDWEB() + ":" + prop.getProperty("PORTWEB") + "/" + prop.getProperty("HYBRIDWEB") + "/", "").replaceAll("http://" + koneksiDB.HOSTHYBRIDWEB() + "/" + prop.getProperty("HYBRIDWEB") + "/", ""));
                                engine.executeScript("history.back()");
                                setCursor(Cursor.getDefaultCursor());
                            } else if (engine.getLocation().replaceAll("http://" + koneksiDB.HOSTHYBRIDWEB() + ":" + prop.getProperty("PORTWEB") + "/" + prop.getProperty("HYBRIDWEB") + "/", "").contains("Keluar")) {
                                SwingUtilities.invokeLater(() -> dispose());
                            }
                        } catch (Exception ex) {
                            System.out.println("Notifikasi : " + ex);
                        }
                    }
                }
            });

            progressBar = new ProgressBar(0);
            progressBar.setMaxWidth(Double.MAX_VALUE);
            progressBar.setPrefHeight(10);
            progressBar.progressProperty().bind(engine.getLoadWorker().progressProperty());

            BorderPane pane = new BorderPane(view);
            pane.setTop(progressBar);

            jfxPanel.setScene(new Scene(pane));
        });

        internalFrame1.add(jfxPanel, BorderLayout.CENTER);
    }

    public void LoadPenggajian(String url){
        runBackground(() ->loadURL(url));
    }

    private void loadURL(String url) {
        Platform.runLater(() -> {
            try {
                engine.load(url);
            }catch (Exception exception) {
                engine.load(url);
            }
        });
    }

    public void CloseScane(){
        Platform.setImplicitExit(false);
    }

    public void print(final Node node) {
        Printer printer = Printer.getDefaultPrinter();
        PageLayout pageLayout = printer.createPageLayout(Paper.NA_LETTER, PageOrientation.PORTRAIT, Printer.MarginType.DEFAULT);
        double scaleX = pageLayout.getPrintableWidth() / node.getBoundsInParent().getWidth();
        double scaleY = pageLayout.getPrintableHeight() / node.getBoundsInParent().getHeight();
        node.getTransforms().add(new Scale(scaleX, scaleY));

        PrinterJob job = PrinterJob.createPrinterJob();
        if (job != null) {
            boolean success = job.printPage(node);
            if (success) {
                job.endJob();
            }
        }
    }


    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        internalFrame1 = new widget.InternalFrame();
        panelGlass8 = new widget.panelisi();
        BtnKeluar = new widget.Button();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("::[ About Program ]::");
        setUndecorated(true);
        setResizable(false);
        addWindowStateListener(new java.awt.event.WindowStateListener() {
            public void windowStateChanged(java.awt.event.WindowEvent evt) {
                formWindowStateChanged(evt);
            }
        });
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
        });

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)), "::[ Pengolahan Data Kepegawaian & Penggajian ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50))); // NOI18N
        internalFrame1.setName("internalFrame1"); // NOI18N
        internalFrame1.setLayout(new java.awt.BorderLayout());

        panelGlass8.setName("panelGlass8"); // NOI18N
        panelGlass8.setPreferredSize(new java.awt.Dimension(44, 44));
        panelGlass8.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 9));

        BtnKeluar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/exit.png"))); // NOI18N
        BtnKeluar.setText("Keluar");
        BtnKeluar.setToolTipText("Alt+K");
        BtnKeluar.setName("BtnKeluar"); // NOI18N
        BtnKeluar.setPreferredSize(new java.awt.Dimension(100, 30));
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

        internalFrame1.add(panelGlass8, java.awt.BorderLayout.PAGE_END);

        getContentPane().add(internalFrame1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        Platform.setImplicitExit(false);
    }//GEN-LAST:event_formWindowClosed

    private void formWindowStateChanged(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowStateChanged
        if(this.isActive()==false){
            Platform.setImplicitExit(false);
        }
    }//GEN-LAST:event_formWindowStateChanged

    private void BtnKeluarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnKeluarActionPerformed
        dispose();
    }//GEN-LAST:event_BtnKeluarActionPerformed

    private void BtnKeluarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnKeluarKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            dispose();
        }
    }//GEN-LAST:event_BtnKeluarKeyPressed

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            DlgPenggajian dialog = new DlgPenggajian(new javax.swing.JFrame(), true);
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
    private widget.Button BtnKeluar;
    private widget.InternalFrame internalFrame1;
    private widget.panelisi panelGlass8;
    // End of variables declaration//GEN-END:variables

    private void runBackground(Runnable task) {
        if (ceksukses) return;
        if (executor.isShutdown() || executor.isTerminated()) return;
        if (!isDisplayable()) return;

        ceksukses = true;
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        try {
            executor.submit(() -> {
                try {
                    task.run();
                } finally {
                    ceksukses = false;
                    SwingUtilities.invokeLater(() -> {
                        if (isDisplayable()) {
                            setCursor(Cursor.getDefaultCursor());
                        }
                    });
                }
            });
        } catch (RejectedExecutionException ex) {
            ceksukses = false;
        }
    }

    /*
    @Override
    public void dispose() {
        executor.shutdownNow();
        super.dispose();
    }
    */
}
