import java.util.Arrays;
import java.util.Vector;

public class QuickSort<T extends Comparable<T>> extends Ordenador<T> {

    @Override
    public void sort() {
        if (this.vetor == null || this.vetor.size() <= 1) {
            return;
        }
        quicksort(0, this.vetor.size() - 1);
    }

    private void quicksort(int baixo, int alto) {
        if (baixo < alto) {
            int pi = particionar(baixo, alto);
            quicksort(baixo, pi - 1);
            quicksort(pi + 1, alto);
        }
    }

    private int particionar(int baixo, int alto) {
        T pivo = this.vetor.get(alto);
        int i = (baixo - 1);

        for (int j = baixo; j < alto; j++) {
            if (this.vetor.get(j).compareTo(pivo) <= 0) {
                i++;
                trocar(i, j);
            }
        }

        trocar(i + 1, alto);
        return (i + 1);
    }

    private void trocar(int i, int j) {
        T temp = this.vetor.get(i);
        this.vetor.set(i, this.vetor.get(j));
        this.vetor.set(j, temp);
    }

    public static void main(String args[]){
        QuickSort<Integer> quick = new QuickSort<>();
        quick.setVetor(new Vector<>(Arrays.asList(4,2,3,1,5,7,8,-9,6,4,7,8)));
        quick.print();
        quick.sort();
        quick.print();
    }
}