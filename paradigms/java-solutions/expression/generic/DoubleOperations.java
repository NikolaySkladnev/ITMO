package expression.generic;

public class DoubleOperations implements Operations<Double> {
    @Override
    public Double add(Double x, Double y) {
        return x + y;
    }

    @Override
    public Double subtract(Double x, Double y) {
        return x - y;
    }

    @Override
    public Double multiply(Double x, Double y) {
        return x * y;
    }

    @Override
    public Double divide(Double x, Double y) {
        return x / y;
    }

    @Override
    public Double negate(Double x) {
        return -1 * x;
    }

    @Override
    public Double parse(int x) {
        return (double) x;
    }

    @Override
    public Double parse(String x) {
        return Double.parseDouble(x);
    }
}
