package expression.generic;

public class Add_generic<T> extends AbstractBinaryOperations_generic<T> {
    private final Operations<T> mode;

    public Add_generic(Generic<T> arg1, Generic<T> arg2, Operations<T> mode) {
        super(arg1, arg2, "+");
        this.mode = mode;
    }

    @Override
    protected T operation(T x, T y) {
        return mode.add(x, y);
    }
}
