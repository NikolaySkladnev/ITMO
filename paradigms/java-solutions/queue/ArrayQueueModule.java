package queue;

import java.util.Objects;
import java.util.function.Predicate;

// Model: a[0]...a[n]
// Inv: n >= 0 && forall i = (0...n): a[i] != null
// Let: immutable(l, r): forall i = (l...r): a'[i] = a[i]
public class ArrayQueueModule {

    private static Object[] elements = new Object[1];
    private static int size = 0;
    private static int start = 0;

    // Pred: object != null
    // Post: R = #[i for i in a[0]...a[n]] : predicate(i)] && n' = n && immutable(n)
    public static int countIf(Predicate<Object> object) {
        Objects.requireNonNull(object);
        int count = 0;
        int index = start;
        for (int i = 0; i < size; i++) {
            if (object.test(elements[index])) {
                count++;
            }
            index = (index + 1) % elements.length;
        }
        return count;
    }

    // Pred: element != null
    // Post: n' = n + 1 && a'[n'] = element && immutable(n)
    public static void enqueue(Object element) {
        Objects.requireNonNull(element);

        increaseSize(size + 1);
        elements[(start + size) % elements.length] = element;
        size++;
    }

    // Pred: true
    // Post: n' = 2 * n && immutable(n)
    private static void increaseSize(int capacity) {
        if (capacity == elements.length) {
            Object[] new_elements = new Object[2 * elements.length];

            if (start == 0) {
                System.arraycopy(elements, 0, new_elements, 0, elements.length);
            } else {
                System.arraycopy(elements, start, new_elements, 0, elements.length - start);
                System.arraycopy(elements, 0, new_elements, elements.length - start, ((start + size) % elements.length));
                start = 0;
            }
            elements = new_elements;
        }
    }

    // Pred: n > 0
    // Post: R = a[0] && immutable(2, n) && for i in range(2, n) : a[i-1] = a[i] && n' = n - 1
    public static Object dequeue() {
        assert size > 0 : "Queue is empty";

        Object res = elements[start];
        elements[start++] = null;
        if (start == elements.length) start = 0;
        size--;
        return res;
    }

    // Pred: n > 0
    // Post: R = a[0] && n' = n && immutable(n)
    public static Object element() {
        assert size > 0 : "Queue is empty.";
        return elements[start];
    }

    // Pred: true
    // Post: R = n && n' = n && immutable(n)
    public static int size() {
        return size;
    }

    // Pred: true
    // Post: n' = 0
    public static void clear() {
        elements = new Object[1];
        start = 0;
        size = 0;
    }

    // Pred: true
    // Post: R = (n == 0) && n' = n && immutable(n)
    public static boolean isEmpty() {
        return size == 0;
    }
}