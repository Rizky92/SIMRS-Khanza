package widget;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.ComboBoxEditor;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.table.TableCellEditor;
import uz.ncipro.calendar.JDateTimePicker;

/**
 *
 * @author smc
 */
public final class TanggalCellEditorSMC extends AbstractCellEditor implements TableCellEditor {

    /*
     * Serial version UID
     */
    private static final long serialVersionUID = 1L;

    public static final String FORMAT = "yyyy-MM-dd";
    public static final String KOSONG = "";

    private static final Color WARNA_NORMAL = new Color(50, 50, 50);
    private static final Color WARNA_SALAH = new Color(200, 0, 0);

    private final PemilihTanggal pemilih;
    private final PenyuntingTanggal penyunting;
    private final SimpleDateFormat format;

    private boolean menyetel = false;
    private boolean sedangEdit = false;

    public TanggalCellEditorSMC() {
        format = new SimpleDateFormat(FORMAT);
        format.setLenient(false);

        penyunting = new PenyuntingTanggal();

        pemilih = new PemilihTanggal();
        pemilih.setDisplayFormat(FORMAT);
        pemilih.setEditable(true);
        pemilih.setEditor(penyunting);
        pemilih.putClientProperty("JComboBox.isTableCellEditor", Boolean.TRUE);
        pemilih.addPopupMenuListener(new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent evt) {
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent evt) {
                selesaikan();
            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent evt) {
            }
        });
        penyunting.addActionListener(evt -> selesaikan());
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        String teks = normalkan(value);
        Date tanggal = urai(teks);

        menyetel = true;
        try {
            pemilih.setDate(null == tanggal ? new Date() : tanggal);
            pemilih.removeAllItems();
            pemilih.addItem(teks);
            pemilih.setSelectedItem(teks);
            penyunting.setItem(teks);
        } finally {
            menyetel = false;
        }

        sedangEdit = true;
        return pemilih;
    }

    @Override
    public Object getCellEditorValue() {
        return normalkan(penyunting.getItem());
    }

    @Override
    public boolean stopCellEditing() {
        sedangEdit = false;
        tutupPopup();
        return super.stopCellEditing();
    }

    @Override
    public void cancelCellEditing() {
        sedangEdit = false;
        tutupPopup();
        super.cancelCellEditing();
    }

    private void selesaikan() {
        if (menyetel || !sedangEdit) {
            return;
        }
        SwingUtilities.invokeLater(() -> {
            if (sedangEdit) {
                stopCellEditing();
            }
        });
    }

    private void tutupPopup() {
        if (pemilih.isPopupVisible()) {
            menyetel = true;
            try {
                pemilih.hidePopup();
            } finally {
                menyetel = false;
            }
        }
    }

    private String normalkan(Object nilai) {
        Date tanggal = urai(null == nilai ? null : nilai.toString());
        return null == tanggal ? KOSONG : format.format(tanggal);
    }

    private Date urai(String teks) {
        if (null == teks) {
            return null;
        }

        String bersih = teks.trim();
        if (bersih.isEmpty()) {
            return null;
        }

        try {
            Date hasil = format.parse(bersih);
            return bersih.equals(format.format(hasil)) ? hasil : null;
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * JDateTimePicker bawaan mengurai teks dengan SimpleDateFormat lenient miliknya
     * lalu mencetak "Unparseable date" ke stdout setiap kali isian kosong. Turunan ini
     * menggantikan actionPerformed dengan versi JComboBox ditambah pembaruan kalender
     * yang ketat, sehingga tidak ada lagi keluaran liar.
     */
    private final class PemilihTanggal extends JDateTimePicker {

        /*
         * Serial version UID
         */
        private static final long serialVersionUID = 1L;

        PemilihTanggal() {
            super();
            setForeground(WARNA_NORMAL);
            setBackground(new Color(255, 255, 255));
            setFont(new Font("Tahoma", 0, 11));
        }

        @Override
        public void actionPerformed(ActionEvent evt) {
            setPopupVisible(false);

            ComboBoxEditor penyuntingAktif = getEditor();
            if (null != penyuntingAktif) {
                getModel().setSelectedItem(penyuntingAktif.getItem());
            }

            Object terpilih = getSelectedItem();
            Date tanggal = urai(null == terpilih ? null : terpilih.toString());
            if (null != tanggal) {
                setDate(tanggal);
            }

            String perintah = getActionCommand();
            setActionCommand("comboBoxEdited");
            fireActionEvent();
            setActionCommand(perintah);
        }
    }

    /**
     * Kotak teks pengganti bawaan JDateTimePicker. getItem() selalu memulangkan
     * tanggal yang sudah normal, supaya JDateTimePicker.actionPerformed tidak
     * pernah mengurai teks mentah dengan SimpleDateFormat lenient miliknya.
     */
    private final class PenyuntingTanggal implements ComboBoxEditor {

        private final JTextField teks;

        PenyuntingTanggal() {
            teks = new JTextField();
            teks.setFont(new Font("Tahoma", 0, 11));
            teks.setForeground(WARNA_NORMAL);
            teks.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 2));
            teks.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent evt) {
                    tandai();
                }

                @Override
                public void removeUpdate(DocumentEvent evt) {
                    tandai();
                }

                @Override
                public void changedUpdate(DocumentEvent evt) {
                    tandai();
                }
            });
        }

        @Override
        public Component getEditorComponent() {
            return teks;
        }

        @Override
        public void setItem(Object nilai) {
            String baru = normalkan(nilai);
            if (!baru.equals(teks.getText())) {
                teks.setText(baru);
            }
            tandai();
        }

        @Override
        public Object getItem() {
            return normalkan(teks.getText());
        }

        @Override
        public void selectAll() {
            teks.selectAll();
            teks.requestFocus();
        }

        @Override
        public void addActionListener(ActionListener pendengar) {
            teks.addActionListener(pendengar);
        }

        @Override
        public void removeActionListener(ActionListener pendengar) {
            teks.removeActionListener(pendengar);
        }

        private void tandai() {
            String isi = teks.getText().trim();
            teks.setForeground(isi.isEmpty() || null != urai(isi) ? WARNA_NORMAL : WARNA_SALAH);
        }
    }
}
