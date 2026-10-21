
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Vector;

public class TestesGenerator {

    private final int[] tamanhos = {
        1000, 10000, 100000, 1000000
    };

    private int repeticoes = 10;

    public TestesGenerator(int repeticoes) {
        this.repeticoes = repeticoes;
    }

    public void rodarTeste(Path arquivoCsv) throws IOException {

        try (BufferedWriter csv = Files.newBufferedWriter(arquivoCsv)) {

            csv.write("algoritmo,n,caso,repeticao,t_ns");
            csv.newLine();

            for (int n : tamanhos) {

                for (String caso : Arrays.asList(
                    "CRESCENTE", "DECRESCENTE", "ALEATORIO", "REPETIDOS"
                )) {

                    if (n == 1000000 && !caso.equals("ALEATORIO")) {
                        continue; // Mantém a otimização padrão para 1M se desejado, ou remova se precisar de todos
                    }

                    Vector<Integer> original = gerarVetor(n, caso);

                    for (int repeticao = 1;
                         repeticao <= repeticoes;
                         repeticao++) {

                        // Cada algoritmo recebe uma cópia dos mesmos dados.
                        executar(new MergeSort<>(), original,
                            "MergeSort", n, caso, repeticao, csv);

                        executar(new BubbleSort<>(), original,
                            "BubbleSort", n, caso, repeticao, csv);

                        executar(new InsertionSort<>(), original,
                            "InsertionSort", n, caso, repeticao, csv);

                        executar(new QuickSort<>(), original,
                            "QuickSort", n, caso, repeticao, csv);

                        executar(new ShellSort<>(), original,
                            "ShellSort", n, caso, repeticao, csv);

                        executar(new SelectionSort<>(), original,
                            "SelectionSort", n, caso, repeticao, csv);

                        executar(new HeapSort<>(), original,
                            "HeapSort", n, caso, repeticao, csv);

                        executar(new CountingSort(), original,
                            "CountingSort", n, caso, repeticao, csv);        

                        executar(new RadixSort(), original,
                           "RadixSort", n, caso, repeticao, csv);

                        executar(new BucketSort(), original,
                            "BucketSort", n, caso, repeticao, csv);

                    }
                }
            }
        }

        System.out.println("Testes finalizados com sucesso! Arquivo gerado: " + arquivoCsv.getFileName());
    }

    private void executar(
        Ordenador<Integer> ordenador,
        Vector<Integer> original,
        String algoritmo,
        int n,
        String caso,
        int repeticao,
        BufferedWriter csv
    ) throws IOException {

        ordenador.setVetor(new Vector<>(original));

        long inicio = System.nanoTime();
        ordenador.sort();
        long fim = System.nanoTime();

        long tempo = fim - inicio;

        csv.write(
            algoritmo + ","
            + n + ","
            + caso + ","
            + repeticao + ","
            + tempo
        );

        csv.newLine();
    }

    private Vector<Integer> gerarVetor(int n, String caso) {

        Integer[] dados = new Integer[n];

        switch (caso) {
            case "CRESCENTE":
                CasosAlgoritmo.ordem_crescente(dados, n);
                break;

            case "DECRESCENTE":
                CasosAlgoritmo.ordem_decrescente(dados, n);
                break;

            case "ALEATORIO":
                CasosAlgoritmo.ordem_aleatoria(dados, n);
                break;

            case "REPETIDOS":
                CasosAlgoritmo.ordem_repetidas(dados, n, 5);
                break;
        }

        return new Vector<>(Arrays.asList(dados));
    }

    public static void main(String args[]) {
        String nomeArquivo = "resultados_tp1.csv";
        int reps = 10;

        if (args.length > 0) {
            nomeArquivo = args[0];
        }
        if (args.length > 1) {
            try {
                reps = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.out.println("Número de repetições inválido. Usando o padrão: 10");
            }
        }

        TestesGenerator gerador = new TestesGenerator(reps);
        Path caminho = Paths.get(nomeArquivo);

        try {
            System.out.println("Iniciando experimentos...");
            gerador.rodarTeste(caminho);
        } catch (IOException e) {
            System.err.println("Erro ao escrever o arquivo CSV: " + e.getMessage());
        }
    }
}
