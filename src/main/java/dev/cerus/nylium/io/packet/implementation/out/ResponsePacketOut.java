package dev.cerus.nylium.io.packet.implementation.out;

import dev.cerus.nylium.io.IOUtils;
import dev.cerus.nylium.io.packet.PacketOut;
import io.netty.buffer.ByteBuf;

/**
 * 0x00 https://wiki.vg/Server_List_Ping#Response
 */
public class ResponsePacketOut extends PacketOut {

    private final String response;

    public ResponsePacketOut(final String response) {
        this.response = response;
    }

    @Override
    public void write(final ByteBuf byteBuf) {
        //IOUtils.writeVarInt(byteBuf, IOUtils.getVarIntSize(0x00) + IOUtils.getVarIntSize(this.response.length()) + this.response.length());
        IOUtils.writeVarInt(byteBuf, 0x00);
        IOUtils.writeString(byteBuf, this.response);
    }

}
