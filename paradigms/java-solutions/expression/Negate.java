package expression;

public class Negate extends AbstractUnaryOperation {

    public Negate(GeneralInterface expression) {
        super(expression, "-(", ")");
    }
    @Override
    protected int operation(int x) {
        return -1 * x;
    }

}
