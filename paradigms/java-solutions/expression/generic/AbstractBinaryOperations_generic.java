package expression.generic;

abstract class AbstractBinaryOperations_generic<T> implements Generic<T> {
    private final Generic<T> arg1, arg2;
    private final String operChar;

    public AbstractBinaryOperations_generic(Generic<T> arg1, Generic<T> arg2, String operChar) {
        this.arg1 = arg1;
        this.arg2 = arg2;
        this.operChar = operChar;
    }

    @Override
    public String toString() {
        return "(" + arg1.toString() + " " + operChar + " " + arg2.toString() + ")";
    }

    @Override
    public int hashCode() {
        return 31 * getClass().hashCode() + 29 * arg1.hashCode() + 59 * arg2.hashCode();
    }

    public T evaluate(T x, T y, T z) {
        return operation(arg1.evaluate(x, y, z), arg2.evaluate(x, y, z));
    }
    protected abstract T operation(T x, T y);

    @Override
    public boolean equals(Object expression) {
        if (this == expression) return true;
        if (!(expression instanceof Generic<?>)) return false;
        if (getClass() != expression.getClass()) return false;
        AbstractBinaryOperations_generic<?> binary = (AbstractBinaryOperations_generic<?>) expression;
        return arg1.equals(binary.arg1) && arg2.equals(binary.arg2);
    }
}
