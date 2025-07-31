package info.kgeorgiy.ja.skladnev.iterative;

import info.kgeorgiy.java.advanced.mapper.ParallelMapper;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.function.Function;

/**
 * Maps function over lists.
 */
public class ParallelMapperImpl implements ParallelMapper {
    private final List<Thread> threads;
    private final Queue<Runnable> tasks;

    /**
     * Creates set number of threads to monitor task's queue.
     *
     * @param threads number of threads to create
     */
    public ParallelMapperImpl(int threads) {
        if (threads <= 0) {
            throw new IllegalArgumentException("Error: number of threads must be greater than 0");
        }

        this.threads = new ArrayList<>();
        this.tasks = new ArrayDeque<>();

        for (int i = 0; i < threads; i++) {
            this.threads.add(new Thread(() -> {
                try {
                    while (true) {
                        Runnable task;
                        synchronized (tasks) {
                            while (tasks.isEmpty()) {
                                tasks.wait();
                            }

                            task = tasks.poll();
                            tasks.notify();
                        }
                        task.run();
                    }
                } catch (InterruptedException _) {
                }
            }));

            this.threads.get(i).start();
        }
    }

    /**
     * Maps function {@code f} over specified {@code items}.
     * Mapping for each item is performed in parallel.
     *
     * @throws InterruptedException if calling thread was interrupted
     */
    @Override
    public <T, R> List<R> map(Function<? super T, ? extends R> f, List<? extends T> items) throws InterruptedException {
        final List<R> result = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            result.add(null);
        }
        //note -- shared lock
        final Object lock = new Object();
        final int[] counter = {0};

        for (int i = 0; i < items.size(); i++) {
            final int finalI = i;
            Runnable task = () -> {
                R value = f.apply(items.get(finalI));
                synchronized (lock) {
                    result.set(finalI, value);
                    counter[0]++;
                    if (counter[0] == items.size()) {
                        lock.notify();
                    }
                }
            };

            synchronized (tasks) {
                tasks.add(task);
                tasks.notify();
            }
        }

        synchronized (lock) {
            while (counter[0] < items.size()) {
                lock.wait();
            }
        }

        return result;
    }

    /**
     * Stops all threads.
     * all unfinished mappings are left in undefined state.
     */
    @Override
    public void close() {
        threads.forEach(Thread::interrupt);
        threads.forEach(thread -> {
            //note -- дождаться join в случае если падает ie (в while обернуть)
            try {
                thread.join();
            } catch (InterruptedException _) {
            }
        });
    }
}
