package dev.cerus.nylium.io;

import io.netty.buffer.ByteBuf;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class IOUtils {

    private IOUtils() {
    }

    public static String readString(final ByteBuf byteBuf) {
        final int len = readVarInt(byteBuf);
        final byte[] bytes = new byte[len];
        byteBuf.readBytes(bytes);
        return new String(bytes);
    }

    public static int readVarInt(final ByteBuffer byteBuf) {
        int decodedInt = 0;
        int bitOffset = 0;
        byte currentByte;
        do {
            currentByte = byteBuf.get();
            decodedInt |= (currentByte & 0b01111111) << bitOffset;

            if (bitOffset == 35) {
                throw new RuntimeException("VarInt is too big");
            }

            bitOffset += 7;
        } while ((currentByte & 0b10000000) != 0);

        return decodedInt;
    }

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

    public static void writeString(final ByteBuf byteBuf, final String s) {
        writeVarInt(byteBuf, s.length());
        byteBuf.writeBytes(s.getBytes(StandardCharsets.UTF_8));
    }


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
