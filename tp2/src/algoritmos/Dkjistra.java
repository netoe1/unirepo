package algoritmos;

import grafo.Aresta;
import grafo.IGrafo;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

/** Calcula caminhos mínimos de uma origem para grafos com pesos não negativos. */
public class Dkjistra {

    public static class ResultadoDkjistra {
        private final int origem;
        private final double[] distancia;
        private final int[] pai;

        private ResultadoDkjistra(int origem, double[] distancia, int[] pai) {
            this.origem = origem;
            this.distancia = distancia.clone();
            this.pai = pai.clone();
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

        public boolean alcancavel(int destino) {
            validarDestino(destino);
            return !Double.isInfinite(distancia[destino]);
        }

        public List<Integer> reconstruirCaminho(int destino) {
            validarDestino(destino);
            if (!alcancavel(destino)) {
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

    private static class EntradaFila {
        private final int vertice;
        private final double distancia;

        private EntradaFila(int vertice, double distancia) {
            this.vertice = vertice;
            this.distancia = distancia;
        }
    }

    public ResultadoDkjistra executar(IGrafo grafo, int origem) {
        validarEntrada(grafo, origem);
        validarPesos(grafo);

        int n = grafo.numVertices();
        double[] distancia = new double[n];
        int[] pai = new int[n];
        Arrays.fill(distancia, Double.POSITIVE_INFINITY);
        Arrays.fill(pai, -1);
        distancia[origem] = 0.0;

        PriorityQueue<EntradaFila> fila = new PriorityQueue<>(Comparator.comparingDouble(entrada -> entrada.distancia));
        fila.add(new EntradaFila(origem, 0.0));

        while (!fila.isEmpty()) {
            EntradaFila atual = fila.poll();
            if (atual.distancia != distancia[atual.vertice]) {
                continue;
            }

            for (Aresta aresta : grafo.adjacentes(atual.vertice)) {
                int destino = aresta.getV().getId();
                double candidata = distancia[atual.vertice] + aresta.getPeso();
                if (candidata < distancia[destino]) {
                    distancia[destino] = candidata;
                    pai[destino] = atual.vertice;
                    fila.add(new EntradaFila(destino, candidata));
                }
            }
        }

        return new ResultadoDkjistra(origem, distancia, pai);
    }

    private void validarEntrada(IGrafo grafo, int origem) {
        if (grafo == null) {
            throw new IllegalArgumentException("O grafo nao pode ser nulo.");
        }
        if (origem < 0 || origem >= grafo.numVertices()) {
            throw new IndexOutOfBoundsException("Vertice de origem fora dos limites: " + origem);
        }
    }

    private void validarPesos(IGrafo grafo) {
        for (int u = 0; u < grafo.numVertices(); u++) {
            for (Aresta aresta : grafo.adjacentes(u)) {
                if (!Double.isFinite(aresta.getPeso()) || aresta.getPeso() < 0.0) {
                    throw new IllegalArgumentException("Dijkstra requer pesos finitos e nao negativos.");
                }
            }
        }
    }
}
