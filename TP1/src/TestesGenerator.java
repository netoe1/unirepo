import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

public class TestesGenerator {

    private final int[] tamanhos = {1000, 10000, 100000, 1000000};
    private final String[] casos = {
        "CRESCENTE", "DECRESCENTE", "ALEATORIO", "REPETIDOS"
    };

    private static final List<String> TODOS_ALGORITMOS = Arrays.asList(
        "MergeSort", "BubbleSort", "InsertionSort", "QuickSort", "ShellSort",
        "SelectionSort", "HeapSort", "CountingSort", "RadixSort", "BucketSort"
    );

    private final int repeticoes;
    private final boolean debug;
    private final List<String> algoritmosSelecionados;

    private long totalExecucoes;
    private long execucoesConcluidas;
    private long inicioExperimento;

    public TestesGenerator(int repeticoes) {
        this(repeticoes, false, TODOS_ALGORITMOS);
    }

    public TestesGenerator(int repeticoes, boolean debug) {
        this(repeticoes, debug, TODOS_ALGORITMOS);
    }

    public TestesGenerator(int repeticoes, boolean debug, List<String> algoritmosSelecionados) {
        this.repeticoes = repeticoes > 0 ? repeticoes : 10;
        this.debug = debug;
        this.algoritmosSelecionados = new ArrayList<>(algoritmosSelecionados);
    }

    /**
     * Executa os experimentos e grava cada combinação algoritmo x tamanho
     * em um CSV separado. Os resultados são anexados quando o arquivo já existe.
     * O argumento arquivoBase define o prefixo e a pasta dos arquivos gerados.
     */
    public void rodarTeste(Path arquivoBase) throws IOException {
        this.totalExecucoes = calcularTotalExecucoes();
        this.execucoesConcluidas = 0;
        this.inicioExperimento = System.nanoTime();

        logDebug("Modo debug ativado.");
        logDebug("Algoritmos selecionados: " + String.join(", ", algoritmosSelecionados));
        logDebug("Medições planejadas: " + this.totalExecucoes
            + " (" + this.repeticoes + " repetições por algoritmo/configuração).");
        logDebug("Prefixo dos CSVs: " + arquivoBase.toAbsolutePath());
        logDebug("Cada algoritmo e tamanho terá seu próprio CSV.");
        logDebug("A estimativa de tempo restante é aproximada e recalculada após cada medição.");

        for (int n : tamanhos) {
            for (String caso : casos) {
                if (deveIgnorarConfiguracao(n, caso)) {
                    logDebug("Configuração ignorada: n=" + n + ", caso=" + caso
                        + " (para n=1.000.000, somente ALEATORIO é executado).");
                    continue;
                }

                logDebug("Preparando vetor: n=" + n + ", caso=" + caso);
                Vector<Integer> original = gerarVetor(n, caso);

                for (int repeticao = 1; repeticao <= repeticoes; repeticao++) {
                    for (String algoritmo : algoritmosSelecionados) {
                        Path caminhoCsv = criarCaminhoCsv(arquivoBase, algoritmo, n);
                        executar(criarOrdenador(algoritmo), original,
                            algoritmo, n, caso, repeticao, caminhoCsv);
                    }
                }
            }
        }

        long duracaoTotalMs = (System.nanoTime() - inicioExperimento) / 1_000_000L;
        System.out.println("Testes finalizados com sucesso!");
        System.out.println("CSV(s) atualizados na pasta: " + obterPastaSaida(arquivoBase).toAbsolutePath());
        System.out.println("Padrão dos arquivos: " + obterPrefixo(arquivoBase)
            + "_<Algoritmo>_n<Tamanho>.csv");
        System.out.println("Algoritmos executados: " + String.join(", ", algoritmosSelecionados));
        System.out.println("Medições registradas: " + execucoesConcluidas
            + "/" + totalExecucoes + " (" + String.format(Locale.ROOT, "%.1f", calcularProgresso()) + "%).");
        System.out.println("Tempo total: " + formatarDuracao(duracaoTotalMs) + ".");
    }

