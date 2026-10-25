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
import java.util.Random;
import java.util.Vector;

/**
 * Executa uma configuracao experimental do TP1 via CLI, sem interacao com stdin.
 *
 * A entrada e configurada com --disposicao. O rotulo --caso (MELHOR, MEDIO ou
 * PIOR) e registrado separadamente porque sua relacao com a entrada depende do
 * algoritmo e da implementacao concreta.
 */
public class ScriptParametrizavel {

    private static final String CONTEXTO = "ScriptParametrizavel";
    private static final int MAX_N = 1_000_000;
    private static final int MAX_REPETICOES = 1_000;
    private static final int AQUECIMENTOS = 2;
    private static final int MAX_N_QUADRATICOS = 10_000;
    private static final String CABECALHO_CSV =
        "algoritmo,n,caso,disposicao,semente,sessao,repeticao,t_ns";

    private static final String[] ALGORITMOS = {
        "BubbleSort", "InsertionSort", "SelectionSort", "ShellSort", "HeapSort",
        "MergeSort", "QuickSort", "CountingSort", "RadixSort", "BucketSort"
    };

    private static final String[] CASOS = {"MELHOR", "MEDIO", "PIOR"};
    private static final String[] DISPOSICOES = {
        "CRESCENTE", "DECRESCENTE", "ALEATORIO", "REPETIDOS"
    };

    private final int repeticoes;
    private final String caso;
    private final String algoritmo;
    private final String disposicao;
    private final int n;
    private final long semente;
    private final Path caminhoCsv;
    private final String sessao;
    private final boolean debug;

    private ScriptParametrizavel(
        int repeticoes,
        String caso,
        String algoritmo,
        String disposicao,
        int n,
        long semente,
        Path caminhoCsv,
        boolean debug
    ) {
        this.repeticoes = repeticoes;
        this.caso = caso;
        this.algoritmo = algoritmo;
        this.disposicao = disposicao;
        this.n = n;
        this.semente = semente;
        this.caminhoCsv = caminhoCsv;
        this.debug = debug;
        this.sessao = LocalDateTime.now().format(
            DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS", Locale.ROOT)
        );
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            imprimirAjuda();
            return;
        }

        final Map<String, String> opcoes;
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
            int repeticoes = parseInteiro(opcoes, "repeticoes", 1, MAX_REPETICOES);
            int n = parseInteiro(opcoes, "n", 1, MAX_N);
            long semente = parseLong(opcoes, "semente", 2026L);
            String caso = normalizarCaso(obrigatorio(opcoes, "caso"));
            String algoritmo = normalizarAlgoritmo(obrigatorio(opcoes, "algoritmo"));
            String disposicao = normalizarDisposicao(obrigatorio(opcoes, "disposicao"));
            String prefixo = opcoes.getOrDefault("saida", "resultados_tp1");

            validarLimites(algoritmo, disposicao, n, repeticoes);
            Path csv = criarCaminhoCsv(prefixo, algoritmo, n);
            avisarSobreCaso(algoritmo, caso, disposicao);

            Logs.divider(CONTEXTO, "Experimento TP1 ACD");
            Logs.info(CONTEXTO, "Algoritmo: " + algoritmo);
            Logs.info(CONTEXTO, "Configuracao: repeticoes=" + repeticoes
                + ", caso=" + caso + ", disposicao=" + disposicao + ", n=" + n
                + ", semente=" + semente + ", aquecimentos=" + AQUECIMENTOS);
            Logs.info(CONTEXTO, "CSV: " + csv.toAbsolutePath());
            Logs.info(CONTEXTO, "Apenas sort() sera cronometrado; geracao e copia ficam fora da medicao.");
            Logs.info(CONTEXTO, "Execucao nao interativa: nenhum dado sera lido de stdin.");

            ScriptParametrizavel gerador = new ScriptParametrizavel(
                repeticoes, caso, algoritmo, disposicao, n, semente, csv, debug);
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

