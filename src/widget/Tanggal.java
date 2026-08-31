/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package widget;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;
import javax.swing.plaf.metal.MetalComboBoxUI;
import uz.ncipro.calendar.JCalendar;

/**
 *
 * @author khanzasoft
 */
public final class Tanggal extends JComboBox implements PropertyChangeListener {
    private String displayFormat;
    private SimpleDateFormat sdf;
    protected JCalendar popupEditor;

    public Tanggal() {
        sdf = new SimpleDateFormat();
        setEditable(true);
        setDisplayFormat(sdf.toPattern());
        //setBackground(new Color(245,160,245));
        //setForeground(new Color(90,90,90));
        setForeground(new Color(50, 50, 50));
        setBackground(new Color(255, 255, 255));
        setFont(new java.awt.Font("Tahoma", 0, 11));
        //setBorder(javax.swing.BorderFactory.createLineBorder(new Color(212,212,152)));
        setSize(WIDTH, 23);
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
