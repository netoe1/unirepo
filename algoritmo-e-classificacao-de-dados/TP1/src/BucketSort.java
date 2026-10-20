import java.util.Arrays;
import java.util.Vector;
import java.util.Collections;

public class BucketSort extends Ordenador<Integer> {

    public BucketSort() {
        super();
    }

    @Override
    public void sort() {
        if (this.vetor == null || this.vetor.size() <= 1) {
            return;
        }

        int max = Collections.max(this.vetor);
        int min = Collections.min(this.vetor);

        if (max == min) {
            return;
        }

        int n = this.vetor.size();
        int numBuckets = (int) Math.sqrt(n);
        if (numBuckets < 1) {
            numBuckets = 1;
        }

        @SuppressWarnings("unchecked")
        Vector<Integer>[] buckets = new Vector[numBuckets + 1];
        for (int i = 0; i <= numBuckets; i++) {
            buckets[i] = new Vector<>();
        }

        double range = (double) (max - min + 1);
        for (int i = 0; i < n; i++) {
            int val = this.vetor.get(i);
            int bucketIndex = (int) ((val - min) / range * numBuckets);
            if (bucketIndex > numBuckets) {
                bucketIndex = numBuckets;
            }
            buckets[bucketIndex].add(val);
        }

        int index = 0;
        for (int i = 0; i <= numBuckets; i++) {
            if (!buckets[i].isEmpty()) {
                Collections.sort(buckets[i]);
                for (int val : buckets[i]) {
                    this.vetor.set(index++, val);
                }
            }
        }
    }

    public static void main(String args[]) {
        BucketSort bucket = new BucketSort();
        bucket.setVetor(new Vector<>(Arrays.asList(4, 2, 3, 1, 5, 6, 2, 3, 4, 5, 2, 3, 3, 4, 7, 8)));
        bucket.print();
        bucket.sort();
        bucket.print();
    }
}