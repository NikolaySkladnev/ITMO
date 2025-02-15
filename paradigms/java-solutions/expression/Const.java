package expression;

import java.util.List;

public class Const implements GeneralInterface {
    private int num = 0;

    public Const(int num) {
        this.num = num;
    }

    public int evaluate(int var) {
        return num;
    }

    public int evaluate(int x, int y, int z) {
        return num;
    }

    public int evaluate(List<Integer> variables) {
        return num;
    }

    public int hashCode() {
        return num * 128;
    }

    public String toString() {
        return String.valueOf(num);
    }

    public boolean equals(Object expression) {
        if (this == expression) return true;
        if (!(expression instanceof GeneralInterface)) return false;
        if (getClass() != expression.getClass()) return false;
        return num == ((Const) expression).num;
    }

}
