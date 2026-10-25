import java.util.Arrays;
import java.util.Vector;

public class ShellSort<T extends Comparable<T>> extends Ordenador<T> {

    public ShellSort() {
        super();
    }

    @Override 
    public void sort() {
        if (vetor == null) return;
        
        int n = vetor.size();

        // Inicia com um intervalo (gap) grande e vai dividindo por 2 a cada passo
        for (int gap = n / 2; gap > 0; gap /= 2) {
            
            // Aplicação do Insertion Sort para o gap atual
            for (int i = gap; i < n; i++) {
                T temp = vetor.get(i);
                int j = i;

                // Desloca os elementos do sub-vetor até encontrar a posição correta da chave
                while (j >= gap && vetor.get(j - gap).compareTo(temp) > 0) {
                    vetor.set(j, vetor.get(j - gap));
                    j -= gap;
                }
                
                // Insere o elemento na posição correta do gap
                vetor.set(j, temp);
            }
        }
    }

    public static void main(String args[]){
        ShellSort<Integer> shell = new ShellSort<>();
        System.out.println("\nCrescente: ");
        shell.setVetor(new Vector<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)));
        shell.print();
        shell.sort();
        shell.print();

        System.out.println("\nDecrescente: ");
        shell.setVetor(new Vector<>(Arrays.asList(15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1)));
        shell.print();
        shell.sort();
        shell.print();

        System.out.println("\nAleatorio: ");
        shell.setVetor(new Vector<>(Arrays.asList(7, 2, 9, 1, 5, 8, 3, 10, 4, 6)));
        shell.print();
        shell.sort();
        shell.print();

        System.out.println("\nRepetido: ");
        shell.setVetor(new Vector<>(Arrays.asList(4, 1, 4, 2, 1, 5, 4, 3, 2, 1, 5)));
        shell.print();
        shell.sort();
        shell.print();
    }
}