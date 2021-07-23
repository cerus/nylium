package dev.cerus.nylium.server.chunk;

import dev.cerus.nylium.server.block.BlockRegistry;
import dev.cerus.nylium.server.block.states.IdentifiableState;
import java.util.Arrays;

public class ChunkColumn {

    private final ChunkSection[] sections = new ChunkSection[16];
    private final int[] biomes = new int[1024];
    private final int[] heightMap = new int[16 * 16];
    private final int x;
    private final int z;

    public ChunkColumn(final int x, final int z) {
        this.x = x;
        this.z = z;
        Arrays.fill(this.biomes, 127);
    }

    public void setBlock(final int x, final int y, final int z, final IdentifiableState state) {
        this.setBlock(x, y, z, BlockRegistry.getProtocolId(state));
    }

    public void setBlock(final int x, final int y, final int z, final int globalId) {
        final int secIdx = y / 16;
        if (this.sections[secIdx] == null) {
            if (globalId == 0) {
                return;
            }
            this.sections[secIdx] = new ChunkSection();
        }

        // Set block
        this.sections[secIdx].setBlock(x, y % 16, z, globalId);

        // Recalculate height map
        int highest = 0;
        for (int yy = 0; yy < 256; yy++) {
            final int protocolId = BlockRegistry.getProtocolId(this.getBlock(x, yy, z));
            if (protocolId != 0 && highest < yy) {
                highest = yy;
            }
        }
        this.heightMap[x + z * 16] = highest;
    }

    public IdentifiableState getBlock(final int x, final int y, final int z) {
        return BlockRegistry.getState(this.getBlockId(x, y, z));
    }

    public int getBlockId(final int x, final int y, final int z) {
        final int secIdx = y / 16;
        if (this.sections[secIdx] == null) {
            return 0;
        }
        return this.sections[secIdx].getBlock(x, y, z);
    }

    public void setBiome(final int x, final int y, final int z, final int biome) {
        final int index = ((y >> 2) & 63) << 4 | ((z >> 2) & 3) << 2 | ((x >> 2) & 3);
        this.biomes[index] = biome;
    }

    public int getBiome(final int x, final int y, final int z) {
        final int index = ((y >> 2) & 63) << 4 | ((z >> 2) & 3) << 2 | ((x >> 2) & 3);
        return this.biomes[index];
    }

    public long calculatePrimaryBitMask() {
        long n = 0;
        for (int i = 0; i < 16; i++) {
            n |= (this.sections[i] != null && !this.sections[i].isEmpty() ? 1 : 0) << i;
        }
        return n;
    }

    public int[] getHeightMap() {
        return this.heightMap;
    }

    public HeightmapArray getEncodedHeightMap() {
        final HeightmapArray array = new HeightmapArray();
        for (int i = 0; i < this.heightMap.length; i++) {
            array.set(i, this.heightMap[i]);
        }
        return array;
    }

    public int getX() {
        return this.x;
    }

    public int getZ() {
        return this.z;
    }

    public ChunkSection[] getSections() {
        return this.sections;
    }

    public int[] getBiomes() {
        return this.biomes;
    }

}
