package expression.generic;

public class Negate_generic<T> extends AbstractUnaryOperation_generic<T> {
    private final Operations<T> mode;

    public Negate_generic(Generic<T> expression, Operations<T> mode) {
        super(expression, "-(", ")");
        this.mode = mode;
    }

    @Override
    protected T operation(T x) {
        return mode.negate(x);
    }
}
