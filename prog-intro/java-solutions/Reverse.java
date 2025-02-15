import java.io.IOException;
import java.util.Arrays;

public class Reverse {
    public static void main(String[] args) throws IOException {
        int i = 0;
        int j = 0;
        String temp;
        int[][] arrayOut = new int[1][];
        OwnScanner scSystem = new OwnScanner(System.in);
        while (scSystem.hasNextLine()) {
            if (i == arrayOut.length) {
                arrayOut = Arrays.copyOf(arrayOut, arrayOut.length * 2);
            }
            j = 0;
            temp = scSystem.nextLine();
            OwnScanner scLine = new OwnScanner(temp);
            int[] addLine = new int[1];
            while (scLine.hasNextInt()) {
                if (j == addLine.length) {
                    addLine = Arrays.copyOf(addLine, addLine.length * 2);
                }
                addLine[j] = scLine.nextInt();
                j++;
            }
            addLine = Arrays.copyOf(addLine, j);
            arrayOut[i] = addLine;
            i++;
            scLine.close();
        }
        scSystem.close();
        arrayOut = Arrays.copyOf(arrayOut, i);
        for (i = (arrayOut.length - 1); i >= 0; i--) {
            for (j = (arrayOut[i].length - 1); j >= 0; j--) {
                System.out.print(arrayOut[i][j] + " ");
            }
            System.out.println();
        }
    }
}