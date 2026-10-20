import java.util.Arrays;
import java.util.Vector;

public class HeapSort<T extends Comparable<T>> extends Ordenador<T> {

    public HeapSort() {
        super();
    }

    @Override
    public void sort() {
        if (this.vetor == null || this.vetor.size() <= 1) {
            return;
        }

        int n = this.vetor.size();

        // Constrói o heap (reorganiza o vetor)
        for (int i = n / 2 - 1; i >= 0; i--) {
            heapify(n, i);
        }

        // Extrai os elementos do heap um a um
        for (int i = n - 1; i > 0; i--) {
            T temp = this.vetor.get(0);
            this.vetor.set(0, this.vetor.get(i));
            this.vetor.set(i, temp);

            heapify(i, 0);
        }
    }

    private void heapify(int n, int i) {
        int maior = i;
        int esquerda = 2 * i + 1;
        int direita = 2 * i + 2;

        if (esquerda < n && this.vetor.get(esquerda).compareTo(this.vetor.get(maior)) > 0) {
            maior = esquerda;
        }

        if (direita < n && this.vetor.get(direita).compareTo(this.vetor.get(maior)) > 0) {
            maior = direita;
        }

        if (maior != i) {
            T troca = this.vetor.get(i);
            this.vetor.set(i, this.vetor.get(maior));
            this.vetor.set(maior, troca);

            heapify(n, maior);
        }
    }

    public static void main(String args[]) {
        HeapSort<Integer> heap = new HeapSort<>();
        heap.setVetor(new Vector<>(Arrays.asList(4, 2, 3, 1, 5, 6, 2, 3, 4, 5, 2, 3, 3, 4, 7, 8)));
        heap.print();
        heap.sort();
        heap.print();
    }
}