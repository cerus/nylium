package dev.cerus.nylium.nbt;

import dev.cerus.simplenbt.tag.Tag;
import dev.cerus.simplenbt.tag.TagByte;
import dev.cerus.simplenbt.tag.TagByteArray;
import dev.cerus.simplenbt.tag.TagCompound;
import dev.cerus.simplenbt.tag.TagDouble;
import dev.cerus.simplenbt.tag.TagFloat;
import dev.cerus.simplenbt.tag.TagInt;
import dev.cerus.simplenbt.tag.TagIntArray;
import dev.cerus.simplenbt.tag.TagList;
import dev.cerus.simplenbt.tag.TagLong;
import dev.cerus.simplenbt.tag.TagLongArray;
import dev.cerus.simplenbt.tag.TagShort;
import dev.cerus.simplenbt.tag.TagString;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//
// !!WARNING!!
// The code below will make you cry!
// If you really need to take a look at it take this safety pig with you.
//
//  _._ _..._ .-',     _.._(`))
// '-. `     '  /-._.-'    ',/
//    )         \            '.
//   / _    _    |             \
//  |  a    a    /              |
//  \   .-.                     ;
//   '-('' ).-'       ,'       ;
//      '-;           |      .'
//         \           \    /
//         | 7  .__  _.-\   \
//         | |  |  ``/  /`  /
//        /,_|  |   /,_/   /
//           /,_/      '`-'
//
// YOU HAVE BEEN WARNED.
//

// TODO: Remove this horrible piece of garbage and replace it
@Deprecated
public class SNBTReader {

    public static Tag<?> readTag(final String snbt) {
        final CharArrayInputStream inputStream = new CharArrayInputStream(snbt.toCharArray());
        return readTag(inputStream);
    }

