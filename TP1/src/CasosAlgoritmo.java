import java.util.Random;
import java.util.Arrays;

public class CasosAlgoritmo {

    public static void ordem_crescente(Integer[] v, int n) {
        int limite = Math.min(n, v.length);
        for (int i = 0; i < limite; i++) {
            v[i] = i;
        }
    }

    public static void ordem_decrescente(Integer[] v, int n) {
        int limite = Math.min(n, v.length);
        for (int i = 0; i < limite; i++) {
            v[i] = limite - i; 
        }
    }

    public static void ordem_aleatoria(Integer[] v, int n) {
        Random gerador = new Random();
        int limite = Math.min(n, v.length);

        for (int i = 0; i < limite; i++) {
            v[i] = gerador.nextInt(1000); 
        }
    }

    public static void ordem_repetidas(Integer[] v, int n, int valor) {
        int limite = Math.min(n, v.length);
        Arrays.fill(v, 0, limite, valor);
    }

}