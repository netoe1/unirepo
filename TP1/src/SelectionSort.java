import java.util.Arrays;
import java.util.Vector;

public class SelectionSort<T extends Comparable<T>> extends Ordenador<T> {

    public SelectionSort() {
        super();
    }

    @Override
    public void sort() {
        if (this.vetor == null || this.vetor.size() <= 1) {
            return;
        }

        int n = this.vetor.size();

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
    }

    public static void main(String args[]) {
        SelectionSort<Integer> selection = new SelectionSort<>();
        selection.setVetor(new Vector<>(Arrays.asList(4, 2, 3, 1, 5, 6, 2, 3, 4, 5, 2, 3, 3, 4, 7, 8)));
        selection.print();
        selection.sort();
        selection.print();
    }
}