    private Path criarCaminhoCsv(Path arquivoBase, String algoritmo, int n) {
        String nome = obterPrefixo(arquivoBase) + "_" + algoritmo + "_n" + n + ".csv";
        return obterPastaSaida(arquivoBase).resolve(nome);
    }

    private String obterPrefixo(Path arquivoBase) {
        Path nomePath = arquivoBase.getFileName();
        String nome = nomePath == null ? "resultados_tp1" : nomePath.toString();
        if (nome.toLowerCase(Locale.ROOT).endsWith(".csv")) {
            nome = nome.substring(0, nome.length() - 4);
        }
        if (nome.trim().isEmpty()) {
            return "resultados_tp1";
        }
        return nome;
    }

    private Path obterPastaSaida(Path arquivoBase) {
        Path parent = arquivoBase.toAbsolutePath().getParent();
        return parent == null ? Paths.get(".").toAbsolutePath() : parent;
    }

    private Ordenador<Integer> criarOrdenador(String algoritmo) {
        switch (algoritmo) {
            case "MergeSort":
                return new MergeSort<>();
            case "BubbleSort":
                return new BubbleSort<>();
            case "InsertionSort":
                return new InsertionSort<>();
            case "QuickSort":
                return new QuickSort<>();
            case "ShellSort":
                return new ShellSort<>();
            case "SelectionSort":
                return new SelectionSort<>();
            case "HeapSort":
                return new HeapSort<>();
            case "CountingSort":
                return new CountingSort();
            case "RadixSort":
                return new RadixSort();
            case "BucketSort":
                return new BucketSort();
            default:
                throw new IllegalArgumentException("Algoritmo não reconhecido: " + algoritmo);
        }
    }

    private void executar(
        Ordenador<Integer> ordenador,
        Vector<Integer> original,
        String algoritmo,
        int n,
        String caso,
        int repeticao,
        Path caminhoCsv
    ) throws IOException {

        // A cópia do vetor fica fora da medição do algoritmo.
        ordenador.setVetor(new Vector<>(original));
        logInicioTeste(algoritmo, n, caso, repeticao, caminhoCsv);

        long inicio = System.nanoTime();
        ordenador.sort();
        long fim = System.nanoTime();
        long tempo = fim - inicio;

        registrarResultado(caminhoCsv, algoritmo, n, caso, repeticao, tempo);
        this.execucoesConcluidas++;
        logTesteConcluido(algoritmo, n, caso, repeticao, tempo, caminhoCsv);
    }

    private void registrarResultado(
        Path caminhoCsv,
        String algoritmo,
        int n,
        String caso,
        int repeticao,
        long tempoNs
    ) throws IOException {
        Files.createDirectories(caminhoCsv.toAbsolutePath().getParent());
        boolean escreverCabecalho = !Files.exists(caminhoCsv) || Files.size(caminhoCsv) == 0;

        try (BufferedWriter csv = Files.newBufferedWriter(
                caminhoCsv,
                StandardOpenOption.CREATE,
                StandardOpenOption.WRITE,
                StandardOpenOption.APPEND)) {
            if (escreverCabecalho) {
                csv.write("algoritmo,n,caso,repeticao,t_ns");
                csv.newLine();
            }

            csv.write(algoritmo + "," + n + "," + caso + "," + repeticao + "," + tempoNs);
            csv.newLine();
        }
    }

    private void logInicioTeste(
        String algoritmo, int n, String caso, int repeticao, Path caminhoCsv
    ) {
        if (!debug) {
            return;
        }

        double progresso = calcularProgresso();
        String decorrido = formatarDuracao((System.nanoTime() - inicioExperimento) / 1_000_000L);
        String restante = estimarTempoRestante();

        System.out.println(String.format(Locale.ROOT,
            "[DEBUG] Iniciando %s | n=%d | caso=%s | repetição=%d/%d | "
                + "progresso=%.1f%% (%d/%d) | decorrido=%s | ETA≈%s | CSV=%s",
            algoritmo, n, caso, repeticao, repeticoes,
            progresso, execucoesConcluidas, totalExecucoes, decorrido, restante,
            caminhoCsv.getFileName()));
    }

