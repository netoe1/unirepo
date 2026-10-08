import java.util.List;
import java.util.Vector;

public class BubbleSort <T extends Comparable<T>> extends Ordenador<T> {

    public BubbleSort(){
        super();
    }

    @Override 
    public void sort(Vector<T> vetor_com_dados){
    
        this.tempoInicial = System.nanoTime();
        this.vetor.clear();
        if (vetor_com_dados != null) {
            this.vetor.addAll(vetor_com_dados);
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

    public static void main(String[] args){
        Vector<Integer> v = new Vector<>(List.of(8, 3, 6, 2, 4, 3, 5, 1, 7, 9));
        BubbleSort<Integer> bubblesort = new BubbleSort<>();
        
        System.out.println("Vetor original: " + v.toString());   
        bubblesort.sort(v);
        System.out.println("Vetor ordenado: " + bubblesort.toString());
        System.out.println("Tempo total (ns): " + (long) bubblesort.getTempoTotal());
    }
}