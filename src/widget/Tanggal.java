package widget;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ContainerAdapter;
import java.awt.event.ContainerEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Locale;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.border.AbstractBorder;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;
import javax.swing.plaf.metal.MetalComboBoxUI;
import javax.swing.text.JTextComponent;
import uz.ncipro.calendar.JCalendar;

public final class Tanggal extends JComboBox implements PropertyChangeListener {
    private static final long serialVersionUID = 2L;
    private static final Color AKSEN     = new Color(0x16A05D);
    private static final Color BORDER    = new Color(0x96BEA5);
    private static final Color NONAKTIF  = new Color(0xFAFCFB);
    private static final Color PEMISAH   = new Color(0xD3DDD7);
    private static final Color TINT      = new Color(0x16, 0xA0, 0x5D, 30);
    private static final Color BENING    = new Color(0, 0, 0, 0);
    private static final Color LATAR_PANAH = Color.WHITE;
    private static final Color TEKS      = new Color(0x1C2520);
    private static final int   RADIUS    = 6;
    private static final String KUNCI    = "widget.Tanggal.digayakan";

    private String displayFormat;
    private SimpleDateFormat sdf;
    protected JCalendar popupEditor;

    private boolean hover;
    private boolean hoverPanah;

    public Tanggal() {
        sdf = new SimpleDateFormat();
        setEditable(true);
        setDisplayFormat(sdf.toPattern());
        setForeground(TEKS);
        setBackground(BENING);
        setFont(new Font("Tahoma", Font.PLAIN, 11));
        gayakan();

        addContainerListener(new ContainerAdapter() {
            @Override
            public void componentAdded(ContainerEvent e) {
                gayakanAnak(e.getChild());
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        super.actionPerformed(e);
        try {
            popupEditor.setDate(sdf.parse((String) getSelectedItem()));
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent e) {
        String propertyName = e.getPropertyName();
        if (propertyName.equals("date") || propertyName.equals("day") ||
            propertyName.equals("month") || propertyName.equals("year")) {
            setValue(sdf.format(popupEditor.getDate()));
        }
    }

    @Override
    public void setLocale(Locale l) {
        super.setLocale(l);
        if (ui != null) {
            popupEditor.setLocale(l);
        }
    }

    @Override
    public void updateUI() {
        String currentLookAndFeel = UIManager.getLookAndFeel().getID();
        if (currentLookAndFeel.equals("Metal")) {
            setUI(MetalJDateTimePickerUI.createUI(this));
            popupEditor = ((MetalJDateTimePickerUI) ui).getPopupEditor();
        } else {
            setUI(BasicTanggalSmcPickerUI.createUI(this));
            popupEditor = ((BasicTanggalSmcPickerUI) ui).getPopupEditor();
        }
        popupEditor.addPropertyChangeListener(this);
        gayakan();
    }

    private void setValue(String value) {
        removeAllItems();
        addItem(value);
    }

    public Date getDate() {
        return popupEditor.getDate();
    }

    public void setDate(Date date) {
        popupEditor.setDate(date);
    }

    public LocalDate getLocalDate() {
        return getLocalDate(ZoneId.systemDefault());
    }

    public LocalDate getLocalDate(ZoneId zoneId) {
        return LocalDate.ofInstant(getDate().toInstant(), zoneId);
    }

    public String getDisplayFormat() {
        return displayFormat;
    }

    public void setDisplayFormat(String displayFormat) {
        String oldDisplayFormat = this.displayFormat;
        this.displayFormat = displayFormat;
        sdf.applyPattern(displayFormat);
        setValue(sdf.format(popupEditor.getDate()));
        firePropertyChange("displayFormat", oldDisplayFormat, displayFormat);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("DateTimePicker");
        Tanggal dtp = new Tanggal();
        dtp.setDisplayFormat("dd.MM.yyyy");
        frame.getContentPane().add(dtp);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.show();
    }

    private void gayakan() {
        setOpaque(false);
        setBorder(new BorderModern());
        for (Component c : getComponents()) {
            gayakanAnak(c);
        }
        repaint();
    }

    private void gayakanAnak(Component c) {
        if (!(c instanceof JComponent)) {
            return;
        }
        JComponent jc = (JComponent) c;
        boolean sudah = Boolean.TRUE.equals(jc.getClientProperty(KUNCI));

        if (c instanceof AbstractButton) {
            AbstractButton b = (AbstractButton) c;
            b.setBackground(BENING);
            b.setOpaque(false);
            b.setContentAreaFilled(false);
            b.setBorderPainted(false);
            b.setBorder(BorderFactory.createEmptyBorder());
            b.setFocusable(false);
            b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            if (!sudah) {
                b.addMouseListener(hoverTombol(b));
                b.addMouseListener(hoverKomponen());
            }
        } else if (c instanceof JTextComponent) {
            JTextComponent t = (JTextComponent) c;
            t.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 4));
            t.setBackground(BENING);
            t.setOpaque(false);
            t.setForeground(TEKS);
            t.setSelectionColor(new Color(0xCDEBDA));
            t.setSelectedTextColor(new Color(0x0E3B24));
            t.setCaretColor(AKSEN);
            if (!sudah) {
                t.addFocusListener(fokusRepaint());
                t.addMouseListener(hoverKomponen());
            }
        }
        jc.putClientProperty(KUNCI, Boolean.TRUE);
    }

    private MouseListener hoverTombol(final AbstractButton b) {
        return new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                hoverPanah = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hoverPanah = false;
                repaint();
            }
        };
    }

