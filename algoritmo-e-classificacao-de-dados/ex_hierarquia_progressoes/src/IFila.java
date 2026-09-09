public interface IFila<T> {
    void add(T obj);
    T remove() throws EmptyQueueException;
    boolean isEmpty();
}
