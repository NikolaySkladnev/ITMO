package expression.exceptions;

import expression.*;

public class CheckedAdd extends Add {
    public CheckedAdd(GeneralInterface arg1, GeneralInterface arg2) {
        super(arg1, arg2);
    }

    @Override
    protected int operation(int x, int y) {
        if (y > 0 && Integer.MAX_VALUE - y < x) {
            throw new OverflowException("Add: Overflow");
        } else if (y < 0 && Integer.MIN_VALUE - y > x) {
            throw new OverflowException("Add: Overflow");
        }
        return super.operation(x, y);
    }
}
