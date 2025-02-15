package expression.generic;

import expression.exceptions.DivisionByZeroException;
import expression.exceptions.OverflowException;

public class GenericTabulator implements Tabulator {
    @Override
    public Object[][][] tabulate(String mode, String expression, int x1, int x2, int y1, int y2, int z1, int z2) throws Exception {
        return switch (mode) {
            case ("i") -> fillArray(expression, x1, x2, y1, y2, z1, z2, new IntegerOperations());
            case ("d") -> fillArray(expression, x1, x2, y1, y2, z1, z2, new DoubleOperations());
            case ("bi") -> fillArray(expression, x1, x2, y1, y2, z1, z2, new BigIntegerOperations());
            case ("u") -> fillArray(expression, x1, x2, y1, y2, z1, z2, new IntegerOperationsNoCheck());
            case ("b") -> fillArray(expression, x1, x2, y1, y2, z1, z2, new ByteOperations());
            default -> throw new IllegalArgumentException("Unknown value in argument mode");
        };
    }

    private <T> Object[][][] fillArray(String expression, int x1, int x2, int y1, int y2, int z1, int z2, Operations<T> operations) throws Exception {
        Object[][][] result = new Object[x2 - x1 + 1][y2 - y1 + 1][z2 - z1 + 1];

        ExpressionParser_generic<T> parser = new ExpressionParser_generic<>(operations);
        Generic<T> expressionParsed = parser.parse(expression);

        for (int i = 0; x1 + i <= x2; i++) {
            for (int j = 0; y1 + j <= y2; j++) {
                for (int k = 0; z1 + k <= z2; k++) {
                    try {
                        result[i][j][k] = expressionParsed.evaluate(operations.parse(x1 + i), operations.parse(y1 + j), operations.parse(z1 + k));
                    } catch (OverflowException | DivisionByZeroException | ArithmeticException e) {
                        result[i][j][k] = null;
                    }
                }
            }
        }
        return result;
    }
}

