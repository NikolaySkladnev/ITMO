package expression.generic;

public class Const_generic<T> implements Generic<T> {
    private final T num;

    public Const_generic(T num) {
        this.num = num;
    }

    public T evaluate(T x, T y, T z) {
        return num;
    }

    public int hashCode() {
        return num.hashCode();
    }

    public String toString() {
        return String.valueOf(num);
    }

    public boolean equals(Object expression) {
        if (this == expression) return true;
        if (!(expression instanceof Generic<?>)) return false;
        if (getClass() != expression.getClass()) return false;
        return num == ((Const_generic<?>) expression).num;
    }
}
