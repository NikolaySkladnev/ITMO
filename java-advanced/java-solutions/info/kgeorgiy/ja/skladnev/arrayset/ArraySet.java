package info.kgeorgiy.ja.skladnev.arrayset;

import java.util.*;

public class ArraySet<T> extends AbstractSet<T> implements SortedSet<T> {
    private final List<T> data;
    private final Comparator<? super T> comparator;

    public ArraySet(Collection<? extends T> collection, Comparator<? super T> comparator) {
        TreeSet<T> tmp = new TreeSet<>(comparator);
        tmp.addAll(collection);
        this.data = new ArrayList<>(tmp); // :NOTE: В данном случае будет mutable list, а у нас неизменяемая коллекция
        this.comparator = comparator;
    }

    // :NOTE: Хочется ещё увидеть конструктор, который принимает только компаратор
    public ArraySet(Collection<? extends T> collection) {
        this(collection, null);
    }

    public ArraySet() {
        this(new ArrayList<>(), null);
    }

    @Override
    public Iterator<T> iterator() {
        return Collections.unmodifiableList(data).iterator(); // Можно в конструкторе сделать immutable list и убрать это
    }

    @Override
    public int size() {
        return data.size();
    }

    @Override
    public Comparator<? super T> comparator() {
        return comparator;
    }

    @Override
    public SortedSet<T> subSet(T fromElement, T toElement) {
        if (toComparator().compare(fromElement, toElement) > 0) {
            throw new IllegalArgumentException("fromElement is greater than toElement");
        }

        // :NOTE: Можно реализовать через headSet и tailSet
        return new ArraySet<>(data.subList(index(fromElement), index(toElement)), comparator);
    }

    @Override
    public SortedSet<T> headSet(T toElement) {
        return new ArraySet<>(data.subList(0, index(toElement)), comparator);
    }

    @Override
    public SortedSet<T> tailSet(T fromElement) {
        return new ArraySet<>(data.subList(index(fromElement), data.size()), comparator);
    }

    @Override
    public T first() {
        return data.getFirst();
    }

    @Override
    public T last() {
        return data.getLast();
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean contains(Object element) {
        // :NOTE: Копипаста, вызов Collections.binarySearch несколько раз в коде
        return Collections.binarySearch(data, (T) element, comparator) >= 0;
    }

    // :NOTE: Какой смысл его переопределять, если ровно такая же реализация у родителя?
    @Override
    public boolean containsAll(Collection<?> elements) {
        for (Object element : elements) {
            if (!contains(element)) {
                return false;
            }
        }
        return true;
    }

    // :NOTE: naming? getOrDefault...
    private Comparator<? super T> toComparator() {
        // :NOTE: Вынести DEFAULT_COMPARATOR в константу
        return (comparator == null) ? Collections.reverseOrder().reversed() : comparator;
    }

    private int index(T element) {
        int index = Collections.binarySearch(data, element, toComparator());
        if (index < 0) {
            index = -index - 1;
        }
        return index;
    }
}
