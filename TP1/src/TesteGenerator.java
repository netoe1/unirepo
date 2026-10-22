import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;

/**
 * CLI nao interativa para executar uma unica configuracao experimental do TP1.
 * Todos os parametros de configuracao sao informados por argumentos, permitindo
 * automatizacao por scripts shell.
 */
public class TesteGenerator {

    private static final String CONTEXTO = "TesteGenerator";
    private static final int MAX_N = 1_000_000;
    private static final int MAX_REPETICOES = 1_000;
    private static final int AQUECIMENTOS = 2;
    private static final String CABECALHO_CSV =
        "algoritmo,n,caso,disposicao,sessao,repeticao,t_ns";

    private static final String[] ALGORITMOS = {
        "BubbleSort", "InsertionSort", "SelectionSort", "ShellSort", "HeapSort",
        "MergeSort", "QuickSort", "CountingSort", "RadixSort", "BucketSort"
    };
    private static final String[] CASOS = {"MELHOR", "PIOR", "MEDIO"};
    private static final String[] DISPOSICOES = {
        "CRESCENTE", "DECRESCENTE", "ALEATORIO", "REPETIDOS"
    };

    private final int repeticoes;
    private final String caso;
    private final String algoritmo;
    private final String disposicao;
    private final int n;
    private final Path caminhoCsv;
    private final String sessao;

    private long inicioExperimentoNs;
    private int repeticoesConcluidas;

    private TesteGenerator(
        int repeticoes,
        String caso,
        String algoritmo,
        String disposicao,
        int n,
        Path caminhoCsv
    ) {
        this.repeticoes = repeticoes;
        this.caso = caso;
        this.algoritmo = algoritmo;
        this.disposicao = disposicao;
        this.n = n;
        this.caminhoCsv = caminhoCsv;
        this.sessao = LocalDateTime.now().format(
            DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS", Locale.ROOT)
        );
    }

    public static void main(String[] args) {
        Map<String, String> opcoes;
        try {
            opcoes = analisarArgumentos(args);
        } catch (IllegalArgumentException e) {
            Logs.error(CONTEXTO, e.getMessage());
            imprimirAjuda();
            System.exit(2);
            return;
        }

        if (opcoes.containsKey("help")) {
            imprimirAjuda();
            return;
        }

        boolean debug = opcoes.containsKey("debug");
        Logs.setDebug(debug);
        if (opcoes.containsKey("no-color")) {
            Logs.setCores(false);
        }

        try {
            int repeticoes = parseInteiroObrigatorio(opcoes, "repeticoes", 1, MAX_REPETICOES);
            int n = parseInteiroObrigatorio(opcoes, "n", 1, MAX_N);
            String caso = normalizarCaso(obrigatorio(opcoes, "caso"));
            String algoritmo = normalizarAlgoritmo(obrigatorio(opcoes, "algoritmo"));
            String disposicao = normalizarDisposicao(obrigatorio(opcoes, "disposicao"));
            String prefixo = opcoes.getOrDefault("saida", "resultados_tp1");

            if (!validarLimites(algoritmo, disposicao, n, repeticoes)) {
                System.exit(2);
                return;
            }

            Path csv = criarCaminhoCsv(prefixo, algoritmo, n);
            avisarSobreCaso(algoritmo, caso, disposicao);
            Logs.divider(CONTEXTO, "Experimento TP1 ACD");
            Logs.info(CONTEXTO, "Algoritmo: " + algoritmo);
            Logs.info(CONTEXTO, "Configuracao: repeticoes=" + repeticoes
                + ", caso=" + caso + ", disposicao=" + disposicao + ", n=" + n
                + ", aquecimentos=" + AQUECIMENTOS);
            Logs.info(CONTEXTO, "CSV: " + csv.toAbsolutePath());
            Logs.info(CONTEXTO, "Somente sort() sera cronometrado; geracao e copia ficam fora da medicao.");
            Logs.info(CONTEXTO, "Modo CLI: nenhum dado sera solicitado pelo terminal.");

            TesteGenerator gerador = new TesteGenerator(
                repeticoes, caso, algoritmo, disposicao, n, csv);
            gerador.executar();
        } catch (IllegalArgumentException e) {
            Logs.error(CONTEXTO, e.getMessage());
            imprimirAjuda();
            System.exit(2);
        } catch (IOException e) {
            Logs.error(CONTEXTO, "Erro de arquivo: " + e.getMessage());
            System.exit(1);
        } catch (RuntimeException e) {
            Logs.error(CONTEXTO, "Erro durante a execucao: " + e.getMessage());
            if (debug) {
                e.printStackTrace(System.err);
            }
            System.exit(1);
        }
    }

