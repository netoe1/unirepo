package algoritmos;

import grafo.Aresta;
import grafo.IGrafo;
import grafo.TipoGrafo;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;

/** Ordena os vértices de um dígrafo acíclico por dependências. */
public class OrdenacaoTopologica {

    public static class ResultadoOrdenacaoTopologica {
        private final List<Integer> ordem;
        private final boolean temCiclo;

        private ResultadoOrdenacaoTopologica(List<Integer> ordem, boolean temCiclo) {
            this.ordem = Collections.unmodifiableList(new ArrayList<>(ordem));
            this.temCiclo = temCiclo;
        }

        public List<Integer> getOrdem() {
            return ordem;
        }

        public boolean isTemCiclo() {
            return temCiclo;
        }
    }

    public ResultadoOrdenacaoTopologica executar(IGrafo grafo) {
        validarGrafo(grafo);

        int n = grafo.numVertices();
        int[] grausEntrada = new int[n];
        for (int u = 0; u < n; u++) {
            for (Aresta aresta : grafo.adjacentes(u)) {
                grausEntrada[aresta.getV().getId()]++;
            }
        }

        Queue<Integer> semPredecessores = new ArrayDeque<>();
        for (int u = 0; u < n; u++) {
            if (grausEntrada[u] == 0) {
                semPredecessores.add(u);
            }
        }

        List<Integer> ordem = new ArrayList<>();
        while (!semPredecessores.isEmpty()) {
            int u = semPredecessores.remove();
            ordem.add(u);
            for (Aresta aresta : grafo.adjacentes(u)) {
                int v = aresta.getV().getId();
                grausEntrada[v]--;
                if (grausEntrada[v] == 0) {
                    semPredecessores.add(v);
                }
            }
        }

        return new ResultadoOrdenacaoTopologica(ordem, ordem.size() != n);
    }

    private void validarGrafo(IGrafo grafo) {
        if (grafo == null) {
            throw new IllegalArgumentException("O grafo nao pode ser nulo.");
        }
        if (grafo.tipo() != TipoGrafo.DIRECIONADO) {
            throw new IllegalArgumentException("Ordenacao topologica requer um grafo direcionado.");
        }
    }
}
