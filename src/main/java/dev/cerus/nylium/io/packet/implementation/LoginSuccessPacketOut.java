package dev.cerus.nylium.io.packet.implementation;

import dev.cerus.nylium.io.IOUtils;
import dev.cerus.nylium.io.packet.PacketOut;
import io.netty.buffer.ByteBuf;
import java.util.UUID;

public class LoginSuccessPacketOut extends PacketOut {

    private final UUID id;
    private final String username;

    public LoginSuccessPacketOut(final UUID uuid, final String username) {
        this.id = uuid;
        this.username = username;
    }

    @Override
    public void write(final ByteBuf byteBuf) {
        IOUtils.writeVarInt(byteBuf, 0x02);
        IOUtils.writeUuid(byteBuf, this.id);
        IOUtils.writeString(byteBuf, this.username);
    }

}
