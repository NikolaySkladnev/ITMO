package game;

public final class Move {
    private final int row;
    private final int column;
    private final Cell value;

    public Move(final Integer row, final Integer column, final Cell value) {
        if (row == null || column == null || value == null) {
            this.row = -1;
            this.column = -1;
            this.value = Cell.E;
        } else {
            this.row = row;
            this.column = column;
            this.value = value;
        }
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public Cell getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "row = " + (row + 1) + ", column = " + (column + 1) + ", value = " + value;
    }
}