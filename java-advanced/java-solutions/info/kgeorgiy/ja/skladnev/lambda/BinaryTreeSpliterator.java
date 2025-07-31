package info.kgeorgiy.ja.skladnev.lambda;

import info.kgeorgiy.java.advanced.lambda.Trees;

public class BinaryTreeSpliterator<T> extends AbstractTreeSpliterator<T, Trees.Binary<T>> {

    public BinaryTreeSpliterator(Trees.Binary<T> node, long size, int characteristics) {
        super(node, size, characteristics);
    }

    @Override
    protected AbstractTreeSpliterator<T, Trees.Binary<T>> createSpliterator(Trees.Binary<T> node) {
        long size = (node instanceof Trees.Leaf<?>) ? 1 : Long.MAX_VALUE;
        int characteristics = ORDERED | IMMUTABLE | ((node instanceof Trees.Leaf<?>) ? SIZED : SUBSIZED);
        return new BinaryTreeSpliterator<>(node, size, characteristics);
    }


    @Override
    protected boolean isLeaf(Trees.Binary<T> node) {
        return node instanceof Trees.Leaf<T>;
    }

    @Override
    protected boolean isBranch(Trees.Binary<T> node) {
        return node instanceof Trees.Binary.Branch<T>;
    }

    @Override
    protected Trees.Binary<T> getRightHalf(Trees.Binary<T> node) {
        if (node instanceof Trees.Binary.Branch<T> branch) {
            return branch.right();
        }
        return null;
    }

    @Override
    protected Trees.Binary<T> getLeftHalf(Trees.Binary<T> node) {
        if (node instanceof Trees.Binary.Branch<T> branch) {
            return branch.left();
        }
        return null;
    }

    @Override
    protected void pushChildren(Trees.Binary<T> node) {
        if (node instanceof Trees.Binary.Branch<T> branch) {
            stack.push(branch.right());
            stack.push(branch.left());
        }
    }
}