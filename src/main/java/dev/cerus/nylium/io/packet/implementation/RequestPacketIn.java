package dev.cerus.nylium.io.packet.implementation;

import dev.cerus.nylium.io.packet.PacketIn;
import io.netty.buffer.ByteBuf;

/**
 * 0x00 https://wiki.vg/Server_List_Ping#Request
 */
public class RequestPacketIn extends PacketIn {

    public RequestPacketIn(final ByteBuf byteBuffer, final int length) {
        super(byteBuffer, length);
    }

    @Override
    protected void read(final ByteBuf byteBuffer, final int length) {
    }

}
