package dev.cerus.nylium.nbt;

import java.io.IOException;
import java.io.InputStream;

public class CharArrayInputStream extends InputStream {

    private final char[] chars;
    private int readerIndex = 0;

    public CharArrayInputStream(final char[] chars) {
        this.chars = chars;
    }

    @Override
    public int read() throws IOException {
        return this.readerIndex >= this.chars.length ? -1 : this.chars[this.readerIndex++];
    }

    public char readChar() {
        return this.readerIndex >= this.chars.length ? '\0' : this.chars[this.readerIndex++];
    }

    public boolean canRead() {
        return this.readerIndex > 0 && this.readerIndex < this.chars.length;
    }

    public char[] readFully() {
        final char[] arr = new char[this.chars.length - this.readerIndex];
        final int i = 0;
        while (this.canRead()) {
            arr[i] = this.readChar();
        }
        return arr;
    }

    public char get(final int index) {
        return this.chars[index];
    }

    public int getReaderIndex() {
        return this.readerIndex;
    }

    public void setReaderIndex(final int readerIndex) {
        this.readerIndex = readerIndex;
    }
}