    /** Analisa pares --opcao valor e flags; nao solicita nenhuma entrada pelo stdin. */
    private static Map<String, String> analisarArgumentos(String[] args) {
        Map<String, String> opcoes = new HashMap<>();
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if ("--help".equalsIgnoreCase(arg) || "-h".equalsIgnoreCase(arg)) {
                opcoes.put("help", "true");
                continue;
            }
            if ("--debug".equalsIgnoreCase(arg)) {
                opcoes.put("debug", "true");
                continue;
            }
            if ("--no-color".equalsIgnoreCase(arg)) {
                opcoes.put("no-color", "true");
                continue;
            }
            if (!arg.startsWith("--")) {
                throw new IllegalArgumentException("Argumento invalido: " + arg);
            }

            String nome = arg.substring(2).toLowerCase(Locale.ROOT);
            if (!Arrays.asList(
                    "repeticoes", "caso", "algoritmo", "disposicao", "n", "saida", "semente"
                ).contains(nome)) {
                throw new IllegalArgumentException("Opcao desconhecida: " + arg);
            }
            if (opcoes.containsKey(nome)) {
                throw new IllegalArgumentException("Opcao informada mais de uma vez: " + arg);
            }
            if (i + 1 >= args.length || args[i + 1].startsWith("--")) {
                throw new IllegalArgumentException("Falta informar o valor de " + arg);
            }
            opcoes.put(nome, args[++i]);
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

    private static int parseInteiro(Map<String, String> opcoes, String chave, int minimo, int maximo) {
        String texto = obrigatorio(opcoes, chave);
        final int valor;
        try {
            valor = Integer.parseInt(texto);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("--" + chave + " deve ser um inteiro: " + texto);
        }
        if (valor < minimo || valor > maximo) {
            throw new IllegalArgumentException("--" + chave + " deve estar entre " + minimo + " e " + maximo + ".");
        }
        return valor;
    }

    private static long parseLong(Map<String, String> opcoes, String chave, long padrao) {
        if (!opcoes.containsKey(chave)) {
            return padrao;
        }
        try {
            return Long.parseLong(opcoes.get(chave));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("--" + chave + " deve ser um inteiro longo.");
        }
    }

    private static String normalizarAlgoritmo(String valor) {
        String informado = limparIdentificador(valor);
        for (String algoritmo : ALGORITMOS) {
            if (limparIdentificador(algoritmo).equals(informado)) {
                return algoritmo;
            }
        }
        String[] apelidos = {
            "bubble", "insertion", "selection", "shell", "heap",
            "merge", "quick", "counting", "radix", "bucket"
        };
        for (int i = 0; i < apelidos.length; i++) {
            if (apelidos[i].equals(informado)) {
                return ALGORITMOS[i];
            }
        }
        throw new IllegalArgumentException("Algoritmo invalido: " + valor + ". Consulte --help.");
    }

    private static String limparIdentificador(String valor) {
        return valor.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]", "");
    }

    private static String normalizarCaso(String valor) {
        String caso = limparIdentificador(valor);
        if ("medio".equals(caso) || "mediocaso".equals(caso)) return "MEDIO";
        if ("melhor".equals(caso) || "melhorcaso".equals(caso)) return "MELHOR";
        if ("pior".equals(caso) || "piorcaso".equals(caso)) return "PIOR";
        throw new IllegalArgumentException("Caso invalido: " + valor + ". Use MELHOR, MEDIO ou PIOR.");
    }

    private static String normalizarDisposicao(String valor) {
        String disposicao = limparIdentificador(valor);
        switch (disposicao) {
            case "crescente": return "CRESCENTE";
            case "decrescente": return "DECRESCENTE";
            case "aleatorio": return "ALEATORIO";
            case "repetidos":
            case "valoresrepetidos": return "REPETIDOS";
            default: throw new IllegalArgumentException(
                "Disposicao invalida: " + valor + ". Use CRESCENTE, DECRESCENTE, ALEATORIO ou REPETIDOS.");
        }
    }

