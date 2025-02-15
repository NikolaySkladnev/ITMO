package expression;

public class Subtract extends AbstractBinaryOperations {
    public Subtract(GeneralInterface arg1, GeneralInterface arg2) {
        super(arg1, arg2, "-");
    }

    @Override
    protected int operation(int x, int y) {
        return x - y;
    }
}
