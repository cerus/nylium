package dev.cerus.nylium.server.chunk;

import dev.cerus.nylium.server.block.BlockRegistry;
import java.util.Set;

public class ChunkSection {

    private final SectionPalette palette;
    private int blocks;
    private VariableValueArray dataArray;

    public ChunkSection() {
        this(0, new SectionPalette(), new VariableValueArray(4, 4096));
    }

    public ChunkSection(final SectionPalette palette, final VariableValueArray dataArray) {
        this.palette = palette;
        this.dataArray = dataArray;

        this.blocks = 0;
        for (int i = 0; i < 4096; i++) {
            if (palette.global(dataArray.get(i)) != 0) {
                this.blocks++;
            }
        }
    }

    public ChunkSection(final int blocks, final SectionPalette palette, final VariableValueArray dataArray) {
        this.blocks = blocks;
        this.palette = palette;
        this.dataArray = dataArray;
    }

    private int index(final int x, final int y, final int z) {
        return (y & 0xf) << 8 | z << 4 | x;
    }

    public void setBlock(final int x, final int y, final int z, final int globalId) {
        // Calculate index for coords
        final int index = this.index(x, y, z);

        // Get current block at index
        final int prev = this.dataArray.get(index);
        // Check if current block is not air
        if (BlockRegistry.getState(prev) != BlockRegistry.AIR) {
            // If new block is air, decrease block count
            if (globalId == 0) {
                this.blocks--;
            }

            // Count instances of the current block
            int instances = 0;
            for (int i = 0; i < 4096; i++) {
                final int val = this.dataArray.get(i);
                if (val == prev) {
                    instances++;
                }
            }

            // If the current block is the only instance of that block, remove from the block palette
            if (instances == 1) {
                // Unmap block
                this.palette.unmapG(this.palette.global(prev));
                this.palette.unmapL(prev);

                // Decrease mapping for every entry that was above the block
                final Set<Integer> integers = this.palette.getLocalToGlobal().keySet();
                for (final Integer integer : integers) {
                    if (integer < prev) {
                        continue;
                    }

                    // This is really expensive, probably not a good idea
                    for (int i = 0; i < 4096; i++) {
                        if (this.dataArray.get(i) == integer) {
                            this.dataArray.set(i, integer - 1);
                        }
                    }

                    // Map decreased int
                    this.palette.map(integer - 1, this.palette.global(integer));
                    this.palette.unmapL(integer);
                }
            }
        } else if (globalId != 0) {
            // If current block is air and new block is not air, increase block count
            this.blocks++;
        }

        // Map new block if palette does not contain it
        if (!this.palette.contains(globalId)) {
            this.palette.map(this.palette.nextFreeLocal(4096), globalId);
        }
        // Set new block
        // If bits-per-block is > 8, use global palette id
        this.dataArray.set(index, this.palette.bitsPerBlock() > 8 ? globalId : this.palette.local(globalId));

        // Do we need to resize the array?
        if (this.palette.bitsPerBlock() > this.dataArray.getBitsPerValue() || this.palette.bitsPerBlock() < this.dataArray.getBitsPerValue()) {
            // Don't resize to lower than 4
            if (this.palette.bitsPerBlock() <= 4 && this.dataArray.getBitsPerValue() == 4) {
                return;
            }

            // Store current bits and resize
            final int prevBits = this.dataArray.getBitsPerValue();
            this.dataArray = this.dataArray.resize(Math.max(this.palette.bitsPerBlock(), 4));

            // If palette bits are > 8 and previous array bits are <= 8, switch to global ids
            if (this.palette.bitsPerBlock() > 8 && prevBits <= 8) {
                // use global
                for (int i = 0; i < 4096; i++) {
                    this.dataArray.set(i, this.palette.global(this.dataArray.get(i)));
                }
            } else if (this.palette.bitsPerBlock() <= 8 && prevBits > 8) {
                // If palette bits are <= 8 and previous array bits are > 8, switch to local ids
                // use local
                for (int i = 0; i < 4096; i++) {
                    this.dataArray.set(i, this.palette.local(this.dataArray.get(i)));
                }
            }
        }
    }

    public int getBlock(final int x, final int y, final int z) {
        return this.palette.global(this.dataArray.get(this.index(x, y, z)));
    }

    public void recalculate() {
        this.blocks = 0;
        for (int i = 0; i < 4096; i++) {
            if (this.dataArray.get(i) != 0) {
                this.blocks++;
            }
        }
    }

    public SectionPalette getPalette() {
        return this.palette;
    }

    public int getBitsPerBlock() {
        return this.dataArray.getBitsPerValue();
    }

    public long[] getDataArray() {
        return this.dataArray.getBacking();
    }

    public int getBlocks() {
        return this.blocks;
    }

    public boolean isEmpty() {
        return this.getBlocks() == 0;
    }

}
