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
        counting.setVetor(new Vector<>(Arrays.asList(4, 2, 3, 1, 5, 6, 2, 3, 4, 5, 2, 3, 3, 4, 7, 8)));
        counting.print();
        counting.sort();
        counting.print();
    }
}