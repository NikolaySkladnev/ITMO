package expression.exceptions;

import expression.*;

public class CheckedMultiply extends Multiply {
    public CheckedMultiply(GeneralInterface arg1, GeneralInterface arg2) {
        super(arg1, arg2);
    }

    @Override
    protected int operation(int x, int y) {
        if (y == -1 && x == Integer.MIN_VALUE) {
            throw new OverflowException("Multiply: Overflow");
        } else if (y > 0 && (x < Integer.MIN_VALUE / y || x > Integer.MAX_VALUE / y)) {
            throw new OverflowException("Multiply: Overflow");
        } else if (y < 0 && y != -1 && (x < Integer.MAX_VALUE / y || x > Integer.MIN_VALUE / y)) {
            throw new OverflowException("Multiply: Overflow");
        }
        return super.operation(x, y);
    }
}
