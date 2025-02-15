package game;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        ArrayList<Integer> players = new ArrayList<>();
        int m, n, k, numOfPlayers, whatGame;
        String tmp;
        Scanner scanner = new Scanner(System.in);
        while (true) {
            try {
                System.out.println("Enter MNK:");
                tmp = scanner.nextLine();
                if (tmp.isEmpty()) throw new InputMismatchException();
                Scanner mnk = new Scanner(tmp);
                if (!mnk.hasNextInt()) throw new InputMismatchException("No numbers");
                m = mnk.nextInt();
                if (!mnk.hasNextInt()) throw new InputMismatchException("Only one numbers found");
                n = mnk.nextInt();
                if (!mnk.hasNextInt()) throw new InputMismatchException("Only two numbers found");
                k = mnk.nextInt();
                if (mnk.hasNext()) throw new InputMismatchException();
                if (!(m == 1 & n == 1 & k == 1) & (k <= 0 || m <= 0 || n <= 0 || (k > m & k > n) || ((m == 1) & (n < 2 * k)) || ((n == 1) & (m < 2 * k)))) {
                    System.out.println("The arguments of the game were entered incorrectly, please restart the game with the correct arguments.");
                    continue;
                }
                mnk.close();
            } catch (InputMismatchException error) {
                System.out.println("Incorrect Input");
                continue;
            } catch (NoSuchElementException error) {
                System.out.println("End of programme");
                break;
            }
            while (true) {
                try {
                    System.out.println("If you want to play tournament enter 1, else enter 0:");
                    tmp = scanner.nextLine();
                    if (tmp.isEmpty()) throw new InputMismatchException();
                    Scanner gameType = new Scanner(tmp);
                    if (!gameType.hasNextInt()) throw new InputMismatchException();
                    whatGame = gameType.nextInt();
                    if (gameType.hasNext()) throw new InputMismatchException();
                    gameType.close();
                    if (whatGame == 1) {
                        while (true) {
                            try {
                                System.out.println("Enter amount of players:");
                                tmp = scanner.nextLine();
                                if (tmp.isEmpty()) throw new InputMismatchException();
                                Scanner playersNum = new Scanner(tmp);
                                if (!playersNum.hasNextInt()) throw new InputMismatchException();
                                numOfPlayers = playersNum.nextInt();
                                if (numOfPlayers < 2 || playersNum.hasNext()) throw new InputMismatchException();
                                playersNum.close();
                                List<Player> playerList = new ArrayList<>();
                                for (int i = 0; i < numOfPlayers; i++) {
                                    playerList.add(new RandomPlayer(m, n));
                                    players.add(i);
                                }

                                Olympiad olympiad = new Olympiad(playerList, players, m, n, k);
                                olympiad.play();
                                System.out.println(olympiad);
                                scanner.close();
                            } catch (InputMismatchException error) {
                                System.out.println("Incorrect Input");
                                continue;
                            } catch (NoSuchElementException error) {
                                System.out.println("End of programme");
                                break;
                            }
                            break;
                        }
                    } else if (whatGame == 0) {
                        final Game game = new Game(true, new HumanPlayer(), new HumanPlayer());
                        int result;
                        result = game.play(new TicTacToeBoard(m, n, k));
                        System.out.println("Game result: " + result);
                        scanner.close();
                        break;
                    } else {
                        System.out.println("Incorrect Input");
                        continue;
                    }
                } catch (InputMismatchException error) {
                    System.out.println("Incorrect Input");
                    continue;
                } catch (NoSuchElementException error) {
                    System.out.println("End of programme");
                    break;
                }
                break;
            }
            break;
        }
    }
}
