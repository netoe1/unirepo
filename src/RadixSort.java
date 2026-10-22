import java.util.Arrays;
import java.util.Vector;

public class RadixSort extends Ordenador<Integer> {

    public RadixSort() {
        super();
    }

    @Override
    public void sort() {
        if (this.vetor == null || this.vetor.size() <= 1) {
            return;
        }

        // n: quantidade de elementos do vetor.
        int n = this.vetor.size();

        /*
         * O Radix Sort LSD trabalha com chaves nao negativas.
         * Deslocamos os valores para preservar a ordem e tambem suportar
         * inteiros negativos, incluindo Integer.MIN_VALUE.
         */
        long[] valores = new long[n];
        long[] auxiliar = new long[n];
        long k = 0;

        for (int i = 0; i < n; i++) {
            valores[i] = (long) this.vetor.get(i) - Integer.MIN_VALUE;
            if (valores[i] > k) {
                k = valores[i];
            }
        }

        // k e a maior chave apos o deslocamento; determina os digitos necessarios.
        for (long exp = 1; k / exp > 0; exp *= 10) {
            int[] contagem = new int[10];

            // Conta quantos valores possuem cada digito nesta posicao.
            for (int i = 0; i < n; i++) {
                int digito = (int) ((valores[i] / exp) % 10);
                contagem[digito]++;
            }

            // Converte as contagens em posicoes finais cumulativas.
            for (int i = 1; i < contagem.length; i++) {
                contagem[i] += contagem[i - 1];
            }

            // Percorrer de tras para frente preserva a estabilidade da passagem.
            for (int i = n - 1; i >= 0; i--) {
                int digito = (int) ((valores[i] / exp) % 10);
                auxiliar[--contagem[digito]] = valores[i];
            }

            // Alterna os vetores de trabalho para a proxima passagem.
            long[] troca = valores;
            valores = auxiliar;
            auxiliar = troca;
        }

        // Retorna as chaves ao intervalo original e grava o resultado no Vector.
        for (int i = 0; i < n; i++) {
            this.vetor.set(i, (int) (valores[i] + Integer.MIN_VALUE));
        }
    }

    public static void main(String[] args) {
        RadixSort radix = new RadixSort();
        radix.setVetor(new Vector<>(Arrays.asList(42, 7, 315, 89, 7, 0, -12, -315,
                Integer.MIN_VALUE, Integer.MAX_VALUE)));

        radix.print();
        radix.sort();
        radix.print();
    }
}
