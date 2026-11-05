package widget;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.KeyboardFocusManager;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.SecondaryLoop;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import javax.swing.JComponent;
import javax.swing.JRootPane;
import javax.swing.JWindow;
import javax.swing.RootPaneContainer;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.Timer;

/**
 * Busy state for kiosk windows.
 * <p>
 * While a window is busy, this component replaces its glass pane. Every mouse and key event aimed at the window is swallowed, including the ones
 * queued while the event dispatch thread was blocked, so a patient tapping a button twice cannot submit twice. When the work lasts longer than
 * {@link #SHOW_DELAY} milliseconds, the window is dimmed and an animated spinner with a message is shown.
 * <p>
 * {@link #run} and {@link #call} keep their task on the event dispatch thread, so existing handlers can be wrapped as they are. Slow I/O inside
 * them should go through {@link #background}, which hands the work to a {@link SwingWorker} and keeps the event queue pumping until it finishes,
 * so the spinner animates and the window keeps repainting. Outside a busy scope {@link #background} simply runs the work in place.
 * <p>
 * The worker signals completion straight from {@code doInBackground()} instead of {@code done()}: Swing delivers {@code done()} through a
 * coalescing timer of about 33 ms, which would add that delay to every database query routed through here.
 */
public final class LoadingOverlaySMC extends JComponent {
    private static final int SHOW_DELAY = 200;
    private static final int FRAME_DELAY = 40;
    private static final int SPINNER_SIZE = 56;
    private static final int CARD_PADDING = 36;
    private static final int CARD_MIN_WIDTH = 340;
    private static final Color SCRIM = new Color(0, 0, 0, 96);
    private static final Color CARD = new Color(255, 255, 255);
    private static final Color ACCENT = new Color(0, 131, 62);
    private static final Color TRACK = new Color(0, 131, 62, 45);
    private static final Font MESSAGE_FONT = new Font("Inter Medium", Font.PLAIN, 22);
    private static final String DEFAULT_MESSAGE = "Mohon tunggu...";
    private static final Color COVER_HIT_AREA = new Color(0, 0, 0, 1);
    private static final int COVER_RAISE_DELAY = 250;
    private static final int SWP_NOSIZE = 0x0001;
    private static final int SWP_NOMOVE = 0x0002;
    private static final int SWP_NOACTIVATE = 0x0010;

    private static final Map<Window, LoadingOverlaySMC> OVERLAYS = new IdentityHashMap<>();
    private static boolean keyDispatcherInstalled = false;
    private static int scopeDepth = 0;
    private static int suspendDepth = 0;

    private final JRootPane rootPane;
    private final Component previousGlassPane;
    private final boolean previousGlassPaneVisible;
    private final Timer showTimer;
    private final Timer frameTimer;
    private final List<Runnable> idleTasks = new ArrayList<>();
    private String message = DEFAULT_MESSAGE;
    private boolean revealed = false;
    private int depth = 0;
    private int angle = 0;

