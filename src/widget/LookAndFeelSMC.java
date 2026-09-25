package widget;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.fonts.inter.FlatInterFont;
import com.formdev.flatlaf.icons.FlatCheckBoxIcon;
import com.formdev.flatlaf.ui.FlatScrollPaneBorder;
import com.formdev.flatlaf.ui.FlatTabbedPaneUI;
import com.formdev.flatlaf.ui.FlatTextBorder;
import com.formdev.flatlaf.ui.FlatUIUtils;
import com.formdev.flatlaf.util.FontUtils;
import com.formdev.flatlaf.util.SystemInfo;
import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.awt.event.ContainerEvent;
import java.beans.PropertyChangeListener;
import java.io.InputStream;
import java.util.Properties;
import javax.swing.JComponent;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.text.JTextComponent;

/**
 * Application look and feel, based on FlatLaf Light.
 * <p>
 * Theme values live in {@code LookAndFeelSMC.properties}, which FlatLaf loads
 * automatically because it sits next to this class. This class adds what a
 * properties file cannot express:
 * <ul>
 *   <li>the bundled Inter Tabular font at size 12, falling back to FlatLaf's Inter
 *       and then to Tahoma 11 when it cannot be loaded;</li>
 *   <li>a larger check box icon;</li>
 *   <li>read-only text fields and text areas drawn with the disabled border color;</li>
 *   <li>the selected tab keeping its background while hovered, and unselected tabs
 *       drawn with their own background and outline;</li>
 *   <li>the native scroll bar on Windows;</li>
 *   <li>remapping of the Tahoma fonts hard-coded by NetBeans forms to the
 *       application font at the same size, including titled border fonts.</li>
 * </ul>
 * Java2D cannot enable OpenType features, so Inter's {@code tnum} feature is baked
 * into {@code fonts/InterTabular-*.ttf}: the digits map to the tabular glyphs and
 * the family is renamed to "Inter Tabular". Inter is licensed under the SIL Open
 * Font License, see {@code fonts/LICENSE.txt}.
 *
 * @author smc
 */
public class LookAndFeelSMC extends FlatLightLaf {

    public static final String NAME = "SIMRS Khanza";

    private static final String LEGACY_FONT_FAMILY = "Tahoma";
    private static final int LEGACY_FONT_SIZE = 11;
    private static final String TABULAR_FONT_FAMILY = "Inter Tabular";
    private static final String[] TABULAR_FONT_FILES = {"InterTabular-Regular.ttf", "InterTabular-Bold.ttf", "InterTabular-Italic.ttf", "InterTabular-BoldItalic.ttf"};
    private static final int FONT_SIZE = 12;
    private static final String FONT_MAPPED_KEY = "LookAndFeelSMC.fontMapped";
    private static final String WINDOWS_SCROLL_BAR_UI = "com.sun.java.swing.plaf.windows.WindowsScrollBarUI";
    private static final int WINDOWS_SCROLL_BAR_WIDTH = 17;
    private static final float CHECK_BOX_ICON_SCALE = 1.2f;

    private static final PropertyChangeListener FONT_LISTENER = e -> mapFont((Component) e.getSource());
    private static final PropertyChangeListener BORDER_LISTENER = e -> mapBorder((Border) e.getNewValue());

    private static boolean fontMappingInstalled = false;
    private static String defaultFont = LEGACY_FONT_SIZE + " " + LEGACY_FONT_FAMILY;

    /**
     * Installs this look and feel. Call once, before any window is created.
     *
     * @return {@code true} when the look and feel was installed
     */
    public static boolean setup() {
        if (null == System.getProperty("flatlaf.useWindowDecorations")) {
            System.setProperty("flatlaf.useWindowDecorations", "false");
        }

        if (installTabularFont()) {
            defaultFont = FONT_SIZE + " \"" + TABULAR_FONT_FAMILY + "\"";
        } else {
            try {
                FlatInterFont.install();
                defaultFont = FONT_SIZE + " " + FlatInterFont.FAMILY;
            } catch (Throwable e) {
                System.out.println("Notif : Font Inter tidak dapat dimuat, menggunakan Tahoma " + e);
            }
        }

        boolean installed = FlatLaf.setup(new LookAndFeelSMC());
        if (installed) {
            installFontMapping();
        }
        return installed;
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
    protected Properties getAdditionalDefaults() {
        Properties properties = super.getAdditionalDefaults();
        if (null == properties) {
            properties = new Properties();
        }
        properties.put("defaultFont", defaultFont);
        return properties;
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
     * Returns the application font equivalent of a legacy Tahoma font, keeping its
     * style and size, so forms laid out for Tahoma 11 keep fitting their text while
     * components without an explicit font use the larger default size. Any other
     * font is returned unchanged.
     *
     * @param font the font to map, may be {@code null}
     * @return the mapped font
     */
    public static Font mapFont(Font font) {
        if (null == font || font instanceof UIResource || !LEGACY_FONT_FAMILY.equals(font.getFamily())) {
            return font;
        }

        Font base = UIManager.getFont("defaultFont");
        if (null == base || LEGACY_FONT_FAMILY.equals(base.getFamily())) {
            return font;
        }

        return FontUtils.getCompositeFont(base.getFamily(), font.getStyle(), font.getSize());
    }

    private static boolean installTabularFont() {
        GraphicsEnvironment environment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        try {
            for (String file : TABULAR_FONT_FILES) {
                try (InputStream stream = LookAndFeelSMC.class.getResourceAsStream("fonts/" + file)) {
                    if (null == stream) {
                        return false;
                    }
                    environment.registerFont(Font.createFont(Font.TRUETYPE_FONT, stream));
                }
            }
        } catch (Exception e) {
            System.out.println("Notif : Font Inter Tabular tidak dapat dimuat " + e);
            return false;
        }
        return TABULAR_FONT_FAMILY.equals(new Font(TABULAR_FONT_FAMILY, Font.PLAIN, FONT_SIZE).getFamily());
    }

    private static void installFontMapping() {
        if (fontMappingInstalled) {
            return;
        }
        fontMappingInstalled = true;

        Toolkit.getDefaultToolkit().addAWTEventListener(event -> {
            if (ContainerEvent.COMPONENT_ADDED == event.getID()) {
                watch(((ContainerEvent) event).getChild());
            }
        }, AWTEvent.CONTAINER_EVENT_MASK);
    }

    private static void watch(Component component) {
        if (component instanceof JComponent) {
            JComponent c = (JComponent) component;
            if (null != c.getClientProperty(FONT_MAPPED_KEY)) {
                return;
            }
            c.putClientProperty(FONT_MAPPED_KEY, Boolean.TRUE);
            c.addPropertyChangeListener("font", FONT_LISTENER);
            c.addPropertyChangeListener("border", BORDER_LISTENER);
            mapBorder(c.getBorder());
        }
        mapFont(component);
    }

    private static void mapFont(Component component) {
        if (!component.isFontSet()) {
            return;
        }

        Font font = component.getFont();
        Font mapped = mapFont(font);
        if (mapped != font) {
            component.setFont(mapped);
        }
    }

    private static void mapBorder(Border border) {
        if (border instanceof TitledBorder) {
            TitledBorder titled = (TitledBorder) border;
            Font font = titled.getTitleFont();
            Font mapped = mapFont(font);
            if (mapped != font) {
                titled.setTitleFont(mapped);
            }
            mapBorder(titled.getBorder());
        } else if (border instanceof CompoundBorder) {
            mapBorder(((CompoundBorder) border).getOutsideBorder());
            mapBorder(((CompoundBorder) border).getInsideBorder());
        }
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
