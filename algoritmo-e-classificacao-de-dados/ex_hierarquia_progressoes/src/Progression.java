public abstract class Progression {
    protected long first;
    protected long cur;


    public Progression(){
        this.cur = this.first = 0;
    }

    protected long firstValue(){
        return(this.cur = this.first);
    }

    protected long nextValue(){
        return ++this.cur;
    }

    protected abstract void printProgression(int n);

}
