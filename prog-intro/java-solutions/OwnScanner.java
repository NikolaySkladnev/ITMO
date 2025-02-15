import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class OwnScanner {
    private final Reader reader;
    private final char[] buffer = new char[128];
    private int currIndexInBuffer = 0;
    private char currSym;
    private char negOneSym;
    private int size = 0;
    private String line;
    private final StringBuilder outputBuilder = new StringBuilder();

    public OwnScanner(Reader toRead) {
        this.reader = toRead;
    }

    public OwnScanner(InputStream toRead) throws FileNotFoundException {
        this(new InputStreamReader(toRead, StandardCharsets.UTF_8));
    }

    public OwnScanner(String toRead) {
        this(new InputStreamReader(new ByteArrayInputStream(toRead.getBytes(StandardCharsets.UTF_8))));
    }

    public boolean bufferFill() throws IOException {
        if (currIndexInBuffer >= size) {
            currIndexInBuffer = 0;
            size = reader.read(buffer);
        }
        return (size != -1);
    }

    public boolean hasNextSym() throws IOException {
        return bufferFill();
    }

    public char nextSym() {
        return buffer[currIndexInBuffer++];
    }

    public boolean hasNextInt() throws IOException {
        while (hasNextSym()) {
            currSym = nextSym();
            if (Character.isDigit(currSym)) {
                return true;
            }
            negOneSym = currSym;
        }
        return false;
    }

    public int nextInt() throws IOException {
        outputBuilder.setLength(0);
        if (Objects.equals(negOneSym, '-')) {
            outputBuilder.append(negOneSym);
            negOneSym = 0;
        }
        outputBuilder.append(currSym);
        while (hasNextSym()) {
            currSym = nextSym();
            if (!(Character.isDigit(currSym))) {
                break;
            }
            outputBuilder.append(currSym);
        }
        return Integer.parseInt(String.valueOf(outputBuilder));
    }

    public boolean hasNextStr() throws IOException {
        while (hasNextSym()) {
            currSym = nextSym();
            if (Character.isLetter(currSym)) {
                return true;
            }
            negOneSym = currSym;
        }
        return false;
    }

    public String nextStr() throws IOException {
        outputBuilder.setLength(0);
        if (Objects.equals(negOneSym, '-')) {
            outputBuilder.append(negOneSym);
            negOneSym = 0;
        }
        outputBuilder.append(currSym);
        while (hasNextSym()) {
            currSym = nextSym();
            if (!(Character.isLetter(currSym))) {
                break;
            }
            outputBuilder.append(currSym);
        }
        return String.valueOf(outputBuilder);
    }

    public boolean hasNextLine() throws IOException {
        outputBuilder.setLength(0);
        while (hasNextSym()) {
            currSym = nextSym();
            if (Objects.equals(currSym, System.lineSeparator().charAt(System.lineSeparator().length() - 1))) {
                line = String.valueOf(outputBuilder);
                return true;
            }
            outputBuilder.append(currSym);
        }
        return false;
    }

    public String nextLine() {
        return line;
    }

    public void close() throws IOException {
        reader.close();
    }

    public String encode(long toEncode) {
        outputBuilder.setLength(0);
        int dif = 'a' - '0';
        String a = String.valueOf(toEncode);
        if (Objects.equals(a.charAt(0), '-')) {
            outputBuilder.append('-');
        } else {
            outputBuilder.append((char) ((int) a.charAt(0) + dif));
        }
        for (int i = 1; i < a.length(); i++) {
            outputBuilder.append((char) ((int) a.charAt(i) + dif));
        }
        return String.valueOf(outputBuilder);
    }

    public long decode(String toEncode) {
        outputBuilder.setLength(0);
        String a = String.valueOf(toEncode);
        if (Objects.equals(a.charAt(0), '-')) {
            outputBuilder.append('-');
        } else {
            outputBuilder.append((char) ((int) a.charAt(0)) - 'a');
        }
        for (int i = 1; i < a.length(); i++) {
            outputBuilder.append((char) ((int) a.charAt(i)) - 'a');
        }
        return Long.parseLong(String.valueOf(outputBuilder));
    }
}