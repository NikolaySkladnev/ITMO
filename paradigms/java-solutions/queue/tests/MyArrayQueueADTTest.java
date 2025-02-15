package queue.tests;

import queue.ArrayQueueADT;

public class MyArrayQueueADTTest {
    public static void main(String[] args) {
        ArrayQueueADT queue1 = new ArrayQueueADT();
        ArrayQueueADT queue2 = new ArrayQueueADT();

        fill(queue1, 3);
        fill(queue2, 11);
        System.out.println("-~queue1~-");
        dump(queue1);
        System.out.println("-~queue2~-");
        dump(queue2);
    }

    public static void fill(ArrayQueueADT queue, int x) {
        for (int i = 0; i < 10; i++) {
            ArrayQueueADT.enqueue(queue, i + x);
        }
    }

    public static void dump(ArrayQueueADT queue) {
        while (!ArrayQueueADT.isEmpty(queue)) {
            System.out.println(ArrayQueueADT.size(queue) + " " + ArrayQueueADT.element(queue) + " " + ArrayQueueADT.dequeue(queue));
        }
    }
}