    private transient FocusListener fokusRepaintCache;
    private transient MouseListener hoverKomponenCache;

    private FocusListener fokusRepaint() {
        if (fokusRepaintCache == null) {
            fokusRepaintCache = new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) { repaint(); }

                @Override
                public void focusLost(FocusEvent e) { repaint(); }
            };
        }
        return fokusRepaintCache;
    }

    private MouseListener hoverKomponen() {
        if (hoverKomponenCache == null) {
            hoverKomponenCache = new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) { hover = true; repaint(); }

                @Override
                public void mouseExited(MouseEvent e) { hover = false; repaint(); }
            };
        }
        return hoverKomponenCache;
    }

    @Override
    public void setEnabled(boolean b) {
        super.setEnabled(b);
        for (Component c : getComponents()) {
            if (c instanceof AbstractButton) {
                c.setBackground(BENING);
            } else if (c instanceof JTextComponent) {
                c.setBackground(BENING);
            }
        }
        repaint();
    }

    private boolean fokus() {
        if (!isEnabled()) {
            return false;
        }
        if (isFocusOwner()) {
            return true;
        }
        for (Component c : getComponents()) {
            if (c.isFocusOwner()) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Rectangle r = areaPanah();
        if (r == null) {
            return;
        }
        Graphics2D g2 = (Graphics2D) g.create();
        try {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.clip(new RoundRectangle2D.Float(2, 2, getWidth() - 4, getHeight() - 4,
                    RADIUS * 2f - 2, RADIUS * 2f - 2));
            g2.clipRect(r.x, r.y, r.width, r.height);

            g2.setColor(isEnabled() ? LATAR_PANAH : NONAKTIF);
            g2.fillRect(r.x, r.y, r.width, r.height);
            if (hoverPanah && isEnabled()) {
                g2.setColor(TINT);
                g2.fillRect(r.x, r.y, r.width, r.height);
            }

            g2.setColor(PEMISAH);
            g2.drawLine(r.x, r.y + 3, r.x, r.y + r.height - 4);

            float cx = r.x + r.width / 2f + 0.5f;
            float cy = r.y + r.height / 2f + 0.5f;
            Path2D chevron = new Path2D.Float();
            chevron.moveTo(cx - 4, cy - 2);
            chevron.lineTo(cx, cy + 2);
            chevron.lineTo(cx + 4, cy - 2);
            g2.setColor(isEnabled() ? new Color(0x3E4B44) : new Color(0xB3BCB7));
            g2.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.draw(chevron);
        } finally {
            g2.dispose();
        }
    }

    private Rectangle areaPanah() {
        Insets in = getInsets();
        int lebar = getHeight() - in.top - in.bottom;
        if (lebar <= 0) {
            return null;
        }
        AbstractButton b = tombolPanah();
        if (b != null && b.isVisible() && b.getWidth() > 0) {
            lebar = Math.max(lebar, getWidth() - in.right - b.getX());
        }
        lebar = Math.min(lebar, getWidth() / 2);
        return new Rectangle(getWidth() - in.right - lebar, in.top, lebar, getHeight() - in.top - in.bottom);
    }

    @Override
    public void doLayout() {
        AbstractButton b = tombolPanah();
        if (b != null) {
            Insets in = getInsets();
            int s = getHeight() - in.top - in.bottom;
            if (s > 0) {
                Dimension d = b.isPreferredSizeSet() ? b.getPreferredSize() : null;
                if (d == null || d.width != s || d.height != s) {
                    b.setPreferredSize(new Dimension(s, s));
                }
            }
        }
        super.doLayout();
    }

    @Override
    public boolean isOptimizedDrawingEnabled() {
        return false;
    }

    @Override
    protected boolean isPaintingOrigin() {
        return true;
    }

    private AbstractButton tombolPanah() {
        for (Component c : getComponents()) {
            if (c instanceof AbstractButton) {
                return (AbstractButton) c;
            }
        }
        return null;
    }

    private class BorderModern extends AbstractBorder {

        private static final long serialVersionUID = 1L;

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();
            try {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                float arc = RADIUS * 2f;
                boolean f = fokus();

                if (f) {
                    g2.setColor(new Color(AKSEN.getRed(), AKSEN.getGreen(), AKSEN.getBlue(), 60));
                    g2.setStroke(new BasicStroke(2f));
                    g2.draw(new RoundRectangle2D.Float(x + 1, y + 1, w - 2, h - 2, arc + 1, arc + 1));
                }
                Color garis = !isEnabled() ? new Color(BORDER.getRed(), BORDER.getGreen(), BORDER.getBlue(), 130)
                        : f ? AKSEN
                        : hover ? new Color(0x5A9170)
                        : BORDER;
                g2.setColor(garis);
                g2.setStroke(new BasicStroke(1f));
                g2.draw(new RoundRectangle2D.Float(x + 1.5f, y + 1.5f, w - 3, h - 3, arc, arc));
            } finally {
                g2.dispose();
            }
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(2, 6, 2, 3);
        }

        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            insets.set(2, 6, 2, 3);
            return insets;
        }
    }

    static class BasicTanggalSmcPickerUI extends BasicComboBoxUI {
        protected JCalendar popupEditor;

        public static ComponentUI createUI(JComponent c) {
            return new BasicTanggalSmcPickerUI();
        }

        @Override
        public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {
            // prevent BasicComboBoxUI from painting gray inset behind popup
            if (!isPopupVisible(comboBox)) {
                super.paintCurrentValueBackground(g, bounds, hasFocus);
            }
        }

        @Override
        public void paint(Graphics g, JComponent c) {
            if (!isPopupVisible(comboBox)) {
                super.paint(g, c);
            }
        }

        @Override
        protected ComboPopup createPopup() {
            popupEditor = new JCalendar();
            BasicJDateTimePickerPopup popup = new BasicJDateTimePickerPopup(comboBox, popupEditor);
            popup.getAccessibleContext().setAccessibleParent(comboBox);
            return popup;
        }

        public JCalendar getPopupEditor() {
            return popupEditor;
        }
    }

    static class MetalJDateTimePickerUI extends MetalComboBoxUI {
        protected JCalendar popupEditor;

        public static ComponentUI createUI(JComponent c) {
            return new MetalJDateTimePickerUI();
        }

        @Override
        public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {
            // prevent BasicComboBoxUI from painting gray inset behind popup
            if (!isPopupVisible(comboBox)) {
                super.paintCurrentValueBackground(g, bounds, hasFocus);
            }
        }

        @Override
        public void paint(Graphics g, JComponent c) {
            if (!isPopupVisible(comboBox)) {
                super.paint(g, c);
            }
        }

        @Override
        protected ComboPopup createPopup() {
            popupEditor = new JCalendar();
            BasicJDateTimePickerPopup popup = new BasicJDateTimePickerPopup(comboBox, popupEditor);
            popup.getAccessibleContext().setAccessibleParent(comboBox);
            return popup;
        }

        public JCalendar getPopupEditor() {
            return popupEditor;
        }
    }

    static class BasicJDateTimePickerPopup extends BasicComboPopup {
        protected JCalendar popupEditor;

        @Override
        public void show() {
            if (popupEditor == null) {
                return;
            }
            syncPopupDataWithPickerData();
            popupEditor.setPreferredSize(null);
            Dimension popupSize = popupEditor.getPreferredSize();
            popupSize.setSize(popupSize.width, popupSize.width + 16);
            Rectangle popupBounds = computePopupBounds(0, comboBox.getBounds().height, popupSize.width, popupSize.height);
            popupEditor.setMaximumSize(popupBounds.getSize());
            popupEditor.setPreferredSize(popupBounds.getSize());
            popupEditor.setMinimumSize(popupBounds.getSize());
            setLightWeightPopupEnabled(comboBox.isLightWeightPopupEnabled());
            show(comboBox, popupBounds.x, popupBounds.y);
        }

        @Override
        protected void configurePopup() {
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setBorderPainted(true);
            setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.black), BorderFactory.createEmptyBorder(8, 8, 8, 8)));
            setOpaque(true);
            setDoubleBuffered(true);
            setRequestFocusEnabled(false);
        }

        public BasicJDateTimePickerPopup(JComboBox comboBox, JCalendar popupEditor) {
            super(comboBox);
            this.popupEditor = popupEditor;
            setFocusEnabled(popupEditor, false);
            add(popupEditor);
        }

        protected void setFocusEnabled(Component component, boolean flag) {
            if (component == null) {
                return;
            }
            if (component instanceof JComponent) {
                JComponent jcomponent = (JComponent) component;
                if (flag != jcomponent.isRequestFocusEnabled()) {
                    jcomponent.setRequestFocusEnabled(flag);
                }
            }
            if (component instanceof Container) {
                Component components[] = ((Container) component).getComponents();
                for (int i = 0; i < components.length; i++) {
                    setFocusEnabled(components[i], flag);
                }
            }
        }

        void syncPopupDataWithPickerData() {
            Locale l = comboBox.getLocale();
            if (!popupEditor.getLocale().equals(l)) {
                popupEditor.setLocale(l);
            }
            SimpleDateFormat sdf = new SimpleDateFormat();
            sdf.applyPattern(((Tanggal) comboBox).getDisplayFormat());
            if (!sdf.format(popupEditor.getDate()).equals(comboBox.getSelectedItem())) {
                comboBox.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, ""));
            }
        }
    }
}
