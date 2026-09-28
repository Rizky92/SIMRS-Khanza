package widget;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.icons.FlatCheckBoxIcon;
import com.formdev.flatlaf.ui.FlatScrollPaneBorder;
import com.formdev.flatlaf.ui.FlatTabbedPaneUI;
import com.formdev.flatlaf.ui.FlatTextBorder;
import com.formdev.flatlaf.ui.FlatUIUtils;
import com.formdev.flatlaf.util.SystemInfo;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Toolkit;
import javax.swing.JComponent;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
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
 *   <li>the selected tab keeping its background while hovered, and unselected tabs
 *       drawn with their own background and outline;</li>
 *   <li>the native scroll bar on Windows.</li>
 * </ul>
 * The default font is Tahoma 11, the font every form is laid out for; its digits
 * are already fixed width.
 *
 * @author smc
 */
public class LookAndFeelSMC extends FlatLightLaf {

    public static final String NAME = "SIMRS Khanza";

    private static final String WINDOWS_SCROLL_BAR_UI = "com.sun.java.swing.plaf.windows.WindowsScrollBarUI";
    private static final int WINDOWS_SCROLL_BAR_WIDTH = 17;
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
        defaults.put("TabbedPaneUI", TabbedPaneUI.class.getName());
        defaults.put(TabbedPaneUI.class.getName(), TabbedPaneUI.class);

        if (SystemInfo.isWindows) {
            Object width = Toolkit.getDefaultToolkit().getDesktopProperty("win.scrollbar.width");
            defaults.put("ScrollBarUI", WINDOWS_SCROLL_BAR_UI);
            defaults.put("ScrollBar.width", width instanceof Integer ? width : WINDOWS_SCROLL_BAR_WIDTH);
        }

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
}
