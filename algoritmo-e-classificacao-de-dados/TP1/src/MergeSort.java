import java.util.List;
import java.util.Vector;

public class MergeSort <T extends Comparable<T>> extends Ordenador<T> {

    public MergeSort(){
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
            executarMergeSort(0, this.vetor.size() - 1);
        }
        this.tempoFinal = System.nanoTime();
        this.tempoTotal = this.tempoFinal - this.tempoInicial;
    }
    

    @Override 
    public long getTempoTotal(){
        this.tempoTotal = this.tempoFinal - this.tempoInicial;
        return this.tempoTotal;
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

    public static void main(String[] args){
        Vector<Integer> v = new Vector<>(List.of(4, 2, 2, 4, 0, 6, 7));
        MergeSort<Integer> mergesort = new MergeSort<Integer>();
        System.out.println(v.toString());   
        mergesort.sort(v);
        System.out.println(mergesort.toString());
        System.out.println((long) mergesort.getTempoTotal());
    }
}