    private void executar() throws IOException {
        prepararCsv(caminhoCsv);
        Vector<Integer> original = gerarVetor(n, disposicao, semente);
        Logs.success(CONTEXTO, "Vetor gerado: n=" + n + ", disposicao=" + disposicao
            + ", minimo=" + minimo(original) + ", maximo=" + maximo(original));

        Logs.divider(CONTEXTO, "Aquecimento da JVM");
        for (int i = 1; i <= AQUECIMENTOS; i++) {
            Ordenador<Integer> ordenador = criarOrdenador(algoritmo);
            ordenador.setVetor(new Vector<>(original));
            Logs.debug(CONTEXTO, "Aquecimento " + i + "/" + AQUECIMENTOS + " - " + algoritmo);
            ordenador.sort();
        }

        Logs.divider(CONTEXTO, "Medicoes");
        long inicioExperimento = System.nanoTime();
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

            long decorridoMs = (System.nanoTime() - inicioExperimento) / 1_000_000L;
            Logs.debug(CONTEXTO, "Concluido " + algoritmo + " | sort=" + tempoNs
                + " ns | repeticao=" + repeticao + "/" + repeticoes);
            Logs.progress(CONTEXTO, repeticao, repeticoes,
                formatarDuracao(decorridoMs), estimarRestante(decorridoMs, repeticao, repeticoes));
        }

