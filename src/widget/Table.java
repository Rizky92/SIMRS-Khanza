package widget;

import java.awt.Component;
import java.awt.Font;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.SwingConstants;
import javax.swing.event.TableModelEvent;
import javax.swing.plaf.UIResource;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

public class Table extends JTable {
    /*    private static final long serialVersionUID = 3L;

    static final Color AKSEN        = new Color(22, 160, 93);
    static final Color PILIH_PASIF  = new Color(220, 234, 226);
    static final Color ZEBRA        = new Color(245, 247, 246);
    static final Color LAPIS_CUSTOM = new Color(22, 160, 93, 28);
    static final Color TEKS         = new Color(50, 50, 50);
    static final Color GARIS_BARIS  = new Color(228, 235, 230);
    static final Color GARIS_KOLOM  = new Color(238, 242, 239);
    static final Color TEKS_PILIH   = new Color(14, 59, 36);
    static final int   TINGGI_HEADER = 22;
    static final int   ARC_PILIH     = 8;

    private static final Border PADDING_SEL = BorderFactory.createEmptyBorder(0, 6, 0, 6);

    private boolean zebra = true;
    private JViewport viewportTerpasang;
    private final ChangeListener scrollHandler = e -> repaint();

    public Table() {
        super();
        setBackground(Color.WHITE);
        setForeground(TEKS);
        setFont(new Font("Tahoma", Font.PLAIN, 11));
        setRowHeight(24);

        setSelectionBackground(PILIH_PASIF);
        setSelectionForeground(TEKS_PILIH);

        setShowHorizontalLines(false);
        setShowVerticalLines(false);
        setIntercellSpacing(new Dimension(0, 0));
        setOpaque(false);

        getTableHeader().setForeground(new Color(50, 50, 50));
        getTableHeader().setBackground(new Color(255, 250, 250));
        getTableHeader().setBorder(BorderFactory.createEmptyBorder());
        getTableHeader().setFont(new Font("Tahoma", Font.PLAIN, 11));
        getTableHeader().setDefaultRenderer(new HeaderFlat());

        addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) { repaint(); }

            @Override
            public void focusLost(FocusEvent e) { repaint(); }
        });
    }

    @Override
    protected JTableHeader createDefaultTableHeader() {
        return new JTableHeader(columnModel) {
            private static final long serialVersionUID = 1L;

            @Override
            public Dimension getPreferredSize() {
                Dimension d = super.getPreferredSize();
                d.height = TINGGI_HEADER;
                return d;
            }
        };
    }

    @Override
    public void addNotify() {
        super.addNotify();
        if (getParent() instanceof JViewport) {
            viewportTerpasang = (JViewport) getParent();
            viewportTerpasang.addChangeListener(scrollHandler);
        }
    }

    @Override
    public void removeNotify() {
        if (viewportTerpasang != null) {
            viewportTerpasang.removeChangeListener(scrollHandler);
            viewportTerpasang = null;
        }
        super.removeNotify();
    }

    private boolean aktif() {
        return isFocusOwner() && isEnabled();
    }

    @Override
    public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
        Component c = super.prepareRenderer(renderer, row, column);
        if (renderer instanceof UIResource) {
            if (c instanceof JComponent) {
                ((JComponent) c).setOpaque(false);
                ((JComponent) c).setBorder(PADDING_SEL);
            }
            boolean pilih = isRowSelected(row) && getRowSelectionAllowed();
            c.setForeground(pilih && aktif() ? Color.WHITE : TEKS);
        }
        return c;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        try {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Rectangle clip = g2.getClipBounds();
 if (clip == null) {
                clip = new Rectangle(0, 0, getWidth(), getHeight());
            }
            Rectangle terlihat = getVisibleRect();

            g2.setColor(getBackground());
            g2.fillRect(clip.x, clip.y, clip.width, clip.height);

            int n = getRowCount();
            if (n > 0) {
                int awal = Math.max(0, rowAtPoint(new java.awt.Point(0, clip.y)));
                int akhir = rowAtPoint(new java.awt.Point(0, clip.y + clip.height - 1));
                if (akhir < 0) {
                    akhir = n - 1;
                }

                for (int row = awal; row <= akhir; row++) {
                    Rectangle r = getCellRect(row, 0, true);

                    if (zebra && row % 2 == 1) {
                        g2.setColor(ZEBRA);
                        g2.fillRect(clip.x, r.y, clip.width, r.height);
                    }

                    if (isRowSelected(row) && getRowSelectionAllowed()) {
                        g2.setColor(aktif() ? AKSEN : PILIH_PASIF);
                        g2.fill(new RoundRectangle2D.Float(terlihat.x + 4, r.y + 1,
                                terlihat.width - 8, r.height - 2, ARC_PILIH, ARC_PILIH));
                    }
                }
            }
        } finally {
            g2.dispose();
        }

        super.paintComponent(g);

        gambarGaris(g);

        int n = getRowCount();
        if (n > 0 && getColumnCount() > 0 && getRowSelectionAllowed() && getSelectedRowCount() > 0) {
            Rectangle clip = g.getClipBounds();
            if (clip == null) {
                clip = new Rectangle(0, 0, getWidth(), getHeight());
            }
            int awal = Math.max(0, rowAtPoint(new java.awt.Point(0, clip.y)));
            int akhir = rowAtPoint(new java.awt.Point(0, clip.y + clip.height - 1));
            if (akhir < 0) {
                akhir = n - 1;
            }
            Graphics2D g3 = null;
            Rectangle terlihat = getVisibleRect();
            try {
                for (int row = awal; row <= akhir; row++) {
                    if (!isRowSelected(row) || !adaRendererCustom(row)) {
                        continue;
                    }
                    if (g3 == null) {
                        g3 = (Graphics2D) g.create();
                        g3.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g3.setColor(LAPIS_CUSTOM);
                    }
                    Rectangle r = getCellRect(row, 0, true);
                    g3.fill(new RoundRectangle2D.Float(terlihat.x + 4, r.y + 1,
                            terlihat.width - 8, r.height - 2, ARC_PILIH, ARC_PILIH));
                }
            } finally {
                if (g3 != null) {
                    g3.dispose();
                }
            }
        }
    }

    private void gambarGaris(Graphics g) {
        int n = getRowCount();
        int kolom = getColumnCount();
        if (n == 0 || kolom == 0) {
            return;
        }
        Rectangle clip = g.getClipBounds();
        if (clip == null) {
            clip = new Rectangle(0, 0, getWidth(), getHeight());
        }
        int awal = Math.max(0, rowAtPoint(new java.awt.Point(0, clip.y)));
        int akhir = rowAtPoint(new java.awt.Point(0, clip.y + clip.height - 1));
        if (akhir < 0) {
            akhir = n - 1;
        }
        int kolAwal = Math.max(0, columnAtPoint(new java.awt.Point(clip.x, 0)));
        int kolAkhir = columnAtPoint(new java.awt.Point(clip.x + clip.width - 1, 0));
        if (kolAkhir < 0) {
            kolAkhir = kolom - 1;
        }
        boolean adaSeleksi = getRowSelectionAllowed();

        for (int row = awal; row <= akhir; row++) {
            if (adaSeleksi && isRowSelected(row)) {
                continue;
            }
            Rectangle r = getCellRect(row, 0, true);
            int yBawah = r.y + r.height - 1;

            g.setColor(GARIS_KOLOM);
            for (int col = kolAwal; col <= kolAkhir; col++) {
                Rectangle c = getCellRect(row, col, true);
                int x = c.x + c.width - 1;
                g.drawLine(x, r.y, x, yBawah);
            }

            if (!(adaSeleksi && row + 1 < n && isRowSelected(row + 1))) {
                g.setColor(GARIS_BARIS);
                g.drawLine(clip.x, yBawah, clip.x + clip.width, yBawah);
            }
        }
    }

    private boolean adaRendererCustom(int row) {
        for (int col = 0; col < getColumnCount(); col++) {
            if (!(getCellRenderer(row, col) instanceof UIResource)) {
                return true;
            }
        }
        return false;
    }

    private static class HeaderFlat extends DefaultTableCellRenderer {

        private static final long serialVersionUID = 1L;
        private static final Color LATAR    = new Color(255, 250, 250);
        private static final Color PEMISAH  = new Color(0xE2D9D9);
        private static final Color GARIS_BW = new Color(0xD9CFCF);
        private static final Border PADDING = BorderFactory.createEmptyBorder(0, 6, 0, 3);

        HeaderFlat() {
            setHorizontalAlignment(SwingConstants.LEFT);
            setOpaque(false);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, false, false, row, column);
            if (table != null && table.getTableHeader() != null) {
                setFont(table.getTableHeader().getFont());
                setForeground(table.getTableHeader().getForeground());
            }
            setBorder(PADDING);
            return this;
        }

        @Override
        protected void paintComponent(Graphics g) {
            int w = getWidth();
            int h = getHeight();
            g.setColor(LATAR);
            g.fillRect(0, 0, w, h);
            g.setColor(PEMISAH);
            g.drawLine(w - 1, 4, w - 1, h - 5);
            g.setColor(GARIS_BW);
            g.drawLine(0, h - 1, w, h - 1);
            super.paintComponent(g);
        }
    }

    public boolean isZebra() { return zebra; }
    public void setZebra(boolean z) { zebra = z; repaint(); }
    */

