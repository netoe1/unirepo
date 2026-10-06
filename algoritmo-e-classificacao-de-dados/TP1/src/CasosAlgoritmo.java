import java.util.Arrays;
import java.util.Random;

public class CasosAlgoritmo{
    // Melhor caso
    public static void ordem_crescente(Integer v[],int min,int max){

        for(int i = min;i < max;i++){
            v[i] = i;
        }
    };

    public static void ordem_decrescente(Integer v[],int min,int max){
        for(int i = max;i > min;i--){
            v[i] = i;
        }
    };

    public void ordem_aleatoria(Integer v[],int n){
        Random gerador = new Random();

        for(int i =0;i < n;i++){
            v[i] = gerador.nextInt(1000);
        }
    };

    public static void ordem_repetidas(Integer[] v, int max) {
        Arrays.fill(v, max);
    }

    public static <T extends Comparable<T>> String toString(T[] v, int n) {
        if (v == null || n <= 0) {
            return "[]";
        }
        StringBuilder aux = new StringBuilder("[");
        int limite = Math.min(n, v.length); 
        
        for (int i = 0; i < limite; i++) {
            aux.append(v[i]);
            if (i < limite - 1) {
                aux.append(", ");
            }
        }
        aux.append("]");
        
        return aux.toString();
    }
}
