import java.util.Arrays;
import java.util.Vector;

public class SelectionSort<T extends Comparable<T>> extends Ordenador<T> {

    public SelectionSort() {
        super();
    }

    @Override
    public void sort() {
        if (this.vetor == null || this.vetor.size() <= 1) {
            this.tempoInicial = System.nanoTime();
            this.tempoFinal = this.tempoInicial;
            this.tempoTotal = 0;
            return;
        }

        int n = this.vetor.size();

        // Início da medição da ordenação
        this.tempoInicial = System.nanoTime();

        for (int i = 0; i < n - 1; i++) {
            int minIndex = i;

            for (int j = i + 1; j < n; j++) {
                if (this.vetor.get(j).compareTo(this.vetor.get(minIndex)) < 0) {
                    minIndex = j;
                }
            }

            if (minIndex != i) {
                T temp = this.vetor.get(i);
                this.vetor.set(i, this.vetor.get(minIndex));
                this.vetor.set(minIndex, temp);
            }
        }

        // Fim da medição da ordenação
        this.tempoFinal = System.nanoTime();
        this.tempoTotal = this.tempoFinal - this.tempoInicial;
    }

    public static void main(String args[]) {
        SelectionSort<Integer> selection = new SelectionSort<>();
        System.out.println("\nCrescente: ");
        selection.setVetor(new Vector<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)));
        selection.print();
        selection.sort();
        selection.print();

        System.out.println("\nDecrescente: ");
        selection.setVetor(new Vector<>(Arrays.asList(15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1)));
        selection.print();
        selection.sort();
        selection.print();

        System.out.println("\nAleatorio: ");
        selection.setVetor(new Vector<>(Arrays.asList(7, 2, 9, 1, 5, 8, 3, 10, 4, 6)));
        selection.print();
        selection.sort();
        selection.print();

        System.out.println("\nRepetido: ");
        selection.setVetor(new Vector<>(Arrays.asList(4, 1, 4, 2, 1, 5, 4, 3, 2, 1, 5)));
        selection.print();
        selection.sort();
        selection.print();
    }
}