package serialportutil;

import java.io.IOException;
import java.io.OutputStream;

public class StringOutputStream extends OutputStream {
    private StringBuilder stringBuilder = new StringBuilder();

    @Override
    public void write(int b) throws IOException {
        this.stringBuilder.append((char) b);
    }

    public String toString() {
        return this.stringBuilder.toString();
    }

    public String[] toStrings() {
        String[] items = toString().split("\n");
        return items;
    }

    public void clear() {
        stringBuilder.delete(0, stringBuilder.length());
    }
}