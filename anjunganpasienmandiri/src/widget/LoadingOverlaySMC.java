package widget;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import javax.swing.JComponent;
import javax.swing.JRootPane;
import javax.swing.RootPaneContainer;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

/**
 * Busy state for kiosk windows.
 * <p>
 * While a window is busy, this component replaces its glass pane. Every mouse and key event aimed at the window is swallowed, including the ones
 * queued while the event dispatch thread was blocked, so a patient tapping a button twice cannot submit twice. When the work lasts longer than
 * {@link #SHOW_DELAY} milliseconds, the window is dimmed and an animated spinner with a message is shown.
 * <p>
 * {@link #run} and {@link #call} keep their task on the event dispatch thread, so existing handlers can be wrapped as they are. Slow I/O inside
 * them should go through {@link #background}, which hands the work to a worker thread and keeps the event queue pumping until it finishes, so the
 * spinner animates and the window keeps repainting. Outside a busy scope {@link #background} simply runs the work in place.
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

    private static final Map<Window, LoadingOverlaySMC> OVERLAYS = new IdentityHashMap<>();
    private static final ExecutorService WORKERS = Executors.newCachedThreadPool(task -> {
        Thread thread = new Thread(task, "LoadingOverlaySMC-worker");
        thread.setDaemon(true);
        return thread;
    });
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
        overlay.setMessage(message);
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
     * Runs slow, UI-free work. Inside {@link #run} or {@link #call} on the event dispatch thread, the work runs on a worker thread while the event
     * queue keeps pumping, and this method returns once it is done. Anywhere else it runs in place.
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

        WORKERS.execute(() -> {
            try {
                result.set(task.call());
            } catch (Throwable t) {
                failure.set(t);
            } finally {
                loop.exit();
            }
        });
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

    private void setMessage(String message) {
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
