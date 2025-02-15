import java.io.IOException;
import java.util.Arrays;

public class ReverseMinRAbc {
    public static void main(String[] args) throws IOException {
        int i = 0;
        int j = 0;
        long min = Long.MAX_VALUE;
        long[][] arrayOut = new long[1][];
        OwnScanner scSystem = new OwnScanner(System.in);
        while (scSystem.hasNextLine()) {
            if (i == arrayOut.length) {
                arrayOut = Arrays.copyOf(arrayOut, arrayOut.length * 2);
            }
            j = 0;
            long[] addLine = new long[1];
            OwnScanner scLine = new OwnScanner(scSystem.nextLine());
            while (scLine.hasNextStr()) {
                if (j == addLine.length) {
                    addLine = Arrays.copyOf(addLine, addLine.length * 2);
                }
                min = Math.min(scLine.decode(scLine.nextStr()), min);
                addLine[j] = min;
                j++;
            }
            min = Long.MAX_VALUE;
            arrayOut[i] = Arrays.copyOf(addLine, j);
            i++;
        }
        arrayOut = Arrays.copyOf(arrayOut, i);
        for (i = 0; i < arrayOut.length; i++) {
            for (j = 0; j < arrayOut[i].length; j++) {
                System.out.print(scSystem.encode(arrayOut[i][j]) + " ");
            }
            System.out.println();
        }
    }
}