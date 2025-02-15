package expression;

import java.util.Objects;

public class R_Zeroes extends AbstractUnaryOperation{

    public R_Zeroes(GeneralInterface expression) {
        super(expression, "t0(", ")");
    }
    @Override
    protected int operation(int x) {
        int count = 0;
        String temp = Integer.toBinaryString(x);
        for (int i = temp.length() - 1; i >= 0; i--) {
            if (Objects.equals('1', temp.charAt(i))) break;
            count += 1;
        }
        if (x == 0) return 32;
        return count;
    }
}
