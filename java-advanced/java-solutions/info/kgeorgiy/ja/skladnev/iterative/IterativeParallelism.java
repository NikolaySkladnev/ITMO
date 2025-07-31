package info.kgeorgiy.ja.skladnev.iterative;

import info.kgeorgiy.java.advanced.iterative.ScalarIP;
import info.kgeorgiy.java.advanced.mapper.ParallelMapper;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Scalar iterative parallelism support.
 */
public class IterativeParallelism implements ScalarIP {
    private final ParallelMapper mapper;

    public IterativeParallelism(ParallelMapper mapper) {
        this.mapper = mapper;
    }

    public IterativeParallelism() {
        this.mapper = null;
    }

    /**
     * Returns index of the first maximum.
     *
     * @param threads    number of concurrent threads.
     * @param values     values to find maximum in.
     * @param comparator value comparator.
     * @param <T>        value type.
     * @return index of the first maximum in given values.
     * @throws InterruptedException             if executing thread was interrupted.
     * @throws java.util.NoSuchElementException if no values are given.
     */
    @Override
    public <T> int argMax(int threads, List<T> values, Comparator<? super T> comparator) throws InterruptedException {
        return argBy(threads, values, comparator);
    }

    /**
     * Returns index of the first minimum.
     *
     * @param threads    number of concurrent threads.
     * @param values     values to find minimum in.
     * @param comparator value comparator.
     * @param <T>        value type.
     * @return index of the first minimum in given values.
     * @throws InterruptedException             if executing thread was interrupted.
     * @throws java.util.NoSuchElementException if no values are given.
     */
    @Override
    public <T> int argMin(int threads, List<T> values, Comparator<? super T> comparator) throws InterruptedException {
        return argBy(threads, values, comparator.reversed());
    }

    /**
     * Returns the index of the first value satisfying a predicate.
     *
     * @param threads   number of concurrent threads.
     * @param values    values to test.
     * @param predicate test predicate.
     * @param <T>       value type.
     * @return index of the first value satisfying the predicate, or {@code -1}, if there are none.
     * @throws InterruptedException if executing thread was interrupted.
     */
    @Override
    public <T> int indexOf(int threads, List<T> values, Predicate<? super T> predicate) throws InterruptedException {
        return findIndex(threads, values, predicate,
                el -> el.isEmpty() ? -1 : el.getFirst(),
                results -> results.stream().filter(el -> el != -1).findFirst().orElse(-1)
        );
    }

    /**
     * Returns the index of the last value satisfying a predicate.
     *
     * @param threads   number of concurrent threads.
     * @param values    values to test.
     * @param predicate test predicate.
     * @param <T>       value type.
     * @return index of the last value satisfying the predicate, or {@code -1}, if there are none.
     * @throws InterruptedException if executing thread was interrupted.
     */
    @Override
    public <T> int lastIndexOf(int threads, List<T> values, Predicate<? super T> predicate) throws InterruptedException {
        return findIndex(threads, values, predicate,
                el -> el.isEmpty() ? -1 : el.getLast(),
                results -> results.stream().filter(el -> el != -1).max(Integer::compare).orElse(-1)
        );
    }

    /**
     * Returns sum of the indices of the values satisfying a predicate.
     *
     * @param threads   number of concurrent threads.
     * @param values    values to test.
     * @param predicate test predicate.
     * @param <T>       value type.
     * @return sum of the indices of values satisfying the predicate.
     * @throws InterruptedException if executing thread was interrupted.
     */
    @Override
    public <T> long sumIndices(int threads, List<? extends T> values, Predicate<? super T> predicate) throws InterruptedException {
        List<Long> partials = parallelProcess(threads, values, (sub, base) -> {
            long s = 0;
            for (int i = 0; i < sub.size(); i++) {
                if (predicate.test(sub.get(i))) {
                    s += base + i;
                }
            }
            return s;
        });
        long total = 0;
        for (long p : partials) {
            total += p;
        }
        return total;
    }

    private <T> Integer argBy(int threads, List<T> values, Comparator<? super T> comparator) throws InterruptedException {
        List<Map.Entry<T, Integer>> maxValues = parallelProcess(threads, values, (list, base) -> {
            int localMaxIndex = 0;
            for (int i = 1; i < list.size(); i++) {
                if (comparator.compare(list.get(i), list.get(localMaxIndex)) > 0) {
                    localMaxIndex = i;
                }
            }
            return Map.entry(list.get(localMaxIndex), base + localMaxIndex);
        });
        return Collections.max(maxValues, Map.Entry.comparingByKey(comparator)).getValue();
    }

    private <T> Integer findIndex(int threads, List<T> values, Predicate<? super T> predicate, Function<List<Integer>, Integer> indexFun, Function<List<Integer>, Integer> resultFun) throws InterruptedException {
        List<Integer> result = parallelProcess(threads, values, (list, base) -> {
            List<Integer> answer = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                if (predicate.test(list.get(i))) {
                    answer.add(base + i);
                }
            }
            return indexFun.apply(answer);
        });
        return resultFun.apply(result);
    }

    private <T, R> List<R> parallelProcess(int threads, List<T> values, BiFunction<List<T>, Integer, R> processor) throws InterruptedException {
        if (threads <= 0) {
            throw new InterruptedException("threads must be greater than 0");
        }

        threads = Math.min(threads, values.size());

        int partSize = values.size() / threads;
        int rest = values.size() % threads;

        if (mapper == null) {
            return iterative(threads, values, processor, partSize, rest);
        }

        List<Pair<List<T>, Integer>> tasksData = new ArrayList<>();
        int start = 0;
        for (int i = 0; i < threads; i++) {
            int end = start + partSize + (rest > 0 ? 1 : 0);
            if (rest != 0) rest--;
            tasksData.add(new Pair<>(values.subList(start, end), start));
            start = end;
        }

        return mapper.map(pair -> processor.apply(pair.first, pair.second), tasksData);

    }

    private <T, R> List<R> iterative(int threads, List<T> values, BiFunction<List<T>, Integer, R> processor, int partSize, int rest) throws InterruptedException {
        List<R> results = new ArrayList<>(threads);
        for (int i = 0; i < threads; i++) {
            results.add(null);
        }

        List<Thread> threadList = new ArrayList<>();

        int start = 0;
        for (int i = 0; i < threads; i++) {
            int end = start + partSize + (rest != 0 ? 1 : 0);
            if (rest != 0) rest--;
            final List<T> subList = values.subList(start, end);
            final int finalStart = start;
            final int index = i;

            Thread thread = new Thread(() -> {
                R result = processor.apply(subList, finalStart);
                results.set(index, result);
            });

            threadList.add(thread);
            thread.start();

            start = end;
        }

        for (Thread thread : threadList) {
            thread.join();
        }

        return results;
    }

    private record Pair<T, R>(T first, R second) {
    }
}
