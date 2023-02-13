package app.misc;

public class PrimitiveWrapper<T> {
    private T item;

    public PrimitiveWrapper(T i) {
        item = i;
    }

    public void set(T change) {
        item = change;
    }

    public T get() {
        return item;
    }

}