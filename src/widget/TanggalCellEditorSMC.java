package widget;

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
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.table.TableCellEditor;

public final class TanggalCellEditorSMC extends AbstractCellEditor implements TableCellEditor {

    private static final long serialVersionUID = 1L;

    public static final String FORMAT = "yyyy-MM-dd";
    public static final String KOSONG = "";

    private final DateChooser chooser;
    private final DateEditor editor;
    private final SimpleDateFormat format;

    private boolean isSetting = false;
    private boolean isEditing = false;

    public TanggalCellEditorSMC() {
        format = new SimpleDateFormat(FORMAT);
        format.setLenient(false);

        editor = new DateEditor();

        chooser = new DateChooser();
        chooser.setDisplayFormat(FORMAT);
        chooser.setEditable(true);
        chooser.setEditor(editor);
        chooser.putClientProperty("JComboBox.isTableCellEditor", Boolean.TRUE);
        chooser.addPopupMenuListener(new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent evt) {
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent evt) {
                finish();
            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent evt) {
            }
        });

        editor.addActionListener(evt -> finish());
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        String teks = normalize(value);
        Date tanggal = apply(teks);

        isSetting = true;
        try {
            chooser.setDate(null == tanggal ? new Date() : tanggal);
            chooser.removeAllItems();
            chooser.addItem(teks);
            chooser.setSelectedItem(teks);
            editor.setItem(teks);
        } finally {
            isSetting = false;
        }

        isEditing = true;
        return chooser;
    }

    @Override
    public Object getCellEditorValue() {
        return normalize(editor.getItem());
    }

    @Override
    public boolean stopCellEditing() {
        isEditing = false;
        hidePopup();
        return super.stopCellEditing();
    }

    @Override
    public void cancelCellEditing() {
        isEditing = false;
        hidePopup();
        super.cancelCellEditing();
    }

    private void finish() {
        if (isSetting || !isEditing) {
            return;
        }
        SwingUtilities.invokeLater(() -> {
            if (isEditing) {
                stopCellEditing();
            }
        });
    }

    private void hidePopup() {
        if (chooser.isPopupVisible()) {
            isSetting = true;
            try {
                chooser.hidePopup();
            } finally {
                isSetting = false;
            }
        }
    }

    private String normalize(Object nilai) {
        Date tanggal = apply(null == nilai ? null : nilai.toString());
        return null == tanggal ? KOSONG : format.format(tanggal);
    }

    private Date apply(String teks) {
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
    private final class DateChooser extends DateTimePickerSMC {

        /*
         * Serial version UID
         */
        private static final long serialVersionUID = 1L;

        DateChooser() {
            super();
            setFont(new Font("Tahoma", 0, 11));
        }

        @Override
        public void actionPerformed(ActionEvent evt) {
            setPopupVisible(false);

            ComboBoxEditor activeEditor = getEditor();
            if (null != activeEditor) {
                getModel().setSelectedItem(activeEditor.getItem());
            }

            Object selected = getSelectedItem();
            Date date = apply(null == selected ? null : selected.toString());
            if (null != date) {
                setDate(date);
            }

            String command = getActionCommand();
            setActionCommand("comboBoxEdited");
            fireActionEvent();
            setActionCommand(command);
        }
    }

    /**
     * Kotak teks pengganti bawaan JDateTimePicker. getItem() selalu memulangkan
     * tanggal yang sudah normal, supaya JDateTimePicker.actionPerformed tidak
     * pernah mengurai teks mentah dengan SimpleDateFormat lenient miliknya.
     */
    private final class DateEditor implements ComboBoxEditor {

        private final JTextField text;

        DateEditor() {
            text = new JTextField();
            text.setFont(new Font("Tahoma", 0, 11));
            text.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 2));
            text.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent evt) {
                    mark();
                }

                @Override
                public void removeUpdate(DocumentEvent evt) {
                    mark();
                }

                @Override
                public void changedUpdate(DocumentEvent evt) {
                    mark();
                }
            });
        }

        @Override
        public Component getEditorComponent() {
            return text;
        }

        @Override
        public void setItem(Object value) {
            String newValue = normalize(value);

            if (!newValue.equals(text.getText())) {
                text.setText(newValue);
            }

            mark();
        }

        @Override
        public Object getItem() {
            return normalize(text.getText());
        }

        @Override
        public void selectAll() {
            text.selectAll();
            text.requestFocus();
        }

        @Override
        public void addActionListener(ActionListener pendengar) {
            text.addActionListener(pendengar);
        }

        @Override
        public void removeActionListener(ActionListener pendengar) {
            text.removeActionListener(pendengar);
        }

        private void mark() {
            String content = text.getText().trim();
            text.setForeground(UIManager.getColor(content.isEmpty() || null != apply(content) ? "TextField.foreground" : "Component.error.focusedBorderColor"));
        }
    }
}
