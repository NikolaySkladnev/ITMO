package expression.generic;

abstract class AbstractUnaryOperation_generic<T> implements Generic<T> {

    private final Generic<T> expression;
    private final String start;
    private final String end;

    public AbstractUnaryOperation_generic(Generic<T> expression, String start, String end) {
        this.expression = expression;
        this.start = start;
        this.end = end;
    }

    public T evaluate(T x, T y, T z) {
        return operation(expression.evaluate(x, y, z));
    }

    public int hashCode() {
        return expression.hashCode() * 37;
    }

    public String toString() {
        return start + expression + end;
    }

    public boolean equals(Object expression) {
        if (this == expression) return true;
        if (!(expression instanceof Generic<?>)) return false;
        if (getClass() != expression.getClass()) return false;
        return this.expression.equals(expression);
    }

    protected abstract T operation(T x);

}