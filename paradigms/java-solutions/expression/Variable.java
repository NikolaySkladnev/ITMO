package expression;

import java.util.List;

public class Variable implements GeneralInterface {
    private String variable;
    private int num;

    public Variable(String variable) {
        this.variable = variable;
        this.num = -1;
    }

    public Variable(int num) {
        this.variable = "";
        this.num = num;
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

    public int evaluate(List<Integer> variables) {
        return variables.get(num);
    }

    public int hashCode() {
        return variable.hashCode();
    }

    public String toString() {
        if (num != -1) return "$" + num;
        return variable;
    }

    public boolean equals(Object expression) {
        if (this == expression) return true;
        if (!(expression instanceof GeneralInterface)) return false;
        if (getClass() != expression.getClass()) return false;
        return variable.equals(((Variable) expression).variable);
    }
}