    private LoadingOverlaySMC(JRootPane rootPane) {
        this.rootPane = rootPane;
        this.previousGlassPane = rootPane.getGlassPane();
        this.previousGlassPaneVisible = previousGlassPane.isVisible();

        setOpaque(false);
        setFocusable(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        MouseAdapter swallow = new MouseAdapter() {
        };
        addMouseListener(swallow);
        addMouseMotionListener(swallow);
        addMouseWheelListener(swallow);

        frameTimer = new Timer(FRAME_DELAY, e -> {
            angle = (angle + 12) % 360;
            if (suspendDepth == 0) {
                repaint(spinnerBounds());
            }
        });

        showTimer = new Timer(SHOW_DELAY, e -> {
            revealed = true;
            frameTimer.start();
            repaint();
        });
        showTimer.setRepeats(false);
    }

    /**
     * Marks the window of {@code owner} as busy until a matching {@link #end} call. Calls nest; the overlay stays up until the last one ends.
     * Meant for work that finishes asynchronously, e.g. a {@code SwingWorker} that calls {@link #end} from {@code done()}.
     *
     * @param owner a component inside the window to block
     * @param message the text shown under the spinner, or {@code null} for a generic one
     * @return {@code true} if the window is now blocked, {@code false} if it is not showing or cannot host an overlay
     */
    public static boolean begin(Component owner, String message) {
        if (!EventQueue.isDispatchThread()) {
            EventQueue.invokeLater(() -> begin(owner, message));
            return false;
        }

        Window target = windowOf(owner);
        if (target == null || !target.isShowing() || !(target instanceof RootPaneContainer)) {
            return false;
        }

        LoadingOverlaySMC overlay = OVERLAYS.get(target);
        if (overlay == null) {
            overlay = new LoadingOverlaySMC(((RootPaneContainer) target).getRootPane());
            OVERLAYS.put(target, overlay);
            overlay.install();
        }

        overlay.depth++;
        overlay.updateMessage(message);
        return true;
    }

    /**
     * Ends one {@link #begin} call on the window of {@code owner}. The overlay is removed on a later turn of the event queue, so input that was
     * queued while the window was busy is still swallowed.
     *
     * @param owner a component inside the window to release
     */
    public static void end(Component owner) {
        if (!EventQueue.isDispatchThread()) {
            EventQueue.invokeLater(() -> end(owner));
            return;
        }

        Window target = windowOf(owner);
        LoadingOverlaySMC overlay = target == null ? null : OVERLAYS.get(target);
        if (overlay == null || overlay.depth == 0) {
            return;
        }

        overlay.depth--;
        if (overlay.depth == 0) {
            EventQueue.invokeLater(() -> {
                if (overlay.depth == 0 && OVERLAYS.get(target) == overlay) {
                    OVERLAYS.remove(target);
                    overlay.uninstall();
                }
            });
        }
    }

    /**
     * Runs {@code task} on the event dispatch thread while the window of {@code owner} is busy.
     *
     * @param owner a component inside the window to block
     * @param message the text shown under the spinner, or {@code null} for a generic one
     * @param task the work to run
     */
    public static void run(Component owner, String message, Runnable task) {
        call(owner, message, () -> {
            task.run();
            return null;
        });
    }

    /**
     * Runs {@code task} on the event dispatch thread while a cover spans the whole screen of {@code owner}, above every window including those of
     * other applications. The cover swallows mouse and touch input but never takes the keyboard focus, so keystrokes sent with
     * {@link java.awt.Robot} still reach whichever application is in the foreground. Meant for driving another application on the patient's
     * behalf, e.g. logging in to a biometric app, without the patient being able to interfere.
     *
     * @param owner a component inside the window that started the work; it is blocked as well
     * @param message the text shown under the spinner, or {@code null} for a generic one
     * @param task the work to run
     */
    public static void runOverScreen(Component owner, String message, Runnable task) {
        if (!EventQueue.isDispatchThread()) {
            task.run();
            return;
        }

        Window ownerWindow = windowOf(owner);
        GraphicsConfiguration configuration = ownerWindow != null ? ownerWindow.getGraphicsConfiguration() : GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();

        JWindow cover = new JWindow(configuration);
        cover.setFocusableWindowState(false);
        cover.setAlwaysOnTop(true);
        JComponent hitArea = new JComponent() {
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(COVER_HIT_AREA);
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        cover.setContentPane(hitArea);
        if (configuration.getDevice().isWindowTranslucencySupported(GraphicsDevice.WindowTranslucency.PERPIXEL_TRANSLUCENT)) {
            cover.setBackground(new Color(0, 0, 0, 0));
        }
        cover.setBounds(configuration.getBounds());

        Timer raise = new Timer(COVER_RAISE_DELAY, e -> raiseAboveOthers(cover));
        boolean ownerBlocked = begin(owner, message);
        cover.setVisible(true);
        raise.start();
        try {
            run(cover, message, task);
        } finally {
            raise.stop();
            cover.dispose();
            if (ownerBlocked) {
                end(owner);
            }
        }
    }

    /**
     * Changes the text under the spinner of every busy window, e.g. to report the progress of a long task.
     *
     * @param message the new text, or {@code null} for a generic one
     */
    public static void setMessage(String message) {
        for (LoadingOverlaySMC overlay : OVERLAYS.values()) {
            overlay.updateMessage(message);
        }
    }

    private static void raiseAboveOthers(Window window) {
        try {
            WinDef.HWND hwnd = new WinDef.HWND(Native.getWindowPointer(window));
            User32.INSTANCE.SetWindowPos(hwnd, new WinDef.HWND(Pointer.createConstant(-1)), 0, 0, 0, 0, SWP_NOMOVE | SWP_NOSIZE | SWP_NOACTIVATE);
        } catch (RuntimeException | LinkageError e) {
            System.out.println("Notif : " + e);
        }
    }

    /**
     * Runs {@code task} on the event dispatch thread while the window of {@code owner} is busy, and returns its result.
     *
     * @param <T> the result type
     * @param owner a component inside the window to block
     * @param message the text shown under the spinner, or {@code null} for a generic one
     * @param task the work to run
     * @return the result of {@code task}
     */
    public static <T> T call(Component owner, String message, Supplier<T> task) {
        if (!EventQueue.isDispatchThread() || !begin(owner, message)) {
            return task.get();
        }

        scopeDepth++;
        try {
            return task.get();
        } finally {
            scopeDepth--;
            end(owner);
        }
    }

    /**
     * Runs slow, UI-free work. Inside {@link #run} or {@link #call} on the event dispatch thread, the work runs in a {@link SwingWorker} while the
     * event queue keeps pumping, and this method returns once it is done. Anywhere else it runs in place.
     *
     * @param <T> the result type
     * @param task the work to run; it must not touch Swing components
     * @return the result of {@code task}
     * @throws Exception whatever {@code task} throws
     */
    public static <T> T background(Callable<T> task) throws Exception {
        if (scopeDepth == 0 || !EventQueue.isDispatchThread()) {
            return task.call();
        }

        SecondaryLoop loop = Toolkit.getDefaultToolkit().getSystemEventQueue().createSecondaryLoop();
        AtomicReference<T> result = new AtomicReference<>();
        AtomicReference<Throwable> failure = new AtomicReference<>();

        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                try {
                    result.set(task.call());
                } catch (Throwable t) {
                    failure.set(t);
                } finally {
                    loop.exit();
                }
                return null;
            }
        }.execute();
        loop.enter();

        Throwable t = failure.get();
        if (t instanceof Exception) {
            throw (Exception) t;
        } else if (t instanceof Error) {
            throw (Error) t;
        }
        return result.get();
    }

