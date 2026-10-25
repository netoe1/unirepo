import java.util.Arrays;
import java.util.Vector;
import java.util.Collections;

public class CountingSort extends Ordenador<Integer> {

    public CountingSort() {
        super();
    }

    @Override
    public void sort() {
        if (this.vetor == null || this.vetor.size() <= 1) {
            return;
        }

        int max = Collections.max(this.vetor);
        int min = Collections.min(this.vetor);
        int range = max - min + 1;

        int[] count = new int[range];
        int n = this.vetor.size();

        for (int i = 0; i < n; i++) {
            count[this.vetor.get(i) - min]++;
        }

        int index = 0;
        for (int i = 0; i < range; i++) {
            while (count[i] > 0) {
                this.vetor.set(index++, i + min);
                count[i]--;
            }
        }
    }

    public static void main(String args[]) {
        CountingSort counting = new CountingSort();
        System.out.println("\nCrescente: ");
        counting.setVetor(new Vector<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)));
        counting.print();
        counting.sort();
        counting.print();

        System.out.println("\nDecrescente: ");
        counting.setVetor(new Vector<>(Arrays.asList(15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1)));
        counting.print();
        counting.sort();
        counting.print();

        System.out.println("\nAleatorio: ");
        counting.setVetor(new Vector<>(Arrays.asList(7, 2, 9, 1, 5, 8, 3, 10, 4, 6)));
        counting.print();
        counting.sort();
        counting.print();

        System.out.println("\nRepetido: ");
        counting.setVetor(new Vector<>(Arrays.asList(4, 1, 4, 2, 1, 5, 4, 3, 2, 1, 5)));
        counting.print();
        counting.sort();
        counting.print();
    }
}