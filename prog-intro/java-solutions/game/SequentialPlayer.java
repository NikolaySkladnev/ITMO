package game;

public class SequentialPlayer implements Player {
    int m, n;

    public SequentialPlayer(int m, int n) {
        this.m = m;
        this.n = n;
    }
    @Override
    public Move move(final Position position, final Cell cell) {
        for (int r = 0; r < m; r++) {
            for (int c = 0; c < n; c++) {
                final Move move = new Move(r, c, cell);
                if (position.isValid(move)) {
                    return move;
                }
            }
        }
        throw new IllegalStateException("No valid moves");
    }
}
