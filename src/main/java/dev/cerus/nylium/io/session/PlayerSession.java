package dev.cerus.nylium.io.session;

import dev.cerus.nylium.io.packet.PacketOut;
import dev.cerus.nylium.server.entity.PlayerEntity;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;

public class PlayerSession {

    private final ChannelHandlerContext context;
    private PlayerEntity playerEntity;
    private int protocolVer;

    public PlayerSession(final ChannelHandlerContext context) {
        this.context = context;
    }

    public PlayerSession(final ChannelHandlerContext context, final PlayerEntity playerEntity) {
        this.context = context;
        this.playerEntity = playerEntity;
    }

    public void sendPacket(final PacketOut packet) {
        final ByteBuf buf = Unpooled.buffer();

        packet.write(buf);
        final byte[] arr = new byte[buf.readableBytes()];
        buf.getBytes(buf.readerIndex(), arr);

        this.context.writeAndFlush(buf);
    }

    public ChannelHandlerContext getContext() {
        return this.context;
    }

    public PlayerEntity getPlayerEntity() {
        return this.playerEntity;
    }

    public void setPlayerEntity(final PlayerEntity playerEntity) {
        this.playerEntity = playerEntity;
    }

    public int getProtocolVer() {
        return this.protocolVer;
    }

    public void setProtocolVer(final int protocolVer) {
        this.protocolVer = protocolVer;
    }
}
