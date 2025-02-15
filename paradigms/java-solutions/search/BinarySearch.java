package search;

public class BinarySearch {

    // Pred: все элементы args цифры, которые вмешаются в 32 бита
    // Post: метод вернёт первый i такой что array[i] <= x, или array.length, если такого i нет.
    public static void main(String[] args) {
        int x = Integer.parseInt(args[0]);
        int[] array = new int[args.length - 1];
        int count = x;
        int temp = 0;
        for (int i = 1; i < args.length; i++) {
            temp = Integer.parseInt(args[i]);
            array[i - 1] = temp;
            count += temp;
        }

        int index;
        if (count % 2 == 0) {
            index = recursiveBinarySearch_new( -1, array.length, x, array);
        } else {
            index = iterativeBinarySearch(x, array);
        }

        if (index == -1) {
            System.out.println(array.length);
        } else {
            System.out.println(index);
        }
    }

    // Pred: forAll i = 0...array.length - 1 : array[i] >= array[i+1] && array.length > 0
    // Post: метод вернёт первый i такой что array[i] <= x, или -1, если такого i нет.
    public static int iterativeBinarySearch(int x, int[] array) {
        // Pred: true
        int l = -1;
        // Post: l = -1
        // Pred: true
        int r = array.length;
        // Post: r = array.length
        // Pred: true
        int m;
        // Post: m = null;

        // Pred: Invariant: l < r
        while (l < r - 1) {
            // Так как l' < r' - 1 - верно -> r' минимум на 2 больше чем l' ->
            // Пусть l' = x, тогда r' = x + 2 ->
            // m' = (2x + 2) / 2 = x + 1 ->
            // x < x + 1 < x + 2 -> l' < m' < r'
            // Pred: true
            m = (l + r) / 2;
            // Post: m = (l + r) / 2

            // Pred: l' < m' < r'
            if (array[m] <= x) {
                // array[m] <= x && l' < m' < r'
                // Pred: l' < m' < r'
                r = m; // Т.к. r минимально подходящее на данном шаге -> числа правее него не подходят
                // l' < m' < r'  && r' = m' ->
                // т.к. r' = m' -> m' новая правая граница, которая меньше прошлой и ближе к x, т.к. массив отсортирован ->
                // Post: l' < r'' && r'' = m'
            } else {
                // array[m] > x && l' < m' < r'
                // Pred: l' < m' < r'
                l = m; // Т.к. array[m] > x && массив отсортирован по не возрастанию -> подходящие нам числа лежат правее ->
                // l' < m' < r' && l' = m' ->
                // т.к. l' = m' -> m' новая левая граница, которая больше прошлой и ближе к x, т.к. массив отсортирован ->
                // -> l'' < r' && l'' = m'
            }
            // Post: l' < r'
        }
        // Post: l' < r' && l >= r - 1
        // Pred: true
        return r;
        // Post: метод вернёт первый i такой что array[i] <= x, или -1, если такого i нет.

    }

    // Pred: forAll i = 0...array.length - 1 : array[i] >= array[i+1] && array.length > 0 && r == -1 && l == array.length
    // Post: метод вернёт первый i такой что array[i] <= x, или array.length, если такого i нет.
    private static int recursiveBinarySearch_new(int l, int r, int x, int[] array) {
        // Pred: forAll i = 0...array.length - 1 : array[i] >= array[i+1] && array.length > 0 && -1 <= r < l <= array.length
        return recursiveBinarySearch(l, r, x, array);
        // Post: метод вернёт первый i такой что array[i] <= x, или array.length, если такого i нет.
    }

    // Pred: forAll i = 0...array.length - 1 : array[i] >= array[i+1] && array.length > 0 && -1 <= r < l <= array.length
    // Post: метод вернёт первый i такой что array[i] <= x, или array.length, если такого i нет.
    public static int recursiveBinarySearch(int l, int r, int x, int[] array) {
        // Pred: true
        int m = (l + r) / 2;
        // Post: m = (l + r) / 2
        // Pred: true
        if (l >= r - 1) {
            // l >= r - 1
            // Pred: l' < r' // прописано в контракте
            return r;
            // Post: метод вернёт первый i такой что array[i] <= x, или array.length, если такого i нет.
        }
        // l < r - 1
        // Так как l' < r' - 1 - верно -> r' минимум на 2 больше чем l' ->
        // Пусть l' = x, тогда r' = x + 2 ->
        // m' = (2x + 2) / 2 = x + 1 ->
        // x < x + 1 < x + 2 -> l' < m' < r'
        if (array[m] <= x) {

            // array[m] <= x && l' < m' < r'
            // Pred: l' < m' < r'
            r = m; // Т.к. r минимально подходящее на данном шаге -> числа правее него не подходят
            // l' < m' < r'  && r' = m' ->
            // т.к. r' = m' -> m' новая правая граница, которая меньше прошлой и ближе к x, т.к. массив отсортирован ->
            // Post: l' < r'' && r'' = m'

            // Pred: forAll i = 0...array.length - 1 : array[i] >= array[i+1] && array.length > 0 && -1 <= r < l <= array.length
            return recursiveBinarySearch(l, r, x, array);
            // Post: метод вернёт первый i такой что array[i] <= x, или array.length, если такого i нет.
        } else {

            // array[m] > x && l' < m' < r'
            // Pred: l' < m' < r'
            l = m; // Т.к. array[m] > x && массив отсортирован по не возрастанию -> подходящие нам числа лежат правее ->
            // l' < m' < r' && l' = m' ->
            // т.к. l' = m' -> m' новая левая граница, которая больше прошлой и ближе к x, т.к. массив отсортирован ->
            // -> l'' < r' && l'' = m'

            // Pred: forAll i = 0...array.length - 1 : array[i] >= array[i+1] && array.length > 0 && -1 <= r < l <= array.length
            return recursiveBinarySearch(l, r, x, array);
            // Post: метод вернёт первый i такой что array[i] <= x, или array.length, если такого i нет.
        }
    }
}