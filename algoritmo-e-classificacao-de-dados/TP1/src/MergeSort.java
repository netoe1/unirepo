public class MergeSort{
    
    public static <T extends Comparable<T>> void mergesort(T[] a, int n) {
        if (a == null || n <= 1) {
            return;
        }

        @SuppressWarnings("unchecked")
        T[] aux = (T[]) new Comparable[n];

        for (int currSize = 1; currSize < n; currSize *= 2) {
            
            for (int leftStart = 0; leftStart < n - 1; leftStart += 2 * currSize) {
                
                // Calcula o fim do subvetor da esquerda (mid) e o fim do subvetor da direita (hi)
                // Math.min previne o acesso a índices fora dos limites do vetor original
                int mid = Math.min(leftStart + currSize - 1, n - 1);
                int hi = Math.min(leftStart + 2 * currSize - 1, n - 1);

                // Intercala os subvetores a[leftStart...mid] e a[mid+1...hi]
                merge(a, aux, leftStart, mid, hi);
            }
        }
    }

    private static <T extends Comparable<T>> void merge(T[] a, T[] aux, int lo, int mid, int hi) {
        // Copia os elementos da fatia atual para o vetor auxiliar
        for (int k = lo; k <= hi; k++) {
            aux[k] = a[k];
        }

        int i = lo;
        int j = mid + 1;

        // Intercala de volta para o vetor original 'a'
        for (int k = lo; k <= hi; k++) {
            if (i > mid) {
                // Elementos da metade esquerda já foram todos consumidos
                a[k] = aux[j++];
            } else if (j > hi) {
                // Elementos da metade direita já foram todos consumidos
                a[k] = aux[i++];
            } else if (aux[j].compareTo(aux[i]) < 0) {
                // O elemento da direita é menor
                a[k] = aux[j++];
            } else {
                // O elemento da esquerda é menor ou igual
                a[k] = aux[i++];
            }
        }
    }

    public static void main(String args[]){
        Integer a[] = {3,4,3,7,2,6,9,0,9,0,9,0,9,0,9,0,9,7,5,2,3,4,6,7,8,9,8,6,54,3,2,3,5,6,7};

        System.out.println("Antes:");
        for(int i = 0; i < a.length;i++){
            System.out.println(String.format("[%d]",a[i]));
        }
        MergeSort.mergesort(a,a.length);

        System.out.println("Depois:");
        for(int i = 0; i < a.length;i++){
            System.out.println(String.format("[%d]",a[i]));
        }

    }
}