package expression.generic;

public class IntegerOperationsNoCheck implements Operations<Integer> {
    @Override
    public Integer add(Integer x, Integer y) {
        return x + y;
    }

    @Override
    public Integer subtract(Integer x, Integer y) {
        return x - y;
    }

    @Override
    public Integer multiply(Integer x, Integer y) {
        return x * y;
    }

    @Override
    public Integer divide(Integer x, Integer y) {
        return x / y;
    }

    @Override
    public Integer negate(Integer x) {
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
