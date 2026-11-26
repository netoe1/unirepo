package test;

import algoritmos.FordFulkerson;
import algoritmos.FordFulkerson.ResultadoFordFulkerson;
import grafo.GrafoListaAdjacencia;
import grafo.GrafoMatrizAdjacencia;
import grafo.IGrafo;
import grafo.TipoGrafo;

public final class FordFulkersonTest {

    private static final double EPS = 1e-9;

    public static void main(String[] args) {
        for (boolean usarMatriz : new boolean[] {false, true}) {
            testaRedeClassicaCormen(usarMatriz);
            testaRedirecionamentoPorArestaReversa(usarMatriz);
            testaCapacidadesPequenasMenorQue1eMinus15(usarMatriz);
            testaSemCaminhoAteSumidouro(usarMatriz);
            testaCapacidadesNaoFinitasENegativas(usarMatriz);
        }
        testaValidacoes();
        System.out.println("FordFulkersonTest: todos os testes passaram.");
    }

    private static void testaRedeClassicaCormen(boolean usarMatriz) {
        IGrafo grafo = novoGrafo(usarMatriz, 6, TipoGrafo.DIRECIONADO);
        grafo.adicionarAresta(0, 1, 16.0);
        grafo.adicionarAresta(0, 2, 13.0);
        grafo.adicionarAresta(1, 2, 10.0);
        grafo.adicionarAresta(2, 1, 4.0);
        grafo.adicionarAresta(1, 3, 12.0);
        grafo.adicionarAresta(3, 2, 9.0);
        grafo.adicionarAresta(2, 4, 14.0);
        grafo.adicionarAresta(4, 3, 7.0);
        grafo.adicionarAresta(3, 5, 20.0);
        grafo.adicionarAresta(4, 5, 4.0);

        ResultadoFordFulkerson resultado = new FordFulkerson().executar(grafo, 0, 5);

        verifica(resultado.getOrigem() == 0 && resultado.getSumidouro() == 5,
                "origem e sumidouro devem ser preservados");
        verificaProximo(resultado.getFluxoMaximo(), 23.0, EPS,
                "o fluxo máximo da rede clássica deve ser 23.0");
        verificaConservacaoDeFluxo(grafo, resultado.getFluxoPorAresta(), 0, 5, EPS);
    }

    private static void testaRedirecionamentoPorArestaReversa(boolean usarMatriz) {
        // Vertices: 0 = s, 1 = L1, 2 = L2, 3 = R1, 4 = R2, 5 = t
        // Arestas com capacidade 1.0: s->L1, s->L2, L1->R1, L1->R2, L2->R1, R1->t, R2->t
        // A 1a busca usa L1->R1; para atingir fluxo 2.0, a 2a busca cancela esse fluxo
        // pela aresta residual reversa R1->L1 e redireciona para L1->R2.
        IGrafo grafo = novoGrafo(usarMatriz, 6, TipoGrafo.DIRECIONADO);
        grafo.adicionarAresta(0, 1, 1.0);
        grafo.adicionarAresta(0, 2, 1.0);
        grafo.adicionarAresta(1, 3, 1.0);
        grafo.adicionarAresta(1, 4, 1.0);
        grafo.adicionarAresta(2, 3, 1.0);
        grafo.adicionarAresta(3, 5, 1.0);
        grafo.adicionarAresta(4, 5, 1.0);

        ResultadoFordFulkerson resultado = new FordFulkerson().executar(grafo, 0, 5);
        double[][] fluxo = resultado.getFluxoPorAresta();

        verificaProximo(resultado.getFluxoMaximo(), 2.0, EPS,
                "fluxo máximo deve atingir 2.0 após cancelar L1 -> R1 e redirecionar para L1 -> R2");
        verificaProximo(fluxo[0][1], 1.0, EPS, "fluxo em s -> L1 deve ser 1.0");
        verificaProximo(fluxo[0][2], 1.0, EPS, "fluxo em s -> L2 deve ser 1.0");
        verificaProximo(fluxo[1][3], 0.0, EPS, "fluxo em L1 -> R1 deve ser cancelado (0.0)");
        verificaProximo(fluxo[1][4], 1.0, EPS, "fluxo em L1 -> R2 deve ser redirecionado para 1.0");
        verificaProximo(fluxo[2][3], 1.0, EPS, "fluxo em L2 -> R1 deve ser 1.0");
        verificaProximo(fluxo[3][5], 1.0, EPS, "fluxo em R1 -> t deve ser 1.0");
        verificaProximo(fluxo[4][5], 1.0, EPS, "fluxo em R2 -> t deve ser 1.0");
        verificaConservacaoDeFluxo(grafo, fluxo, 0, 5, EPS);
    }

