import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Vector;

/**
 * Testes pequenos de corretude exigidos no TP1.
 * Confere se cada algoritmo ordena a entrada e preserva todos os elementos.
 */
public final class TestesCorretude {

    private static final int[] TAMANHOS = {10, 11, 15};
    private static final String[] DISPOSICOES = {
        "CRESCENTE", "DECRESCENTE", "ALEATORIO", "REPETIDOS"
    };
    private static final String[] ALGORITMOS = {
        "BubbleSort", "InsertionSort", "SelectionSort", "ShellSort",
        "HeapSort", "MergeSort", "QuickSort", "CountingSort",
        "RadixSort", "BucketSort"
    };

    private TestesCorretude() {
        // Classe utilitaria.
    }

    public static void main(String[] args) {
        int aprovados = 0;
        int falhas = 0;

        for (String algoritmo : ALGORITMOS) {
            for (int tamanho : TAMANHOS) {
                for (String disposicao : DISPOSICOES) {
                    try {
                        testar(algoritmo, tamanho, disposicao);
                        aprovados++;
                        System.out.println("[OK] " + algoritmo + " | n=" + tamanho
                            + " | " + disposicao);
                    } catch (Throwable erro) {
                        falhas++;
                        System.err.println("[FALHA] " + algoritmo + " | n=" + tamanho
                            + " | " + disposicao + " | " + erro.getClass().getSimpleName()
                            + ": " + erro.getMessage());
                    }
                }
            }
        }

        System.out.println();
        System.out.println("Testes de corretude: " + aprovados + " aprovados; "
            + falhas + " falhas; " + (aprovados + falhas) + " configurações.");

        if (falhas > 0) {
            System.exit(1);
        }
    }

    private static void testar(String algoritmo, int tamanho, String disposicao) {
        Vector<Integer> original = gerarVetor(tamanho, disposicao);
        Vector<Integer> esperado = new Vector<>(original);

        Ordenador<Integer> ordenador = criarOrdenador(algoritmo);
        ordenador.setVetor(new Vector<>(original));
        ordenador.sort();

        Vector<Integer> resultado = new Vector<>(ordenador.vetor);
        if (resultado.size() != esperado.size()) {
            throw new AssertionError("O tamanho do vetor mudou.");
        }
        if (!estaOrdenado(resultado)) {
            throw new AssertionError("O vetor final não está em ordem crescente: " + resultado);
        }
        if (!mesmaMulticonjunto(esperado, resultado)) {
            throw new AssertionError("O vetor final não preservou todos os elementos.");
        }
    }

    private static Vector<Integer> gerarVetor(int tamanho, String disposicao) {
        Vector<Integer> vetor = new Vector<>(tamanho);
        Random aleatorio = new Random(2026L + tamanho);

        for (int i = 0; i < tamanho; i++) {
            switch (disposicao) {
                case "CRESCENTE":
                    vetor.add(i);
                    break;
                case "DECRESCENTE":
                    vetor.add(tamanho - 1 - i);
                    break;
                case "ALEATORIO":
                    // Valores não negativos e repetidos de propósito para testar duplicatas.
                    vetor.add(aleatorio.nextInt(21));
                    break;
                case "REPETIDOS":
                    vetor.add(5);
                    break;
                default:
                    throw new IllegalArgumentException("Disposição desconhecida: " + disposicao);
            }
        }
        return vetor;
    }

    private static boolean estaOrdenado(Vector<Integer> vetor) {
        for (int i = 1; i < vetor.size(); i++) {
            if (vetor.get(i - 1) > vetor.get(i)) {
                return false;
            }
        }
        return true;
    }

    private static boolean mesmaMulticonjunto(Vector<Integer> original, Vector<Integer> resultado) {
        Map<Integer, Integer> contagem = new HashMap<>();
        for (Integer valor : original) {
            contagem.put(valor, contagem.getOrDefault(valor, 0) + 1);
        }
        for (Integer valor : resultado) {
            contagem.put(valor, contagem.getOrDefault(valor, 0) - 1);
        }
        for (Integer quantidade : contagem.values()) {
            if (quantidade != 0) {
                return false;
            }
        }
        return true;
    }

    private static Ordenador<Integer> criarOrdenador(String algoritmo) {
        switch (algoritmo) {
            case "BubbleSort": return new BubbleSort<>();
            case "InsertionSort": return new InsertionSort<>();
            case "SelectionSort": return new SelectionSort<>();
            case "ShellSort": return new ShellSort<>();
            case "HeapSort": return new HeapSort<>();
            case "MergeSort": return new MergeSort<>();
            case "QuickSort": return new QuickSort<>();
            case "CountingSort": return (Ordenador<Integer>) new CountingSort();
            case "RadixSort": return (Ordenador<Integer>) new RadixSort();
            case "BucketSort": return (Ordenador<Integer>) new BucketSort();
            default: throw new IllegalArgumentException("Algoritmo desconhecido: " + algoritmo);
        }
    }
}
