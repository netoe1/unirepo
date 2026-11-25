public class FibonnaciProgression extends Progression{
    protected long prev;

    public FibonnaciProgression(){
        this(0,1);
    }

    public FibonnaciProgression(long v1,long v2){
        super();
        this.first = v1;
        this.prev = v2 - v1;    
    }

    protected long nextValue(){
        long temp = prev;
        prev=cur;
        cur+=temp;
        return cur;
    }

    protected void printProgression(int n){
        int i = 0;

        String classe = this.getClass().getName();
        System.out.print("Classe:" + classe + "\n");
        for(i = 0; i < n;i++){
            System.out.println(cur);
            this.nextValue();
        }
    }

}