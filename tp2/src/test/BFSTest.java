package test;

import algoritmos.BFS;
import algoritmos.BFS.ResultadoBFS;
import grafo.GrafoListaAdjacencia;
import grafo.GrafoMatrizAdjacencia;
import grafo.IGrafo;
import grafo.TipoGrafo;
import java.util.Arrays;
import java.util.List;

public final class BFSTest {

    public static void main(String[] args) {
        for (boolean usarMatriz : new boolean[] {false, true}) {
            for (TipoGrafo tipo : TipoGrafo.values()) {
                testaDistanciasCaminhosEDesconexao(usarMatriz, tipo);
                testaOrigemUnica(usarMatriz, tipo);
            }
        }
        testaValidacoes();
        System.out.println("BFSTest: todos os testes passaram.");
    }

    private static void testaDistanciasCaminhosEDesconexao(boolean usarMatriz, TipoGrafo tipo) {
        IGrafo grafo = novoGrafo(usarMatriz, 6, tipo);
        grafo.adicionarAresta(0, 1, 100.0);
        grafo.adicionarAresta(0, 2, 0.0);
        grafo.adicionarAresta(1, 3, 1.0);
        grafo.adicionarAresta(2, 3, 1.0);
        grafo.adicionarAresta(3, 4, 1.0);

        ResultadoBFS resultado = new BFS().executar(grafo, 0);

        verifica(resultado.getOrigem() == 0, "BFS deve preservar a origem");
        verifica(Arrays.equals(resultado.getDistancia(), new int[] {0, 1, 1, 2, 3, -1}),
                "BFS deve calcular distâncias por quantidade de arestas, inclusive com pesos");
        verifica(Arrays.equals(resultado.getVisitado(), new boolean[] {true, true, true, true, true, false}),
                "BFS deve marcar apenas o componente alcançável");
        verifica(resultado.getPai()[0] == -1 && resultado.getPai()[5] == -1,
                "origem e vértice desconectado devem não ter pai");
        verifica(resultado.getOrdemVisitacao().size() == 5,
                "ordem de visitação deve conter cada vértice alcançável uma vez");
        verifica(resultado.getOrdemVisitacao().get(0) == 0,
                "BFS deve visitar a origem primeiro");

        List<Integer> caminho = resultado.reconstruirCaminho(4);
        verifica(caminho.size() == 4 && caminho.get(0) == 0 && caminho.get(3) == 4,
                "caminho reconstruído deve ter distância mínima e extremos corretos");
        for (int i = 0; i + 1 < caminho.size(); i++) {
            verifica(grafo.obterAresta(caminho.get(i), caminho.get(i + 1)) != null,
                    "cada passo do caminho reconstruído deve ser uma aresta válida");
        }
        verifica(resultado.reconstruirCaminho(5).isEmpty(),
                "vértice desconectado não deve ter caminho reconstruído");
        verifica(resultado.reconstruirCaminho(-1).isEmpty(),
                "destino fora dos limites deve retornar caminho vazio");
    }

    private static void testaOrigemUnica(boolean usarMatriz, TipoGrafo tipo) {
        IGrafo grafo = novoGrafo(usarMatriz, 1, tipo);
        ResultadoBFS resultado = new BFS().executar(grafo, 0);

        verifica(Arrays.equals(resultado.getDistancia(), new int[] {0}),
                "BFS de um vértice deve atribuir distância zero à origem");
        verifica(Arrays.equals(resultado.getPai(), new int[] {-1}),
                "origem única não deve ter pai");
        verifica(resultado.reconstruirCaminho(0).equals(Arrays.asList(0)),
                "caminho da origem para si mesma deve conter apenas a origem");
    }

    private static void testaValidacoes() {
        BFS bfs = new BFS();
        esperaExcecao(IllegalArgumentException.class, () -> bfs.executar(null, 0));

        IGrafo vazio = new GrafoListaAdjacencia(0, TipoGrafo.DIRECIONADO);
        esperaExcecao(IndexOutOfBoundsException.class, () -> bfs.executar(vazio, 0));

        IGrafo grafo = new GrafoListaAdjacencia(2, TipoGrafo.DIRECIONADO);
        esperaExcecao(IndexOutOfBoundsException.class, () -> bfs.executar(grafo, -1));
        esperaExcecao(IndexOutOfBoundsException.class, () -> bfs.executar(grafo, 2));
    }

    private static IGrafo novoGrafo(boolean usarMatriz, int numVertices, TipoGrafo tipo) {
        return usarMatriz
                ? new GrafoMatrizAdjacencia(numVertices, tipo)
                : new GrafoListaAdjacencia(numVertices, tipo);
    }

    private static void verifica(boolean condicao, String mensagem) {
        if (!condicao) {
            throw new AssertionError(mensagem);
        }
    }

    private static void esperaExcecao(Class<? extends Throwable> tipo, Runnable acao) {
        try {
            acao.run();
        } catch (Throwable erro) {
            if (tipo.isInstance(erro)) {
                return;
            }
            throw new AssertionError("Esperada " + tipo.getSimpleName() + ", recebida " + erro, erro);
        }
        throw new AssertionError("Esperada exceção " + tipo.getSimpleName() + ", mas nenhuma foi lançada");
    }
}
