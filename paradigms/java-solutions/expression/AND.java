package expression;

public class AND extends AbstractBinaryOperations{

    public AND(GeneralInterface arg1, GeneralInterface arg2) {
        super(arg1, arg2, "&");
    }

    @Override
    protected int operation(int x, int y) {
        return x & y;
    }

}
