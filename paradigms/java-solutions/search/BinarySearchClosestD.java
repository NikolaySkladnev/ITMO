package search;
// ∃ ∀
public class BinarySearchClosestD {

    // :NOTE: args[i] is Integer
    // Pred: forAll i = 1...args.length - 1 : args[i] is Integer && INTEGER.MIN_VALUE <= args[i] <= INTEGER.MAX_VALUE && args[i] >= args[i+1] && args.length > 0 && INTEGER.MIN_VALUE <= args[0] <= INTEGER.MAX_VALUE
    // Post: if (∃ i : array[i] <= x): R = array[i] : (array[i] <= x && i - min) else: R = array.length
    // метод вернёт первый i такой что array[i] <= x, или array.length, если такого i нет.
    public static void main(String[] args) {
        // :NOTE: contract?
        // Pred: true
        int x = Integer.parseInt(args[0]);
        // Post: x = Integer.parseInt(args[0])
        // Pred: true
        int[] array = new int[args.length - 1];
        // Post: array = new int[args.length - 1]
        // Pred:
        int count = x;
        // Post: count = x
        // Pred: true
        int temp;
        // Post: temp = null
        for (int i = 1; i < args.length; i++) {
            // Pred: true
            temp = Integer.parseInt(args[i]);
            // Post: temp = Integer.parseInt(args[i])
            // Pred: true
            array[i - 1] = temp;
            // Post: array[i - 1] = temp
            // Pred: true
            count += temp;
            // Post: count = sum_(j = 0)^(i) array[j]
        }
        // Post: count = sum_(j = 0)^(array.length - 1) array[j]
        // Pred: forAll i = 1...args.length - 1 : args[i] is Integer && INTEGER.MIN_VALUE <= args[i] <= INTEGER.MAX_VALUE && args[i] >= args[i+1] && args.length > 0
        if (count % 2 == 0) {
            // count % 2 == 0 -> run recursive
            // Pred: forAll i = 1...args.length - 1 : args[i] is Integer && INTEGER.MIN_VALUE <= args[i] <= INTEGER.MAX_VALUE && args[i] >= args[i+1] && args.length > 0
            System.out.println(recursiveBinarySearch_new( -1, array.length, x, array));
            // Post: if (∃ i : array[i] <= x): R = array[i] : (array[i] <= x && i - min) else: R = array.length
        } else {
            // count % 2 != 0 -> run iterative
            // Pred: forAll i = 1...args.length - 1 : args[i] is Integer && INTEGER.MIN_VALUE <= args[i] <= INTEGER.MAX_VALUE && args[i] >= args[i+1] && args.length > 0
            System.out.println(iterativeBinarySearch(x, array));
            // Post: if (∃ i : array[i] <= x): R = array[i] : (array[i] <= x && i - min) else: R = array.length
        }
    }

    // Pred: forAll i = 0...array.length - 1 : array[i] >= array[i+1] && array.length > 0
    // :NOTE: Не совпадает с main
    // Post: if (∃ i : array[i] <= x): R = array[i] : (array[i] <= x && i - min) else: R = array.length
    // метод вернёт первый i такой что array[i] <= x, или array.length, если такого i нет.
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
            // l' < r' - 1 -> r' - l' > 1 -> (r' минимум на 2 больше чем l')
            // ⊐ l' = x -> r' = x + t , t >= 2 ->
            // m' = (2x + t) / 2 = x + t/2 ->
            // x < x + t/2 < x + t -> l' < m' < r'
            // Pred: true
            m = (l + r) / 2;
            // Post: m = (l + r) / 2

