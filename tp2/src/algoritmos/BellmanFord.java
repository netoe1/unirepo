package algoritmos;

import grafo.Aresta;
import grafo.IGrafo;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/** Calcula caminhos mínimos de uma origem, aceitando arestas de peso negativo. */
public class BellmanFord {

    public static class ResultadoBellmanFord {
        private final int origem;
        private final double[] distancia;
        private final int[] pai;
        private final boolean temCicloNegativo;

        private ResultadoBellmanFord(int origem, double[] distancia, int[] pai, boolean temCicloNegativo) {
            this.origem = origem;
            this.distancia = distancia.clone();
            this.pai = pai.clone();
            this.temCicloNegativo = temCicloNegativo;
        }

        public int getOrigem() {
            return origem;
        }

        public double[] getDistancia() {
            return distancia.clone();
        }

        public int[] getPai() {
            return pai.clone();
        }

        public boolean isTemCicloNegativo() {
            return temCicloNegativo;
        }

        public boolean alcancavel(int destino) {
            validarDestino(destino);
            return !Double.isInfinite(distancia[destino]);
        }

        public List<Integer> reconstruirCaminho(int destino) {
            validarDestino(destino);
            if (temCicloNegativo || !alcancavel(destino)) {
                return Collections.emptyList();
            }

            List<Integer> caminho = new ArrayList<>();
            for (int atual = destino; atual != -1; atual = pai[atual]) {
                caminho.add(atual);
            }
            Collections.reverse(caminho);
            return caminho;
        }

        private void validarDestino(int destino) {
            if (destino < 0 || destino >= distancia.length) {
                throw new IndexOutOfBoundsException("Destino fora dos limites: " + destino);
            }
        }
    }

    public ResultadoBellmanFord executar(IGrafo grafo, int origem) {
        validarEntrada(grafo, origem);

        int n = grafo.numVertices();
        double[] distancia = new double[n];
        int[] pai = new int[n];
        Arrays.fill(distancia, Double.POSITIVE_INFINITY);
        Arrays.fill(pai, -1);
        distancia[origem] = 0.0;

        for (int i = 1; i < n; i++) {
            boolean alterou = false;
            for (int u = 0; u < n; u++) {
                for (Aresta aresta : grafo.adjacentes(u)) {
                    validarPeso(aresta);
                    int v = aresta.getV().getId();
                    if (relaxar(u, v, aresta.getPeso(), distancia, pai)) {
                        alterou = true;
                    }
                }
            }
            if (!alterou) {
                break;
            }
        }

        boolean temCicloNegativo = false;
        for (int u = 0; u < n && !temCicloNegativo; u++) {
            for (Aresta aresta : grafo.adjacentes(u)) {
                validarPeso(aresta);
                int v = aresta.getV().getId();
                if (!Double.isInfinite(distancia[u]) && distancia[u] + aresta.getPeso() < distancia[v]) {
                    temCicloNegativo = true;
                    break;
                }
            }
        }

        return new ResultadoBellmanFord(origem, distancia, pai, temCicloNegativo);
    }

    private boolean relaxar(int u, int v, double peso, double[] distancia, int[] pai) {
        if (Double.isInfinite(distancia[u])) {
            return false;
        }
        double candidata = distancia[u] + peso;
        if (candidata < distancia[v]) {
            distancia[v] = candidata;
            pai[v] = u;
            return true;
        }
        return false;
    }

    private void validarEntrada(IGrafo grafo, int origem) {
        if (grafo == null) {
            throw new IllegalArgumentException("O grafo nao pode ser nulo.");
        }
        if (origem < 0 || origem >= grafo.numVertices()) {
            throw new IndexOutOfBoundsException("Vertice de origem fora dos limites: " + origem);
        }
    }

    private void validarPeso(Aresta aresta) {
        if (!Double.isFinite(aresta.getPeso())) {
            throw new IllegalArgumentException("Bellman-Ford requer pesos finitos.");
        }
    }
}
