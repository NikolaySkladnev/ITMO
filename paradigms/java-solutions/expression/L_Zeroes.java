package expression;

public class L_Zeroes extends AbstractUnaryOperation{

    public L_Zeroes(GeneralInterface expression) {
        super(expression, "l0(", ")");
    }

    @Override
    protected int operation(int x) {
        if (x == 0) return 32;
        return 32 - Integer.toBinaryString(x).length();
    }

}
