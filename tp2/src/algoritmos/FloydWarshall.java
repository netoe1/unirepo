package algoritmos;

import grafo.Aresta;
import grafo.IGrafo;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Implementacao do algoritmo de Floyd-Warshall para caminho minimo entre todos os pares.
 */
public class FloydWarshall {

    public static class ResultadoFloydWarshall {
        private final double[][] distancias;
        private final int[][] proximo;
        private final boolean temCicloNegativo;

        public ResultadoFloydWarshall(double[][] distancias, int[][] proximo, boolean temCicloNegativo) {
            this.distancias = distancias;
            this.proximo = proximo;
            this.temCicloNegativo = temCicloNegativo;
        }

        public double[][] getDistancias() {
            return distancias;
        }

        public int[][] getProximo() {
            return proximo;
        }

        public boolean isTemCicloNegativo() {
            return temCicloNegativo;
        }

        private boolean existeCicloNegativoEntre(int origem, int destino) {
            int n = distancias.length;
            for (int m = 0; m < n; m++) {
                if (distancias[m][m] < 0.0
                        && !Double.isInfinite(distancias[origem][m])
                        && !Double.isInfinite(distancias[m][destino])) {
                    return true;
                }
            }
            return false;
        }

        /**
         * Reconstroi o caminho minimo do vertice origem ate o vertice destino.
         * Retorna lista vazia se nao existir caminho ou se houver ciclo negativo
         * alcancavel de origem que alcance destino.
         */
        public List<Integer> reconstruirCaminho(int origem, int destino) {
            int n = distancias.length;
            if (origem < 0 || origem >= n || destino < 0 || destino >= n) {
                throw new IndexOutOfBoundsException("Indice de vertice fora dos limites.");
            }
            if (proximo[origem][destino] == -1
                    || Double.isInfinite(distancias[origem][destino])
                    || existeCicloNegativoEntre(origem, destino)) {
                return Collections.emptyList();
            }

            List<Integer> caminho = new ArrayList<>();
            int atual = origem;
            caminho.add(atual);
            int passos = 0;

            while (atual != destino) {
                if (++passos > n) {
                    return Collections.emptyList();
                }
                atual = proximo[atual][destino];
                if (atual == -1) {
                    return Collections.emptyList();
                }
                caminho.add(atual);
            }
            return caminho;
        }
    }

    public ResultadoFloydWarshall executar(IGrafo grafo) {
        if (grafo == null) {
            throw new IllegalArgumentException("O grafo nao pode ser nulo.");
        }

        int n = grafo.numVertices();
        double[][] dist = new double[n][n];
        int[][] prox = new int[n][n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                prox[i][j] = -1;
                if (i == j) {
                    dist[i][j] = 0.0;
                    prox[i][j] = i;
                } else {
                    dist[i][j] = Double.POSITIVE_INFINITY;
                }
            }
        }

        for (int u = 0; u < n; u++) {
            for (Aresta aresta : grafo.adjacentes(u)) {
                int v = aresta.getV().getId();
                double peso = aresta.getPeso();
                if (Double.isNaN(peso) || Double.isInfinite(peso)) {
                    throw new IllegalArgumentException("Peso de aresta invalido (NaN ou infinito): " + peso);
                }
                if (peso < dist[u][v]) {
                    dist[u][v] = peso;
                    prox[u][v] = v;
                }
            }
        }

        for (int k = 0; k < n; k++) {
            for (int i = 0; i < n; i++) {
                if (Double.isInfinite(dist[i][k])) {
                    continue;
                }
                for (int j = 0; j < n; j++) {
                    if (Double.isInfinite(dist[k][j])) {
                        continue;
                    }
                    double novaDist = dist[i][k] + dist[k][j];
                    if (novaDist < dist[i][j]) {
                        dist[i][j] = novaDist;
                        prox[i][j] = prox[i][k];
                    }
                }
            }
        }

        boolean cicloNegativo = false;
        for (int i = 0; i < n; i++) {
            if (dist[i][i] < 0.0) {
                cicloNegativo = true;
                break;
            }
        }

        return new ResultadoFloydWarshall(dist, prox, cicloNegativo);
    }
}