package dev.cerus.nylium.io;

import dev.cerus.nylium.event.EventBus;
import dev.cerus.nylium.event.implementation.PacketReceivedEvent;
import dev.cerus.nylium.io.netty.FrameSplitter;
import dev.cerus.nylium.io.netty.PacketCodec;
import dev.cerus.nylium.io.netty.PacketEncryptor;
import dev.cerus.nylium.io.netty.PacketLengthPrepender;
import dev.cerus.nylium.io.packet.Packet;
import dev.cerus.nylium.io.packet.PacketIn;
import dev.cerus.nylium.io.session.PlayerSession;
import dev.cerus.nylium.io.session.PlayerSessionController;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The heart of the Nylium server: The Netty bootstrapper
 * Starts a Netty server with the appropriate channel handlers
 */
public class NettyBootstrapper {

    private static final Logger LOGGER = Logger.getLogger(NettyBootstrapper.class.getName());

    private final PlayerSessionController sessionController;
    private final EventBus eventBus;
    private ChannelFuture future;
    private Thread nettyThread;

    public NettyBootstrapper(final PlayerSessionController sessionController, final EventBus eventBus) {
        this.sessionController = sessionController;
        this.eventBus = eventBus;
    }

    /**
     * Start the Netty server
     */
    public void start() {
        this.nettyThread = new Thread(this::startNetty, "Netty-Bootstrapper");
        this.nettyThread.start();
    }

    /**
     * Stop the Netty server
     */
    public void stop() {
        this.future.cancel(true);
        this.nettyThread.interrupt();
    }

    private void startNetty() {
        final EventLoopGroup bossGroup = new NioEventLoopGroup();
        final EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            final ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(final SocketChannel ch) throws Exception {
                            // Encryptor handles encryption and decryption of packets when needed
                            ch.pipeline().addLast("encryptor", new PacketEncryptor(NettyBootstrapper.this.sessionController));
                            // Frame splitter is responsible for breaking down the stream into packets
                            ch.pipeline().addLast("splitter", new FrameSplitter());
                            // Length prepender prepends the packet length on outgoing packets
                            ch.pipeline().addLast("lengthPrepender", new PacketLengthPrepender());
                            // Packet codec transforms bytes into packet instances
                            ch.pipeline().addLast("codec", new PacketCodec(NettyBootstrapper.this.sessionController));
                            // The last handler takes the end result and distributes it using the event bus
                            ch.pipeline().addLast("handler", new SimpleChannelInboundHandler<Packet>() {
                                @Override
                                public void channelActive(final ChannelHandlerContext ctx) throws Exception {
                                    NettyBootstrapper.this.sessionController.addSession(new PlayerSession(ctx));
                                    LOGGER.info("New connection from " + ctx.channel().remoteAddress().toString());
                                }

                                @Override
                                public void channelInactive(final ChannelHandlerContext ctx) throws Exception {
                                    final PlayerSession session = NettyBootstrapper.this.sessionController.getByChId(ctx.channel().id());
                                    NettyBootstrapper.this.sessionController.removeSession(session);
                                    LOGGER.info("Client " + session.getGameProfile().getUsername() + " ("
                                            + ctx.channel().remoteAddress().toString() + ") disconnected [" + session.getState() + "]");
                                }

                                @Override
                                protected void channelRead0(final ChannelHandlerContext ctx, final Packet msg) throws Exception {
                                    if (!(msg instanceof PacketIn)) {
                                        throw new IllegalStateException("Received a packet that is not a PacketIn");
                                    }

                                    System.out.println(msg.getClass().getSimpleName());
                                    final PlayerSession session = NettyBootstrapper.this.sessionController.getByChId(ctx.channel().id());
                                    NettyBootstrapper.this.eventBus.callEvent(new PacketReceivedEvent(session, (PacketIn) msg));
                                }

                                @Override
                                public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) throws Exception {
                                    cause.printStackTrace();
                                    LOGGER.log(Level.SEVERE, "The Netty bootstrapper caught an exception");
                                }
                            });
                        }
                    });

            // Bind and start to accept incoming connections
            this.future = bootstrap.bind(25565).sync();

            // Wait until the server socket is closed
            this.future.channel().closeFuture().sync();
        } catch (final InterruptedException e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

}