    /** Parseia flags e opcoes em pares --nome valor; nao le dados de stdin. */
    private static Map<String, String> analisarArgumentos(String[] args) {
        Map<String, String> opcoes = new HashMap<>();
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if ("--help".equalsIgnoreCase(arg) || "-h".equalsIgnoreCase(arg)) {
                opcoes.put("help", "true");
            } else if ("--debug".equalsIgnoreCase(arg)) {
                opcoes.put("debug", "true");
            } else if ("--no-color".equalsIgnoreCase(arg)) {
                opcoes.put("no-color", "true");
            } else {
                if (!arg.startsWith("--")) {
                    throw new IllegalArgumentException("Argumento invalido: " + arg);
                }
                String nome = arg.substring(2).toLowerCase(Locale.ROOT);
                if (!Arrays.asList("repeticoes", "caso", "algoritmo", "disposicao", "n", "saida")
                        .contains(nome)) {
                    throw new IllegalArgumentException("Opcao desconhecida: " + arg);
                }
                if (i + 1 >= args.length || args[i + 1].startsWith("--")) {
                    throw new IllegalArgumentException("Falta informar o valor de " + arg);
                }
                opcoes.put(nome, args[++i]);
            }
        }

        if (!opcoes.containsKey("help")) {
            String[] obrigatorias = {"repeticoes", "caso", "algoritmo", "disposicao", "n"};
            for (String chave : obrigatorias) {
                if (!opcoes.containsKey(chave)) {
                    throw new IllegalArgumentException("Parametro obrigatorio ausente: --" + chave);
                }
            }
        }
        return opcoes;
    }

    private static String obrigatorio(Map<String, String> opcoes, String chave) {
        String valor = opcoes.get(chave);
        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException("O parametro --" + chave + " nao pode estar vazio.");
        }
        return valor.trim();
    }

    private static int parseInteiroObrigatorio(
        Map<String, String> opcoes, String chave, int minimo, int maximo
    ) {
        String valorTexto = obrigatorio(opcoes, chave);
        final int valor;
        try {
            valor = Integer.parseInt(valorTexto);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("--" + chave + " deve ser um numero inteiro: " + valorTexto);
        }
        if (valor < minimo || valor > maximo) {
            throw new IllegalArgumentException("--" + chave + " deve estar entre " + minimo + " e " + maximo + ".");
        }
        return valor;
    }

    private static String normalizarAlgoritmo(String valor) {
        String informado = limparIdentificador(valor);
        for (String algoritmo : ALGORITMOS) {
            if (limparIdentificador(algoritmo).equals(informado)) {
                return algoritmo;
            }
        }
        String[] apelidos = {"bubble", "insertion", "selection", "shell", "heap", "merge", "quick", "counting", "radix", "bucket"};
        for (int i = 0; i < apelidos.length; i++) {
            if (apelidos[i].equals(informado)) {
                return ALGORITMOS[i];
            }
        }
        throw new IllegalArgumentException("Algoritmo invalido: " + valor + ". Use --help para ver as opcoes.");
    }

    private static String limparIdentificador(String valor) {
        return valor.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]", "");
    }

    private static String normalizarCaso(String valor) {
        String caso = valor.trim().toUpperCase(Locale.ROOT);
        if ("MEDIO".equals(caso) || "MÉDIO".equals(caso) || "MEDIOCASO".equals(caso)) {
            return "MEDIO";
        }
        if ("MELHOR".equals(caso) || "MELHORCASO".equals(caso)) {
            return "MELHOR";
        }
        if ("PIOR".equals(caso) || "PIORCASO".equals(caso)) {
            return "PIOR";
        }
        throw new IllegalArgumentException("Caso invalido: " + valor + ". Valores aceitos: MELHOR, PIOR, MEDIO.");
    }

    private static String normalizarDisposicao(String valor) {
        String disposicao = limparIdentificador(valor);
        switch (disposicao) {
            case "crescente":
                return "CRESCENTE";
            case "decrescente":
                return "DECRESCENTE";
            case "aleatorio":
                return "ALEATORIO";
            case "repetidos":
            case "valoresrepetidos":
                return "REPETIDOS";
            default:
                throw new IllegalArgumentException("Disposicao invalida: " + valor
                    + ". Valores aceitos: CRESCENTE, DECRESCENTE, ALEATORIO, REPETIDOS.");
        }
    }

    private void executar() throws IOException {
        prepararCsv(caminhoCsv);
        inicioExperimentoNs = System.nanoTime();
        repeticoesConcluidas = 0;

        Logs.divider(CONTEXTO, "Preparacao dos dados");
        Vector<Integer> original = gerarVetor(n, disposicao);
        Logs.success(CONTEXTO, "Vetor gerado: n=" + n + ", disposicao=" + disposicao);

        Logs.divider(CONTEXTO, "Aquecimento da JVM");
        for (int i = 1; i <= AQUECIMENTOS; i++) {
            Ordenador<Integer> ordenador = criarOrdenador(algoritmo);
            ordenador.setVetor(new Vector<>(original));
            Logs.debug(CONTEXTO, "Aquecimento " + i + "/" + AQUECIMENTOS + " - " + algoritmo);
            ordenador.sort();
        }

        Logs.divider(CONTEXTO, "Medicoes");
        for (int repeticao = 1; repeticao <= repeticoes; repeticao++) {
            Ordenador<Integer> ordenador = criarOrdenador(algoritmo);
            ordenador.setVetor(new Vector<>(original)); // copia fora do cronometro

            Logs.debug(CONTEXTO, "Iniciando " + algoritmo + " | n=" + n
                + " | caso=" + caso + " | disposicao=" + disposicao
                + " | repeticao=" + repeticao + "/" + repeticoes);

            long inicio = System.nanoTime();
            ordenador.sort();
            long tempoNs = System.nanoTime() - inicio;

            registrarResultado(repeticao, tempoNs);
            repeticoesConcluidas++;

            long decorridoMs = (System.nanoTime() - inicioExperimentoNs) / 1_000_000L;
            Logs.debug(CONTEXTO, "Concluido " + algoritmo + " | sort=" + tempoNs
                + " ns | repeticao=" + repeticao + "/" + repeticoes
                + " | CSV=" + caminhoCsv.getFileName());
            Logs.progress(CONTEXTO, repeticoesConcluidas, repeticoes,
                formatarDuracao(decorridoMs), estimarRestante(decorridoMs, repeticoesConcluidas, repeticoes));
        }

        long totalMs = (System.nanoTime() - inicioExperimentoNs) / 1_000_000L;
        Logs.divider(CONTEXTO, "Resumo");
        Logs.success(CONTEXTO, "Experimento finalizado.");
        Logs.info(CONTEXTO, "Medicoes gravadas nesta execucao: " + repeticoesConcluidas);
        Logs.info(CONTEXTO, "Tempo total (inclui preparacao e aquecimentos): " + formatarDuracao(totalMs));
        Logs.info(CONTEXTO, "CSV: " + caminhoCsv.toAbsolutePath());
    }

    private void registrarResultado(int repeticao, long tempoNs) throws IOException {
        try (BufferedWriter escritor = Files.newBufferedWriter(
                caminhoCsv,
                StandardOpenOption.CREATE,
                StandardOpenOption.WRITE,
                StandardOpenOption.APPEND)) {
            escritor.write(algoritmo + "," + n + "," + caso + "," + disposicao + ","
                + sessao + "," + repeticao + "," + tempoNs);
            escritor.newLine();
        }
    }

    private static void prepararCsv(Path caminho) throws IOException {
        Path pasta = caminho.toAbsolutePath().getParent();
        if (pasta != null) {
            Files.createDirectories(pasta);
        }
        if (Files.exists(caminho) && Files.size(caminho) > 0) {
            try (BufferedReader leitor = Files.newBufferedReader(caminho)) {
                String cabecalhoExistente = leitor.readLine();
                if (!CABECALHO_CSV.equals(cabecalhoExistente)) {
                    throw new IOException("Cabecalho CSV incompatível em " + caminho
                        + ". Esperado: " + CABECALHO_CSV + ". Use outro --saida para evitar misturar formatos.");
                }
            }
        } else {
            try (BufferedWriter escritor = Files.newBufferedWriter(
                    caminho, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING,
                    StandardOpenOption.WRITE)) {
                escritor.write(CABECALHO_CSV);
                escritor.newLine();
            }
        }
    }

    private static Vector<Integer> gerarVetor(int n, String disposicao) {
        Integer[] dados = new Integer[n];
        switch (disposicao) {
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
                throw new IllegalArgumentException("Disposicao desconhecida: " + disposicao);
        }
        return new Vector<>(Arrays.asList(dados));
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
            case "CountingSort": return new CountingSort();
            case "RadixSort": return new RadixSort();
            case "BucketSort": return new BucketSort();
            default: throw new IllegalArgumentException("Algoritmo nao reconhecido: " + algoritmo);
        }
    }

    private static boolean validarLimites(String algoritmo, String disposicao, int n, int repeticoes) {
        if (n > MAX_N) {
            Logs.error(CONTEXTO, "Limite maximo de n: " + MAX_N + ".");
            return false;
        }
        if (isQuadratico(algoritmo) && n > 10_000) {
            Logs.error(CONTEXTO, algoritmo + " limitado a n <= 10000 para reduzir execucoes quadraticas excessivas.");
            return false;
        }
        if ("QuickSort".equals(algoritmo) && n > 100_000) {
            Logs.error(CONTEXTO, "QuickSort limitado a n <= 100000 por risco de degradacao/recursao.");
            return false;
        }
        if ("QuickSort".equals(algoritmo)
            && ("CRESCENTE".equals(disposicao) || "DECRESCENTE".equals(disposicao))
            && n > 1_000) {
            Logs.error(CONTEXTO, "QuickSort com disposicao crescente/decrescente limitado a n <= 1000.");
            return false;
        }
        if (isQuadratico(algoritmo) && n >= 5_000 && repeticoes > 10) {
            Logs.error(CONTEXTO, "Configuracao conservadora: algoritmo quadratico com n >= 5000 aceita no maximo 10 repeticoes.");
            return false;
        }
        if (n >= 100_000) {
            Logs.warn(CONTEXTO, "n elevado: consumo de memoria/tempo depende da implementacao e da heap da JVM.");
        }
        return true;
    }

    private static boolean isQuadratico(String algoritmo) {
        return "BubbleSort".equals(algoritmo)
            || "InsertionSort".equals(algoritmo)
            || "SelectionSort".equals(algoritmo);
    }

    private static void avisarSobreCaso(String algoritmo, String caso, String disposicao) {
        if (("BubbleSort".equals(algoritmo) || "InsertionSort".equals(algoritmo))) {
            String esperada = "MELHOR".equals(caso) ? "CRESCENTE"
                : ("PIOR".equals(caso) ? "DECRESCENTE" : "ALEATORIO");
            if (!esperada.equals(disposicao)) {
                Logs.warn(CONTEXTO, "Para " + algoritmo + ", a disposicao normalmente associada ao caso "
                    + caso + " e " + esperada + "; a configuracao solicitada sera registrada mesmo assim.");
            }
        }
        Logs.info(CONTEXTO, "caso e disposicao sao registrados separadamente; a disposicao nao define automaticamente o caso teorico para todos os algoritmos.");
    }

    private static Path criarCaminhoCsv(String prefixoInformado, String algoritmo, int n) {
        String prefixo = prefixoInformado.trim();
        if (prefixo.toLowerCase(Locale.ROOT).endsWith(".csv")) {
            prefixo = prefixo.substring(0, prefixo.length() - 4);
        }
        if (prefixo.isEmpty()) {
            prefixo = "resultados_tp1";
        }
        Path caminhoPrefixo = Paths.get(prefixo).toAbsolutePath();
        Path pasta = caminhoPrefixo.getParent();
        String nomePrefixo = caminhoPrefixo.getFileName() == null
            ? "resultados_tp1" : caminhoPrefixo.getFileName().toString();
        String nomeArquivo = nomePrefixo + "_" + algoritmo + "_n" + n + ".csv";
        return pasta == null ? Paths.get(nomeArquivo) : pasta.resolve(nomeArquivo);
    }

    private static String estimarRestante(long decorridoMs, int concluidas, int total) {
        if (concluidas <= 0) return "calculando...";
        if (concluidas >= total) return "00:00:00";
        double mediaMs = decorridoMs / (double) concluidas;
        long restanteMs = Math.max(0L, (long) (mediaMs * (total - concluidas)));
        return formatarDuracao(restanteMs);
    }

    private static String formatarDuracao(long milissegundos) {
        long segundos = Math.max(0L, milissegundos) / 1000L;
        long horas = segundos / 3600L;
        long minutos = (segundos % 3600L) / 60L;
        long segundosRestantes = segundos % 60L;
        return String.format(Locale.ROOT, "%02d:%02d:%02d", horas, minutos, segundosRestantes);
    }

    private static void imprimirAjuda() {
        System.out.println("Uso: ./executar_testes.sh --repeticoes N --caso MELHOR|PIOR|MEDIO"
            + " --algoritmo NOME --disposicao CRESCENTE|DECRESCENTE|ALEATORIO|REPETIDOS --n TAMANHO [opcoes]");
        System.out.println();
        System.out.println("Parametros obrigatorios:");
        System.out.println("  --repeticoes N       Numero de medicoes oficiais (1.." + MAX_REPETICOES + ")");
        System.out.println("  --caso VALOR         MELHOR, PIOR ou MEDIO (aceita MEDIO)");
        System.out.println("  --algoritmo NOME     BubbleSort, InsertionSort, SelectionSort, ShellSort,");
        System.out.println("                       HeapSort, MergeSort, QuickSort, CountingSort, RadixSort ou BucketSort");
        System.out.println("  --disposicao VALOR   CRESCENTE, DECRESCENTE, ALEATORIO ou REPETIDOS");
        System.out.println("  --n TAMANHO          Quantidade de elementos (1.." + MAX_N + ")");
        System.out.println();
        System.out.println("Opcoes:");
        System.out.println("  --saida PREFIXO      Prefixo e/ou pasta dos CSVs (padrao: resultados_tp1)");
        System.out.println("  --debug              Mostra detalhes e progresso");
        System.out.println("  --no-color           Desativa as cores ANSI");
        System.out.println("  --help, -h           Mostra esta ajuda");
        System.out.println();
        System.out.println("Exemplo:");
        System.out.println("  ./executar_testes.sh --repeticoes 10 --caso MEDIO --algoritmo InsertionSort"
            + " --disposicao ALEATORIO --n 1000 --saida resultados/tp1 --debug");
        System.out.println("CSV: <prefixo>_<Algoritmo>_n<Tamanho>.csv; resultados existentes sao acumulados.");
    }
}
