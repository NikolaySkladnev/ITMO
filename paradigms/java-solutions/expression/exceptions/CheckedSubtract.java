package expression.exceptions;

import expression.*;

public class CheckedSubtract extends Subtract {
    public CheckedSubtract(GeneralInterface arg1, GeneralInterface arg2) {
        super(arg1, arg2);
    }

    @Override
    protected int operation(int x, int y) {
        if (y > 0 && Integer.MIN_VALUE + y > x) {
            throw new OverflowException("Subtract: Overflow");
        } else if (y < 0 && Integer.MAX_VALUE + y < x) {
            throw new OverflowException("Subtract: Overflow");
        } else if (x == 0 && y == Integer.MIN_VALUE) {
            throw new OverflowException("Subtract: Overflow");
        }
        return super.operation(x, y);
    }
}
