import java.util.Arrays;
import java.util.Vector;

public class MergeSort <T extends Comparable<T>> extends Ordenador<T> {

    public MergeSort(){
        super();
    }

    @Override 
    public void sort(){
    
        this.tempoInicial = System.nanoTime();
        if (this.vetor == null) {
            throw new Error("Vetor Vazio!");
        }
        if (this.vetor.size() > 1) {
            executarMergeSort(0, this.vetor.size() - 1);
        }
        this.tempoFinal = System.nanoTime();
        this.tempoTotal = this.tempoFinal - this.tempoInicial;
    }

    private void intercalar(int inicio, int meio, int fim) {
        // Cria cópias temporárias das duas sublistas usando a estrutura Vector
        Vector<T> esquerda = new Vector<>(this.vetor.subList(inicio, meio + 1));
        Vector<T> direita = new Vector<>(this.vetor.subList(meio + 1, fim + 1));

        int i = 0; // Índice para a sublista esquerda
        int j = 0; // Índice para a sublista direita
        int k = inicio; // Índice para o vetor principal original

        // Compara os elementos das duas metades e reinsere no vetor principal ordenadamente
        while (i < esquerda.size() && j < direita.size()) {
            if (esquerda.get(i).compareTo(direita.get(j)) <= 0) {
                this.vetor.set(k, esquerda.get(i));
                i++;
            } else {
                this.vetor.set(k, direita.get(j));
                j++;
            }
            k++;
        }

        // Copia os elementos restantes da sublista esquerda, se houver
        while (i < esquerda.size()) {
            this.vetor.set(k, esquerda.get(i));
            i++;
            k++;
        }

        // Copia os elementos restantes da sublista direita, se houver
        while (j < direita.size()) {
            this.vetor.set(k, direita.get(j));
            j++;
            k++;
        }
    }

    private void executarMergeSort(int inicio, int fim) {
        if (inicio < fim) {
            int meio = inicio + (fim - inicio) / 2;

            // Divide recursivamente a metade esquerda e a direita
            executarMergeSort(inicio, meio);
            executarMergeSort(meio + 1, fim);

            // Une as duas metades ordenadas
            intercalar(inicio, meio, fim);
        }
    }

    public static void main(String args[]){
        MergeSort<Integer> merge = new MergeSort<>();
        merge.setVetor(new Vector<>(Arrays.asList(4,2,3,1,5,6,4,7,8)));
        merge.print();
        merge.sort();
        merge.print();
    }
    
}