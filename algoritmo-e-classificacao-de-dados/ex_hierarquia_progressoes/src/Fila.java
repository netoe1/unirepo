public class Fila<T> extends List<T> implements IFila<T>{

    @Override 
    public void add(T obj){
        super.insertAtBack(obj);
    }

    @Override 
    public T remove() throws EmptyQueueException{
        return super.removeFromFront();
    };

    @Override
    public boolean isEmpty(){
        return super.isEmpty();
    };
}
