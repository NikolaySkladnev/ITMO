package expression.generic;

public class ByteOperations implements Operations<Byte> {
    @Override
    public Byte add(Byte x, Byte y) {
        return (byte) (x + y);
    }

    @Override
    public Byte subtract(Byte x, Byte y) {
        return (byte) (x - y);
    }

    @Override
    public Byte multiply(Byte x, Byte y) {
        return (byte) (x * y);
    }

    @Override
    public Byte divide(Byte x, Byte y) {
        return (byte) (x / y);
    }

    @Override
    public Byte negate(Byte x) {
        return (byte) (-1 * x);
    }

    @Override
    public Byte parse(int x) {
        return (byte) x;
    }

    @Override
    public Byte parse(String x) {
        return Byte.parseByte(x);
    }
}
