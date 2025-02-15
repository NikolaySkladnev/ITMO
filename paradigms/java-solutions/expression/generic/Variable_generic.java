package expression.generic;

public class Variable_generic<T> implements Generic<T> {
    private final String variable;

    public Variable_generic(String variable) {
        this.variable = variable;
    }

    public int evaluate(int variable) {
        return variable;
    }

    public T evaluate(T x, T y, T z) {
        return switch (variable) {
            case "x" -> x;
            case "y" -> y;
            case "z" -> z;
            default -> throw new IllegalStateException("Unexpected value: " + variable);
        };
    }

    public int hashCode() {
        return variable.hashCode();
    }

    public String toString() {
        return variable;
    }

    public boolean equals(Object expression) {
        if (this == expression) return true;
        if (!(expression instanceof Generic<?>)) return false;
        if (getClass() != expression.getClass()) return false;
        return variable.equals(((Variable_generic<?>) expression).variable);
    }
}
