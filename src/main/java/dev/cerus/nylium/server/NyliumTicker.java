package dev.cerus.nylium.server;

import java.util.concurrent.TimeUnit;

public class NyliumTicker {

    public static final int TICKS_PER_SECOND = 20;
    public static final int MILLIS_PER_TICK = 1000 / TICKS_PER_SECOND;
    public static final long MILLIS_PER_TICK_NANOS = TimeUnit.MILLISECONDS.toNanos(MILLIS_PER_TICK);

    private final NyliumServer server;

    private Thread tickingThread;
    private boolean run;

    public NyliumTicker(final NyliumServer server) {
        this.server = server;
    }

    public void startTicking() {
        this.tickingThread = new Thread(this::startLoop, "Nylium-Ticker");
        this.run = true;
        this.tickingThread.start();
    }

    private void startLoop() {
        // Unused - don't know how to tackle this yet
        long nanosToCatchUp = 0;

        while (this.run) {
            final long nanoBefore = System.nanoTime();

            // Tick
            this.server.tick();

            final long nanoAfter = System.nanoTime();

            final long nanoDiff = nanoAfter - nanoBefore;
            if (nanoDiff < MILLIS_PER_TICK_NANOS) {
                // Sleep the rest
                try {
                    Thread.sleep(TimeUnit.NANOSECONDS.toMillis(MILLIS_PER_TICK_NANOS - nanoDiff));
                } catch (final InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                // The tick took too long :(
                nanosToCatchUp += nanoDiff - MILLIS_PER_TICK_NANOS;
            }
        }
    }

    public void stopTicking() {
        this.run = false;
    }

    public void awaitThreadDeath() throws InterruptedException {
        this.tickingThread.join();
    }

    public Thread getTickingThread() {
        return this.tickingThread;
    }

}
