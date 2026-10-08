package widget;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.icons.FlatCheckBoxIcon;
import com.formdev.flatlaf.ui.FlatComboBoxUI;
import com.formdev.flatlaf.ui.FlatEmptyBorder;
import com.formdev.flatlaf.ui.FlatOptionPaneUI;
import com.formdev.flatlaf.ui.FlatScrollBarUI;
import com.formdev.flatlaf.ui.FlatScrollPaneBorder;
import com.formdev.flatlaf.ui.FlatTabbedPaneUI;
import com.formdev.flatlaf.ui.FlatTableHeaderBorder;
import com.formdev.flatlaf.ui.FlatTableUI;
import com.formdev.flatlaf.ui.FlatTextBorder;
import com.formdev.flatlaf.ui.FlatUIUtils;
import com.formdev.flatlaf.util.UIScale;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Rectangle;
import java.beans.PropertyChangeListener;
import java.util.IdentityHashMap;
import java.util.Map;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.ComboPopup;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.text.JTextComponent;

/**
 * Application look and feel, based on FlatLaf Light.
 * <p>
 * Theme values live in {@code LookAndFeelSMC.properties}, which FlatLaf loads
 * automatically because it sits next to this class. This class adds what a
 * properties file cannot express:
 * <ul>
 *   <li>a larger check box icon;</li>
 *   <li>read-only text fields and text areas drawn with the disabled border color;</li>
 *   <li>scroll panes around a table drawn with {@code ScrollPane.viewBorderColor};</li>
 *   <li>table header column separators that stop short of the top and bottom edges;</li>
 *   <li>extra padding around combo box popup items, apart from the padding of the
 *       combo box itself;</li>
 *   <li>dialog components, such as the choice list of an input dialog or a check
 *       box in an {@code Object[]} message, drawn in the message font, and custom
 *       option buttons drawn in the button font;</li>
 *   <li>the selected tab keeping its background while hovered, and unselected tabs
 *       drawn with their own background and outline;</li>
 *   <li>a scroll bar thumb that thickens while hovered or dragged, beside a
 *       separator line along the track;</li>
 *   <li>check boxes in table cells centered horizontally and drawn with the
 *       larger check box icon, while editing too.</li>
 * </ul>
 * The default font is Tahoma 11, the font every form is laid out for; its digits
 * are already fixed width.
 *
 * @author smc
 */
public class LookAndFeelSMC extends FlatLightLaf {

    public static final String NAME = "SIMRS Khanza";

    private static final float CHECK_BOX_ICON_SCALE = 1.15f;

