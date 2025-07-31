package info.kgeorgiy.ja.skladnev.lambda;

import info.kgeorgiy.java.advanced.lambda.Trees;

public class SizedBinaryTreeSpliterator<T> extends AbstractTreeSpliterator<T, Trees.SizedBinary<T>> {

    public SizedBinaryTreeSpliterator(Trees.SizedBinary<T> node, long size, int characteristics) {
        super(node, size, characteristics);
    }

    @Override
    protected AbstractTreeSpliterator<T, Trees.SizedBinary<T>> createSpliterator(Trees.SizedBinary<T> node) {
        return new SizedBinaryTreeSpliterator<>(node, node.size(), ORDERED | SIZED | SUBSIZED | IMMUTABLE);
    }

    @Override
    protected boolean isLeaf(Trees.SizedBinary<T> node) {
        return node instanceof Trees.Leaf<T>;
    }

    @Override
    protected boolean isBranch(Trees.SizedBinary<T> node) {
        return node instanceof Trees.SizedBinary.Branch<T>;
    }

    @Override
    protected Trees.SizedBinary<T> getRightHalf(Trees.SizedBinary<T> node) {
        if (node instanceof Trees.SizedBinary.Branch<T> branch) {
            return branch.right();
        }
        return null;
    }

    @Override
    protected Trees.SizedBinary<T> getLeftHalf(Trees.SizedBinary<T> node) {
        if (node instanceof Trees.SizedBinary.Branch<T> branch) {
            return branch.left();
        }
        return null;
    }

    @Override
    protected void pushChildren(Trees.SizedBinary<T> node) {
        if (node instanceof Trees.SizedBinary.Branch<T> branch) {
            stack.push(branch.right());
            stack.push(branch.left());
        }
    }
}
