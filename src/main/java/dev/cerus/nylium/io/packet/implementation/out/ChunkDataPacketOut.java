package dev.cerus.nylium.io.packet.implementation.out;

import dev.cerus.nylium.io.IOUtils;
import dev.cerus.nylium.io.packet.PacketOut;
import dev.cerus.nylium.server.chunk.ChunkColumn;
import dev.cerus.nylium.server.chunk.ChunkSection;
import dev.cerus.simplenbt.tag.TagCompound;
import dev.cerus.simplenbt.tag.TagLongArray;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class ChunkDataPacketOut extends PacketOut {

    private final boolean fullChunk;
    private final ChunkColumn chunk;

    public ChunkDataPacketOut(final boolean fullChunk, final ChunkColumn chunk) {
        this.fullChunk = fullChunk;
        this.chunk = chunk;
    }

    @Override
    public void write(final ByteBuf byteBuf) {
        IOUtils.writeVarInt(byteBuf, 0x22);
        byteBuf.writeInt(this.chunk.getX());
        byteBuf.writeInt(this.chunk.getZ());
        //byteBuf.writeByte(this.fullChunk ? 0x01 : 0x00);
        IOUtils.writeVarInt(byteBuf, 1);
        byteBuf.writeLong(this.chunk.calculatePrimaryBitMask());

        final TagCompound rootTag = TagCompound.createRootTag();
        rootTag.set(new TagLongArray("MOTION_BLOCKING", this.chunk.getEncodedHeightMap().getBacking()));
        rootTag.set(new TagLongArray("WORLD_SURFACE", this.chunk.getEncodedHeightMap().getBacking()));
        IOUtils.writeNbt(byteBuf, rootTag, true, false);

        if (this.fullChunk) {
            IOUtils.writeVarInt(byteBuf, this.chunk.getBiomes().length);
            for (final int biome : this.chunk.getBiomes()) {
                IOUtils.writeVarInt(byteBuf, biome);
            }
        }

        final ByteBuf buffer = Unpooled.buffer();
        for (final ChunkSection section : this.chunk.getSections()) {
            if (section == null || section.isEmpty()) {
                continue;
            }

            buffer.writeShort(section.getBlocks());
            buffer.writeByte(section.getBitsPerBlock());
            if (section.getBitsPerBlock() <= 8) {
                IOUtils.writeVarInt(buffer, section.getPalette().getLocalToGlobal().size());
                for (final Integer glob : section.getPalette().getGlobalToLocal().keySet()) {
                    IOUtils.writeVarInt(buffer, glob);
                }
            }
            IOUtils.writeVarInt(buffer, section.getDataArray().length);
            for (final long l : section.getDataArray()) {
                buffer.writeLong(l);
            }
        }

        buffer.readerIndex(0);
        IOUtils.writeVarInt(byteBuf, buffer.readableBytes());
        final byte[] data = new byte[buffer.readableBytes()];
        buffer.readBytes(data);
        byteBuf.writeBytes(data);

        IOUtils.writeVarInt(byteBuf, 0);
    }

}
