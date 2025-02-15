package expression.generic;


import expression.exceptions.DivisionByZeroException;
import expression.exceptions.OverflowException;

public class IntegerOperations implements Operations<Integer> {
    @Override
    public Integer add(Integer x, Integer y) {
        if (y > 0 && Integer.MAX_VALUE - y < x) {
            throw new OverflowException("Add: Overflow");
        } else if (y < 0 && Integer.MIN_VALUE - y > x) {
            throw new OverflowException("Add: Overflow");
        }
        return x + y;
    }

    @Override
    public Integer subtract(Integer x, Integer y) {
        if (y > 0 && Integer.MIN_VALUE + y > x) {
            throw new OverflowException("Subtract: Overflow");
        } else if (y < 0 && Integer.MAX_VALUE + y < x) {
            throw new OverflowException("Subtract: Overflow");
        } else if (x == 0 && y == Integer.MIN_VALUE) {
            throw new OverflowException("Subtract: Overflow");
        }
        return x - y;
    }

    @Override
    public Integer multiply(Integer x, Integer y) {
        if (y == -1 && x == Integer.MIN_VALUE) {
            throw new OverflowException("Multiply: Overflow");
        } else if (y > 0 && (x < Integer.MIN_VALUE / y || x > Integer.MAX_VALUE / y)) {
            throw new OverflowException("Multiply: Overflow");
        } else if (y < 0 && y != -1 && (x < Integer.MAX_VALUE / y || x > Integer.MIN_VALUE / y)) {
            throw new OverflowException("Multiply: Overflow");
        }
        return x * y;
    }

    @Override
    public Integer divide(Integer x, Integer y) {
        if (y == 0) throw new DivisionByZeroException("Division by zero");
        if (y == -1 && x == Integer.MIN_VALUE) {
            throw new OverflowException("Divide: Overflow");
        }
        return x / y;
    }

    @Override
    public Integer negate(Integer x) {
        if (x == Integer.MIN_VALUE) throw new OverflowException("Negate: Overflow");
        return -1 * x;
    }

    @Override
    public Integer parse(int x) {
        return x;
    }

    @Override
    public Integer parse(String x) {
        return Integer.valueOf(x);
    }
}
