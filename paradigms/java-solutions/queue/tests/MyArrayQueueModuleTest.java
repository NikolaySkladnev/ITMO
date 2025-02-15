package queue.tests;

import queue.ArrayQueueModule;

public class MyArrayQueueModuleTest {

    public static void main(String[] args) {
        fill();
        dump();
    }

    public static void fill() {
        for (int i = 0; i < 10; i++) {
            ArrayQueueModule.enqueue(i * 10);

        }
    }

    public static void dump() {
        while (!ArrayQueueModule.isEmpty()) {
            System.out.println(ArrayQueueModule.size() + " " + ArrayQueueModule.element() + " " + ArrayQueueModule.dequeue());
        }
    }
}