    /**
     * {@link Thread#sleep(long)} that keeps the event queue pumping when called inside {@link #run} or {@link #call}.
     *
     * @param millis how long to wait
     * @throws InterruptedException if the waiting thread is interrupted
     */
    public static void sleep(long millis) throws InterruptedException {
        try {
            background(() -> {
                Thread.sleep(millis);
                return null;
            });
        } catch (InterruptedException | RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * @param owner a component inside the window to check
     * @return {@code true} while the window of {@code owner} is blocked, including the short tail after the last {@link #end}
     */
    public static boolean isBusy(Component owner) {
        Window target = windowOf(owner);
        return target != null && OVERLAYS.containsKey(target);
    }

    /**
     * Runs {@code task} once the window of {@code owner} is no longer busy, or right away when it is not busy.
     *
     * @param owner a component inside the window to watch
     * @param task the work to run on the event dispatch thread
     */
    public static void whenIdle(Component owner, Runnable task) {
        Window target = windowOf(owner);
        LoadingOverlaySMC overlay = target == null ? null : OVERLAYS.get(target);
        if (overlay == null) {
            task.run();
        } else {
            overlay.idleTasks.add(task);
        }
    }

    /**
     * Hides the dimming and spinner of every busy window, e.g. while a message dialog is shown on top. Input stays blocked. Calls nest and must be
     * paired with {@link #resume()}.
     */
    public static void suspend() {
        suspendDepth++;
        repaintAll();
    }

    /**
     * Ends one {@link #suspend()} call.
     */
    public static void resume() {
        if (suspendDepth > 0) {
            suspendDepth--;
        }
        repaintAll();
    }

    private static void repaintAll() {
        for (LoadingOverlaySMC overlay : OVERLAYS.values()) {
            overlay.repaint();
        }
    }

    private static Window windowOf(Component owner) {
        if (owner == null) {
            return null;
        } else if (owner instanceof Window) {
            return (Window) owner;
        }
        return SwingUtilities.getWindowAncestor(owner);
    }

    private static void installKeyDispatcher() {
        if (keyDispatcherInstalled) {
            return;
        }

        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(e -> {
            Window target = windowOf(e.getComponent());
            return target != null && OVERLAYS.containsKey(target);
        });
        keyDispatcherInstalled = true;
    }

    private void install() {
        installKeyDispatcher();
        rootPane.setGlassPane(this);
        setVisible(true);
        revalidate();
        showTimer.start();
    }

    private void uninstall() {
        showTimer.stop();
        frameTimer.stop();
        setVisible(false);
        rootPane.setGlassPane(previousGlassPane);
        previousGlassPane.setVisible(previousGlassPaneVisible);
        rootPane.repaint();

        List<Runnable> tasks = new ArrayList<>(idleTasks);
        idleTasks.clear();
        for (Runnable task : tasks) {
            task.run();
        }
    }

    private void updateMessage(String message) {
        String next = message == null || message.isBlank() ? DEFAULT_MESSAGE : message;
        if (!next.equals(this.message)) {
            this.message = next;
            repaint();
        }
    }

    private Rectangle cardBounds(FontMetrics metrics, String[] lines) {
        int textWidth = 0;
        for (String line : lines) {
            textWidth = Math.max(textWidth, metrics.stringWidth(line));
        }

        int width = Math.max(CARD_MIN_WIDTH, textWidth + CARD_PADDING * 2);
        int height = CARD_PADDING + SPINNER_SIZE + 24 + lines.length * metrics.getHeight() + CARD_PADDING;
        return new Rectangle((getWidth() - width) / 2, (getHeight() - height) / 2, width, height);
    }

    private Rectangle spinnerBounds() {
        FontMetrics metrics = getFontMetrics(MESSAGE_FONT);
        Rectangle card = cardBounds(metrics, message.split("\n"));
        return new Rectangle(card.x + (card.width - SPINNER_SIZE) / 2 - 4, card.y + CARD_PADDING - 4, SPINNER_SIZE + 8, SPINNER_SIZE + 8);
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (!revealed || suspendDepth > 0) {
            return;
        }

        Graphics2D g2 = (Graphics2D) g.create();
        try {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setColor(SCRIM);
            g2.fillRect(0, 0, getWidth(), getHeight());

            g2.setFont(MESSAGE_FONT);
            FontMetrics metrics = g2.getFontMetrics();
            String[] lines = message.split("\n");
            Rectangle card = cardBounds(metrics, lines);
            g2.setColor(CARD);
            g2.fillRoundRect(card.x, card.y, card.width, card.height, 24, 24);

            int spinnerX = card.x + (card.width - SPINNER_SIZE) / 2;
            int spinnerY = card.y + CARD_PADDING;
            g2.setStroke(new BasicStroke(6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.setColor(TRACK);
            g2.drawOval(spinnerX, spinnerY, SPINNER_SIZE, SPINNER_SIZE);
            g2.setColor(ACCENT);
            g2.drawArc(spinnerX, spinnerY, SPINNER_SIZE, SPINNER_SIZE, -angle, 100);

            int textY = spinnerY + SPINNER_SIZE + 24 + metrics.getAscent();
            for (String line : lines) {
                g2.drawString(line, card.x + (card.width - metrics.stringWidth(line)) / 2, textY);
                textY += metrics.getHeight();
            }
        } finally {
            g2.dispose();
        }
    }
}