    private static final long serialVersionUID = 2L;

    private static final Pattern GROUPED_NUMBER = Pattern.compile("-?\\d{1,3}(,\\d{3})+(\\.\\d+)?");
    private static final Pattern PLAIN_NUMBER = Pattern.compile("-?\\d+(\\.\\d+)?");
    private static final String ALIGNMENT_KEY = "Table.numberAlignmentSMC";
    private static final DecimalFormat DECIMAL_FORMAT = decimalFormatSMC();

    private final Map<Integer, Boolean> numberTextColumns = new HashMap<>();

    public Table() {
        super();
        setFont(new Font("Tahoma", Font.PLAIN, 11));
        setShowGrid(true);
        getTableHeader().setBorder(BorderFactory.createEmptyBorder());
        getTableHeader().setFont(new java.awt.Font("Tahoma", 0, 11));
        getTableHeader().setReorderingAllowed(false);
        getTableHeader().setDefaultRenderer(new LeftHeaderRendererSMC(getTableHeader().getDefaultRenderer()));
    }

    /**
     * Right-aligns numbers and displays decimals in Indonesian format. Only the
     * rendered text changes; the table model keeps its original values.
     */
    @Override
    public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
        Component component = super.prepareRenderer(renderer, row, column);
        if (!(component instanceof JLabel)) {
            return component;
        }

