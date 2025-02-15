package expression.exception;

import expression.*;

public class CheckedMultiply extends Multiply{
    public CheckedMultiply(GeneralInterface arg1, GeneralInterface arg2) {
        super(arg1, arg2);
    }

    @Override
    protected int operation(int x, int y) {
        long res = (long) x * y;
        if (res > Integer.MAX_VALUE || res < Integer.MIN_VALUE) {
            throw new OverflowException("err");
        }
        return (int) res;
    }
}