    public static Tag<?> readTag(final CharArrayInputStream inputStream) {
        while (Character.isWhitespace(inputStream.get(inputStream.getReaderIndex()))) {
            inputStream.readChar();
        }

        char c = inputStream.get(inputStream.getReaderIndex());
        if (c == '"' || c == '\'') {
            // string
            final String str = readString(inputStream);
            if (inputStream.canRead() && inputStream.get(inputStream.getReaderIndex()) == ':') {
                // tag
                inputStream.readChar()/* + " should be :")*/;
                final Tag<?> tag = readTag(inputStream);
                tag.setName(str);
                return tag;
            } else {
                return new TagString("", str);
            }
        } else if ((c >= '0' && c <= '9') || (c == '-' && inputStream.get(inputStream.getReaderIndex() + 1) >= '0'
                && inputStream.get(inputStream.getReaderIndex() + 1) <= '9')) {
            // number
            final Number number = readNumber(inputStream);
            if (number instanceof Byte) {
                return new TagByte("", (Byte) number);
            } else if (number instanceof Integer) {
                return new TagInt("", (Integer) number);
            } else if (number instanceof Long) {
                return new TagLong("", (Long) number);
            } else if (number instanceof Double) {
                return new TagDouble("", (Double) number);
            } else if (number instanceof Float) {
                return new TagFloat("", (Float) number);
            } else if (number instanceof Short) {
                return new TagShort("", (Short) number);
            } else {
                return null;
            }
        } else if (c == '[') {
            //array / list
            return readArrayOrList(inputStream);
        } else if (c == '{') {
            // compound
            inputStream.readChar()/* + " should be {")*/;// opening {
            final List<Tag<?>> list = new ArrayList<>();

            while (Character.isWhitespace(inputStream.get(inputStream.getReaderIndex()))) {
                inputStream.readChar();
            }
            while (inputStream.canRead() && inputStream.get(inputStream.getReaderIndex()) != '}') {
                while (Character.isWhitespace(inputStream.get(inputStream.getReaderIndex()))) {
                    inputStream.readChar();
                }

                list.add(readTag(inputStream));

                while (inputStream.canRead() && Character.isWhitespace(inputStream.get(inputStream.getReaderIndex()))) {
                    inputStream.readChar();
                }
                if (inputStream.canRead() && inputStream.get(inputStream.getReaderIndex()) == ',') {
                    inputStream.readChar()/* + " should be ,")*/; //,
                }
                while (inputStream.canRead() && Character.isWhitespace(inputStream.get(inputStream.getReaderIndex()))) {
                    inputStream.readChar();
                }
            }
            while (inputStream.canRead() && Character.isWhitespace(inputStream.get(inputStream.getReaderIndex()))) {
                inputStream.readChar();
            }

            inputStream.readChar()/* + " shouzld be closing")*/;// closing }

            while (inputStream.canRead() && Character.isWhitespace(inputStream.get(inputStream.getReaderIndex()))) {
                inputStream.readChar();
            }

            final TagCompound temp = new TagCompound("", list);
            return temp;
        } else if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) {
            if (isBool(inputStream)) {
                return parseBool(inputStream);
            }

            final StringBuilder buffer = new StringBuilder();
            while ((c = inputStream.get(inputStream.getReaderIndex())) != ':') {
                buffer.append(c);
                inputStream.readChar();
            }

            inputStream.readChar()/* + " should be :")*/; // :
            while (Character.isWhitespace(inputStream.get(inputStream.getReaderIndex()))) {
                inputStream.readChar();
            }
            final Tag<?> tag = readTag(inputStream);
            tag.setName(buffer.toString());
            return tag;
        } else {
            return null;
        }
    }

    private static TagByte parseBool(final CharArrayInputStream inputStream) {
        if ((inputStream.get(inputStream.getReaderIndex()) == 't'
                && inputStream.get(inputStream.getReaderIndex() + 1) == 'r'
                && inputStream.get(inputStream.getReaderIndex() + 2) == 'u'
                && inputStream.get(inputStream.getReaderIndex() + 3) == 'e')) {
            for (int i = 0; i < 4; i++) {
                inputStream.readChar();
            }
            return new TagByte("", (byte) 0x01);
        } else {
            for (int i = 0; i < 5; i++) {
                inputStream.readChar();
            }
            return new TagByte("", (byte) 0x00);
        }
    }

    private static boolean isBool(final CharArrayInputStream inputStream) {
        return (inputStream.get(inputStream.getReaderIndex()) == 't'
                && inputStream.get(inputStream.getReaderIndex() + 1) == 'r'
                && inputStream.get(inputStream.getReaderIndex() + 2) == 'u'
                && inputStream.get(inputStream.getReaderIndex() + 3) == 'e') ||
                (inputStream.get(inputStream.getReaderIndex()) == 'f'
                        && inputStream.get(inputStream.getReaderIndex() + 1) == 'a'
                        && inputStream.get(inputStream.getReaderIndex() + 2) == 'l'
                        && inputStream.get(inputStream.getReaderIndex() + 3) == 's'
                        && inputStream.get(inputStream.getReaderIndex() + 4) == 'e');
    }

    private static Tag<?> readArrayOrList(final CharArrayInputStream inputStream) {
        inputStream.readChar(); // skip [
        if (inputStream.get(inputStream.getReaderIndex() + 1) == ';') {
            // array
            char c;
            final StringBuilder buffer = new StringBuilder();
            while ((c = inputStream.readChar()) != ']') {
                buffer.append(c);
            }

            final String[] split = buffer.toString().split(";")[1].split(",");
            switch (c) {
                case 'I':
                    return new TagIntArray("", Arrays.stream(split).mapToInt(s -> Integer.parseInt(s.trim())).toArray());
                case 'L':
                    return new TagLongArray("", Arrays.stream(split).mapToLong(s -> Long.parseLong(s.trim())).toArray());
                case 'B':
                    final Byte[] bytes = Arrays.stream(split).map(s -> Byte.parseByte(s.trim())).toArray(Byte[]::new);
                    final byte[] arr = new byte[bytes.length];
                    for (int i = 0; i < bytes.length; i++) {
                        arr[i] = bytes[i];
                    }
                    return new TagByteArray("", arr);
                default:
                    return null;
            }
        } else {
            // list
            final List<Tag<?>> list = new ArrayList<>();
            char c;
            while (inputStream.canRead() && (c = inputStream.get(inputStream.getReaderIndex())) != ']') {
                while (Character.isWhitespace(inputStream.get(inputStream.getReaderIndex()))) {
                    inputStream.readChar();
                }
                final Tag<?> tag = readTag(inputStream);
                list.add(tag);

                while (Character.isWhitespace(inputStream.get(inputStream.getReaderIndex()))) {
                    inputStream.readChar();
                }

                if (inputStream.readChar() == ']') {
                    break;
                }

                inputStream.readChar(); // ,
                while (Character.isWhitespace(inputStream.get(inputStream.getReaderIndex()))) {
                    inputStream.readChar();
                }
            }

            while (inputStream.canRead() && Character.isWhitespace(inputStream.get(inputStream.getReaderIndex()))) {
                inputStream.readChar();
            }
            if (inputStream.canRead()) {
                inputStream.readChar(); //,
            }
            while (inputStream.canRead() && Character.isWhitespace(inputStream.get(inputStream.getReaderIndex()))) {
                inputStream.readChar();
            }

            return new TagList("", list, list.isEmpty() ? 1 : list.get(0).getId());
        }
    }

    private static String readString(final CharArrayInputStream inputStream) {
        char c = inputStream.readChar();
        final boolean singleQuote = c == '\'';
        boolean escaped = false;

        final StringBuilder buffer = new StringBuilder();
        while (true) {
            c = inputStream.readChar();
            if (c == (singleQuote ? '\'' : '"')) {
                if (!escaped) {
                    break;
                }
                escaped = false;
            }
            if (c == '\\') {
                if (!escaped) {
                    escaped = true;
                } else {
                    buffer.append(c);
                    escaped = false;
                }
            } else {
                buffer.append(c);
            }
        }
        return buffer.toString();
    }

    private static Number readNumber(final CharArrayInputStream inputStream) {
        final StringBuilder buffer = new StringBuilder();
        char c = '\0';
        while (inputStream.canRead() && ((c = inputStream.get(inputStream.getReaderIndex())) >= '0' && c <= '9' || c == '.' || c == '-')) {
            buffer.append(c);
            inputStream.readChar();
        }

        switch (c) {
            case 'b':
                inputStream.readChar();
                return Byte.parseByte(buffer.toString());
            case 'L':
                inputStream.readChar();
                return Long.parseLong(buffer.toString());
            case 'd':
                inputStream.readChar();
                return Double.parseDouble(buffer.toString());
            case 'f':
                inputStream.readChar();
                return Float.parseFloat(buffer.toString());
            case 's':
                inputStream.readChar();
                return Short.parseShort(buffer.toString());
            default:
                return Integer.parseInt(buffer.toString());
        }
    }

}
