package expression.exceptions;

import expression.*;

public class CheckedDivide extends Divide {
    public CheckedDivide(GeneralInterface arg1, GeneralInterface arg2) {
        super(arg1, arg2);
    }

    @Override
    protected int operation(int x, int y) {
        if (y == 0) throw new DivisionByZeroException("Division by zero");
        if (y == -1 && x == Integer.MIN_VALUE) {
            throw new OverflowException("Divide: Overflow");
        }
        return super.operation(x, y);
    }
}
