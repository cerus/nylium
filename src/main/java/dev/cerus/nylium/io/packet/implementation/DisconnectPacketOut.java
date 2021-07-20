package dev.cerus.nylium.io.packet.implementation;

import dev.cerus.nylium.io.IOUtils;
import dev.cerus.nylium.io.packet.PacketOut;
import io.netty.buffer.ByteBuf;

public class DisconnectPacketOut extends PacketOut {

    private final boolean login;

    public DisconnectPacketOut(final boolean login) {
        this.login = login;
    }

    @Override
    public void write(final ByteBuf byteBuf) {
        IOUtils.writeVarInt(byteBuf, this.login ? 0x00 : 0x1A);
        IOUtils.writeString(byteBuf, "{\"text\":\"muss los\"}");
    }

}
