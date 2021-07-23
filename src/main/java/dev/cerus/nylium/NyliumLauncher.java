package dev.cerus.nylium;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import dev.cerus.nylium.event.EventBus;
import dev.cerus.nylium.io.NettyBootstrapper;
import dev.cerus.nylium.io.session.PlayerSessionController;
import dev.cerus.nylium.server.NyliumServer;
import dev.cerus.nylium.server.NyliumTicker;
import dev.cerus.nylium.server.block.BlockRegistry;
import dev.cerus.nylium.server.dimension.DimensionCodec;
import dev.cerus.nylium.server.listener.EncryptionListener;
import dev.cerus.nylium.server.listener.LoginListener;
import dev.cerus.nylium.server.listener.PingListener;
import dev.cerus.nylium.server.listener.PlayerMoveListener;
import dev.cerus.nylium.server.listener.PluginMessageListener;
import dev.cerus.nylium.server.listener.SettingsListener;
import dev.cerus.nylium.server.tick.KeepAliveTickable;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class NyliumLauncher {

    private static final Logger LOGGER = Logger.getLogger("");

    public static void main(final String[] args) {
        // Setup logger
        final Handler handler = LOGGER.getHandlers()[0];
        handler.setFormatter(new Formatter() {
            private final SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");

            @Override
            public String format(final LogRecord record) {
                final long threadId = record.getLongThreadID();
                final String threadName = Thread.getAllStackTraces().keySet().stream()
                        .filter(t -> t.getId() == threadId)
                        .findFirst()
                        .map(Thread::getName)
                        .orElseGet(() -> "Thread " + threadId);

                return "[" + this.format.format(new Date(record.getMillis())) + "] [" + record.getLevel().getName()
                        + "] [" + threadName + "] " + record.getMessage() + "\n";
            }
        });

        // Set thread name, print our brand
        Thread.currentThread().setName("Server");
        printBrand();

        LOGGER.info("Loading resources");

        try {
            DimensionCodec.initialize();
        } catch (final IOException e) {
            e.printStackTrace();
            LOGGER.severe("Failed to load dimension codec");
            return;
        }

        try {
            final InputStream inputStream = NyliumLauncher.class.getClassLoader().getResourceAsStream("blocks.json");
            BlockRegistry.load(inputStream);
        } catch (final JsonIOException | JsonSyntaxException e) {
            e.printStackTrace();
            LOGGER.severe("Failed to load blocks");
            return;
        }

        // Create the event bus and add important listeners
        final EventBus eventBus = new EventBus();
        eventBus.registerListener(new LoginListener(eventBus));
        eventBus.registerListener(new EncryptionListener(eventBus));
        eventBus.registerListener(new PingListener(eventBus));
        eventBus.registerListener(new SettingsListener(eventBus));
        eventBus.registerListener(new PluginMessageListener(eventBus));
        eventBus.registerListener(new PlayerMoveListener(eventBus));

        // Create the player session controller
        final PlayerSessionController sessionController = new PlayerSessionController(eventBus);

        LOGGER.info("Starting Netty server");

        // Boot up Netty
        final NettyBootstrapper nettyBootstrapper = new NettyBootstrapper(sessionController, eventBus);
        nettyBootstrapper.start();

        LOGGER.info("Starting server ticker");

        final NyliumTicker ticker = new NyliumTicker(new NyliumServer(sessionController));
        final KeepAliveTickable keepAliveTickable = new KeepAliveTickable(sessionController);
        ticker.addTickable(keepAliveTickable);
        eventBus.registerListener(keepAliveTickable);

        ticker.startTicking();
    }

    private static void printBrand() {
        LOGGER.info(" _____     _ _           ");
        LOGGER.info("|   | |_ _| |_|_ _ _____ ");
        LOGGER.info("| | | | | | | | | |     |");
        LOGGER.info("|_|___|_  |_|_|___|_|_|_|");
        LOGGER.info("      |___|              ");
        LOGGER.info("https://github.com/cerus/nylium");
        LOGGER.info("");
    }

}
