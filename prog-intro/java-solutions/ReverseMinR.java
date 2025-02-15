import java.util.Arrays;
import java.util.Scanner;

public class ReverseMinR {
    public static void main(String[] args) {
        int i = 0;
        int j = 0;
        int min = Integer.MAX_VALUE;
        int[][] arrayOut = new int[1][];
        Scanner scSystem = new Scanner(System.in);

        while (scSystem.hasNextLine()) {
            if (i == arrayOut.length) {
                arrayOut = Arrays.copyOf(arrayOut, arrayOut.length * 2);
            }
            j = 0;
            int[] addLine = new int[1];
            Scanner scLine = new Scanner((scSystem.nextLine()));
            while (scLine.hasNextInt()) {
                if (j == addLine.length) {
                    addLine = Arrays.copyOf(addLine, addLine.length * 2);
                }
                min = Math.min(scLine.nextInt(), min);
                addLine[j] = min;
                j++;
            }
            min = Integer.MAX_VALUE;
            arrayOut[i] = Arrays.copyOf(addLine, j);
	    scLine.close();
            i++;
        }
        arrayOut = Arrays.copyOf(arrayOut, i);
	scSystem.close();
        for (i = 0; i < arrayOut.length; i++) {
            for (j = 0; j < arrayOut[i].length; j++) {
                System.out.print(arrayOut[i][j] + " ");
            }
            System.out.println();
        }
    }
}