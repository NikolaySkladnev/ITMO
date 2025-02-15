package expression;


import java.util.List;

abstract class AbstractBinaryOperations implements GeneralInterface {
    private final GeneralInterface arg1, arg2;
    private final String operChar;

    public AbstractBinaryOperations(GeneralInterface arg1, GeneralInterface arg2, String operChar) {
        this.arg1 = arg1;
        this.arg2 = arg2;
        this.operChar = operChar;
    }

    @Override
    public String toString() {
        return "(" + arg1.toString() + " " + operChar + " " + arg2.toString() + ")";
    }

    @Override
    public int hashCode() {
        return 31 * getClass().hashCode() + 29 * arg1.hashCode() + 59 * arg2.hashCode();
    }

    public int evaluate(int x) {
        return operation(arg1.evaluate(x), arg2.evaluate(x));
    }

    public int evaluate(int x, int y, int z) {
        return operation(arg1.evaluate(x, y, z), arg2.evaluate(x, y, z));
    }

    public int evaluate(List<Integer> variables) {
        return operation(arg1.evaluate(variables), arg2.evaluate(variables));
    }

    protected abstract int operation(int x, int y);

    @Override
    public boolean equals(Object expression) {
        if (this == expression) return true;
        if (!(expression instanceof GeneralInterface)) return false;
        if (getClass() != expression.getClass()) return false;
        AbstractBinaryOperations binary = (AbstractBinaryOperations) expression;
        return arg1.equals(binary.arg1) && arg2.equals(binary.arg2);
    }
}
