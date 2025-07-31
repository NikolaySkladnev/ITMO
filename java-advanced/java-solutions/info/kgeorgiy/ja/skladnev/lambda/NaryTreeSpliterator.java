package info.kgeorgiy.ja.skladnev.lambda;

import info.kgeorgiy.java.advanced.lambda.Trees;

import java.util.List;

public class NaryTreeSpliterator<T> extends AbstractTreeSpliterator<T, Trees.Nary<T>> {

    public NaryTreeSpliterator(Trees.Nary<T> node, long size, int characteristics) {
        super(node, size, characteristics);
    }

    @Override
    protected AbstractTreeSpliterator<T, Trees.Nary<T>> createSpliterator(Trees.Nary<T> node) {
        long size = (node instanceof Trees.Leaf<?>) ? 1 : Long.MAX_VALUE;
        int characteristics = ORDERED | IMMUTABLE | ((node instanceof Trees.Leaf<?>) ? SIZED : SUBSIZED);
        return new NaryTreeSpliterator<>(node, size, characteristics);
    }

    @Override
    protected boolean isLeaf(Trees.Nary<T> node) {
        return node instanceof Trees.Leaf;
    }

    @Override
    protected boolean isBranch(Trees.Nary<T> node) {
        return node instanceof Trees.Nary.Node<T>;
    }

    @Override
    protected Trees.Nary<T> getRightHalf(Trees.Nary<T> node) {
        if (node instanceof Trees.Nary.Node<T> nodes) {
            List<Trees.Nary<T>> children = nodes.children();
            return new Trees.Nary.Node<>(children.subList(children.size() / 2, children.size()));
        }
        return null;
    }

    @Override
    protected Trees.Nary<T> getLeftHalf(Trees.Nary<T> node) {
        if (node instanceof Trees.Nary.Node<T> nodes) {
            List<Trees.Nary<T>> children = nodes.children();
            return new Trees.Nary.Node<>(children.subList(0, children.size() / 2));
        }
        return null;
    }

    @Override
    protected void pushChildren(Trees.Nary<T> node) {
        if (node instanceof Trees.Nary.Node<T> nodes) {
            List<Trees.Nary<T>> children = nodes.children();
            for (Trees.Nary<T> child : children.reversed()) {
                stack.push(child);
            }
        }
    }
}
