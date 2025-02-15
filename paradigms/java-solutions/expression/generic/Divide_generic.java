package expression.generic;

public class Divide_generic<T> extends AbstractBinaryOperations_generic<T> {
    private final Operations<T> mode;

    public Divide_generic(Generic<T> arg1, Generic<T> arg2, Operations<T> mode) {
        super(arg1, arg2, "/");
        this.mode = mode;
    }

    @Override
    protected T operation(T x, T y) {
        return mode.divide(x, y);
    }
}
