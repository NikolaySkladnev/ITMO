package expression.exception;

import expression.*;

public class CheckedSubtract extends Subtract{
    public CheckedSubtract(GeneralInterface arg1, GeneralInterface arg2) {
        super(arg1, arg2);
    }

    @Override
    protected int operation(int x, int y) {
        long res = Long.parseLong(String.valueOf(x)) - Long.parseLong(String.valueOf(y));
        if (res > Integer.MAX_VALUE || res < Integer.MIN_VALUE) {
            throw new OverflowException("err");
        }
        return (int) res;
    }
}
