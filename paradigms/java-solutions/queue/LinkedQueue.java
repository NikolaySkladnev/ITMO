package queue;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LinkedQueue extends AbstractQueue {

    private Node head;
    private Node tail;

    @Override
    protected void enqueueImpl(final Object element) {
        Node new_node = new Node(element, null);

        if (isEmpty()) {
            head = new_node;
            tail = new_node;
        } else {
            tail.next = new_node;
            tail = new_node;
        }
    }

    @Override
    protected Object dequeueImpl() {
        Object result = head.value;
        head = head.next;
        return result;
    }

    @Override
    protected Object elementImpl() {
        return head.value;
    }

    @Override
    protected void clearImpl() {
        head = tail = new Node(0, null);
    }

    public void move()  {
        head = head.next;
    }


    private static class Node {
        private final Object value;
        private Node next;

        public Node(Object value, Node next) {
            this.value = value;
            this.next = next;
        }
    }
}