package algoritmos;

import grafo.Aresta;
import grafo.IGrafo;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class BFS {

    public static class ResultadoBFS {
        private final int origem;
        private final boolean[] visitado;
        private final int[] distancia;
        private final int[] pai;
        private final List<Integer> ordemVisitacao;

        public ResultadoBFS(int origem, boolean[] visitado, int[] distancia, int[] pai, List<Integer> ordemVisitacao) {
            this.origem = origem;
            this.visitado = visitado;
            this.distancia = distancia;
            this.pai = pai;
            this.ordemVisitacao = ordemVisitacao;
        }

        public int getOrigem() {
            return origem;
        }

        public boolean[] getVisitado() {
            return visitado;
        }

        public int[] getDistancia() {
            return distancia;
        }

        public int[] getPai() {
            return pai;
        }

        public List<Integer> getOrdemVisitacao() {
            return ordemVisitacao;
        }

        public List<Integer> reconstruirCaminho(int destino) {
            if (destino < 0 || destino >= visitado.length || !visitado[destino]) {
                return Collections.emptyList();
            }
            List<Integer> caminho = new ArrayList<>();
            for (int atual = destino; atual != -1; atual = pai[atual]) {
                caminho.add(atual);
            }
            Collections.reverse(caminho);
            return caminho;
        }
    }

    public ResultadoBFS executar(IGrafo grafo, int origem) {
        if (grafo == null) {
            throw new IllegalArgumentException("O grafo nao pode ser nulo.");
        }
        validarOrigem(grafo, origem);

        int n = grafo.numVertices();
        boolean[] visitado = new boolean[n];
        int[] distancia = new int[n];
        int[] pai = new int[n];
        List<Integer> ordemVisitacao = new ArrayList<>();

        Arrays.fill(distancia, -1);
        Arrays.fill(pai, -1);

        Queue<Integer> fila = new LinkedList<>();
        visitado[origem] = true;
        distancia[origem] = 0;
        fila.add(origem);

        while (!fila.isEmpty()) {
            int u = fila.poll();
            ordemVisitacao.add(u);

            for (Aresta aresta : grafo.adjacentes(u)) {
                int v = aresta.getV().getId();
                if (!visitado[v]) {
                    visitado[v] = true;
                    distancia[v] = distancia[u] + 1;
                    pai[v] = u;
                    fila.add(v);
                }
            }
        }

        return new ResultadoBFS(origem, visitado, distancia, pai, ordemVisitacao);
    }

    private void validarOrigem(IGrafo grafo, int origem) {
        if (origem < 0 || origem >= grafo.numVertices()) {
            throw new IndexOutOfBoundsException(
                "Vertice de origem fora dos limites: " + origem + " (valido: 0.." + (grafo.numVertices() - 1) + ")"
            );
        }
    }
}