    private void logTesteConcluido(
        String algoritmo, int n, String caso, int repeticao, long tempoNs, Path caminhoCsv
    ) {
        if (!debug) {
            return;
        }

        double progresso = calcularProgresso();
        String decorrido = formatarDuracao((System.nanoTime() - inicioExperimento) / 1_000_000L);
        String restante = estimarTempoRestante();

        System.out.println(String.format(Locale.ROOT,
            "[DEBUG] Concluído %s | n=%d | caso=%s | repetição=%d/%d | "
                + "sort=%d ns | progresso=%.1f%% (%d/%d) | decorrido=%s | ETA≈%s | CSV=%s",
            algoritmo, n, caso, repeticao, repeticoes, tempoNs,
            progresso, execucoesConcluidas, totalExecucoes, decorrido, restante,
            caminhoCsv.getFileName()));
    }

    private long calcularTotalExecucoes() {
        long configuracoes = 0;
        for (int n : tamanhos) {
            for (String caso : casos) {
                if (!deveIgnorarConfiguracao(n, caso)) {
                    configuracoes++;
                }
            }
        }
        return configuracoes * algoritmosSelecionados.size() * repeticoes;
    }

    private boolean deveIgnorarConfiguracao(int n, String caso) {
        return n == 1000000 && !"ALEATORIO".equals(caso);
    }

    private double calcularProgresso() {
        if (totalExecucoes <= 0) {
            return 100.0;
        }
        return Math.min(100.0, execucoesConcluidas * 100.0 / totalExecucoes);
    }

    private String estimarTempoRestante() {
        if (execucoesConcluidas == 0) {
            return "calculando...";
        }
        if (execucoesConcluidas >= totalExecucoes) {
            return "00:00:00";
        }

        long decorridoNs = System.nanoTime() - inicioExperimento;
        double mediaNsPorTeste = decorridoNs / (double) execucoesConcluidas;
        double restanteNs = mediaNsPorTeste * (totalExecucoes - execucoesConcluidas);
        double restanteMs = restanteNs / 1_000_000.0;
        long restanteMsSeguro = restanteMs >= Long.MAX_VALUE
            ? Long.MAX_VALUE : Math.max(0L, (long) restanteMs);

        return formatarDuracao(restanteMsSeguro);
    }

    private String formatarDuracao(long milissegundos) {
        long segundosTotais = Math.max(0L, milissegundos) / 1000L;
        long horas = segundosTotais / 3600L;
        long minutos = (segundosTotais % 3600L) / 60L;
        long segundos = segundosTotais % 60L;
        return String.format(Locale.ROOT, "%02d:%02d:%02d", horas, minutos, segundos);
    }

    private void logDebug(String mensagem) {
        if (debug) {
            System.out.println("[DEBUG] " + mensagem);
        }
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
            default:
                throw new IllegalArgumentException("Caso de teste desconhecido: " + caso);
        }