        JLabel label = (JLabel) component;
        Object value = getValueAt(row, column);
        String text = null == value || !(String.valueOf(value).equals(label.getText()) || renderer instanceof UIResource) ? null : numberTextSMC(value, column);
        if (null != text) {
            if (null == label.getClientProperty(ALIGNMENT_KEY)) {
                label.putClientProperty(ALIGNMENT_KEY, label.getHorizontalAlignment());
            }
            label.setText(text);
            label.setHorizontalAlignment(SwingConstants.RIGHT);
        } else {
            Object alignment = label.getClientProperty(ALIGNMENT_KEY);
            if (alignment instanceof Integer) {
                if (SwingConstants.RIGHT == label.getHorizontalAlignment()) {
                    label.setHorizontalAlignment((Integer) alignment);
                }
                label.putClientProperty(ALIGNMENT_KEY, null);
            }
        }
        return component;
    }

    @Override
    public void tableChanged(TableModelEvent e) {
        super.tableChanged(e);
        if (null != numberTextColumns) {
            numberTextColumns.clear();
        }
    }

    private String numberTextSMC(Object value, int column) {
        if (value instanceof Double || value instanceof Float || value instanceof BigDecimal) {
            double number = ((Number) value).doubleValue();
            return Double.isNaN(number) || Double.isInfinite(number) ? null : DECIMAL_FORMAT.format(value);
        }
        if (value instanceof Integer || value instanceof Long || value instanceof Short || value instanceof Byte || value instanceof BigInteger) {
            return value.toString();
        }
        if (value instanceof String) {
            String text = ((String) value).trim();
            if (!text.isEmpty() && isNumberTextColumnSMC(column) && PLAIN_NUMBER.matcher(text.replace(",", "")).matches()) {
                return indonesianNumberSMC(text.replace(",", ""));
            }
        }
        return null;
    }

    private boolean isNumberTextColumnSMC(int column) {
        int modelColumn = convertColumnIndexToModel(column);
        Boolean cached = numberTextColumns.get(modelColumn);
        if (null == cached) {
            cached = scanNumberTextColumnSMC(getModel(), modelColumn);
            numberTextColumns.put(modelColumn, cached);
        }
        return cached;
    }

    private static boolean scanNumberTextColumnSMC(TableModel model, int column) {
        boolean grouped = false;
        for (int row = 0; row < model.getRowCount(); row++) {
            Object value = model.getValueAt(row, column);
            if (null == value || value instanceof Number) {
                continue;
            }
            String text = value.toString().trim();
            if (text.isEmpty()) {
                continue;
            }
            if (GROUPED_NUMBER.matcher(text).matches()) {
                grouped = true;
            } else if (!PLAIN_NUMBER.matcher(text).matches()) {
                return false;
            }
        }
        return grouped;
    }

    private static String indonesianNumberSMC(String number) {
        boolean negative = number.startsWith("-");
        String digits = negative ? number.substring(1) : number;
        int dot = digits.indexOf('.');
        String integer = dot < 0 ? digits : digits.substring(0, dot);
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < integer.length(); i++) {
            if (i > 0 && 0 == (integer.length() - i) % 3) {
                result.append('.');
            }
            result.append(integer.charAt(i));
        }
        if (dot >= 0) {
            result.append(',').append(digits.substring(dot + 1));
        }
        return (negative ? "-" : "") + result;
    }

    private static DecimalFormat decimalFormatSMC() {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator('.');
        symbols.setDecimalSeparator(',');
        symbols.setMinusSign('-');
        return new DecimalFormat("#,##0.###", symbols);
    }

    private static class LeftHeaderRendererSMC implements TableCellRenderer {
        private final TableCellRenderer delegate;

        LeftHeaderRendererSMC(TableCellRenderer delegate) {
            this.delegate = delegate;
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component component = delegate.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (component instanceof JLabel) {
                ((JLabel) component).setHorizontalAlignment(SwingConstants.LEADING);
            }

            return component;
        }
    }
}
