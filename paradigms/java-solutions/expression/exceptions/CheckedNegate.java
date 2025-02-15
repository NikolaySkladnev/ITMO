package expression.exceptions;

import expression.*;

public class CheckedNegate extends Negate {

    public CheckedNegate(GeneralInterface expression) {
        super(expression);
    }

    @Override
    protected int operation(int x) {
        if (x == Integer.MIN_VALUE) throw new OverflowException("Negate: Overflow");
        return super.operation(x);
    }
}
