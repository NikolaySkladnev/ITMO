package expression.generic;

import expression.generic.AbstractBinaryOperations_generic;
import expression.generic.Generic;
import expression.generic.Operations;

public class Subtract_generic<T> extends AbstractBinaryOperations_generic<T> {
    private final Operations<T> mode;

    public Subtract_generic(Generic<T> arg1, Generic<T> arg2, Operations<T> mode) {
        super(arg1, arg2, "-");
        this.mode = mode;
    }

    @Override
    protected T operation(T x, T y) {
        return mode.subtract(x, y);
    }
}
