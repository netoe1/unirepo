package algoritmos;

import grafo.Aresta;
import grafo.IGrafo;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DFS {

    public static class ResultadoDFS {
        private final boolean[] visitado;
        private final int[] pai;
        private final int[] descoberta;
        private final int[] finalizacao;
        private final List<Integer> ordemPreOrdem;
        private final boolean temCiclo;

        public ResultadoDFS(
                boolean[] visitado,
                int[] pai,
                int[] descoberta,
                int[] finalizacao,
                List<Integer> ordemPreOrdem,
                boolean temCiclo
        ) {
            this.visitado = visitado;
            this.pai = pai;
            this.descoberta = descoberta;
            this.finalizacao = finalizacao;
            this.ordemPreOrdem = ordemPreOrdem;
            this.temCiclo = temCiclo;
        }

        public boolean[] getVisitado() {
            return visitado;
        }

        public int[] getPai() {
            return pai;
        }

        public int[] getDescoberta() {
            return descoberta;
        }

        public int[] getFinalizacao() {
            return finalizacao;
        }

        public List<Integer> getOrdemPreOrdem() {
            return ordemPreOrdem;
        }

        public boolean isTemCiclo() {
            return temCiclo;
        }
    }

    private int tempo;
    private boolean cicloDetectado;

    public ResultadoDFS executarDeOrigem(IGrafo grafo, int origem) {
        if (grafo == null) {
            throw new IllegalArgumentException("O grafo nao pode ser nulo.");
        }
        validarVertice(grafo, origem);

        int n = grafo.numVertices();
        boolean[] visitado = new boolean[n];
        int[] pai = new int[n];
        int[] descoberta = new int[n];
        int[] finalizacao = new int[n];
        List<Integer> ordemPreOrdem = new ArrayList<>();

        Arrays.fill(pai, -1);
        Arrays.fill(descoberta, -1);
        Arrays.fill(finalizacao, -1);

        this.tempo = 0;
        this.cicloDetectado = false;

        visitar(grafo, origem, visitado, pai, descoberta, finalizacao, ordemPreOrdem);

        return new ResultadoDFS(visitado, pai, descoberta, finalizacao, ordemPreOrdem, cicloDetectado);
    }

    public ResultadoDFS executarCompleto(IGrafo grafo) {
        if (grafo == null) {
            throw new IllegalArgumentException("O grafo nao pode ser nulo.");
        }

        int n = grafo.numVertices();
        boolean[] visitado = new boolean[n];
        int[] pai = new int[n];
        int[] descoberta = new int[n];
        int[] finalizacao = new int[n];
        List<Integer> ordemPreOrdem = new ArrayList<>();

        Arrays.fill(pai, -1);
        Arrays.fill(descoberta, -1);
        Arrays.fill(finalizacao, -1);

        this.tempo = 0;
        this.cicloDetectado = false;

        for (int u = 0; u < n; u++) {
            if (!visitado[u]) {
                visitar(grafo, u, visitado, pai, descoberta, finalizacao, ordemPreOrdem);
            }
        }

        return new ResultadoDFS(visitado, pai, descoberta, finalizacao, ordemPreOrdem, cicloDetectado);
    }

    private void visitar(
            IGrafo grafo,
            int u,
            boolean[] visitado,
            int[] pai,
            int[] descoberta,
            int[] finalizacao,
            List<Integer> ordemPreOrdem
    ) {
        visitado[u] = true;
        descoberta[u] = ++tempo;
        ordemPreOrdem.add(u);

        for (Aresta aresta : grafo.adjacentes(u)) {
            int v = aresta.getV().getId();
            if (!visitado[v]) {
                pai[v] = u;
                visitar(grafo, v, visitado, pai, descoberta, finalizacao, ordemPreOrdem);
            } else if (finalizacao[v] == -1 && v != pai[u]) {
                // Em grafos direcionados, qualquer aresta para um vertice ativo (descoberto e nao finalizado) indica ciclo.
                // Em nao direcionados, ignora o retorno imediato ao pai direto.
                cicloDetectado = true;
            }
        }

        finalizacao[u] = ++tempo;
    }

    private void validarVertice(IGrafo grafo, int u) {
        if (u < 0 || u >= grafo.numVertices()) {
            throw new IndexOutOfBoundsException(
                "Indice de vertice fora dos limites: " + u + " (valido: 0.." + (grafo.numVertices() - 1) + ")"
            );
        }
    }
}