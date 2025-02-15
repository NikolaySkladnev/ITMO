package expression;

public class Variable implements GeneralInterface {
    private final String variable;

    public Variable(String variable) {
        this.variable = variable;
    }

    public int evaluate(int variable) {
        return variable;
    }

    public int evaluate(int x, int y, int z) {
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
        if (!(expression instanceof GeneralInterface)) return false;
        if (getClass() != expression.getClass()) return false;
        return variable.equals(((Variable) expression).variable);
    }
}
