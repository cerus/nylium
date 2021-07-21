package dev.cerus.nylium.server.dimension;

import dev.cerus.nylium.NyliumLauncher;
import dev.cerus.simplenbt.tag.SimpleNbtUtil;
import dev.cerus.simplenbt.tag.TagCompound;
import dev.cerus.simplenbt.tag.TagInt;
import dev.cerus.simplenbt.tag.TagList;
import java.io.IOException;
import java.io.InputStream;

public class DimensionCodec {

    private static TagCompound dimensionCodecCompound;

    public static void initialize() throws IOException {
        final InputStream inputStream = NyliumLauncher.class.getClassLoader().getResourceAsStream("dimension_codec.nbt");
        dimensionCodecCompound = SimpleNbtUtil.readCompound(inputStream);
    }

    public static TagCompound getDimensionCodec() {
        return dimensionCodecCompound;
    }

    public static TagCompound getDimensionType(final int id) {
        final TagList tagList = ((TagCompound) dimensionCodecCompound.get("minecraft:dimension_type")).get("value");
        return tagList.getValue().stream()
                .map(tag -> (TagCompound) tag)
                .filter(tagCompound -> ((TagInt) tagCompound.get("id")).getValue() == id)
                .map(tagCompound -> (TagCompound) tagCompound.get("element"))
                .findAny()
                .orElse(null);
    }

}
