package jrviewersmc;

import java.awt.BorderLayout;
import java.io.File;
import java.util.prefs.Preferences;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

public class JRViewerWindow extends JFrame {

    private static final String PREF_LAST_FILE = "JRViewerSMC_lastFile";
    private static final Preferences PREFS = Preferences.userNodeForPackage(JRViewerWindow.class);

    private final JTextField txtFilePath = new JTextField(40);
    private final JButton btnBrowse = new JButton("Pilih...");
    private File selectedFile;

    public JRViewerWindow() {
        super("JRPrint File Viewer SMC");
        initComponents();
    }

    private void initComponents() {
        txtFilePath.setEditable(false);

        btnBrowse.addActionListener(e -> browseFile());

        JPanel filePanel = new JPanel(new BorderLayout(4, 0));
        filePanel.add(txtFilePath, BorderLayout.CENTER);
        filePanel.add(btnBrowse, BorderLayout.EAST);

        JPanel content = new JPanel(new BorderLayout());
        content.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        content.add(filePanel, BorderLayout.CENTER);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(content);

        pack();
        setLocationRelativeTo(null);
    }

    private void browseFile() {
        String lastPath = PREFS.get(PREF_LAST_FILE, null);
        File startDir = selectedFile != null ? selectedFile.getParentFile()
                : lastPath != null ? new File(lastPath).getParentFile()
                : null;
        JFileChooser chooser = new JFileChooser(startDir);
        chooser.setFileFilter(new FileNameExtensionFilter("JasperReports Print File (*.jrprint)", "jrprint"));
        chooser.setDialogTitle("Pilih File JRPrint");
        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        selectedFile = chooser.getSelectedFile();
        txtFilePath.setText(selectedFile.getAbsolutePath());
        PREFS.put(PREF_LAST_FILE, selectedFile.getAbsolutePath());
        Utils.open(selectedFile);
    }
}
