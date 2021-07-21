package dev.cerus.nylium.io.packet.implementation;

import dev.cerus.nylium.io.IOUtils;
import dev.cerus.nylium.io.packet.PacketIn;
import io.netty.buffer.ByteBuf;

/**
 * 0x00 https://wiki.vg/Server_List_Ping#Handshake
 */
public class HandshakePacketIn extends PacketIn {

    private int protocolVer;
    private String serverAddress;
    private short serverPort;
    private int nextState;

    public HandshakePacketIn(final ByteBuf byteBuffer, final int length) {
        super(byteBuffer, length);
    }

    @Override
    protected void read(final ByteBuf byteBuffer, final int length) {
        this.protocolVer = IOUtils.readVarInt(byteBuffer);
        this.serverAddress = IOUtils.readString(byteBuffer);
        this.serverPort = byteBuffer.readShort();
        this.nextState = IOUtils.readVarInt(byteBuffer);
    }

    public int getProtocolVer() {
        return this.protocolVer;
    }

    public String getServerAddress() {
        return this.serverAddress;
    }

    public short getServerPort() {
        return this.serverPort;
    }

    public int getNextState() {
        return this.nextState;
    }

    @Override
    public String toString() {
        return "HandshakePacketIn{" +
                "protocolVer=" + this.protocolVer +
                ", serverAddress='" + this.serverAddress + '\'' +
                ", serverPort=" + this.serverPort +
                ", nextState=" + this.nextState +
                '}';
    }
}
