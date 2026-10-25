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
        System.out.println("\nCrescente: ");
        bubble.setVetor(new Vector<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)));
        bubble.print();
        bubble.sort();
        bubble.print();

        System.out.println("\nDecrescente: ");
        bubble.setVetor(new Vector<>(Arrays.asList(15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1)));
        bubble.print();
        bubble.sort();
        bubble.print();

        System.out.println("\nAleatorio: ");
        bubble.setVetor(new Vector<>(Arrays.asList(7, 2, 9, 1, 5, 8, 3, 10, 4, 6)));
        bubble.print();
        bubble.sort();
        bubble.print();

        System.out.println("\nRepetido: ");
        bubble.setVetor(new Vector<>(Arrays.asList(4, 1, 4, 2, 1, 5, 4, 3, 2, 1, 5)));
        bubble.print();
        bubble.sort();
        bubble.print();
    }
}