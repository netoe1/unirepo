import java.util.Vector;

public class InsertionSort<T extends Comparable<T>> extends Ordenador<T> {

    public InsertionSort() {
        super();
    }

    public void sort(Vector<T> vetor_com_dados) {
        if (vetor_com_dados == null) return;
        
        int n = vetor_com_dados.size();
        
        for (int i = 1; i < n; i++) {
            T chave = vetor_com_dados.get(i);
            int j = i - 1;

            // Desloca os elementos maiores para a direita utilizando .get() e .set()
            while (j >= 0 && vetor_com_dados.get(j).compareTo(chave) > 0) {
                vetor_com_dados.set(j + 1, vetor_com_dados.get(j));
                j--;
            }
            
            // Insere a chave na sua posição correta
            vetor_com_dados.set(j + 1, chave);
        }
    }
}