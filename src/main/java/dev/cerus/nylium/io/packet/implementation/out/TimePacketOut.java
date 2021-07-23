package dev.cerus.nylium.io.packet.implementation.out;

import dev.cerus.nylium.io.IOUtils;
import dev.cerus.nylium.io.packet.PacketOut;
import io.netty.buffer.ByteBuf;

public class TimePacketOut extends PacketOut {

    private final long worldAge;
    private final long time;

    public TimePacketOut(final long worldAge, final long time) {
        this.worldAge = worldAge;
        this.time = time;
    }

    @Override
    public void write(final ByteBuf byteBuf) {
        IOUtils.writeVarInt(byteBuf, 0x58);
        byteBuf.writeLong(this.worldAge);
        byteBuf.writeLong(this.time);
    }

}
