package expression;

import java.util.List;

abstract class AbstractUnaryOperation implements GeneralInterface {

    private final GeneralInterface expression;
    private final String start;
    private final String end;

    public AbstractUnaryOperation(GeneralInterface expression, String start, String end) {
        this.expression = expression;
        this.start = start;
        this.end = end;
    }

    public int evaluate(int x) {
        return operation(expression.evaluate(x));
    }

    public int evaluate(List<Integer> variables) {
        return operation(expression.evaluate(variables));
    }

    public int evaluate(int x, int y, int z) {
        return operation(expression.evaluate(x, y, z));
    }

    public int hashCode() {
        return expression.hashCode() * 37;
    }

    public String toString() {
        return start + expression + end;
    }

    public boolean equals(Object expression) {
        if (this == expression) return true;
        if (!(expression instanceof GeneralInterface)) return false;
        if (getClass() != expression.getClass()) return false;
        return this.expression.equals(expression);
    }

    protected abstract int operation(int x);

}