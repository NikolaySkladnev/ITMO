package queue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public abstract class AbstractQueue implements Queue{

    protected int size = 0;

    public void enqueue(final Object element) {
        enqueueImpl(Objects.requireNonNull(element));
        size++;
    }

    protected abstract void enqueueImpl(Object element);

    public Object dequeue() {
        assert size > 0 : "Queue is empty.";
        size--;

        return dequeueImpl();
    }

    protected abstract Object dequeueImpl();

    public Object element() {
        assert size > 0 : "Queue is empty.";
        return elementImpl();
    }

    protected abstract Object elementImpl();

    public void clear() {
        clearImpl();
        size = 0;
    }

    public void dedup() {
        if (size > 1) {
            Object[] save = new Object[size];
            Object lastValue = element();
            Object currValue;
            int j = 0;
            save[j++] = lastValue;
            move();

            for (int i = 0; i < size - 1; i++) {
                currValue = element();
                if (!Objects.equals(lastValue, currValue)) {
                    save[j++] = currValue;
                }
                lastValue = currValue;
                move();
            }

            clear();
            for (Object object : save) {
                if (object == null) {
                    break;
                }
                enqueue(object);
            }
        }
    }

    protected abstract void move();

    protected abstract void clearImpl();

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }
}