        return new Vector<>(Arrays.asList(dados));
    }

    private static List<String> resolverAlgoritmos(String valor) {
        if (valor == null || valor.trim().isEmpty()) {
            return new ArrayList<>(TODOS_ALGORITMOS);
        }

        String chave = normalizarNomeAlgoritmo(valor);
        if (chave.equals("todos") || chave.equals("all") || chave.equals("*")) {
            return new ArrayList<>(TODOS_ALGORITMOS);
        }

        for (String algoritmo : TODOS_ALGORITMOS) {
            if (normalizarNomeAlgoritmo(algoritmo).equals(chave)) {
                return new ArrayList<>(Arrays.asList(algoritmo));
            }
        }

        switch (chave) {
            case "merge": return new ArrayList<>(Arrays.asList("MergeSort"));
            case "bubble": return new ArrayList<>(Arrays.asList("BubbleSort"));
            case "insertion": return new ArrayList<>(Arrays.asList("InsertionSort"));
            case "quick": return new ArrayList<>(Arrays.asList("QuickSort"));
            case "shell": return new ArrayList<>(Arrays.asList("ShellSort"));
            case "selection": return new ArrayList<>(Arrays.asList("SelectionSort"));
            case "heap": return new ArrayList<>(Arrays.asList("HeapSort"));
            case "counting": return new ArrayList<>(Arrays.asList("CountingSort"));
            case "radix": return new ArrayList<>(Arrays.asList("RadixSort"));
            case "bucket": return new ArrayList<>(Arrays.asList("BucketSort"));
            default: return null;
        }
    }

    private static String normalizarNomeAlgoritmo(String valor) {
        return valor.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]", "");
    }

    private static void imprimirAjuda() {
        System.out.println("Uso: java TestesGenerator [prefixo.csv] [repeticoes] [opções]");
        System.out.println("Opções:");
        System.out.println("  --debug                  Ativa logs, progresso e estimativa de tempo");
        System.out.println("  --algoritmo <nome>       Executa um algoritmo ou 'todos' (padrão: todos)");
        System.out.println("  --algoritmo=<nome>       Forma alternativa para selecionar o algoritmo");
        System.out.println("  --help                   Mostra esta ajuda");
        System.out.println("Algoritmos: " + String.join(", ", TODOS_ALGORITMOS));
        System.out.println("Os CSVs serão separados por algoritmo e tamanho, ex.: resultados_tp1_RadixSort_n1000.csv");
        System.out.println("Exemplo: java TestesGenerator resultados_tp1.csv 10 --algoritmo RadixSort --debug");
    }

    public static void main(String[] args) {
        String nomeArquivo = "resultados_tp1.csv";
        String algoritmoArg = "todos";
        int reps = 10;
        boolean debug = false;
        List<String> argumentosPosicionais = new ArrayList<>();

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if ("--help".equalsIgnoreCase(arg) || "-h".equalsIgnoreCase(arg)) {
                imprimirAjuda();
                return;
            } else if ("--debug".equalsIgnoreCase(arg)) {
                debug = true;
            } else if (arg.startsWith("--debug=")) {
                debug = Boolean.parseBoolean(arg.substring("--debug=".length()));
            } else if ("--algoritmo".equalsIgnoreCase(arg) || "--algorithm".equalsIgnoreCase(arg)
                || "-a".equalsIgnoreCase(arg)) {
                if (i + 1 >= args.length) {
                    System.err.println("Erro: informe um nome após " + arg + ".");
                    imprimirAjuda();
                    return;
                }
                algoritmoArg = args[++i];
            } else if (arg.startsWith("--algoritmo=")) {
                algoritmoArg = arg.substring("--algoritmo=".length());
            } else if (arg.startsWith("--algorithm=")) {
                algoritmoArg = arg.substring("--algorithm=".length());
            } else {
                argumentosPosicionais.add(arg);
            }
        }

        if (!argumentosPosicionais.isEmpty()) {
            nomeArquivo = argumentosPosicionais.get(0);
        }
        if (argumentosPosicionais.size() > 1) {
            try {
                reps = Integer.parseInt(argumentosPosicionais.get(1));
                if (reps < 1) {
                    throw new NumberFormatException("O número de repetições deve ser positivo.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Número de repetições inválido. Usando o padrão: 10");
                reps = 10;
            }
        }

        List<String> selecionados = resolverAlgoritmos(algoritmoArg);
        if (selecionados == null) {
            System.err.println("Algoritmo desconhecido: " + algoritmoArg);
            imprimirAjuda();
            return;
        }

        TestesGenerator gerador = new TestesGenerator(reps, debug, selecionados);
        Path caminho = Paths.get(nomeArquivo);

        try {
            System.out.println("Iniciando experimentos..." + (debug ? " (DEBUG ativado)" : ""));
            gerador.rodarTeste(caminho);
        } catch (IOException e) {
            System.err.println("Erro ao escrever os arquivos CSV: " + e.getMessage());
        }
    }
}
