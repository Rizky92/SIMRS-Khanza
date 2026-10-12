package smc.updater;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.CodeSource;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public final class PembaruanSMC {
    private static final ScheduledExecutorService SCHEDULER = Executors.newSingleThreadScheduledExecutor(job -> {
        Thread thread = new Thread(job, "PembaruanSMC");

        thread.setDaemon(true);

        return thread;
    });

    private static boolean hasStarted = false;

    private static SumberDayaSMC sumberDaya;

    private PembaruanSMC() {
    }

    public static synchronized void checkForUpdate() {
        if (!isInstalled()) {
            return;
        }

        if (hasStarted) {
            SCHEDULER.execute(() -> attemptSync(true));
        } else {
            hasStarted = true;

            Path installDir = Paths.get("").toAbsolutePath().normalize();
            sumberDaya = new SumberDayaSMC(new ClientUpdaterSMC(), installDir);

            SCHEDULER.execute(() -> putaran(true));
        }
    }

    private static void putaran(boolean menyeluruh) {
        attemptSync(menyeluruh);

        SCHEDULER.schedule(() -> putaran(false), ThreadLocalRandom.current().nextInt(240, 360 + 1), TimeUnit.SECONDS);
    }

    private static void attemptSync(boolean menyeluruh) {
        try {
            sumberDaya.sync(menyeluruh);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            System.out.println("Pembaruan : " + e);
        }
    }

    private static boolean isInstalled() {
        try {
            CodeSource sumber = PembaruanSMC.class.getProtectionDomain().getCodeSource();

            return sumber != null && sumber.getLocation().getPath().toLowerCase(Locale.ROOT).endsWith(".jar");
        } catch (SecurityException e) {
            return false;
        }
    }
}
