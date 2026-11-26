package algoritmos;

import grafo.Aresta;
import grafo.IGrafo;
import grafo.TipoGrafo;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Implementacao do algoritmo de Ford-Fulkerson (com busca de caminho aumentante via BFS).
 */
public class FordFulkerson {

    public static class ResultadoFordFulkerson {
        private final int origem;
        private final int sumidouro;
        private final double fluxoMaximo;
        private final double[][] fluxoPorAresta;

        public ResultadoFordFulkerson(int origem, int sumidouro, double fluxoMaximo, double[][] fluxoPorAresta) {
            this.origem = origem;
            this.sumidouro = sumidouro;
            this.fluxoMaximo = fluxoMaximo;
            this.fluxoPorAresta = fluxoPorAresta;
        }

        public int getOrigem() {
            return origem;
        }

        public int getSumidouro() {
            return sumidouro;
        }

        public double getFluxoMaximo() {
            return fluxoMaximo;
        }

        public double[][] getFluxoPorAresta() {
            return fluxoPorAresta;
        }
    }

    public ResultadoFordFulkerson executar(IGrafo grafo, int origem, int sumidouro) {
        if (grafo == null) {
            throw new IllegalArgumentException("O grafo nao pode ser nulo.");
        }
        if (grafo.tipo() != TipoGrafo.DIRECIONADO) {
            throw new IllegalArgumentException("O algoritmo de Ford-Fulkerson requer um grafo DIRECIONADO.");
        }

        int n = grafo.numVertices();
        validarVertice(grafo, origem, "origem");
        validarVertice(grafo, sumidouro, "sumidouro");

        if (origem == sumidouro) {
            throw new IllegalArgumentException("Origem e sumidouro devem ser vertices distintos.");
        }

        double[][] capacidadeResidual = new double[n][n];
        double[][] capacidadeOriginal = new double[n][n];
        double menorCapacidadePositiva = Double.POSITIVE_INFINITY;

        for (int u = 0; u < n; u++) {
            for (Aresta aresta : grafo.adjacentes(u)) {
                int v = aresta.getV().getId();
                double cap = aresta.getPeso();
                if (Double.isNaN(cap) || Double.isInfinite(cap) || cap < 0.0) {
                    throw new IllegalArgumentException("Capacidade de aresta invalida: " + cap);
                }
                if (cap > 0.0 && cap < menorCapacidadePositiva) {
                    menorCapacidadePositiva = cap;
                }
                capacidadeResidual[u][v] += cap;
                capacidadeOriginal[u][v] += cap;
            }
        }

        double eps = Double.isFinite(menorCapacidadePositiva)
                ? menorCapacidadePositiva * 1e-6
                : 0.0;

        double fluxoMaximo = 0.0;
        int[] pai = new int[n];

        while (true) {
            Arrays.fill(pai, -1);
            double gargalo = encontrarCaminhoAumentanteBFS(n, origem, sumidouro, capacidadeResidual, pai, eps);
            if (gargalo <= eps) {
                break;
            }

            boolean houveAlteracaoResidual = false;
            int atual = sumidouro;
            while (atual != origem) {
                int ant = pai[atual];
                double antesDireta = capacidadeResidual[ant][atual];
                double antesReversa = capacidadeResidual[atual][ant];
                double depoisDireta = Math.max(0.0, antesDireta - gargalo);
                double depoisReversa = antesReversa + gargalo;

                if (depoisDireta != antesDireta || depoisReversa != antesReversa) {
                    houveAlteracaoResidual = true;
                }
                capacidadeResidual[ant][atual] = depoisDireta;
                capacidadeResidual[atual][ant] = depoisReversa;
                atual = ant;
            }

            if (!houveAlteracaoResidual) {
                break;
            }

            fluxoMaximo += gargalo;
        }

        double[][] fluxoPorAresta = new double[n][n];
        for (int u = 0; u < n; u++) {
            for (int v = 0; v < n; v++) {
                if (capacidadeOriginal[u][v] > 0.0) {
                    double enviado = capacidadeOriginal[u][v] - capacidadeResidual[u][v];
                    fluxoPorAresta[u][v] = Math.max(0.0, enviado);
                }
            }
        }

        return new ResultadoFordFulkerson(origem, sumidouro, fluxoMaximo, fluxoPorAresta);
    }

    private double encontrarCaminhoAumentanteBFS(
            int n,
            int origem,
            int sumidouro,
            double[][] capacidadeResidual,
            int[] pai,
            double eps
    ) {
        double[] gargaloAte = new double[n];
        gargaloAte[origem] = Double.POSITIVE_INFINITY;
        pai[origem] = -2;

        Queue<Integer> fila = new LinkedList<>();
        fila.add(origem);

        while (!fila.isEmpty()) {
            int u = fila.poll();

            for (int v = 0; v < n; v++) {
                double capDisp = capacidadeResidual[u][v];
                if (pai[v] == -1 && capDisp > eps) {
                    pai[v] = u;
                    gargaloAte[v] = Math.min(gargaloAte[u], capDisp);
                    if (v == sumidouro) {
                        return gargaloAte[sumidouro];
                    }
                    fila.add(v);
                }
            }
        }

        return 0.0;
    }

    private void validarVertice(IGrafo grafo, int indice, String rotulo) {
        if (indice < 0 || indice >= grafo.numVertices()) {
            throw new IndexOutOfBoundsException(
                "Vertice de " + rotulo + " fora dos limites: " + indice +
                " (valido: 0.." + (grafo.numVertices() - 1) + ")"
            );
        }
    }
}