package info.kgeorgiy.ja.skladnev.lambda;

import info.kgeorgiy.java.advanced.lambda.EasyLambda;
import info.kgeorgiy.java.advanced.lambda.Trees;

import java.util.*;
import java.util.stream.Collector;

import static java.util.Spliterator.*;

public class Lambda implements EasyLambda {

    //Вынестм в абстрактный класс логику, сплититераторы можно сделать как классы
    @Override
    public <T> java.util.Spliterator<T> binaryTreeSpliterator(Trees.Binary<T> tree) {
        long size = (tree instanceof Trees.Leaf<?>) ? 1 : Long.MAX_VALUE;
        int characteristics = ORDERED | IMMUTABLE | ((tree instanceof Trees.Leaf<?>) ? SIZED : SUBSIZED);
        return new BinaryTreeSpliterator<>(tree, size, characteristics);
    }

    @Override
    public <T> java.util.Spliterator<T> sizedBinaryTreeSpliterator(Trees.SizedBinary<T> tree) {
        return new SizedBinaryTreeSpliterator<>(tree, tree.size(), ORDERED | SIZED | SUBSIZED | IMMUTABLE);
    }

    @Override
    public <T> java.util.Spliterator<T> naryTreeSpliterator(Trees.Nary<T> tree) {
        long size = (tree instanceof Trees.Leaf<?>) ? 1 : Long.MAX_VALUE;
        int characteristics = ORDERED | IMMUTABLE | ((tree instanceof Trees.Leaf<?>) ? SIZED : SUBSIZED);
        return new NaryTreeSpliterator<>(tree, size, characteristics);
    }

    //first и last копипаста
    @Override
    public <T> Collector<T, ?, Optional<T>> first() {
        return firstOrLast(true);
    }

    @Override
    public <T> Collector<T, ?, Optional<T>> last() {
        return firstOrLast(false);
    }

    @Override
    public <T> Collector<T, ?, Optional<T>> middle() {
        return Collector.of(
                () -> new Object() {
                    final List<T> list = new ArrayList<>();
                    int index = 0;
                },
                (list, item) -> {
                    list.list.add(item);
                    list.index++;
                    if (list.index % 2 == 0) {
                        list.list.removeFirst();
                    }
                },
                (left, right) -> right,
                list -> list.list.isEmpty() ? Optional.empty() : Optional.of(list.list.getFirst())
        );
    }

    //NOTE -- копипаста prefix и suffix
    @Override
    public Collector<CharSequence, ?, String> commonPrefix() {
        return prefixOrSuffix(true);
    }

    @Override
    public Collector<CharSequence, ?, String> commonSuffix() {
        return prefixOrSuffix(false);
    }

    private <T> Collector<T, ?, Optional<T>> firstOrLast(boolean firstOrLast) {
        return Collector.of(
                () -> new ArrayList<T>(),
                ArrayList::add,
                (left, right) -> right,
                list -> list.isEmpty() ? Optional.empty() : ((firstOrLast) ? Optional.of(list.getFirst()) : Optional.of(list.getLast()))
        );
    }

    private Collector<CharSequence, ?, String> prefixOrSuffix(boolean prefixOrSuffix) {
        return Collector.of(
                () -> new ArrayList<String>(),
                (list, str) -> list.add(str.toString()),
                (left, right) -> {
                    left.addAll(right);
                    return left;
                },
                list -> {
                    if (list.isEmpty()) {
                        return "";
                    }

                    String spfix = list.getFirst();
                    for (String str : list) {
                        int minLength = Math.min(spfix.length(), str.length());
                        int i = 0;

                        while (i < minLength && spfix.charAt(prefixOrSuffix ? i : spfix.length() - 1 - i) == str.charAt(prefixOrSuffix ? i : str.length() - 1 - i)) {
                            i++;
                        }

                        spfix = prefixOrSuffix ? spfix.substring(0, i) : spfix.substring(spfix.length() - i);
                    }
                    return spfix;
                }
        );
    }
}
