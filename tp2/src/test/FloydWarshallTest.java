package test;

import algoritmos.FloydWarshall;
import algoritmos.FloydWarshall.ResultadoFloydWarshall;
import grafo.GrafoListaAdjacencia;
import grafo.GrafoMatrizAdjacencia;
import grafo.IGrafo;
import grafo.TipoGrafo;
import java.util.Arrays;

public final class FloydWarshallTest {

    public static void main(String[] args) {
        for (boolean usarMatriz : new boolean[] {false, true}) {
            testaCaminhosMinimosTodosOsPares(usarMatriz, TipoGrafo.DIRECIONADO);
            testaCaminhosMinimosTodosOsPares(usarMatriz, TipoGrafo.NAO_DIRECIONADO);
            testaCicloNegativo(usarMatriz);
            testaGrafoVazioEUnicoVertice(usarMatriz);
            testaPesosNaoFinitos(usarMatriz);
        }
        testaValidacoes();
        System.out.println("FloydWarshallTest: todos os testes passaram.");
    }

    private static void testaCaminhosMinimosTodosOsPares(boolean usarMatriz, TipoGrafo tipo) {
        IGrafo grafo = novoGrafo(usarMatriz, 5, tipo);
        if (tipo == TipoGrafo.DIRECIONADO) {
            grafo.adicionarAresta(0, 1, 3.0);
            grafo.adicionarAresta(0, 2, 8.0);
            grafo.adicionarAresta(1, 2, -2.0);
            grafo.adicionarAresta(1, 3, 4.0);
            grafo.adicionarAresta(2, 3, 1.0);
        } else {
            grafo.adicionarAresta(0, 1, 3.0);
            grafo.adicionarAresta(0, 2, 10.0);
            grafo.adicionarAresta(1, 2, 2.0);
            grafo.adicionarAresta(2, 3, 1.0);
        }

        ResultadoFloydWarshall resultado = new FloydWarshall().executar(grafo);
        double[][] dist = resultado.getDistancias();

        verifica(!resultado.isTemCicloNegativo(), "este grafo não possui ciclo negativo");
        verifica(dist.length == 5 && dist[0].length == 5, "matriz de distâncias deve ter dimensão 5x5");

        if (tipo == TipoGrafo.DIRECIONADO) {
            verificaDistancias(dist[0], new double[] {0.0, 3.0, 1.0, 2.0, Double.POSITIVE_INFINITY});
            verificaDistancias(dist[1], new double[] {Double.POSITIVE_INFINITY, 0.0, -2.0, -1.0, Double.POSITIVE_INFINITY});
            verificaDistancias(dist[2], new double[] {Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, 0.0, 1.0, Double.POSITIVE_INFINITY});
            verificaDistancias(dist[3], new double[] {Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, 0.0, Double.POSITIVE_INFINITY});
            verificaDistancias(dist[4], new double[] {Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, 0.0});

            verifica(resultado.reconstruirCaminho(0, 3).equals(Arrays.asList(0, 1, 2, 3)),
                    "caminho mínimo de 0 para 3 deve passar por 1 e 2");
            verifica(resultado.reconstruirCaminho(3, 0).isEmpty(),
                    "em grafo direcionado não deve existir caminho reverso de 3 para 0");
        } else {
            verificaDistancias(dist[0], new double[] {0.0, 3.0, 5.0, 6.0, Double.POSITIVE_INFINITY});
            verificaDistancias(dist[3], new double[] {6.0, 3.0, 1.0, 0.0, Double.POSITIVE_INFINITY});
            verificaDistancias(dist[4], new double[] {Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, 0.0});

            verifica(resultado.reconstruirCaminho(0, 3).equals(Arrays.asList(0, 1, 2, 3)),
                    "caminho mínimo de 0 para 3 deve ser 0 -> 1 -> 2 -> 3");
            verifica(resultado.reconstruirCaminho(3, 0).equals(Arrays.asList(3, 2, 1, 0)),
                    "caminho mínimo reverso em grafo não direcionado deve existir");
        }

        verifica(resultado.reconstruirCaminho(0, 0).equals(Arrays.asList(0)),
                "caminho de um vértice para si mesmo deve conter apenas o próprio vértice");
        verifica(resultado.reconstruirCaminho(0, 4).isEmpty(),
                "vértice desconectado não deve ter caminho");
    }

    private static void testaCicloNegativo(boolean usarMatriz) {
        IGrafo grafo = novoGrafo(usarMatriz, 3, TipoGrafo.DIRECIONADO);
        grafo.adicionarAresta(0, 1, 1.0);
        grafo.adicionarAresta(1, 2, -2.0);
        grafo.adicionarAresta(2, 0, -1.0);

        ResultadoFloydWarshall resultado = new FloydWarshall().executar(grafo);
        verifica(resultado.isTemCicloNegativo(), "ciclo negativo deve ser detectado na diagonal principal");
        verifica(resultado.reconstruirCaminho(0, 2).isEmpty(),
                "reconstrução de caminho deve retornar lista vazia quando há ciclo negativo");
    }

    private static void testaGrafoVazioEUnicoVertice(boolean usarMatriz) {
        IGrafo umVertice = novoGrafo(usarMatriz, 1, TipoGrafo.DIRECIONADO);
        ResultadoFloydWarshall resUm = new FloydWarshall().executar(umVertice);
        verifica(!resUm.isTemCicloNegativo(), "grafo de 1 vértice sem laço negativo não tem ciclo negativo");
        verificaDistancias(resUm.getDistancias()[0], new double[] {0.0});
        verifica(resUm.reconstruirCaminho(0, 0).equals(Arrays.asList(0)),
                "caminho de 0 para 0 deve ser [0]");

        IGrafo vazio = novoGrafo(usarMatriz, 0, TipoGrafo.DIRECIONADO);
        ResultadoFloydWarshall resVazio = new FloydWarshall().executar(vazio);
        verifica(!resVazio.isTemCicloNegativo(), "grafo vazio não tem ciclo negativo");
        verifica(resVazio.getDistancias().length == 0, "matriz de grafo vazio deve ter tamanho 0");
    }

    private static void testaPesosNaoFinitos(boolean usarMatriz) {
        FloydWarshall algoritmo = new FloydWarshall();

        IGrafo grafoNaN = novoGrafo(usarMatriz, 2, TipoGrafo.DIRECIONADO);
        grafoNaN.adicionarAresta(0, 1, Double.NaN);
        esperaExcecao(IllegalArgumentException.class, () -> algoritmo.executar(grafoNaN));

        IGrafo grafoInf = novoGrafo(usarMatriz, 2, TipoGrafo.DIRECIONADO);
        grafoInf.adicionarAresta(0, 1, Double.POSITIVE_INFINITY);
        esperaExcecao(IllegalArgumentException.class, () -> algoritmo.executar(grafoInf));
    }

    private static void testaValidacoes() {
        FloydWarshall algoritmo = new FloydWarshall();
        esperaExcecao(IllegalArgumentException.class, () -> algoritmo.executar(null));

        IGrafo grafo = new GrafoListaAdjacencia(2, TipoGrafo.DIRECIONADO);
        ResultadoFloydWarshall res = algoritmo.executar(grafo);
        esperaExcecao(IndexOutOfBoundsException.class, () -> res.reconstruirCaminho(-1, 0));
        esperaExcecao(IndexOutOfBoundsException.class, () -> res.reconstruirCaminho(0, 2));
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
                    "distância divergente na coluna " + i);
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