    /**
     * Installs this look and feel. Call once, before any window is created.
     *
     * @return {@code true} when the look and feel was installed
     */
    public static boolean setup() {
        if (null == System.getProperty("flatlaf.useWindowDecorations")) {
            System.setProperty("flatlaf.useWindowDecorations", "false");
        }

        return FlatLaf.setup(new LookAndFeelSMC());
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getDescription() {
        return "SIMRS Khanza look and feel, based on FlatLaf Light";
    }

    @Override
    public UIDefaults getDefaults() {
        UIDefaults defaults = super.getDefaults();
        defaults.put("CheckBox.icon", (UIDefaults.LazyValue) table -> new CheckBoxIcon());
        defaults.put("TextField.border", (UIDefaults.LazyValue) table -> new TextBorder());
        defaults.put("FormattedTextField.border", (UIDefaults.LazyValue) table -> new TextBorder());
        defaults.put("PasswordField.border", (UIDefaults.LazyValue) table -> new TextBorder());
        defaults.put("ScrollPane.border", (UIDefaults.LazyValue) table -> new ScrollPaneBorder());
        defaults.put("Table.scrollPaneBorder", (UIDefaults.LazyValue) table -> new ViewBorder());
        defaults.put("TableHeader.cellBorder", (UIDefaults.LazyValue) table -> new TableHeaderBorder());
        defaults.put("ComboBoxUI", ComboBoxUI.class.getName());
        defaults.put(ComboBoxUI.class.getName(), ComboBoxUI.class);
        defaults.put("OptionPaneUI", OptionPaneUI.class.getName());
        defaults.put(OptionPaneUI.class.getName(), OptionPaneUI.class);
        defaults.put("TabbedPaneUI", TabbedPaneUI.class.getName());
        defaults.put(TabbedPaneUI.class.getName(), TabbedPaneUI.class);
        defaults.put("ScrollBarUI", ScrollBarUI.class.getName());
        defaults.put(ScrollBarUI.class.getName(), ScrollBarUI.class);
        defaults.put("TableUI", TableUI.class.getName());
        defaults.put(TableUI.class.getName(), TableUI.class);

        return defaults;
    }

    /**
     * FlatLaf check box icon, scaled up slightly because the default is too small.
     */
    public static class CheckBoxIcon extends FlatCheckBoxIcon {

        public CheckBoxIcon() {
            super();
            setScale(CHECK_BOX_ICON_SCALE);
        }
    }

    private static boolean isEditable(Component c) {
        return !(c instanceof JTextComponent) || ((JTextComponent) c).isEditable();
    }

    /**
     * Text field border that uses the disabled border color for read-only fields.
     */
    public static class TextBorder extends FlatTextBorder {

        @Override
        protected boolean isEnabled(Component c) {
            return super.isEnabled(c) && isEditable(c);
        }
    }

    /**
     * Scroll pane border that uses the disabled border color when its view is a
     * read-only text component, such as a non-editable text area.
     */
    public static class ScrollPaneBorder extends FlatScrollPaneBorder {

        @Override
        protected boolean isEnabled(Component c) {
            return super.isEnabled(c) && isEditable(c);
        }
    }

    /**
     * Scroll pane border drawn with {@code ScrollPane.viewBorderColor}, for scroll
     * panes around a view such as a table. {@code JTable} puts it on its enclosing
     * scroll pane as {@code Table.scrollPaneBorder}, replacing any border that is a
     * {@code UIResource}, which every FlatLaf border is.
     */
    public static class ViewBorder extends FlatScrollPaneBorder {

        public ViewBorder() {
            super();
            Color color = UIManager.getColor("ScrollPane.viewBorderColor");
            if (null != color) {
                borderColor = color;
            }
        }
    }

    /**
     * FlatLaf table header cell border whose column separator lines stop
     * {@code TableHeader.separatorInset} pixels short of the top and bottom
     * edges, so they stand apart from the bottom separator line.
     */
    public static class TableHeaderBorder extends FlatTableHeaderBorder {

        private final int separatorInset = UIManager.getInt("TableHeader.separatorInset");

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            int inset = UIScale.scale(separatorInset);
            int lineWidth = UIScale.scale(1);

            if (inset <= 0 || height <= inset * 2) {
                super.paintBorder(c, g, x, y, width, height);
                return;
            }

            Graphics separator = g.create();
            try {
                separator.clipRect(x, y + inset, width, height - inset * 2);
                super.paintBorder(c, separator, x, y, width, height);
            } finally {
                separator.dispose();
            }

            Graphics bottom = g.create();
            try {
                bottom.clipRect(x, y + height - lineWidth, width, lineWidth);
                super.paintBorder(c, bottom, x, y, width, height);
            } finally {
                bottom.dispose();
            }
        }
    }

    /**
     * FlatLaf combo box UI that pads each popup item by
     * {@code ComboBox.popupItemInsets}. FlatLaf pads popup items with
     * {@code ComboBox.padding}, the same padding as the combo box itself, so a
     * compact combo box would otherwise get equally compact popup items.
     */
    public static class ComboBoxUI extends FlatComboBoxUI {

        private Insets popupItemInsets;

        public static ComponentUI createUI(JComponent c) {
            return new ComboBoxUI();
        }

        @Override
        protected void installDefaults() {
            super.installDefaults();
            popupItemInsets = UIManager.getInsets("ComboBox.popupItemInsets");
        }

        @Override
        protected ComboPopup createPopup() {
            return new PaddedComboPopup(comboBox);
        }