    private static void testaCapacidadesPequenasMenorQue1eMinus15(boolean usarMatriz) {
        double capPequena = 1e-16;
        double tol = 1e-22;

        IGrafo grafo = novoGrafo(usarMatriz, 3, TipoGrafo.DIRECIONADO);
        grafo.adicionarAresta(0, 1, capPequena);
        grafo.adicionarAresta(1, 2, capPequena);

        ResultadoFordFulkerson resultado = new FordFulkerson().executar(grafo, 0, 2);
        verificaProximo(resultado.getFluxoMaximo(), capPequena, tol,
                "capacidades positivas menores que 1e-15 (como 1e-16) não devem ser descartadas");
        verificaConservacaoDeFluxo(grafo, resultado.getFluxoPorAresta(), 0, 2, tol);
    }

    private static void testaSemCaminhoAteSumidouro(boolean usarMatriz) {
        IGrafo grafo = novoGrafo(usarMatriz, 4, TipoGrafo.DIRECIONADO);
        grafo.adicionarAresta(0, 1, 5.0);
        grafo.adicionarAresta(2, 3, 5.0);

        ResultadoFordFulkerson resultado = new FordFulkerson().executar(grafo, 0, 3);
        verificaProximo(resultado.getFluxoMaximo(), 0.0, EPS,
                "fluxo máximo deve ser 0.0 quando o sumidouro é inalcançável");
    }

    private static void testaCapacidadesNaoFinitasENegativas(boolean usarMatriz) {
        FordFulkerson ff = new FordFulkerson();

        IGrafo capNaN = novoGrafo(usarMatriz, 2, TipoGrafo.DIRECIONADO);
        capNaN.adicionarAresta(0, 1, Double.NaN);
        esperaExcecao(IllegalArgumentException.class, () -> ff.executar(capNaN, 0, 1));

        IGrafo capInf = novoGrafo(usarMatriz, 2, TipoGrafo.DIRECIONADO);
        capInf.adicionarAresta(0, 1, Double.POSITIVE_INFINITY);
        esperaExcecao(IllegalArgumentException.class, () -> ff.executar(capInf, 0, 1));

        IGrafo capNeg = novoGrafo(usarMatriz, 2, TipoGrafo.DIRECIONADO);
        capNeg.adicionarAresta(0, 1, -3.0);
        esperaExcecao(IllegalArgumentException.class, () -> ff.executar(capNeg, 0, 1));
    }

    private static void testaValidacoes() {
        FordFulkerson ff = new FordFulkerson();
        esperaExcecao(IllegalArgumentException.class, () -> ff.executar(null, 0, 1));

        IGrafo naoDirecionado = new GrafoListaAdjacencia(3, TipoGrafo.NAO_DIRECIONADO);
        naoDirecionado.adicionarAresta(0, 1, 5.0);
        naoDirecionado.adicionarAresta(1, 2, 5.0);
        esperaExcecao(IllegalArgumentException.class, () -> ff.executar(naoDirecionado, 0, 2));

        IGrafo direcionado = new GrafoListaAdjacencia(3, TipoGrafo.DIRECIONADO);
        esperaExcecao(IllegalArgumentException.class, () -> ff.executar(direcionado, 1, 1));
        esperaExcecao(IndexOutOfBoundsException.class, () -> ff.executar(direcionado, -1, 2));
        esperaExcecao(IndexOutOfBoundsException.class, () -> ff.executar(direcionado, 0, 3));
    }

    private static void verificaConservacaoDeFluxo(IGrafo grafo, double[][] fluxo, int origem, int sumidouro, double tol) {
        int n = grafo.numVertices();
        for (int u = 0; u < n; u++) {
            for (int v = 0; v < n; v++) {
                double cap = grafo.obterAresta(u, v) != null ? grafo.obterAresta(u, v).getPeso() : 0.0;
                verifica(fluxo[u][v] >= -tol && fluxo[u][v] <= cap + tol,
                        "fluxo fora dos limites [0, capacidade] na aresta (" + u + " -> " + v + ")");
            }
            if (u != origem && u != sumidouro) {
                double entrada = 0.0;
                double saida = 0.0;
                for (int v = 0; v < n; v++) {
                    entrada += fluxo[v][u];
                    saida += fluxo[u][v];
                }
                verificaProximo(entrada, saida, tol,
                        "conservação de fluxo violada no vértice intermediário " + u);
            }
        }
    }

    private static IGrafo novoGrafo(boolean usarMatriz, int vertices, TipoGrafo tipo) {
        return usarMatriz
                ? new GrafoMatrizAdjacencia(vertices, tipo)
                : new GrafoListaAdjacencia(vertices, tipo);
    }

    private static void verificaProximo(double recebido, double esperado, double tol, String mensagem) {
        verifica(Math.abs(recebido - esperado) <= tol,
                mensagem + " (recebido=" + recebido + ", esperado=" + esperado + ")");
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