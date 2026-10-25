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
        // Otimização de cauda com ciclo while para limitar a pilha de recursão a O(log N)
        while (baixo < alto) {
            int pi = particionar(baixo, alto);

            // Recursão na menor partição primeiro
            if(pi - baixo < alto - pi){
                quicksort(baixo, pi - 1);
                baixo = pi + 1;
            } else {
                quicksort(pi + 1, alto);
                alto = pi - 1;
            }
        }
    }

    private int particionar(int baixo, int alto) {
        int meio = baixo + (alto - baixo) / 2;
        trocar(meio, alto);
        
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
        System.out.println("\nCrescente: ");
        quick.setVetor(new Vector<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)));
        quick.print();
        quick.sort();
        quick.print();

        System.out.println("\nDecrescente: ");
        quick.setVetor(new Vector<>(Arrays.asList(15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1)));
        quick.print();
        quick.sort();
        quick.print();

        System.out.println("\nAleatorio: ");
        quick.setVetor(new Vector<>(Arrays.asList(7, 2, 9, 1, 5, 8, 3, 10, 4, 6)));
        quick.print();
        quick.sort();
        quick.print();

        System.out.println("\nRepetido: ");
        quick.setVetor(new Vector<>(Arrays.asList(4, 1, 4, 2, 1, 5, 4, 3, 2, 1, 5)));
        quick.print();
        quick.sort();
        quick.print();
    }
}