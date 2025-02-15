package game;

import java.io.PrintStream;
import java.util.InputMismatchException;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class HumanPlayer implements Player {
    private final PrintStream out;
    private final Scanner in;


    public HumanPlayer(final PrintStream out, final Scanner in) {
        this.out = out;
        this.in = in;
    }

    public HumanPlayer() {
        this(System.out, new Scanner(System.in));
    }

    @Override
    public Move move(final Position position, final Cell cell) {

        while (true) {
            out.println("Position:");
            out.println(position);
            out.println(cell + "'s move");
            out.println("Enter row and column");

            try {
                String tmp = in.nextLine();
                Scanner moveLine = new Scanner(tmp);
                if (tmp.isEmpty()) {
                    System.out.println("Incorrect Input");
                    continue;
                }
                if (!moveLine.hasNextInt()) throw new InputMismatchException();
                int tmp1 = moveLine.nextInt();
                if (!moveLine.hasNextInt()) throw new InputMismatchException();
                int tmp2 = moveLine.nextInt();
                if (moveLine.hasNext()) {
                    System.out.println("Incorrect Input");
                    continue;
                }
                final Move move = new Move(tmp1 - 1, tmp2 - 1, cell);
                if (position.isValid(move)) {
                    return move;
                }
                final int row = move.getRow();
                final int column = move.getColumn();
                out.println("Move " + move + " is invalid");
            } catch (InputMismatchException error) {
                System.out.println("Incorrect Input");
            } catch (NoSuchElementException error) {
                return new Move(null, null, cell);
            }
        }
    }
}
