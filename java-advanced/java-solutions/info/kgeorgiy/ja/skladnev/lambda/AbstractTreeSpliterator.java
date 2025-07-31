package info.kgeorgiy.ja.skladnev.lambda;

import info.kgeorgiy.java.advanced.lambda.Trees;

import java.util.*;
import java.util.function.Consumer;

public abstract class AbstractTreeSpliterator<T, N> extends Spliterators.AbstractSpliterator<T> {

    protected final Deque<N> stack = new ArrayDeque<>();

    protected AbstractTreeSpliterator(N node, long size, int characteristics) {
        super(size, characteristics);
        stack.push(node);
    }

    @Override
    public boolean tryAdvance(Consumer<? super T> action) {
        while (!stack.isEmpty()) {
            N node = stack.pop();
            if (isLeaf(node)) {
                action.accept(getLeafValue(node));
                return true;
            }
            pushChildren(node);
        }
        return false;
    }

    @Override
    public Spliterator<T> trySplit() {
        N node = stack.pop();
        if (isBranch(node)) {
            stack.push(getRightHalf(node));
            return createSpliterator(getLeftHalf(node));
        } else {
            stack.push(node);
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private T getLeafValue(N node) {
        if (node instanceof Trees.Leaf<?> leaf) {
            return (T) leaf.value();
        }
        return null;
    }

    protected abstract AbstractTreeSpliterator<T, N> createSpliterator(N node);

    protected abstract boolean isLeaf(N node);

    protected abstract boolean isBranch(N node);

    protected abstract N getRightHalf(N node);

    protected abstract N getLeftHalf(N node);

    protected abstract void pushChildren(N node);
}