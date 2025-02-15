package expression;

public class Add extends AbstractBinaryOperations {

    public Add(GeneralInterface arg1, GeneralInterface arg2) {
        super(arg1, arg2, "+");
    }

    @Override
    protected int operation(int x, int y) {
        return x + y;
    }
}
