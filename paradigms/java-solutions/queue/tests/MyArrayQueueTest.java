package queue.tests;

import queue.ArrayQueue;

public class MyArrayQueueTest {
    public static void main(String[] args) {
        int[] array = new int[10];
        for (int i = 0; i < 10; i++) {
            array[i] = i + 1;
        }

        ArrayQueue queue = new ArrayQueue();
        fill(queue, array);
        dump(queue);
    }

    public static void fill(ArrayQueue queue, int[] array) {
        for (int i = 0; i < array.length; i++) {
            queue.enqueue(array[i]);
        }
    }

    public static void dump(ArrayQueue queue) {
        while (!queue.isEmpty()) {
            System.out.println(queue.size() + " " + queue.element() + " " + queue.dequeue());
        }
    }
}