            // Pred: l' < m' < r'
            if (array[m] <= x) {
                // array[m] <= x && l' < m' < r'
                // Pred: l' < m' < r'
                r = m;
                // l' < m' < r' && r' := m' ->
                // l' < r' := m', m' < r' -> |array[m'] - x| <= |array[r'] - x| (forAll i = 0...array.length - 1 : array[i] >= array[i+1)
                // && m' < r' (значение на новой границе ближе к x и индекс границы меньше предыдущего) ->
                // Post: l' < r'
            } else {
                // array[m] > x && l' < m' < r'
                // Pred: l' < m' < r'
                l = m;
                // l' < m' < r' && l' := m' ->
                // l' := m' < r', l' < m' -> |array[m'] - x| <= |array[l'] - x| (forAll i = 0...array.length - 1 : array[i] >= array[i+1)
                // && l' < m' (значение новой границе ближе к x)
                // -> l'' < r'
            }
            // Post: l' < r'
        }
        // Post: l' < r' && l' >= r' - 1 ==> r' - l' == 1
        // (r' и l' два ближайших к x, т.к. массив отсортирован и r' - l' == 1)

        // Pred: r' - l' == 1
        if (l <= -1 || (r < array.length && Math.abs(array[r] - x) < Math.abs(array[l] - x))) {
            // l' < r' && l' <= -1 || (r' < array.length && Math.abs(array[r'] - x) < Math.abs(array[l'] - x))
            // if (l <= -1) : array[r] else: min(Math.abs(array[r'] - x), Math.abs(array[l'] - x))
            // (Если l <= -1 если число слева не определенно, возьмём ближайшее к x число справа
            // Если l > -1 сравним разницу между x и ближайшими к нему числами и найдём минимальную)
            // Pred: r' < array.length
            return array[r];
            // Post: if (∃ i : array[i] <= x): R = array[i] : (array[i] <= x && i - min) else: R = array.length
        } else {
            // l' < r' && l' > -1 && (r' >= array.length || Math.abs(array[r'] - x) >= Math.abs(array[l'] - x))
            // if (r >= array.length) : array[l] else: min(Math.abs(array[r'] - x), Math.abs(array[l'] - x))
            // Если r >= array.length если число справа не определенно, возьмём ближайшее к x число слева
            // Если l > -1 сравним разницу между x и ближайшими к нему числами и найдём минимальную)
            // Pred: -1 < l'
            return array[l];
            // Post: if (∃ i : array[i] <= x): R = array[i] : (array[i] <= x && i - min) else: R = array.length
        }
        // Post: if (∃ i : array[i] <= x): R = array[i] : (array[i] <= x && i - min) else: R = array.length
    }

    // Pred: forAll i = 0...array.length - 1 : array[i] >= array[i+1] && array.length > 0 && l == -1 && r == array.length
    // Post: if (∃ i : array[i] <= x): R = array[i] : (array[i] <= x && i - min) else: R = array.length
    // метод вернёт первый i такой что array[i] <= x, или -1, если такого i нет.
    public static int recursiveBinarySearch_new(int l, int r, int x, int[] array) {
        // :NOTE: array.length (l) < -1 (r) ?
        // Pred: forAll i = 0...array.length - 1 : array[i] >= array[i+1] && array.length > 0 && -1 <= l < r <= array.length
        return recursiveBinarySearch(l, r, x, array);
        // Post: if (∃ i : array[i] <= x): R = array[i] : (array[i] <= x && i - min) else: R = array.length
    }

    // Pred: forAll i = 0...array.length - 1 : array[i] >= array[i+1] && array.length > 0 && -1 <= l < r <= array.length
    // Post: if (∃ i : array[i] <= x): R = array[i] : (array[i] <= x && i - min) else: R = array.length
    // метод вернёт первый i такой что array[i] <= x, или -1, если такого i нет.
    private static int recursiveBinarySearch(int l, int r, int x, int[] array) {
        // Pred: true
        int m = (l + r) / 2;
        // Post: m = (l + r) / 2
        // Pred: true
        if (l >= r - 1) {
            // l >= r - 1 && l' < r' (прописано в контракте) => r' - l' == 1
            // r' и l' два ближайших к x, т.к. массив отсортирован и r' - l' == 1

            // Pred: l' < r'
            if (l <= -1 || (r < array.length && Math.abs(array[r] - x) < Math.abs(array[l] - x))) {
                // l' < r' && l' <= -1 || (r' < array.length && Math.abs(array[r'] - x) < Math.abs(array[l'] - x))
                // if (l <= -1) : array[r] else: min(Math.abs(array[r'] - x), Math.abs(array[l'] - x))
                // (Если l <= -1 если число слева не определенно, возьмём ближайшее к x число справа
                // Если l > -1 сравним разницу между x и ближайшими к нему числами и найдём минимальную)
                // Pred: r' < array.length
                return array[r];
                // Post: if (∃ i : array[i] <= x): R = array[i] : (array[i] <= x && i - min) else: R = array.length
            } else {
                // l' < r' && l' > -1 && (r' >= array.length || Math.abs(array[r'] - x) >= Math.abs(array[l'] - x))
                // if (r >= array.length) : array[l] else: min(Math.abs(array[r'] - x), Math.abs(array[l'] - x))
                // Если r >= array.length если число справа не определенно, возьмём ближайшее к x число слева
                // Если l > -1 сравним разницу между x и ближайшими к нему числами и найдём минимальную)
                // Pred: -1 < l'
                return array[l];
                // Post: if (∃ i : array[i] <= x): R = array[i] : (array[i] <= x && i - min) else: R = array.length
            }
            // Post: if (∃ i : array[i] <= x): R = array[i] : (array[i] <= x && i - min) else: R = array.length
        }
        // l < r - 1
        // l' < r' - 1 -> r' - l' > 1 -> (r' минимум на 2 больше чем l')
        // ⊐ l' = x -> r' = x + t , t >= 2 ->
        // m' = (2x + t) / 2 = x + t/2 ->
        // x < x + t/2 < x + t -> l' < m' < r'
        if (array[m] <= x) {

            // array[m] <= x && l' < m' < r'
            // Pred: l' < m' < r'
            r = m;
            // l' < m' < r' && r' := m' ->
            // l' < r' := m', m' < r' -> |array[m'] - x| <= |array[r'] - x| (forAll i = 0...array.length - 1 : array[i] >= array[i+1)
            // && m' < r' (значение на новой границе ближе к x и индекс границы меньше предыдущего) ->
            // Post: l' < r'

            // Pred: forAll i = 0...array.length - 1 : array[i] >= array[i+1] && array.length > 0 && -1 <= r < l <= array.length
            return recursiveBinarySearch(l, r, x, array);
            // Post: if (∃ i : array[i] <= x): R = array[i] : (array[i] <= x && i - min) else: R = array.length
        } else {

            // array[m] > x && l' < m' < r'
            // Pred: l' < m' < r'
            l = m;
            // l' < m' < r' && l' := m' ->
            // l' := m' < r', l' < m' -> |array[m'] - x| <= |array[l'] - x| (forAll i = 0...array.length - 1 : array[i] >= array[i+1)
            // && l' < m' (значение новой границе ближе к x)
            // -> l'' < r'

            // Pred: forAll i = 0...array.length - 1 : array[i] >= array[i+1] && array.length > 0 && -1 <= r < l <= array.length
            return recursiveBinarySearch(l, r, x, array);

            // Post: if (∃ i : array[i] <= x): R = array[i] : (array[i] <= x && i - min) else: R = array.length
        }
    }
}
