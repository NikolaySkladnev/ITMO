package expression;

public class Negate implements GeneralInterface {
    private final GeneralInterface expression;

    public Negate(GeneralInterface expression) {
        this.expression = expression;
    }

    public int evaluate(int var) {
        return -1 * expression.evaluate(var);
    }

    public int evaluate(int x, int y, int z) {
        return -1 * expression.evaluate(x, y, z);
    }

    public int hashCode() {
        return expression.hashCode() * 256;
    }

    public String toString() {
        return "-(" + expression + ")";
    }

    public boolean equals(Object expression) {
        if (this == expression) return true;
        if (!(expression instanceof GeneralInterface)) return false;
        if (getClass() != expression.getClass()) return false;
        return this.expression.equals(expression);
    }
}
