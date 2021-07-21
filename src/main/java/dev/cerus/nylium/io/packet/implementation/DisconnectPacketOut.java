package dev.cerus.nylium.io.packet.implementation;

import dev.cerus.nylium.io.IOUtils;
import dev.cerus.nylium.io.packet.PacketOut;
import io.netty.buffer.ByteBuf;

/**
 * 0x1A https://wiki.vg/Protocol#Disconnect_.28play.29
 * TODO: Implement proper chat components
 */
public class DisconnectPacketOut extends PacketOut {

    private final boolean login;
    private final String message;

    public DisconnectPacketOut(final boolean login, final String message) {
        this.login = login;
        this.message = message;
    }

    @Override
    public void write(final ByteBuf byteBuf) {
        IOUtils.writeVarInt(byteBuf, this.login ? 0x00 : 0x1A);
        IOUtils.writeString(byteBuf, "{\"text\":\"" + this.message + "\"}");
    }

}
