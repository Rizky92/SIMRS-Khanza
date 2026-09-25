package widget;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.ui.FlatComboBoxUI;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import javax.swing.AbstractSpinnerModel;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.ComboBoxUI;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;

/**
 * Editable combo box that picks a date from a calendar popup.
 * <p>
 * Re-implementation of {@code uz.ncipro.calendar.JDateTimePicker}, keeping its
 * contract so existing forms behave the same:
 * <ul>
 *   <li>the selected item is the date formatted with {@link #getDisplayFormat()};</li>
 *   <li>every date change replaces the single item, firing item and action events;</li>
 *   <li>edited text is parsed leniently when the editor commits (enter, focus lost,
 *       or opening the popup), and unparseable text keeps the previous date;</li>
 *   <li>clicking a day closes the popup; the month and year spinners (arrows or
 *       mouse wheel) keep it open and change the date immediately.</li>
 * </ul>
 * The combo box itself is drawn by the installed look and feel, so it follows the
 * FlatLaf theme; only the popup content is custom.
 *
 * @author smc
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class DateTimePickerSMC extends JComboBox {

    private static final long serialVersionUID = 1L;

    private static final int DAYS_IN_WEEK = 7;
    private static final int WEEKS_IN_MONTH = 6;

    private SimpleDateFormat format;
    private String displayFormat;
    private Calendar calendar;

    public DateTimePickerSMC() {
        super();
        format = new SimpleDateFormat();
        setEditable(true);
        setDisplayFormat(format.toPattern());
    }

    @Override
    public void updateUI() {
        ComboBoxUI ui = UIManager.getLookAndFeel() instanceof FlatLaf ? new FlatPickerUI() : new BasicPickerUI();
        setUI(ui);
    }

    @Override
    public void setLocale(Locale locale) {
        super.setLocale(locale);
        Date date = calendar().getTime();
        calendar = Calendar.getInstance(locale);
        calendar.setTime(date);
    }

    /**
     * Returns the date of this component.
     *
     * @return the value of the date property
     */
    public Date getDate() {
        return calendar().getTime();
    }

    /**
     * Sets the date and replaces the displayed text with it.
     *
     * @param date the new date value, must not be {@code null}
     */
    public void setDate(Date date) {
        calendar().setTime(date);
        refreshValue();
    }

    /**
     * Returns the pattern used to display the date.
     *
     * @return the value of the displayFormat property
     */
    public String getDisplayFormat() {
        return displayFormat;
    }

    /**
     * Sets the {@link SimpleDateFormat} pattern used to display and parse the date.
     *
     * @param displayFormat the new date and time pattern
     */
    public void setDisplayFormat(String displayFormat) {
        String oldDisplayFormat = this.displayFormat;
        this.displayFormat = displayFormat;
        format.applyPattern(displayFormat);
        refreshValue();
        firePropertyChange("displayFormat", oldDisplayFormat, displayFormat);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        super.actionPerformed(e);
        try {
            setDate(format.parse((String) getSelectedItem()));
        } catch (Exception ex) {
        }
    }

    private Calendar calendar() {
        if (null == calendar) {
            calendar = Calendar.getInstance(getLocale());
        }
        return calendar;
    }

    private void refreshValue() {
        if (null == format) {
            return;
        }
        removeAllItems();
        addItem(format.format(calendar().getTime()));
    }

    private void addToDate(int field, int amount) {
        calendar().add(field, amount);
        refreshValue();
    }

    private void setDay(int day) {
        calendar().set(Calendar.DAY_OF_MONTH, day);
        refreshValue();
    }

    private boolean isFormattedDateShown() {
        return format.format(calendar().getTime()).equals(getSelectedItem());
    }

    private static class FlatPickerUI extends FlatComboBoxUI {

        @Override
        protected ComboPopup createPopup() {
            return new CalendarPopup(comboBox);
        }
    }

    private static class BasicPickerUI extends BasicComboBoxUI {

        @Override
        protected ComboPopup createPopup() {
            return new CalendarPopup(comboBox);
        }
    }

    /**
     * Popup hosting the month calendar, replacing the list of a regular combo box.
     */
    private static class CalendarPopup extends BasicComboPopup {

        private static final long serialVersionUID = 1L;

        private final DateTimePickerSMC picker;
        private final JSpinner month;
        private final JSpinner year;
        private final JLabel[] weekdays;
        private final JButton[] days;
        private final Color dayBackground;
        private final Font dayFont;

        CalendarPopup(JComboBox comboBox) {
            super(comboBox);
            picker = (DateTimePickerSMC) comboBox;

            month = spinner(Calendar.MONTH, "Bulan", 9);
            year = spinner(Calendar.YEAR, "Tahun", 4);

            JPanel header = new JPanel(new BorderLayout(4, 0));
            header.setOpaque(false);
            header.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 0));
            header.add(month, BorderLayout.CENTER);
            header.add(year, BorderLayout.EAST);

            JPanel grid = new JPanel(new GridLayout(WEEKS_IN_MONTH + 1, DAYS_IN_WEEK, 2, 2));
            grid.setOpaque(false);
            weekdays = new JLabel[DAYS_IN_WEEK];
            for (int i = 0; i < DAYS_IN_WEEK; i++) {
                weekdays[i] = new JLabel("", SwingConstants.CENTER);
                grid.add(weekdays[i]);
            }
            days = new JButton[DAYS_IN_WEEK * WEEKS_IN_MONTH];
            for (int i = 0; i < days.length; i++) {
                JButton day = flatButton();
                day.addActionListener(e -> {
                    picker.setDay(Integer.parseInt(day.getText()));
                    picker.setPopupVisible(false);
                });
                days[i] = day;
                grid.add(day);
            }
            dayBackground = days[0].getBackground();
            dayFont = days[0].getFont();

            JPanel content = new JPanel(new BorderLayout());
            content.setOpaque(false);
            content.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
            content.add(header, BorderLayout.NORTH);
            content.add(grid, BorderLayout.CENTER);
            add(content, BorderLayout.CENTER);
        }

        @Override
        protected void configurePopup() {
            setLayout(new BorderLayout());
            setBorderPainted(true);
            Border border = UIManager.getBorder("PopupMenu.border");
            setBorder(null == border ? BorderFactory.createLineBorder(UIManager.getColor("Component.borderColor")) : border);
            setOpaque(true);
            setDoubleBuffered(true);
            setFocusable(false);
        }

        @Override
        public void show() {
            if (!picker.isFormattedDateShown()) {
                picker.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, ""));
            }
            refresh();
            Dimension size = getPreferredSize();
            Rectangle bounds = computePopupBounds(0, comboBox.getHeight(), size.width, size.height);
            setLightWeightPopupEnabled(comboBox.isLightWeightPopupEnabled());
            show(comboBox, bounds.x, bounds.y);
        }

        private void refresh() {
            Locale locale = picker.getLocale();
            Calendar selected = picker.calendar();
            DateFormatSymbols symbols = DateFormatSymbols.getInstance(locale);
            ((FieldModel) month.getModel()).changed();
            ((FieldModel) year.getModel()).changed();

            String[] names = symbols.getShortWeekdays();
            int firstDayOfWeek = selected.getFirstDayOfWeek();
            for (int i = 0; i < DAYS_IN_WEEK; i++) {
                int weekday = (firstDayOfWeek - 1 + i) % DAYS_IN_WEEK + 1;
                weekdays[i].setText(names[weekday]);
                weekdays[i].setForeground(UIManager.getColor(Calendar.SUNDAY == weekday ? "DateTimePicker.sundayForeground" : "Label.disabledForeground"));
            }

            Calendar cursor = (Calendar) selected.clone();
            cursor.set(Calendar.DAY_OF_MONTH, 1);
            int offset = (cursor.get(Calendar.DAY_OF_WEEK) - firstDayOfWeek + DAYS_IN_WEEK) % DAYS_IN_WEEK;
            int length = cursor.getActualMaximum(Calendar.DAY_OF_MONTH);
            int day = selected.get(Calendar.DAY_OF_MONTH);
            Calendar today = Calendar.getInstance(locale);
            boolean currentMonth = today.get(Calendar.YEAR) == selected.get(Calendar.YEAR) && today.get(Calendar.MONTH) == selected.get(Calendar.MONTH);

            Color accent = UIManager.getColor("Component.accentColor");
            Color selectionBackground = UIManager.getColor("List.selectionBackground");
            Color selectionForeground = UIManager.getColor("List.selectionForeground");
            Color foreground = UIManager.getColor("Button.foreground");
            Font font = dayFont;
            for (int i = 0; i < days.length; i++) {
                JButton button = days[i];
                int number = i - offset + 1;
                boolean visible = number >= 1 && number <= length;
                button.setVisible(visible);
                if (!visible) {
                    continue;
                }
                button.setText(Integer.toString(number));
                boolean isSelected = number == day;
                boolean isToday = currentMonth && number == today.get(Calendar.DAY_OF_MONTH);
                button.setFont(isToday ? font.deriveFont(Font.BOLD) : font);
                button.setBackground(isSelected ? selectionBackground : dayBackground);
                button.setForeground(isSelected ? selectionForeground : isToday && null != accent ? accent : foreground);
            }
        }

        private JSpinner spinner(int field, String toolTip, int columns) {
            JSpinner spinner = new JSpinner(new FieldModel(field));
            spinner.setToolTipText(toolTip);
            spinner.setFocusable(false);
            spinner.setRequestFocusEnabled(false);
            for (Component child : spinner.getComponents()) {
                child.setFocusable(false);
                if (child instanceof JComponent) {
                    ((JComponent) child).setRequestFocusEnabled(false);
                }
            }
            JFormattedTextField text = ((JSpinner.DefaultEditor) spinner.getEditor()).getTextField();
            text.setEditable(false);
            text.setFocusable(false);
            text.setColumns(columns);
            text.setHorizontalAlignment(Calendar.YEAR == field ? JTextField.RIGHT : JTextField.LEFT);
            Color background = UIManager.getColor("TextField.background");
            if (null != background) {
                text.setBackground(background);
            }
            spinner.addMouseWheelListener(e -> {
                if (0 != e.getWheelRotation()) {
                    picker.addToDate(field, e.getWheelRotation() < 0 ? 1 : -1);
                    refresh();
                }
            });
            return spinner;
        }

        private static JButton flatButton() {
            JButton button = new JButton();
            button.putClientProperty("JButton.buttonType", "toolBarButton");
            button.setMargin(new Insets(2, 2, 2, 2));
            button.setPreferredSize(new Dimension(28, 22));
            button.setFocusable(false);
            button.setRequestFocusEnabled(false);
            button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            return button;
        }

        /**
         * Spinner model stepping one calendar field of the picker. The value is the
         * displayed text; next and previous values are the step to add, so month
         * steps roll over into the next or previous year.
         */
        private final class FieldModel extends AbstractSpinnerModel {

            private static final long serialVersionUID = 1L;

            private final int field;

            FieldModel(int field) {
                this.field = field;
            }

            @Override
            public Object getValue() {
                Calendar selected = picker.calendar();
                if (Calendar.MONTH == field) {
                    return DateFormatSymbols.getInstance(picker.getLocale()).getMonths()[selected.get(Calendar.MONTH)];
                }
                return Integer.toString(selected.get(field));
            }

            @Override
            public void setValue(Object value) {
                if (value instanceof Integer) {
                    picker.addToDate(field, (Integer) value);
                    refresh();
                }
            }

            @Override
            public Object getNextValue() {
                return 1;
            }

            @Override
            public Object getPreviousValue() {
                return -1;
            }

            void changed() {
                fireStateChanged();
            }
        }
    }
}