        /**
         * FlatLaf combo popup whose list renderer is wrapped in a
         * {@link PaddedItemRenderer}, again whenever the combo box renderer changes.
         */
        protected class PaddedComboPopup extends FlatComboPopup {

            protected PaddedComboPopup(JComboBox<?> combo) {
                super(combo);
            }

            @Override
            protected void configurePopup() {
                super.configurePopup();
                wrapCellRenderer();
            }

            @Override
            protected PropertyChangeListener createPropertyChangeListener() {
                PropertyChangeListener superListener = super.createPropertyChangeListener();
                return e -> {
                    superListener.propertyChange(e);

                    if ("renderer".equals(e.getPropertyName())) {
                        wrapCellRenderer();
                    }
                };
            }

            private void wrapCellRenderer() {
                ListCellRenderer<? super Object> renderer = list.getCellRenderer();
                if (!(renderer instanceof PaddedItemRenderer)) {
                    list.setCellRenderer(new PaddedItemRenderer(renderer));
                }
            }
        }

        /**
         * Renders a popup item inside a panel whose border adds the item padding.
         * The panel takes the item background, so a selected item stays highlighted
         * across the whole row.
         */
        private class PaddedItemRenderer implements ListCellRenderer<Object> {

            private final ListCellRenderer<? super Object> renderer;
            private final JPanel item = new JPanel(new BorderLayout());

            PaddedItemRenderer(ListCellRenderer<? super Object> renderer) {
                this.renderer = renderer;

                if (null != popupItemInsets) {
                    item.setBorder(new FlatEmptyBorder(popupItemInsets));
                }
            }

            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                @SuppressWarnings("unchecked")
                Component c = renderer.getListCellRendererComponent((JList<Object>) list, value, index, isSelected, cellHasFocus);

                if (index < 0 || null == item.getBorder()) {
                    return c;
                }

                if (c.getParent() != item) {
                    item.removeAll();
                    item.add(c, BorderLayout.CENTER);
                }

                item.setOpaque(c.isOpaque());
                item.setBackground(c.getBackground());
                item.setComponentOrientation(c.getComponentOrientation());

