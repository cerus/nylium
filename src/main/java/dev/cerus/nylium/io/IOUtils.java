package dev.cerus.nylium.io;

import dev.cerus.simplenbt.tag.SimpleNbtUtil;
import dev.cerus.simplenbt.tag.Tag;
import io.netty.buffer.ByteBuf;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class IOUtils {

    private IOUtils() {
    }

    public static void writeNbt(final ByteBuf byteBuf, final Tag<?> tag, final boolean name, final boolean zip) {
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            if (zip) {
                SimpleNbtUtil.writeAndCompressTag(tag, outputStream, name);
            } else {
                SimpleNbtUtil.writeTag(tag, outputStream, name);
            }
        } catch (final IOException e) {
            e.printStackTrace();
        }
        byteBuf.writeBytes(outputStream.toByteArray());
    }

    public static UUID readUuid(final ByteBuf byteBuf) {
        final long mostSig = byteBuf.readLong();
        final long leastSig = byteBuf.readLong();
        return new UUID(mostSig, leastSig);
    }

    public static void writeUuid(final ByteBuf byteBuf, final UUID uuid) {
        byteBuf.writeLong(uuid.getMostSignificantBits());
        byteBuf.writeLong(uuid.getLeastSignificantBits());
    }

    /**
     * Reads a string from a buffer
     *
     * @param byteBuf The buffer
     *
     * @return A string
     */
    public static String readString(final ByteBuf byteBuf) {
        final int len = readVarInt(byteBuf);
        final byte[] bytes = new byte[len];
        byteBuf.readBytes(bytes);
        return new String(bytes);
    }

    /**
     * Writes a var int to a buffer
     *
     * @param byteBuffer The buffer
     * @param value      The var int
     */
    public static void writeVarInt(final ByteBuf byteBuffer, int value) {
        do {
            byte currentByte = (byte) (value & 0b01111111);

            // Note: >>> means that the sign bit is shifted with the rest of the number rather than being left alone
            value >>>= 7;
            if (value != 0) {
                currentByte |= 0b10000000;
            }

            byteBuffer.writeByte(currentByte);
        } while (value != 0);
    }

    /**
     * Writes a string to the provided buffer
     *
     * @param byteBuf The buffer
     * @param s       The string
     */
    public static void writeString(final ByteBuf byteBuf, final String s) {
        writeVarInt(byteBuf, s.length());
        byteBuf.writeBytes(s.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Reads a var int from the buffer
     *
     * @param byteBuf The buffer
     *
     * @return A var int
     */
    public static int readVarInt(final ByteBuf byteBuf) {
        int decodedInt = 0;
        int bitOffset = 0;
        byte currentByte;
        do {
            currentByte = byteBuf.readByte();
            decodedInt |= (currentByte & 0b01111111) << bitOffset;

            if (bitOffset == 35) {
                throw new RuntimeException("VarInt is too big");
            }

            bitOffset += 7;
        } while ((currentByte & 0b10000000) != 0);

        return decodedInt;
    }

    /**
     * Same as {@link IOUtils#readVarInt(ByteBuf)} but does not increment the read counter
     *
     * @param byteBuf The buffer
     *
     * @return A var int
     */
    public static int peekVarInt(final ByteBuf byteBuf) {
        int decodedInt = 0;
        int bitOffset = 0;
        byte currentByte;
        int index = 0;
        do {
            currentByte = byteBuf.getByte(byteBuf.readerIndex() + index++);
            decodedInt |= (currentByte & 0b01111111) << bitOffset;

            if (bitOffset == 35) {
                throw new RuntimeException("VarInt is too big");
            }

            bitOffset += 7;
        } while ((currentByte & 0b10000000) != 0);

        return decodedInt;
    }

    /**
     * Gets the size in bytes for the provided var int
     * <p>
     * Source: https://github.com/Steveice10/PacketLib/blob/master/src/main/java/com/github/steveice10/packetlib/packet/DefaultPacketHeader.java#L23
     *
     * @param varInt The var int
     *
     * @return The size in bytes
     */
    public static int getVarIntSize(final int varInt) {
        if ((varInt & -128) == 0) {
            return 1;
        } else if ((varInt & -16384) == 0) {
            return 2;
        } else if ((varInt & -2097152) == 0) {
            return 3;
        } else if ((varInt & -268435456) == 0) {
            return 4;
        } else {
            return 5;
        }
    }

}
