import java.util.Arrays;
import java.util.Vector;

public class InsertionSort<T extends Comparable<T>> extends Ordenador<T> {

    public InsertionSort() {
        super();
    }

    public void sort() {
        if (this.vetor == null) return;
        
        int n = this.vetor.size();
        
        for (int i = 1; i < n; i++) {
            T chave = this.vetor.get(i);
            int j = i - 1;

            // Desloca os elementos maiores para a direita utilizando .get() e .set()
            while (j >= 0 && this.vetor.get(j).compareTo(chave) > 0) {
                this.vetor.set(j + 1, this.vetor.get(j));
                j--;
            }
            
            // Insere a chave na sua posição correta
            this.vetor.set(j + 1, chave);
        }
    }

    public static void main(String args[]){
        InsertionSort<Integer> insertion = new InsertionSort<>();
        insertion.setVetor(new Vector<>(Arrays.asList(4,2,3,1,5,6,4,7,8)));
        insertion.print();
        insertion.sort();
        insertion.print();
    }
}