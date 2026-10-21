public class GeomProgression extends Progression {
    protected long base;

    public GeomProgression(){
        this(2);
    }

    public GeomProgression(long base){
        super();
        this.base = base;
        first = 1;
        this.cur = first;
    }

    protected long nextValue(){
        return this.cur*=base;
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
