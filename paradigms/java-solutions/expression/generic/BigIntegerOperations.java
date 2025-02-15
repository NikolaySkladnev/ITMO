package expression.generic;


import java.math.BigInteger;

public class BigIntegerOperations implements Operations<BigInteger> {
    @Override
    public BigInteger add(BigInteger x, BigInteger y) {
        return x.add(y);
    }

    @Override
    public BigInteger subtract(BigInteger x, BigInteger y) {
        return x.subtract(y);
    }

    @Override
    public BigInteger multiply(BigInteger x, BigInteger y) {
        return x.multiply(y);
    }

    @Override
    public BigInteger divide(BigInteger x, BigInteger y) {
        if (y.toString().equals("0")) throw new ArithmeticException();
        return x.divide(y);
    }

    @Override
    public BigInteger negate(BigInteger x) {
        return x.negate();
    }

    @Override
    public BigInteger parse(int x) {
        return BigInteger.valueOf(x);
    }

    @Override
    public BigInteger parse(String x) {
        return new BigInteger(x);
    }

}
