package dev.cerus.nylium.io.packet.implementation;

import dev.cerus.nylium.io.IOUtils;
import dev.cerus.nylium.io.packet.PacketIn;
import dev.cerus.nylium.io.session.PlayerSession;
import io.netty.buffer.ByteBuf;

/**
 * 0x05 https://wiki.vg/Protocol#Client_Settings
 */
public class ClientSettingsPacketIn extends PacketIn {

    private PlayerSession.Settings settings;

    public ClientSettingsPacketIn(final ByteBuf byteBuffer) {
        super(byteBuffer);
    }

    @Override
    protected void read(final ByteBuf byteBuffer) {
        this.settings = new PlayerSession.Settings(
                IOUtils.readString(byteBuffer),
                byteBuffer.readByte(),
                PlayerSession.Settings.ChatMode.getById(IOUtils.readVarInt(byteBuffer)),
                byteBuffer.readByte() == 0x01,
                new PlayerSession.Settings.SkinParts(byteBuffer.readByte()),
                byteBuffer.readByte() == 0x01,
                byteBuffer.readByte() == 0x01
        );
    }

    public PlayerSession.Settings getSettings() {
        return this.settings;
    }

}
