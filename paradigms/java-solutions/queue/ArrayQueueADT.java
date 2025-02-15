package queue;

import java.util.Objects;
import java.util.function.Predicate;

// Model: a[0]...a[n]
// Inv: n >= 0 && forall i = (0...n): a[i] != null
// Let: immutable(l, r): forall i = (l...r): a'[i] = a[i]
public class ArrayQueueADT {

    private Object[] elements = new Object[1];
    private int start = 0;
    private int size = 0;

    // Pred: element != null && queue != null
    // Post: n' = n + 1 && a'[n'] = element && immutable(n)
    public static void enqueue(ArrayQueueADT queue, final Object element) {

        Objects.requireNonNull(queue);
        Objects.requireNonNull(element);

        increaseSize(queue, queue.size + 1);
        queue.elements[(queue.start + queue.size) % queue.elements.length] = element;
        queue.size++;
    }

    // Pred: object != null && queue != null
    // Post: R = #[i for i in a[0]...a[n]] : predicate(i)] && n' = n && immutable(n)
    public static int countIf(ArrayQueueADT queue, Predicate<Object> object) {
        Objects.requireNonNull(object);
        int count = 0;
        int index = queue.start;
        for (int i = 0; i < queue.size; i++) {
            if (object.test(queue.elements[index])) {
                count++;
            }
            index = (index + 1) % queue.elements.length;
        }
        return count;
    }

    // Pred: queue != null
    // Post: n' = 2 * n && immutable(n)
    private static void increaseSize(ArrayQueueADT queue, int capacity) {
        Objects.requireNonNull(queue);

        if (capacity == queue.elements.length) {
            Object[] new_elements = new Object[2 * queue.elements.length];

            if (queue.start == 0) {
                System.arraycopy(queue.elements, 0, new_elements, 0, queue.elements.length);
            } else {
                System.arraycopy(queue.elements, queue.start, new_elements, 0, queue.elements.length - queue.start);
                System.arraycopy(queue.elements, 0, new_elements, queue.elements.length - queue.start, (queue.start + queue.size) % queue.elements.length);
                queue.start = 0;
            }
            queue.elements = new_elements;
        }
    }

    // Pred: n > 0
    // Post: R = a[0] && immutable(2, n) && for i in range(2, n) : a[i-1] = a[i] && n' = n - 1
    public static Object dequeue(ArrayQueueADT queue) {

        Objects.requireNonNull(queue);
        assert queue.size > 0 : "Queue is empty.";

        Object res = queue.elements[queue.start];
        queue.elements[queue.start++] = null;
        if (queue.start == queue.elements.length) queue.start = 0;
        queue.size--;
        return res;
    }

    // Pred: n > 0
    // Post: R = a[0] && n' = n && immutable(n)
    public static Object element(ArrayQueueADT queue) {

        Objects.requireNonNull(queue);
        assert queue.size > 0 : "Queue is empty.";

        return queue.elements[queue.start];
    }

    // Pred: queue != null
    // Post: R = n && n' = n && immutable(n)
    public static int size(ArrayQueueADT queue) {

        Objects.requireNonNull(queue);

        return queue.size;
    }

    // Pred: queue != null
    // Post: n' = 0
    public static void clear(ArrayQueueADT queue) {

        Objects.requireNonNull(queue);

        queue.elements = new Object[1];
        queue.start = 0;
        queue.size = 0;
    }

    // Pred: queue != null
    // Post: R = (n == 0) && n' = n && immutable(n)
    public static boolean isEmpty(ArrayQueueADT queue) {

        Objects.requireNonNull(queue);

        return queue.size == 0;
    }
}