        long totalMs = (System.nanoTime() - inicioExperimento) / 1_000_000L;
        Logs.divider(CONTEXTO, "Resumo");
        Logs.success(CONTEXTO, "Experimento finalizado; medicoes gravadas: " + repeticoes);
        Logs.info(CONTEXTO, "Tempo total das medicoes: " + formatarDuracao(totalMs));
        Logs.info(CONTEXTO, "CSV: " + caminhoCsv.toAbsolutePath());
    }

    private void registrarResultado(int repeticao, long tempoNs) throws IOException {
        try (BufferedWriter escritor = Files.newBufferedWriter(
                caminhoCsv,
                StandardOpenOption.CREATE,
                StandardOpenOption.WRITE,
                StandardOpenOption.APPEND)) {
            escritor.write(algoritmo + "," + n + "," + caso + "," + disposicao + ","
                + semente + "," + sessao + "," + repeticao + "," + tempoNs);
            escritor.newLine();
        }
    }

    private static void prepararCsv(Path caminho) throws IOException {
        Path pasta = caminho.toAbsolutePath().getParent();
        if (pasta != null) Files.createDirectories(pasta);

        if (Files.exists(caminho) && Files.size(caminho) > 0) {
            try (BufferedReader leitor = Files.newBufferedReader(caminho)) {
                String existente = leitor.readLine();
                if (!CABECALHO_CSV.equals(existente)) {
                    throw new IOException("Cabecalho CSV incompativel. Esperado: " + CABECALHO_CSV
                        + ". Informe outro --saida para nao misturar formatos.");
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

    /** Cria sempre a mesma entrada para a combinacao n/disposicao/semente. */
    private static Vector<Integer> gerarVetor(int n, String disposicao, long semente) {
        Integer[] dados = new Integer[n];
        switch (disposicao) {
            case "CRESCENTE":
                for (int i = 0; i < n; i++) dados[i] = i;
                break;
            case "DECRESCENTE":
                for (int i = 0; i < n; i++) dados[i] = n - 1 - i;
                break;
            case "ALEATORIO":
                // Permutacao de 0..n-1: evita que o teste aleatorio tenha duplicatas acidentais.
                for (int i = 0; i < n; i++) dados[i] = i;
                Random random = new Random(semente ^ (31L * n));
                for (int i = n - 1; i > 0; i--) {
                    int j = random.nextInt(i + 1);
                    int temporario = dados[i];
                    dados[i] = dados[j];
                    dados[j] = temporario;
                }
                break;
            case "REPETIDOS":
                Arrays.fill(dados, 5);
                break;
            default:
                throw new IllegalArgumentException("Disposicao desconhecida: " + disposicao);
        }
        return new Vector<>(Arrays.asList(dados));
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
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
            default: throw new IllegalArgumentException("Algoritmo nao reconhecido: " + algoritmo);
        }
    }

    private static void validarLimites(String algoritmo, String disposicao, int n, int repeticoes) {
        if (isQuadratico(algoritmo) && n > MAX_N_QUADRATICOS) {
            throw new IllegalArgumentException(algoritmo + " esta limitado a n <= "
                + MAX_N_QUADRATICOS + " para evitar experimentos quadraticos excessivamente longos.");
        }
        if ("QuickSort".equals(algoritmo) && n > 100_000) {
            throw new IllegalArgumentException("QuickSort esta limitado a n <= 100000 por risco de degradacao/recursao.");
        }
        if ("QuickSort".equals(algoritmo) && n > 1_000 && !"ALEATORIO".equals(disposicao)) {
            throw new IllegalArgumentException("QuickSort com disposicao " + disposicao
                + " esta limitado a n <= 1000; com pivo no ultimo elemento pode degenerar para O(n^2).");
        }
        if (isQuadratico(algoritmo) && n >= 5_000 && repeticoes > 10) {
            throw new IllegalArgumentException("Para algoritmo quadratico e n >= 5000, use no maximo 10 repeticoes.");
        }
        if (n >= 100_000) {
            Logs.warn(CONTEXTO, "n elevado: tempo e memoria dependem da implementacao, JVM e hardware.");
        }
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
                Logs.warn(CONTEXTO, "Para " + algoritmo + ", a disposicao geralmente associada ao caso "
                    + caso + " e " + esperada + "; a combinacao informada sera mantida e registrada.");
            }
        }
        if ("QuickSort".equals(algoritmo) && "PIOR".equals(caso)
                && "ALEATORIO".equals(disposicao)) {
            Logs.warn(CONTEXTO, "QuickSort com entrada aleatoria normalmente nao representa seu pior caso.");
        }
        Logs.info(CONTEXTO, "O rotulo caso e a disposicao sao registrados separadamente; a disposicao nao determina o caso teorico para todos os algoritmos.");
    }

    private static int minimo(Vector<Integer> vetor) {
        int minimo = Integer.MAX_VALUE;
        for (int valor : vetor) if (valor < minimo) minimo = valor;
        return minimo;
    }

    private static int maximo(Vector<Integer> vetor) {
        int maximo = Integer.MIN_VALUE;
        for (int valor : vetor) if (valor > maximo) maximo = valor;
        return maximo;
    }

    private static Path criarCaminhoCsv(String prefixoInformado, String algoritmo, int n) {
        String prefixo = prefixoInformado.trim();
        if (prefixo.toLowerCase(Locale.ROOT).endsWith(".csv")) {
            prefixo = prefixo.substring(0, prefixo.length() - 4);
        }
        if (prefixo.isEmpty()) prefixo = "resultados_tp1";
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
        System.out.println("Uso: java ScriptParametrizavel --repeticoes N --caso MELHOR|MEDIO|PIOR"
            + " --algoritmo NOME --disposicao CRESCENTE|DECRESCENTE|ALEATORIO|REPETIDOS --n TAMANHO [opcoes]");
        System.out.println();
        System.out.println("Parametros obrigatorios:");
        System.out.println("  --repeticoes N       Medicoes oficiais (1.." + MAX_REPETICOES + ")");
        System.out.println("  --caso VALOR         MELHOR, MEDIO ou PIOR (rotulo teorico/experimental)");
        System.out.println("  --algoritmo NOME     " + String.join(", ", ALGORITMOS));
        System.out.println("  --disposicao VALOR   CRESCENTE, DECRESCENTE, ALEATORIO ou REPETIDOS");
        System.out.println("  --n TAMANHO          Numero de elementos (1.." + MAX_N + ")");
        System.out.println();
        System.out.println("Parametros opcionais:");
        System.out.println("  --saida PREFIXO      Pasta/prefixo dos CSVs (padrao: resultados_tp1)");
        System.out.println("  --semente INTEIRO    Semente do caso aleatorio (padrao: 2026; entrada reproduzivel)");
        System.out.println("  --debug              Mostra configuracao, progresso e estimativa de tempo restante");
        System.out.println("  --no-color           Desativa cores ANSI nos logs");
        System.out.println("  --help, -h           Mostra esta ajuda");
        System.out.println();
        System.out.println("Exemplo:");
        System.out.println("  java ScriptParametrizavel --repeticoes 10 --caso MEDIO --algoritmo InsertionSort"
            + " --disposicao ALEATORIO --n 1000 --saida resultados/tp1 --debug");
        System.out.println("CSV: <prefixo>_<Algoritmo>_n<Tamanho>.csv; novas medicoes sao acrescentadas ao arquivo.");
        System.out.println("O programa nao usa Scanner, Console nem System.in: toda configuracao vem dos argumentos CLI.");
    }
}