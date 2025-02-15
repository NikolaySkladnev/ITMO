package game;

import java.util.Arrays;
import java.util.Map;
import java.util.Random;

public class TicTacToeBoard implements Board, Position {
    private static final Map<Cell, Character> SYMBOLS = Map.of(
            Cell.X, 'X',
            Cell.O, 'O',
            Cell.E, '.'
    );

    private final Cell[][] cells;
    private Cell turn;
    int m, n, k, empty;
    Random random = new Random();

    public TicTacToeBoard(int m, int n, int k) {
        this.cells = new Cell[m][n];
        for (Cell[] row : cells) {
            Arrays.fill(row, Cell.E);
        }
        turn = Cell.X;
        this.empty = m * n;
        this.m = m;
        this.n = n;
        this.k = k;
    }

    @Override
    public Position getPosition() {
        return this;
    }

    @Override
    public Cell getCell() {
        return turn;
    }

    private int Check(int currentX, int currentY, int x, int y) {
        int res = 1;
        int invertX = -1 * x;
        int invertY = -1 * y;
        int currentXInvert = currentX;
        int currentYInvert = currentY;
        for (int i = 0; i < k; i++) {
            if (((0 <= (currentX + x)) && ((currentX + x) < n)) && ((0 <= (currentY + y)) && ((currentY + y) < m)) && (cells[currentY + y][currentX + x] == turn)) {
                currentX += x;
                currentY += y;
                res++;
            }
            if (((0 <= (currentXInvert + invertX)) && ((currentXInvert + invertX) < n)) && ((0 <= (currentYInvert + invertY)) && ((currentYInvert + invertY) < m)) && (cells[currentYInvert + invertY][currentXInvert + invertX] == turn)) {
                currentXInvert += invertX;
                currentYInvert += invertY;
                res++;
            }
        } return res;
    }

    @Override
    public Result makeMove(final Move move) {
        if (!isValid(move)) {
            return Result.LOSE;
        }

        int x = move.getColumn();
        int y = move.getRow();

        cells[y][x] = move.getValue();
        empty--;

        if (Check(x, y, 1, 1) >= k || Check(x, y, 1, 0) >= k || Check(x, y, 0, 1) >= k || (Check(x, y, -1, 1) >= k)) {
            return Result.WIN;
        }

        if (empty == 0) {
            return Result.DRAW;
        }

        turn = turn == Cell.X ? Cell.O : Cell.X;
        return Result.UNKNOWN;

    }

    @Override
    public boolean isValid(final Move move) {
        return 0 <= move.getRow() && move.getRow() < m
                && 0 <= move.getColumn() && move.getColumn() < n
                && cells[move.getRow()][move.getColumn()] == Cell.E
                && turn == getCell();
    }

    @Override
    public Cell getCell(final int r, final int c) {
        return cells[r][c];
    }

    @Override
    public String toString() {
        int count = 0;
        final StringBuilder sb = new StringBuilder(" ".repeat(String.valueOf(m).length()));
        for (int r = 0; r < n; r++) {
            sb.append(" ").append(r + 1);
        }
        for (int r = 0; r < m; r++) {
            sb.append(System.lineSeparator());
            sb.append(r + 1).append(" ".repeat(String.valueOf(m).length() - String.valueOf(r + 1).length()));
            for (int c = 0; c < n; c++) {
                sb.append(" ".repeat(String.valueOf(c).length())).append(SYMBOLS.get(cells[r][c]));
            }
        }
        return sb.toString();
    }
}

//        int vertical = 1;
//        int horizontal = 1;
//        int diagonalRight = 1;
//        int diagonalLeft = 1;
//
//        int[] X = new int[]{x, x, x, x, x, x, x, x};
//        int[] Y = new int[]{y, y, y, y, y, y, y, y};
//
//        for (int i = 0; i < k; i++) {
//            if (X[0] + 1 < n && cells[Y[0]][X[0] + 1] == turn) {
//                horizontal++;
//                X[0] += 1;
//            }
//            if (X[1] - 1 >= 0 && cells[Y[1]][X[1] - 1] == turn) {
//                horizontal++;
//                X[1] -= 1;
//            }
//            if (Y[2] + 1 < m && cells[Y[2] + 1][X[2]] == turn) {
//                vertical++;
//                Y[2] += 1;
//            }
//            if (Y[3] - 1 >= 0 && cells[Y[3] - 1][X[3]] == turn) {
//                vertical++;
//                Y[3] -= 1;
//            }
//            if (X[4] + 1 < n && Y[4] + 1 < m && cells[Y[4] + 1][X[4] + 1] == turn) {
//                diagonalRight++;
//                X[4] += 1;
//                Y[4] += 1;
//            }
//            if (X[5] - 1 >= 0 && Y[5] - 1 >= 0 && cells[Y[5] - 1][X[5] - 1] == turn) {
//                diagonalRight++;
//                X[5] -= 1;
//                Y[5] -= 1;
//            }
//            if (X[6] + 1 < n && Y[6] - 1 >= 0 && cells[Y[6] - 1][X[6] + 1] == turn) {
//                diagonalLeft++;
//                X[6] += 1;
//                Y[6] -= 1;
//            }
//            if (X[7] - 1 >= 0 && Y[7] + 1 < m && cells[Y[7] + 1][X[7] - 1] == turn) {
//                diagonalLeft++;
//                X[7] -= 1;
//                Y[7] += 1;
//            }
//        }


