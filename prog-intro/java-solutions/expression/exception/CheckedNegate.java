package expression.exception;

import expression.*;

public class CheckedNegate extends Negate {

    public CheckedNegate(GeneralInterface expression) {
        super(expression);
    }

    @Override
    public int evaluate(int x) {
        int tmp = evaluate(x);
        if (tmp == Integer.MIN_VALUE) {
            throw new OverflowException("err");
        }
        return -1 * tmp;
    }

    @Override
    public int evaluate(int x, int y, int z) {
        int tmp = evaluate(x, y, z);
        if (tmp == Integer.MIN_VALUE) {
            throw new OverflowException("err");
        }
        return -1 * tmp;
    }
}
