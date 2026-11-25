package test;

import algoritmos.Dkjistra;
import algoritmos.Dkjistra.ResultadoDkjistra;
import grafo.GrafoListaAdjacencia;
import grafo.GrafoMatrizAdjacencia;
import grafo.IGrafo;
import grafo.TipoGrafo;
import java.util.Arrays;

public final class DkjistraTest {

    public static void main(String[] args) {
        for (boolean usarMatriz : new boolean[] {false, true}) {
            testaCaminhosMinimos(usarMatriz, TipoGrafo.DIRECIONADO);
            testaCaminhosMinimos(usarMatriz, TipoGrafo.NAO_DIRECIONADO);
            testaPesoNegativo(usarMatriz);
        }
        testaValidacoes();
        System.out.println("DkjistraTest: todos os testes passaram.");
    }

    private static void testaCaminhosMinimos(boolean usarMatriz, TipoGrafo tipo) {
        IGrafo grafo = novoGrafo(usarMatriz, 5, tipo);
        grafo.adicionarAresta(0, 1, 4.0);
        grafo.adicionarAresta(0, 2, 1.0);
        grafo.adicionarAresta(2, 1, 2.0);
        grafo.adicionarAresta(1, 3, 1.0);
        grafo.adicionarAresta(2, 3, 5.0);

        ResultadoDkjistra resultado = new Dkjistra().executar(grafo, 0);

        verifica(resultado.getOrigem() == 0, "a origem deve ser preservada");
        verificaDistancias(resultado.getDistancia(), new double[] {0.0, 3.0, 1.0, 4.0, Double.POSITIVE_INFINITY});
        verifica(Arrays.equals(resultado.getPai(), new int[] {-1, 2, 0, 1, -1}),
                "os predecessores devem formar os caminhos mínimos esperados");
        verifica(resultado.reconstruirCaminho(3).equals(Arrays.asList(0, 2, 1, 3)),
                "o caminho mínimo até o destino deve ser reconstruído");
        verifica(resultado.reconstruirCaminho(4).isEmpty(),
                "vértice desconectado não deve ter caminho");
        verifica(resultado.alcancavel(3) && !resultado.alcancavel(4),
                "alcancavel deve distinguir vértices conectados e desconectados");
    }

    private static void testaPesoNegativo(boolean usarMatriz) {
        IGrafo grafo = novoGrafo(usarMatriz, 2, TipoGrafo.DIRECIONADO);
        grafo.adicionarAresta(0, 1, -1.0);
        esperaExcecao(IllegalArgumentException.class, () -> new Dkjistra().executar(grafo, 0));
    }

    private static void testaValidacoes() {
        Dkjistra algoritmo = new Dkjistra();
        esperaExcecao(IllegalArgumentException.class, () -> algoritmo.executar(null, 0));
        esperaExcecao(IndexOutOfBoundsException.class,
                () -> algoritmo.executar(new GrafoListaAdjacencia(1, TipoGrafo.DIRECIONADO), 1));

        IGrafo pesoInfinito = new GrafoListaAdjacencia(2, TipoGrafo.DIRECIONADO);
        pesoInfinito.adicionarAresta(0, 1, Double.POSITIVE_INFINITY);
        esperaExcecao(IllegalArgumentException.class, () -> algoritmo.executar(pesoInfinito, 0));
    }

    private static IGrafo novoGrafo(boolean usarMatriz, int vertices, TipoGrafo tipo) {
        return usarMatriz
                ? new GrafoMatrizAdjacencia(vertices, tipo)
                : new GrafoListaAdjacencia(vertices, tipo);
    }

    private static void verificaDistancias(double[] recebidas, double[] esperadas) {
        verifica(recebidas.length == esperadas.length, "quantidade de distâncias divergente");
        for (int i = 0; i < esperadas.length; i++) {
            verifica(Double.compare(recebidas[i], esperadas[i]) == 0,
                    "distância divergente no vértice " + i);
        }
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
