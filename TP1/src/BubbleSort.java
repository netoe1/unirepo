import java.util.Arrays;
import java.util.Vector;

public class BubbleSort <T extends Comparable<T>> extends Ordenador<T> {

    public BubbleSort(){
        super();
    }

    @Override 
    public void sort(){
    
        this.tempoInicial = System.nanoTime();
        if (this.vetor == null) {
            throw new Error("Vetor Vazio!   ");
        }
        if (this.vetor.size() > 1) {
            executarBubbleSort();
        }
        this.tempoFinal = System.nanoTime();
        this.tempoTotal = this.tempoFinal - this.tempoInicial;
    }
    

    @Override 
    public long getTempoTotal(){
        this.tempoTotal = this.tempoFinal - this.tempoInicial;
        return this.tempoTotal;
    }

    private void executarBubbleSort() {
        int n = this.vetor.size();
        boolean trocou;

        for(int i = 0; i < n - 1; i++){
            trocou = false;

            for(int j = 0; j < n - 1 - i; j++){
                if(this.vetor.get(j).compareTo(this.vetor.get(j + 1)) > 0){
                    T temp = this.vetor.get(j);
                    this.vetor.set(j, this.vetor.get(j + 1));
                    this.vetor.set(j + 1, temp);
                    trocou = true;
                }
            }

            if(!trocou){
                break;
            }
        }
    }

    public static void main(String args[]){
        BubbleSort<Integer> bubble = new BubbleSort<>();
        bubble.setVetor(new Vector<>(Arrays.asList(4,2,3,1,5,6,2,3,4,5,2,3,3,4,7,8)));
        bubble.print();
        bubble.sort();
        bubble.print();
    }
}