package game;

import java.util.*;

public class Olympiad {
    private final int m, n, k;
    private final List<Player> playerList;
    private final List<Integer> players;
    private final List<Integer> tmpWinOnPlace = new ArrayList<>();
    private final Map<Integer, Integer> stat = new LinkedHashMap<>();

    public Olympiad(List<Player> playerList, ArrayList<Integer> players, int m, int n, int k) {
        this.playerList = playerList;
        this.players = players;
        this.m = m;
        this.n = n;
        this.k = k;
    }

    private void dataUpdate(int win, int lost, int place) {
        tmpWinOnPlace.add(win);
        stat.put(lost, place + 1);
        players.remove((Integer) lost);
    }
    
    public void play() {

        int first, second;

        int places = (int) Math.ceil(Math.log10(players.size()) / Math.log10(2));
        Random random = new Random();

        while (places != 0) {
            int size = players.size();
            first = players.get(random.nextInt(size));
            while (tmpWinOnPlace.contains(first)) {
                first = players.get(random.nextInt(size));
            }
            second = players.get(random.nextInt(size));
            while (tmpWinOnPlace.contains(second) || second == first) {
                second = players.get(random.nextInt(size));
            }

            final Game game = new Game(false, playerList.get(first), playerList.get(second));
            int result;
            do {
                result = game.play(new TicTacToeBoard(m, n, k));
                if (result == 1) {
                    dataUpdate(first, second, places);
                } else if (result == 2) {
                    dataUpdate(second, first, places);
                }
            } while (result == 0);

            if (Math.log10(players.size()) / Math.log10(2) % 1 == 0) {
                tmpWinOnPlace.clear();
                places--;
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();
        for (int n : stat.keySet()) {
            output.append("Player №").append(n + 1).append(" lost. Place: ").append(stat.get(n)).append(System.lineSeparator());
        }
        output.append("Player №").append(players.get(0) + 1).append(" won!");
        return String.valueOf(output);
    }
}