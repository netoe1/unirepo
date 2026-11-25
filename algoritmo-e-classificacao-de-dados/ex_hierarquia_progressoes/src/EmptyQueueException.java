public class EmptyQueueException extends EmptyListException {

    public EmptyQueueException(){
        this("Queue");
    }
    
    public EmptyQueueException(String name){
        super( name + " is empty" );
    }
}
