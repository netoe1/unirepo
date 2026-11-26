package test;

import algoritmos.BFS;
import algoritmos.BellmanFord;
import algoritmos.DFS;
import algoritmos.Dijkstra;
import algoritmos.FloydWarshall;
import algoritmos.FordFulkerson;
import algoritmos.Kruskal;
import algoritmos.OrdenacaoTopologica;
import algoritmos.Prim;
import grafo.GrafoListaAdjacencia;
import grafo.IGrafo;
import grafo.TipoGrafo;
import io.EscritaGrafo;
import io.LeituraGrafo;
import io.LeituraGrafo.Representacao;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.function.Predicate;

public final class ExemplosDemonstracaoTest {

    private static final String PASTA_EXEMPLOS = "tp2/samples/demo/";

    public static void main(String[] args) throws Exception {
        testaExemplosDosAlgoritmos();
        testaLimitesDeExibicao();
        System.out.println("ExemplosDemonstracaoTest: todos os testes passaram.");
    }

    private static void testaExemplosDosAlgoritmos() throws Exception {
        LeituraGrafo leitor = new LeituraGrafo();
        for (Representacao representacao : Representacao.values()) {
            testaBusca(leitor, representacao, "bfs.gr");
            testaBusca(leitor, representacao, "dfs.gr");
            executa(leitor, representacao, "ordenacao-topologica.gr", TipoGrafo.DIRECIONADO,
                    grafo -> !new OrdenacaoTopologica().executar(grafo).isTemCiclo());
            executa(leitor, representacao, "kruskal.gr", TipoGrafo.NAO_DIRECIONADO,
                    grafo -> {
                        var resultado = new Kruskal().executar(grafo);
                        return resultado.getNumeroComponentes() == 1 && resultado.getPesoTotal() == 10.0;
                    });
            executa(leitor, representacao, "prim.gr", TipoGrafo.NAO_DIRECIONADO,
                    grafo -> {
                        var resultado = new Prim().executar(grafo);
                        return resultado.getNumeroComponentes() == 1 && resultado.getPesoTotal() == 10.0;
                    });
            executa(leitor, representacao, "bellman-ford.gr", TipoGrafo.DIRECIONADO,
                    grafo -> !new BellmanFord().executar(grafo, 0).isTemCicloNegativo());
            executa(leitor, representacao, "dijkstra.gr", TipoGrafo.DIRECIONADO,
                    grafo -> new Dijkstra().executar(grafo, 0).getDistancia()[4] == 16.0);
            executa(leitor, representacao, "floyd-warshall.gr", TipoGrafo.DIRECIONADO,
                    grafo -> !new FloydWarshall().executar(grafo).isTemCicloNegativo());
            executa(leitor, representacao, "floyd-warshall.gr", TipoGrafo.NAO_DIRECIONADO,
                    grafo -> !new FloydWarshall().executar(grafo).isTemCicloNegativo());
            executa(leitor, representacao, "ford-fulkerson.gr", TipoGrafo.DIRECIONADO,
                    grafo -> new FordFulkerson().executar(grafo, 0, grafo.numVertices() - 1).getFluxoMaximo() == 23.0);
        }
    }

    private static void testaBusca(LeituraGrafo leitor, Representacao representacao, String arquivo)
            throws Exception {
        for (TipoGrafo tipo : TipoGrafo.values()) {
            IGrafo grafo = leitor.lerArquivo(PASTA_EXEMPLOS + arquivo, representacao, tipo);
            verifica(new BFS().executar(grafo, 0).getOrdemVisitacao().size() == grafo.numVertices(),
                    "o exemplo de BFS deve alcançar todos os vertices");
            verifica(new DFS().executarDeOrigem(grafo, 0).getOrdemPreOrdem().size() == grafo.numVertices(),
                    "o exemplo de DFS deve alcançar todos os vertices");
        }
    }

    private static void executa(
            LeituraGrafo leitor,
            Representacao representacao,
            String arquivo,
            TipoGrafo tipo,
            Predicate<IGrafo> condicaoAdequacao
    ) throws Exception {
        IGrafo grafo = leitor.lerArquivo(PASTA_EXEMPLOS + arquivo, representacao, tipo);
        verifica(condicaoAdequacao.test(grafo), "resultado inesperado para o exemplo " + arquivo);
    }

    private static void testaLimitesDeExibicao() {
        IGrafo grafo = new GrafoListaAdjacencia(25, TipoGrafo.DIRECIONADO);
        for (int destino = 1; destino < 25; destino++) {
            grafo.adicionarAresta(0, destino, destino);
        }

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        PrintStream saidaOriginal = System.out;
        try {
            System.setOut(new PrintStream(bytes, true, StandardCharsets.UTF_8));
            new EscritaGrafo().imprimirListaAdjacencia(grafo, 20, 10);
        } finally {
            System.setOut(saidaOriginal);
        }

        String saida = bytes.toString(StandardCharsets.UTF_8);
        verifica(saida.contains("(14 adjacencias omitidas)"),
                "arestas excedentes devem ser indicadas na exibicao");
        verifica(saida.contains("(5 vertices omitidos)"),
                "vertices excedentes devem ser indicados na exibicao");
        verifica(!saida.contains("  20 -> "),
                "vertices acima do limite nao devem ser impressos");
    }

    private static void verifica(boolean condicao, String mensagem) {
        if (!condicao) {
            throw new AssertionError(mensagem);
        }
    }
}
