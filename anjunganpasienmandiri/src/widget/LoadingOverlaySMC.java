package widget;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinUser;
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
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
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
    private static final long SCREEN_TIMEOUT = 60_000;
    private static final int CANCEL_DELAY = 3000;
    private static final int WATCH_INTERVAL = 50;
    private static final int HOTKEY_ID = 0x4C4F;
    private static final int VK_ESCAPE = 0x1B;
    private static final int PM_REMOVE = 0x0001;
    private static final int HWND_NOTOPMOST = -2;
    private static final String CANCEL_TEXT = "Batalkan (Esc)";
    private static final int CANCEL_WIDTH = 220;
    private static final int CANCEL_HEIGHT = 52;
    private static final int CANCEL_GAP = 24;

    private static final Map<Window, LoadingOverlaySMC> OVERLAYS = new IdentityHashMap<>();
    private static boolean keyDispatcherInstalled = false;
    private static int scopeDepth = 0;
    private static int suspendDepth = 0;
    private static volatile ScreenScope screenScope = null;

    /**
     * How a {@link #runOverScreen} call ended.
     */
    public enum ScreenOutcome {
        /** The task ran to its end. */
        COMPLETED,
        /** The cover was dismissed with its cancel button or the Escape key before the task ended. */
        CANCELLED,
        /** The cover was dismissed because the task outlasted {@link #SCREEN_TIMEOUT}. */
        TIMED_OUT
    }

    /**
     * State of one {@link #runOverScreen} call that other threads may touch. A watchdog thread enforces the time limit and owns the Escape hotkey,
     * so both still work when the event dispatch thread is stuck: stopping hides the cover natively first and only then asks Swing to clean up.
     */
    private static final class ScreenScope {
        private final JWindow cover;
        private final WinDef.HWND coverHandle;
        private final long deadline;
        private final AtomicReference<ScreenOutcome> outcome = new AtomicReference<>();
        private volatile SecondaryLoop loop;
        private volatile boolean closed = false;

        ScreenScope(JWindow cover, WinDef.HWND coverHandle, long deadline) {
            this.cover = cover;
            this.coverHandle = coverHandle;
            this.deadline = deadline;
        }

        boolean stopped() {
            return outcome.get() != null;
        }

        void stop(ScreenOutcome reason) {
            if (closed || !outcome.compareAndSet(null, reason)) {
                return;
            }
            System.out.println("Notif : layar tunggu " + (ScreenOutcome.TIMED_OUT == reason ? "melewati batas waktu " + SCREEN_TIMEOUT / 1000 + " detik" : "dibatalkan") + ", layar dibuka kembali");
            if (coverHandle != null) {
                try {
                    User32.INSTANCE.SetWindowPos(coverHandle, new WinDef.HWND(Pointer.createConstant(HWND_NOTOPMOST)), 0, 0, 0, 0, SWP_NOMOVE | SWP_NOSIZE | SWP_NOACTIVATE);
                    User32.INSTANCE.ShowWindow(coverHandle, WinUser.SW_HIDE);
                } catch (RuntimeException | LinkageError e) {
                    System.out.println("Notif : " + e);
                }
            }
            SecondaryLoop current = loop;
            if (current != null) {
                current.exit();
            }
            EventQueue.invokeLater(() -> cover.setVisible(false));
        }

        void close() {
            closed = true;
        }

        void watch() {
            boolean hotkey = false;
            try {
                hotkey = User32.INSTANCE.RegisterHotKey(null, HOTKEY_ID, WinUser.MOD_NOREPEAT, VK_ESCAPE);
            } catch (RuntimeException | LinkageError e) {
                System.out.println("Notif : " + e);
            }
            if (!hotkey) {
                System.out.println("Notif : tombol Esc tidak dapat dipasang untuk membatalkan layar tunggu");
            }

            WinUser.MSG pesan = new WinUser.MSG();
            try {
                while (!closed && !stopped()) {
                    while (hotkey && User32.INSTANCE.PeekMessage(pesan, null, 0, 0, PM_REMOVE)) {
                        if (WinUser.WM_HOTKEY == pesan.message && HOTKEY_ID == pesan.wParam.intValue()) {
                            stop(ScreenOutcome.CANCELLED);
                        }
                    }
                    if (System.nanoTime() - deadline >= 0) {
                        stop(ScreenOutcome.TIMED_OUT);
                    }
                    Thread.sleep(WATCH_INTERVAL);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                if (hotkey) {
                    User32.INSTANCE.UnregisterHotKey(null, HOTKEY_ID);
                }
            }
        }
    }

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
    private Runnable cancelAction = null;
    private Timer cancelTimer = null;
    private boolean cancelVisible = false;
    private boolean cancelPressed = false;

    private LoadingOverlaySMC(JRootPane rootPane) {
        this.rootPane = rootPane;
        this.previousGlassPane = rootPane.getGlassPane();
        this.previousGlassPaneVisible = previousGlassPane.isVisible();

        setOpaque(false);
        setFocusable(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        MouseAdapter swallow = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                cancelPressed = cancelHit(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                boolean clicked = cancelPressed && cancelHit(e);
                cancelPressed = false;
                if (clicked) {
                    cancelAction.run();
                }
            }
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
     * <p>
     * The cover never outlives {@link #SCREEN_TIMEOUT}, whatever the task does, and it can be dismissed early with the cancel button shown on the
     * card after {@link #CANCEL_DELAY} milliseconds or with the Escape key, which is registered as a system-wide hotkey while the cover is up. Once
     * the cover is dismissed, every {@link #background} or {@link #sleep} call the task makes throws a {@link CancellationException}, so a task that
     * polls through them stops at its next step.
     *
     * @param owner a component inside the window that started the work; it is blocked as well
     * @param message the text shown under the spinner, or {@code null} for a generic one
     * @param task the work to run
     * @return whether the task ran to its end or the cover was dismissed first
     */
    public static ScreenOutcome runOverScreen(Component owner, String message, Runnable task) {
        if (!EventQueue.isDispatchThread() || screenScope != null) {
            task.run();
            return ScreenOutcome.COMPLETED;
        }

        Window ownerWindow = windowOf(owner);
        GraphicsConfiguration configuration = ownerWindow != null ? ownerWindow.getGraphicsConfiguration() : GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();

        JWindow cover = new JWindow(configuration);
        cover.setModalExclusionType(java.awt.Dialog.ModalExclusionType.APPLICATION_EXCLUDE);
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

        boolean ownerBlocked = begin(owner, message);
        cover.setVisible(true);
        ScreenScope scope = new ScreenScope(cover, handleOf(cover), System.nanoTime() + SCREEN_TIMEOUT * 1_000_000L);
        screenScope = scope;
        Thread watchdog = new Thread(scope::watch, "LoadingOverlayWatchdog");
        watchdog.setDaemon(true);
        watchdog.start();
        Timer raise = new Timer(COVER_RAISE_DELAY, e -> {
            if (!scope.stopped()) {
                raiseAboveOthers(cover);
            }
        });
        raise.start();
        boolean coverBlocked = begin(cover, message);
        if (coverBlocked) {
            OVERLAYS.get(cover).enableCancel(() -> scope.stop(ScreenOutcome.CANCELLED));
        }
        try {
            run(cover, message, task);
        } catch (CancellationException e) {
            if (!scope.stopped()) {
                throw e;
            }
        } finally {
            scope.close();
            screenScope = null;
            raise.stop();
            if (coverBlocked) {
                end(cover);
            }
            cover.dispose();
            if (ownerBlocked) {
                end(owner);
            }
        }
        ScreenOutcome outcome = scope.outcome.get();
        return outcome == null ? ScreenOutcome.COMPLETED : outcome;
    }

    private static WinDef.HWND handleOf(Window window) {
        try {
            return new WinDef.HWND(Native.getWindowPointer(window));
        } catch (RuntimeException | LinkageError e) {
            System.out.println("Notif : " + e);
            return null;
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

        ScreenScope scope = screenScope;
        if (scope != null && scope.stopped()) {
            throw new CancellationException("Layar tunggu sudah ditutup");
        }

        SecondaryLoop loop = Toolkit.getDefaultToolkit().getSystemEventQueue().createSecondaryLoop();
        AtomicReference<T> result = new AtomicReference<>();
        AtomicReference<Throwable> failure = new AtomicReference<>();

        SecondaryLoop previousLoop = null;
        if (scope != null) {
            previousLoop = scope.loop;
            scope.loop = loop;
            if (scope.stopped()) {
                scope.loop = previousLoop;
                throw new CancellationException("Layar tunggu sudah ditutup");
            }
        }

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
        try {
            loop.enter();
        } finally {
            if (scope != null) {
                scope.loop = previousLoop;
            }
        }

        if (scope != null && scope.stopped()) {
            throw new CancellationException("Layar tunggu sudah ditutup");
        }

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

    private void enableCancel(Runnable action) {
        cancelAction = action;
        cancelTimer = new Timer(CANCEL_DELAY, e -> {
            cancelVisible = true;
            repaint();
        });
        cancelTimer.setRepeats(false);
        cancelTimer.start();
    }

    private boolean cancelHit(MouseEvent e) {
        return cancelAction != null && cancelVisible && revealed && suspendDepth == 0 && cancelBounds(getFontMetrics(MESSAGE_FONT)).contains(e.getPoint());
    }

    private Rectangle cancelBounds(FontMetrics metrics) {
        Rectangle card = cardBounds(metrics, message.split("\n"));
        return new Rectangle(card.x + (card.width - CANCEL_WIDTH) / 2, card.y + card.height - CARD_PADDING - CANCEL_HEIGHT, CANCEL_WIDTH, CANCEL_HEIGHT);
    }

    private void uninstall() {
        if (cancelTimer != null) {
            cancelTimer.stop();
        }
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
        int height = CARD_PADDING + SPINNER_SIZE + 24 + lines.length * metrics.getHeight() + (cancelVisible ? CANCEL_GAP + CANCEL_HEIGHT : 0) + CARD_PADDING;
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

            if (cancelAction != null && cancelVisible) {
                Rectangle button = cancelBounds(metrics);
                g2.setStroke(new BasicStroke(2f));
                g2.drawRoundRect(button.x, button.y, button.width, button.height, 16, 16);
                g2.drawString(CANCEL_TEXT, button.x + (button.width - metrics.stringWidth(CANCEL_TEXT)) / 2, button.y + (button.height - metrics.getHeight()) / 2 + metrics.getAscent());
            }
        } finally {
            g2.dispose();
        }
    }
}
