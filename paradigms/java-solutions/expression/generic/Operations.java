package expression.generic;

public interface Operations<T> {
    T add(T x, T y);
    T subtract(T x, T y);
    T multiply(T x, T y);
    T divide(T x, T y);
    T negate(T x);
    T parse(int x);
    T parse(String x);
}
