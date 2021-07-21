package dev.cerus.nylium.io.packet.implementation;

import dev.cerus.nylium.io.IOUtils;
import dev.cerus.nylium.io.packet.PacketOut;
import dev.cerus.nylium.server.key.NamespacedKey;
import dev.cerus.simplenbt.tag.TagCompound;
import io.netty.buffer.ByteBuf;

/**
 * 0x26 https://wiki.vg/Protocol#Join_Game
 */
public class JoinGamePacketOut extends PacketOut {

    private final int entityId;
    private final boolean hardcore;
    private final byte gamemode;
    private final byte previousGamemode;
    private final TagCompound dimensionCodec;
    private final TagCompound dimension;
    private final NamespacedKey worldName;
    private final long hashedSeed;
    private final int maxPlayers;
    private final int viewDistance;
    private final boolean reducedDebugInfo;
    private final boolean enableRespawnScreen;
    private final boolean debug;
    private final boolean flat;
    private final NamespacedKey[] worldNames;

    public JoinGamePacketOut(final int entityId,
                             final boolean hardcore,
                             final byte gamemode,
                             final byte previousGamemode,
                             final NamespacedKey[] worldNames,
                             final TagCompound dimensionCodec,
                             final TagCompound dimension,
                             final NamespacedKey worldName,
                             final long hashedSeed,
                             final int maxPlayers,
                             final int viewDistance,
                             final boolean reducedDebugInfo,
                             final boolean enableRespawnScreen,
                             final boolean debug,
                             final boolean flat) {
        this.entityId = entityId;
        this.hardcore = hardcore;
        this.gamemode = gamemode;
        this.previousGamemode = previousGamemode;
        this.worldNames = worldNames;
        this.dimensionCodec = dimensionCodec;
        this.dimension = dimension;
        this.worldName = worldName;
        this.hashedSeed = hashedSeed;
        this.maxPlayers = maxPlayers;
        this.viewDistance = viewDistance;
        this.reducedDebugInfo = reducedDebugInfo;
        this.enableRespawnScreen = enableRespawnScreen;
        this.debug = debug;
        this.flat = flat;
    }

    @Override
    public void write(final ByteBuf byteBuf) {
        byteBuf.writeInt(this.entityId);
        byteBuf.writeByte(this.hardcore ? 0x01 : 0x00);
        byteBuf.writeByte(this.gamemode);
        byteBuf.writeByte(this.previousGamemode);
        IOUtils.writeVarInt(byteBuf, this.worldNames.length);
        for (final NamespacedKey name : this.worldNames) {
            IOUtils.writeString(byteBuf, name.toString());
        }
        IOUtils.writeNbt(byteBuf, this.dimensionCodec, false, false);
        IOUtils.writeNbt(byteBuf, this.dimension, false, false);
        IOUtils.writeString(byteBuf, this.worldName.toString());
        byteBuf.writeLong(this.hashedSeed);
        IOUtils.writeVarInt(byteBuf, this.maxPlayers);
        IOUtils.writeVarInt(byteBuf, this.viewDistance);
        byteBuf.writeByte(this.reducedDebugInfo ? 0x01 : 0x00);
        byteBuf.writeByte(this.enableRespawnScreen ? 0x01 : 0x00);
        byteBuf.writeByte(this.debug ? 0x01 : 0x00);
        byteBuf.writeByte(this.flat ? 0x01 : 0x00);
    }

}
