package test;

import algoritmos.BellmanFord;
import algoritmos.BellmanFord.ResultadoBellmanFord;
import grafo.GrafoListaAdjacencia;
import grafo.GrafoMatrizAdjacencia;
import grafo.IGrafo;
import grafo.TipoGrafo;
import java.util.Arrays;

public final class BellmanFordTest {

    public static void main(String[] args) {
        for (boolean usarMatriz : new boolean[] {false, true}) {
            testaCaminhosMinimos(usarMatriz, TipoGrafo.DIRECIONADO);
            testaCaminhosMinimos(usarMatriz, TipoGrafo.NAO_DIRECIONADO);
            testaCicloNegativoAlcancavel(usarMatriz);
            testaCicloNegativoInalcancavel(usarMatriz);
        }
        testaValidacoes();
        System.out.println("BellmanFordTest: todos os testes passaram.");
    }

    private static void testaCaminhosMinimos(boolean usarMatriz, TipoGrafo tipo) {
        IGrafo grafo = novoGrafo(usarMatriz, 5, tipo);
        if (tipo == TipoGrafo.DIRECIONADO) {
            grafo.adicionarAresta(0, 1, 4.0);
            grafo.adicionarAresta(0, 2, 5.0);
            grafo.adicionarAresta(1, 2, -2.0);
            grafo.adicionarAresta(1, 3, 4.0);
            grafo.adicionarAresta(2, 3, 3.0);
        } else {
            grafo.adicionarAresta(0, 1, 4.0);
            grafo.adicionarAresta(0, 2, 10.0);
            grafo.adicionarAresta(1, 2, 2.0);
            grafo.adicionarAresta(2, 3, 3.0);
        }

        ResultadoBellmanFord resultado = new BellmanFord().executar(grafo, 0);

        verifica(resultado.getOrigem() == 0, "a origem deve ser preservada");
        verifica(!resultado.isTemCicloNegativo(), "este grafo não tem ciclo negativo alcançável");
        if (tipo == TipoGrafo.DIRECIONADO) {
            verificaDistancias(resultado.getDistancia(), new double[] {0.0, 4.0, 2.0, 5.0, Double.POSITIVE_INFINITY});
        } else {
            verificaDistancias(resultado.getDistancia(), new double[] {0.0, 4.0, 6.0, 9.0, Double.POSITIVE_INFINITY});
        }
        verifica(Arrays.equals(resultado.getPai(), new int[] {-1, 0, 1, 2, -1}),
                "os predecessores devem formar o caminho mínimo esperado");
        verifica(resultado.reconstruirCaminho(3).equals(Arrays.asList(0, 1, 2, 3)),
                "o caminho mínimo até o destino deve ser reconstruído");
        verifica(resultado.reconstruirCaminho(4).isEmpty(),
                "vértice desconectado não deve ter caminho");
        verifica(resultado.alcancavel(3) && !resultado.alcancavel(4),
                "alcancavel deve distinguir vértices conectados e desconectados");
    }

    private static void testaCicloNegativoAlcancavel(boolean usarMatriz) {
        IGrafo grafo = novoGrafo(usarMatriz, 4, TipoGrafo.DIRECIONADO);
        grafo.adicionarAresta(0, 1, 1.0);
        grafo.adicionarAresta(1, 2, -2.0);
        grafo.adicionarAresta(2, 1, -2.0);
        grafo.adicionarAresta(2, 3, 1.0);

        ResultadoBellmanFord resultado = new BellmanFord().executar(grafo, 0);
        verifica(resultado.isTemCicloNegativo(), "ciclo negativo alcançável deve ser detectado");
        verifica(resultado.reconstruirCaminho(3).isEmpty(),
                "caminhos não devem ser retornados quando há ciclo negativo alcançável");
    }

    private static void testaCicloNegativoInalcancavel(boolean usarMatriz) {
        IGrafo grafo = novoGrafo(usarMatriz, 4, TipoGrafo.DIRECIONADO);
        grafo.adicionarAresta(0, 1, 2.0);
        grafo.adicionarAresta(2, 3, -1.0);
        grafo.adicionarAresta(3, 2, -1.0);

        ResultadoBellmanFord resultado = new BellmanFord().executar(grafo, 0);
        verifica(!resultado.isTemCicloNegativo(),
                "ciclo negativo fora do alcance da origem não deve invalidar o resultado");
        verificaDistancias(resultado.getDistancia(), new double[] {0.0, 2.0, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY});
    }

    private static void testaValidacoes() {
        BellmanFord algoritmo = new BellmanFord();
        esperaExcecao(IllegalArgumentException.class, () -> algoritmo.executar(null, 0));
        esperaExcecao(IndexOutOfBoundsException.class,
                () -> algoritmo.executar(new GrafoListaAdjacencia(1, TipoGrafo.DIRECIONADO), 1));

        IGrafo pesoInvalido = new GrafoListaAdjacencia(2, TipoGrafo.DIRECIONADO);
        pesoInvalido.adicionarAresta(0, 1, Double.NaN);
        esperaExcecao(IllegalArgumentException.class, () -> algoritmo.executar(pesoInvalido, 0));
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
