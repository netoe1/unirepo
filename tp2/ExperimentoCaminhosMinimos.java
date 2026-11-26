import algoritmos.BellmanFord;
import algoritmos.BellmanFord.ResultadoBellmanFord;
import algoritmos.Dijkstra;
import algoritmos.Dijkstra.ResultadoDijkstra;
import grafo.GrafoListaAdjacencia;
import grafo.GrafoMatrizAdjacencia;
import grafo.IGrafo;
import grafo.TipoGrafo;
import io.ExportadorCSV;
import io.LeituraGrafo.Representacao;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public final class ExperimentoCaminhosMinimos {

    private static final String[] ARQUIVOS = {
        "sample100-1980.gr", "sample100-3960.gr", "sample100-5940.gr", "sample100-7920.gr", "sample100-9900.gr",
        "sample200-7960.gr", "sample200-15920.gr", "sample200-23880.gr", "sample200-31840.gr", "sample200-39800.gr",
        "sample500-49900.gr", "sample500-99800.gr", "sample500-149700.gr", "sample500-199600.gr", "sample500-249500.gr"
    };

    private final Path raizRepositorio;
    private final Path pastaSaida;
    private final int repeticoes;
    private final int limiteGrafos;
    private final ExportadorCSV exportador;
    private int linhasRegistradas;

    private ExperimentoCaminhosMinimos(Path raizRepositorio, int repeticoes, int limiteGrafos, Path pastaSaida) {
        this.raizRepositorio = raizRepositorio;
        this.pastaSaida = pastaSaida;
        this.repeticoes = repeticoes;
        this.limiteGrafos = limiteGrafos;
        this.exportador = new ExportadorCSV(pastaSaida.toString());
    }

    public static void main(String[] args) throws IOException {
        if (args.length < 1 || args.length > 3) {
            throw new IllegalArgumentException(
                    "Uso: ExperimentoCaminhosMinimos <raiz-repositorio> [repeticoes] [limite-grafos]"
            );
        }

        Path raiz = Paths.get(args[0]).toAbsolutePath().normalize();
        int repeticoes = args.length >= 2 ? Integer.parseInt(args[1]) : 10;
        int limiteGrafos = args.length >= 3 ? Integer.parseInt(args[2]) : ARQUIVOS.length;
        if (repeticoes < 1) {
            throw new IllegalArgumentException("O numero de repeticoes deve ser positivo.");
        }
        if (limiteGrafos < 1 || limiteGrafos > ARQUIVOS.length) {
            throw new IllegalArgumentException("O limite de grafos deve estar entre 1 e " + ARQUIVOS.length + ".");
        }

        Path pastaSaida = criarPastaSaida(raiz);
        ExperimentoCaminhosMinimos experimento = new ExperimentoCaminhosMinimos(
                raiz, repeticoes, limiteGrafos, pastaSaida
        );
        experimento.executar();
    }

    private static Path criarPastaSaida(Path raiz) throws IOException {
        Path base = raiz.resolve(Paths.get("tp2", "output"));
        Files.createDirectories(base);
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss-SSS"));
        Path pasta = base.resolve(timestamp);
        int sufixo = 1;
        while (Files.exists(pasta)) {
            pasta = base.resolve(timestamp + "-" + sufixo++);
        }
        Files.createDirectories(pasta);
        return pasta;
    }

    private void executar() throws IOException {
        Path pastaSamples = raizRepositorio.resolve(Paths.get("tp2", "samples"));
        for (int indice = 0; indice < limiteGrafos; indice++) {
            Path arquivo = pastaSamples.resolve(ARQUIVOS[indice]);
            DadosGrafo dados = lerGrafo(arquivo);
            System.out.printf(
                    "Grafo %d/%d: %s (%d vertices, %d arcos no arquivo, %d arcos efetivos)%n",
                    indice + 1,
                    limiteGrafos,
                    arquivo.getFileName(),
                    dados.numVertices,
                    dados.numArestasArquivo,
                    dados.arestas.size()
            );
            for (Representacao representacao : Representacao.values()) {
                executarConfiguracao(arquivo, dados, representacao);
            }
        }

        int linhasEsperadas = limiteGrafos * 2 * 2 * repeticoes;
        if (linhasRegistradas != linhasEsperadas) {
            throw new IllegalStateException(
                    "Esperadas " + linhasEsperadas + " medicoes, mas foram registradas " + linhasRegistradas + "."
            );
        }
        System.out.println("Medicoes registradas: " + linhasRegistradas);
        System.out.println("Bellman-Ford: " + (linhasRegistradas / 2) + " | Dijkstra: " + (linhasRegistradas / 2));
        System.out.println("CSV: " + pastaSaida.resolve("experimento_caminhos_minimos.csv"));
    }

    private void executarConfiguracao(Path arquivo, DadosGrafo dados, Representacao representacao) throws IOException {
        IGrafo grafo = criarGrafo(dados, representacao);
        BellmanFord bellmanFord = new BellmanFord();
        Dijkstra dijkstra = new Dijkstra();
        int origem = 0;

        double[] distanciasEsperadas = dijkstra.executar(grafo, origem).getDistancia();
        ResultadoBellmanFord aquecimentoBellmanFord = bellmanFord.executar(grafo, origem);
        validarResultadoBellmanFord(aquecimentoBellmanFord, distanciasEsperadas, arquivo, representacao);

        for (int repeticao = 1; repeticao <= repeticoes; repeticao++) {
            if (repeticao % 2 == 1) {
                registrarBellmanFord(arquivo, dados, grafo, representacao, repeticao, origem,
                        bellmanFord, distanciasEsperadas);
                registrarDijkstra(arquivo, dados, grafo, representacao, repeticao, origem,
                        dijkstra, distanciasEsperadas);
            } else {
                registrarDijkstra(arquivo, dados, grafo, representacao, repeticao, origem,
                        dijkstra, distanciasEsperadas);
                registrarBellmanFord(arquivo, dados, grafo, representacao, repeticao, origem,
                        bellmanFord, distanciasEsperadas);
            }
        }
    }

    private void registrarBellmanFord(
            Path arquivo,
            DadosGrafo dados,
            IGrafo grafo,
            Representacao representacao,
            int repeticao,
            int origem,
            BellmanFord algoritmo,
            double[] distanciasEsperadas
    ) throws IOException {
        long inicio = System.nanoTime();
        ResultadoBellmanFord resultado = algoritmo.executar(grafo, origem);
        long fim = System.nanoTime();
        validarResultadoBellmanFord(resultado, distanciasEsperadas, arquivo, representacao);
        registrarMedicao(arquivo, dados, grafo, representacao, "BELLMAN_FORD", repeticao, origem,
                (fim - inicio) / 1_000_000.0);
    }

    private void registrarDijkstra(
            Path arquivo,
            DadosGrafo dados,
            IGrafo grafo,
            Representacao representacao,
            int repeticao,
            int origem,
            Dijkstra algoritmo,
            double[] distanciasEsperadas
    ) throws IOException {
        long inicio = System.nanoTime();
        ResultadoDijkstra resultado = algoritmo.executar(grafo, origem);
        long fim = System.nanoTime();
        validarDistancias(distanciasEsperadas, resultado.getDistancia(), arquivo, representacao, "Dijkstra");
        registrarMedicao(arquivo, dados, grafo, representacao, "DIJKSTRA", repeticao, origem,
                (fim - inicio) / 1_000_000.0);
    }

    private void registrarMedicao(
            Path arquivo,
            DadosGrafo dados,
            IGrafo grafo,
            Representacao representacao,
            String algoritmo,
            int repeticao,
            int origem,
            double tempoMs
    ) throws IOException {
        double denominador = (double) dados.numVertices * (dados.numVertices - 1L);
        double densidadeArquivo = dados.numArestasArquivo / denominador;
        double densidadeGrafo = dados.numArestasSemLacos / denominador;
        exportador.registrarExperimento(
                arquivo.getFileName().toString(),
                algoritmo,
                representacao.name(),
                TipoGrafo.DIRECIONADO.name(),
                dados.numVertices,
                dados.numArestasArquivo,
                grafo.numArestas(),
                densidadeArquivo,
                densidadeGrafo,
                repeticao,
                origem,
                tempoMs
        );
        linhasRegistradas++;
    }

    private static void validarResultadoBellmanFord(
            ResultadoBellmanFord resultado,
            double[] distanciasEsperadas,
            Path arquivo,
            Representacao representacao
    ) {
        if (resultado.isTemCicloNegativo()) {
            throw new IllegalStateException("Ciclo negativo detectado em " + arquivo + " (" + representacao + ").");
        }
        validarDistancias(distanciasEsperadas, resultado.getDistancia(), arquivo, representacao, "Bellman-Ford");
    }

    private static void validarDistancias(
            double[] esperadas,
            double[] recebidas,
            Path arquivo,
            Representacao representacao,
            String algoritmo
    ) {
        if (esperadas.length != recebidas.length) {
            throw new IllegalStateException("Quantidade de distancias divergente em " + arquivo + ".");
        }
        for (int vertice = 0; vertice < esperadas.length; vertice++) {
            double esperada = esperadas[vertice];
            double recebida = recebidas[vertice];
            if (Double.isInfinite(esperada) && Double.isInfinite(recebida)) {
                continue;
            }
            if (!Double.isFinite(esperada) || !Double.isFinite(recebida)
                    || Math.abs(esperada - recebida) > 1e-9) {
                throw new IllegalStateException(
                        algoritmo + " divergiu em " + arquivo + " (" + representacao + "), vertice " + vertice
                                + ": esperado " + esperada + ", recebido " + recebida + "."
                );
            }
        }
    }

    private static IGrafo criarGrafo(DadosGrafo dados, Representacao representacao) {
        IGrafo grafo = representacao == Representacao.LISTA_ADJACENCIA
                ? new GrafoListaAdjacencia(dados.numVertices, TipoGrafo.DIRECIONADO)
                : new GrafoMatrizAdjacencia(dados.numVertices, TipoGrafo.DIRECIONADO);
        for (ArestaDados aresta : dados.arestas) {
            grafo.adicionarAresta(aresta.origem, aresta.destino, aresta.peso);
        }
        return grafo;
    }

    private static DadosGrafo lerGrafo(Path arquivo) throws IOException {
        if (!Files.isRegularFile(arquivo)) {
            throw new IOException("Amostra nao encontrada: " + arquivo);
        }

        int numVertices = -1;
        int numArestasArquivo = -1;
        int linhasArestas = 0;
        Map<Long, Double> menorPesoPorPar = new TreeMap<>();
        try (BufferedReader leitor = Files.newBufferedReader(arquivo, StandardCharsets.UTF_8)) {
            String linha;
            while ((linha = leitor.readLine()) != null) {
                linha = linha.trim();
                if (linha.isEmpty() || linha.startsWith("c")) {
                    continue;
                }
                String[] partes = linha.split("\\s+");
                if ("p".equals(partes[0])) {
                    if (partes.length < 4 || numVertices >= 0) {
                        throw new IOException("Cabecalho 'p' invalido em " + arquivo + ".");
                    }
                    numVertices = Integer.parseInt(partes[2]);
                    numArestasArquivo = Integer.parseInt(partes[3]);
                } else if ("a".equals(partes[0])) {
                    if (numVertices < 1 || partes.length < 4) {
                        throw new IOException("Aresta invalida antes do cabecalho em " + arquivo + ".");
                    }
                    int origem = Integer.parseInt(partes[1]) - 1;
                    int destino = Integer.parseInt(partes[2]) - 1;
                    double peso = Double.parseDouble(partes[3]);
                    if (origem < 0 || origem >= numVertices || destino < 0 || destino >= numVertices) {
                        throw new IOException("Vertice fora dos limites em " + arquivo + ".");
                    }
                    if (!Double.isFinite(peso) || peso < 0.0) {
                        throw new IOException("Dijkstra exige pesos finitos e nao negativos em " + arquivo + ".");
                    }
                    long chave = (long) origem * numVertices + destino;
                    menorPesoPorPar.merge(chave, peso, Math::min);
                    linhasArestas++;
                }
            }
        }

        if (numVertices < 1 || numArestasArquivo < 0) {
            throw new IOException("Cabecalho 'p' ausente ou invalido em " + arquivo + ".");
        }
        if (linhasArestas != numArestasArquivo) {
            throw new IOException("Cabecalho declara " + numArestasArquivo + " arestas, mas foram lidas "
                    + linhasArestas + " em " + arquivo + ".");
        }

        List<ArestaDados> arestas = new ArrayList<>(menorPesoPorPar.size());
        int arestasSemLacos = 0;
        for (Map.Entry<Long, Double> entrada : menorPesoPorPar.entrySet()) {
            int origem = (int) (entrada.getKey() / numVertices);
            int destino = (int) (entrada.getKey() % numVertices);
            arestas.add(new ArestaDados(origem, destino, entrada.getValue()));
            if (origem != destino) {
                arestasSemLacos++;
            }
        }
        return new DadosGrafo(numVertices, numArestasArquivo, arestasSemLacos, arestas);
    }

    private static final class DadosGrafo {
        private final int numVertices;
        private final int numArestasArquivo;
        private final int numArestasSemLacos;
        private final List<ArestaDados> arestas;

        private DadosGrafo(int numVertices, int numArestasArquivo, int numArestasSemLacos, List<ArestaDados> arestas) {
            this.numVertices = numVertices;
            this.numArestasArquivo = numArestasArquivo;
            this.numArestasSemLacos = numArestasSemLacos;
            this.arestas = arestas;
        }
    }

    private static final class ArestaDados {
        private final int origem;
        private final int destino;
        private final double peso;

        private ArestaDados(int origem, int destino, double peso) {
            this.origem = origem;
            this.destino = destino;
            this.peso = peso;
        }
    }
}