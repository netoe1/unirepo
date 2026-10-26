import java.util.Arrays;
import java.util.Vector;

public class InsertionSort<T extends Comparable<T>> extends Ordenador<T> {

    public InsertionSort() {
        super();
    }

    @Override
    public void sort() {
        if (this.vetor == null) {
            this.tempoInicial = System.nanoTime();
            this.tempoFinal = this.tempoInicial;
            this.tempoTotal = 0;
            return;
        }

        int n = this.vetor.size();

        // Início da medição da ordenação
        this.tempoInicial = System.nanoTime();

        for (int i = 1; i < n; i++) {
            T chave = this.vetor.get(i);
            int j = i - 1;

            // Desloca os elementos maiores para a direita
            while (j >= 0 && this.vetor.get(j).compareTo(chave) > 0) {
                this.vetor.set(j + 1, this.vetor.get(j));
                j--;
            }

            // Insere a chave na posição correta
            this.vetor.set(j + 1, chave);
        }

        // Fim da medição
        this.tempoFinal = System.nanoTime();
        this.tempoTotal = this.tempoFinal - this.tempoInicial;
    }

    public static void main(String args[]) {
        InsertionSort<Integer> insertion = new InsertionSort<>();
        System.out.println("\nCrescente: ");
        insertion.setVetor(new Vector<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)));
        insertion.print();
        insertion.sort();
        insertion.print();

        System.out.println("\nDecrescente: ");
        insertion.setVetor(new Vector<>(Arrays.asList(15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1)));
        insertion.print();
        insertion.sort();
        insertion.print();

        System.out.println("\nAleatorio: ");
        insertion.setVetor(new Vector<>(Arrays.asList(7, 2, 9, 1, 5, 8, 3, 10, 4, 6)));
        insertion.print();
        insertion.sort();
        insertion.print();

        System.out.println("\nRepetido: ");
        insertion.setVetor(new Vector<>(Arrays.asList(4, 1, 4, 2, 1, 5, 4, 3, 2, 1, 5)));
        insertion.print();
        insertion.sort();
        insertion.print();
    }
}