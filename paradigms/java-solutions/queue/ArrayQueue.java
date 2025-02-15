package queue;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public class ArrayQueue extends AbstractQueue {

    private Object[] elements = new Object[1];
    private int start = 0;

    // Pred: object != null
    // Post: R = #[i for i in a[0]...a[n]] : predicate(i)] && n' = n && immutable(n)
    public int countIf(Predicate<Object> object) {
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

    @Override
    protected void enqueueImpl(Object element) {
        increaseSize(size + 1);
        elements[(start + size) % elements.length] = element;
    }

    private void increaseSize(int size) {
        if (size == elements.length) {
            Object[] new_elements = new Object[2 * size];
            if (start == 0) {
                System.arraycopy(elements, 0, new_elements, 0, size);
            } else {
                System.arraycopy(elements, start, new_elements, 0, size - start);
                System.arraycopy(elements, 0, new_elements, size - start, (start + super.size) % elements.length);
                start = 0;
            }
            elements = new_elements;
        }
    }

    @Override
    protected Object dequeueImpl() {
        Object result = elements[start];
        elements[start] = null;
        start = (start + 1) % elements.length;

        return result;
    }

    @Override
    protected Object elementImpl() {
        return elements[start];
    }

    @Override
    protected void move() {
        start = (start + 1) % elements.length;
    }

    @Override
    protected void clearImpl() {
        elements = new Object[1];
        start = 0;
    }
}