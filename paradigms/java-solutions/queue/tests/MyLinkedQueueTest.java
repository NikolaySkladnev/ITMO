package queue.tests;

import queue.LinkedQueue;

public class MyLinkedQueueTest {
    public static void main(String[] args) {
        LinkedQueue queue = new LinkedQueue();
        queue.enqueue(1);
        queue.enqueue(5);
        queue.enqueue(7);
        queue.enqueue(9);

//        queue.to_string();
//        System.out.println();
//        queue.to_string();
//        System.out.println();

        queue.enqueue(8);

//        queue.to_string();
//        System.out.println();
//        queue.to_string();
//        System.out.println();
    }

    public static void dump(LinkedQueue stack) {
        while (!stack.isEmpty()) {
            System.out.println(stack.size() + " " + stack.element() + " " + stack.dequeue());
        }
    }

    public static void fill(LinkedQueue stack) {
        for (int i = 0; i < 10; i++) {
            stack.enqueue(i);
        }
    }
}