                return item;
            }
        }
    }

    /**
     * FlatLaf option pane UI that draws the components of a dialog in
     * {@code OptionPane.messageFont}, like its text: the input field that
     * {@code JOptionPane.showInputDialog} creates on its own, and components the
     * caller puts in the message, such as a check box in an {@code Object[]}
     * message. Buttons the caller passes as options get
     * {@code OptionPane.buttonFont}, like the buttons the option pane creates.
     * A component keeps a font that the caller set explicitly; only the look and
     * feel default is replaced.
     */
    public static class OptionPaneUI extends FlatOptionPaneUI {

        private Font messageFont;
        private Font buttonFont;

        public static ComponentUI createUI(JComponent c) {
            return new OptionPaneUI();
        }

        @Override
        protected void installDefaults() {
            super.installDefaults();
            messageFont = UIManager.getFont("OptionPane.messageFont");
            buttonFont = UIManager.getFont("OptionPane.buttonFont");
        }

        @Override
        protected Object getMessage() {
            Object message = super.getMessage();
            applyFont(inputComponent, messageFont);

            return message;
        }

        @Override
        protected void addMessageComponents(Container container, GridBagConstraints cons, Object msg, int maxll, boolean internallyCreated) {
            if (msg instanceof Component) {
                applyFont((Component) msg, messageFont);
            }

            super.addMessageComponents(container, cons, msg, maxll, internallyCreated);
        }

        @Override
        protected void addButtonComponents(Container container, Object[] buttons, int initialIndex) {
            if (null != buttons) {
                for (Object button : buttons) {
                    if (button instanceof Component) {
                        applyFont((Component) button, buttonFont);
                    }
                }
            }

            super.addButtonComponents(container, buttons, initialIndex);
        }

        private static void applyFont(Component c, Font font) {
            if (null == c || null == font) {
                return;
            }

            Font current = c.getFont();
            if (null == current || current instanceof UIResource) {
                c.setFont(font);
            }
        }
    }

    /**
     * FlatLaf tabbed pane UI where the hover color does not replace the solid
     * background of the selected tab, and unselected tabs get the
     * {@code TabbedPane.unselectedBackground} color with an outline in the
     * content area color, so they stand apart from a white tab area.
     */
    public static class TabbedPaneUI extends FlatTabbedPaneUI {

        private Color unselectedBackground;

        public static ComponentUI createUI(JComponent c) {
            return new TabbedPaneUI();
        }

        @Override
        protected void installDefaults() {
            super.installDefaults();
            unselectedBackground = UIManager.getColor("TabbedPane.unselectedBackground");
        }

        @Override
        protected Color getTabBackground(int tabPlacement, int tabIndex, boolean isSelected) {
            if (isSelected && tabPane.isEnabled() && tabPane.isEnabledAt(tabIndex) && tabPane.getBackgroundAt(tabIndex) == tabPane.getBackground()) {
                if (null != focusColor && FlatUIUtils.isPermanentFocusOwner(tabPane)) {
                    return focusColor;
                }

                if (null != selectedBackground) {
                    return selectedBackground;
                }
            }

            if (!isSelected && null != unselectedBackground && getRolloverTab() != tabIndex && tabPane.getBackgroundAt(tabIndex) == tabPane.getBackground()) {
                return unselectedBackground;
            }

            return super.getTabBackground(tabPlacement, tabIndex, isSelected);
        }

        @Override
        protected void paintTabBorder(Graphics g, int tabPlacement, int tabIndex, int x, int y, int w, int h, boolean isSelected) {
            super.paintTabBorder(g, tabPlacement, tabIndex, x, y, w, h, isSelected);

            if (isSelected || null == contentAreaColor) {
                return;
            }

            boolean first = tabRuns[getRunForTab(tabPane.getTabCount(), tabIndex)] == tabIndex;
            g.setColor(contentAreaColor);

            switch (tabPlacement) {
                case LEFT:
                    g.fillRect(x, y, 1, h);
                    g.fillRect(x, y + h - 1, w, 1);
                    if (first) {
                        g.fillRect(x, y, w, 1);
                    }
                    break;
                case RIGHT:
                    g.fillRect(x + w - 1, y, 1, h);
                    g.fillRect(x, y + h - 1, w, 1);
                    if (first) {
                        g.fillRect(x, y, w, 1);
                    }
                    break;
                case BOTTOM:
                    g.fillRect(x, y + h - 1, w, 1);
                    g.fillRect(x + w - 1, y, 1, h);
                    if (first) {
                        g.fillRect(x, y, 1, h);
                    }
                    break;
                default:
                    g.fillRect(x, y, w, 1);
                    g.fillRect(x + w - 1, y, 1, h);
                    if (first) {
                        g.fillRect(x, y, 1, h);
                    }
                    break;
            }
        }
    }

    /**
     * FlatLaf scroll bar UI whose thumb uses {@code ScrollBar.hoverThumbInsets}
     * instead of {@code ScrollBar.thumbInsets} while it is hovered or dragged, so
     * it grows thicker, and whose track has a {@code ScrollBar.trackSeparatorColor}
     * line along the side facing the view. Both insets are given for the vertical
     * scroll bar, with the left side facing the view; the horizontal scroll bar
     * faces the view with its top side.
     */
    public static class ScrollBarUI extends FlatScrollBarUI {

        private Insets hoverThumbInsets;
        private Color trackSeparatorColor;

        public static ComponentUI createUI(JComponent c) {
            return new ScrollBarUI();
        }

        @Override
        protected void installDefaults() {
            super.installDefaults();
            hoverThumbInsets = UIManager.getInsets("ScrollBar.hoverThumbInsets");
            trackSeparatorColor = UIManager.getColor("ScrollBar.trackSeparatorColor");
        }

        @Override
        protected void uninstallDefaults() {
            super.uninstallDefaults();
            hoverThumbInsets = null;
            trackSeparatorColor = null;
        }

        @Override
        protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
            super.paintTrack(g, c, trackBounds);

            if (trackBounds.isEmpty() || null == trackSeparatorColor) {
                return;
            }

            g.setColor(trackSeparatorColor);

            if (JScrollBar.VERTICAL == scrollbar.getOrientation()) {
                g.fillRect(trackBounds.x, trackBounds.y, 1, trackBounds.height);
            } else {
                g.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, 1);
            }
        }

        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
            if (thumbBounds.isEmpty() || !scrollbar.isEnabled()) {
                return;
            }

            boolean active = hoverThumb || isDragging;
            Insets insets = active && null != hoverThumbInsets ? hoverThumbInsets : thumbInsets;

            if (JScrollBar.HORIZONTAL == scrollbar.getOrientation()) {
                insets = new Insets(insets.top, insets.right, insets.bottom, insets.left);
            }

            g.setColor(getThumbColor(c, hoverThumb, isDragging));
            paintTrackOrThumb(g, c, thumbBounds, insets, thumbArc);
        }
    }

    /**
     * FlatLaf table UI that renders {@code Boolean} cells with a
     * {@link BooleanRenderer} in place of the FlatLaf boolean renderer, whose
     * check box follows the uneven {@code Table.cellMargins} and sits right of
     * the cell center.
     */
    public static class TableUI extends FlatTableUI {

        private TableCellRenderer flatBooleanRenderer;

        public static ComponentUI createUI(JComponent c) {
            return new TableUI();
        }

        @Override
        protected void installDefaults() {
            super.installDefaults();

            TableCellRenderer renderer = table.getDefaultRenderer(Boolean.class);
            if (renderer instanceof UIResource && !(renderer instanceof BooleanRenderer)) {
                flatBooleanRenderer = renderer;
                table.setDefaultRenderer(Boolean.class, new BooleanRenderer());
            }
        }

        @Override
        protected void uninstallDefaults() {
            if (null != flatBooleanRenderer && table.getDefaultRenderer(Boolean.class) instanceof BooleanRenderer) {
                table.setDefaultRenderer(Boolean.class, flatBooleanRenderer);
            }
            flatBooleanRenderer = null;

            super.uninstallDefaults();
        }
    }

    /**
     * Table cell renderer for {@code Boolean} values that draws the
     * {@link CheckBoxIcon} at the horizontal center of the cell. Its cell border
     * is wrapped in a {@link CenteredBorder}. {@code DefaultCellEditor} copies
     * the renderer border onto the editing check box, so the check box stays in
     * place while the cell is edited.
     */
    public static class BooleanRenderer extends DefaultTableCellRenderer implements UIResource {

        private final Map<Border, Border> centeredBorders = new IdentityHashMap<>();
        private boolean selected;

        public BooleanRenderer() {
            super();
            setHorizontalAlignment(CENTER);

            CheckBoxIcon icon = new CheckBoxIcon() {
                @Override
                protected boolean isSelected(Component c) {
                    return selected;
                }
            };
            setIcon(icon);
            setDisabledIcon(icon);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            Border border = getBorder();
            if (null != border && !(border instanceof CenteredBorder)) {
                setBorder(centeredBorders.computeIfAbsent(border, CenteredBorder::new));
            }

            return this;
        }

        @Override
        protected void setValue(Object value) {
            selected = Boolean.TRUE.equals(value);
        }
    }

    /**
     * Border that paints like the border it wraps but splits the left and right
     * insets evenly between both sides, so content centered within the insets is
     * centered within the component.
     */
    private static class CenteredBorder implements Border, UIResource {

        private final Border border;

        CenteredBorder(Border border) {
            this.border = border;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            border.paintBorder(c, g, x, y, width, height);
        }

        @Override
        public Insets getBorderInsets(Component c) {
            Insets insets = border.getBorderInsets(c);
            int side = (insets.left + insets.right) / 2;

            return new Insets(insets.top, side, insets.bottom, side);
        }

        @Override
        public boolean isBorderOpaque() {
            return border.isBorderOpaque();
        }
    }
}
