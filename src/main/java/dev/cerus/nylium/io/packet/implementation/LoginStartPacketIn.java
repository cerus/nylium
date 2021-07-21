package dev.cerus.nylium.io.packet.implementation;

import dev.cerus.nylium.io.IOUtils;
import dev.cerus.nylium.io.packet.PacketIn;
import io.netty.buffer.ByteBuf;

/**
 * 0x00 https://wiki.vg/Protocol#Login_Start
 */
public class LoginStartPacketIn extends PacketIn {

    private String username;

    public LoginStartPacketIn(final ByteBuf byteBuffer) {
        super(byteBuffer);
    }

    @Override
    protected void read(final ByteBuf byteBuffer) {
        this.username = IOUtils.readString(byteBuffer);
    }

    public String getUsername() {
        return this.username;
    }

}
