public class ArithProgression extends Progression{
    protected long inc;

    public ArithProgression(long inc){
        super();
        this.inc = inc;
    }
    
    public ArithProgression(){
        this(1);
    }

    protected long nextValue(){
        return cur+=inc;
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
    
};