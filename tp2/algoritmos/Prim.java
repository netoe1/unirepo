package algoritmos;

import grafo.Aresta;
import grafo.IGrafo;
import grafo.TipoGrafo;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

/** Constrói uma árvore geradora mínima usando o algoritmo de Prim. */
public class Prim {

    private static class Candidata {
        private final Aresta aresta;

        private Candidata(Aresta aresta) {
            this.aresta = aresta;
        }
    }

    public ResultadoArvoreGeradoraMinima executar(IGrafo grafo) {
        validarGrafo(grafo);
        if (grafo.numVertices() == 0) {
            return new ResultadoArvoreGeradoraMinima(new ArrayList<>(), 0.0, 0);
        }
        return executar(grafo, 0);
    }

    public ResultadoArvoreGeradoraMinima executar(IGrafo grafo, int origem) {
        validarGrafo(grafo);
        validarOrigem(grafo, origem);

        boolean[] incluido = new boolean[grafo.numVertices()];
        List<Aresta> selecionadas = new ArrayList<>();
        double pesoTotal = 0.0;
        int componentes = 0;

        componentes++;
        pesoTotal += incluirComponente(grafo, origem, incluido, selecionadas);
        for (int vertice = 0; vertice < grafo.numVertices(); vertice++) {
            if (!incluido[vertice]) {
                componentes++;
                pesoTotal += incluirComponente(grafo, vertice, incluido, selecionadas);
            }
        }

        return new ResultadoArvoreGeradoraMinima(selecionadas, pesoTotal, componentes);
    }

    private double incluirComponente(IGrafo grafo, int inicio, boolean[] incluido, List<Aresta> selecionadas) {
        PriorityQueue<Candidata> fila = new PriorityQueue<>(Comparator.comparingDouble(candidata -> candidata.aresta.getPeso()));
        adicionarArestasDoVertice(grafo, inicio, incluido, fila);
        double pesoComponente = 0.0;

        while (!fila.isEmpty()) {
            Aresta aresta = fila.poll().aresta;
            int u = aresta.getU().getId();
            int v = aresta.getV().getId();
            if (incluido[u] && incluido[v]) {
                continue;
            }

            int proximo = incluido[u] ? v : u;
            selecionadas.add(aresta);
            pesoComponente += aresta.getPeso();
            adicionarArestasDoVertice(grafo, proximo, incluido, fila);
        }
        return pesoComponente;
    }

    private void adicionarArestasDoVertice(
            IGrafo grafo,
            int vertice,
            boolean[] incluido,
            PriorityQueue<Candidata> fila
    ) {
        incluido[vertice] = true;
        for (Aresta aresta : grafo.adjacentes(vertice)) {
            validarPeso(aresta);
            if (!incluido[aresta.getV().getId()]) {
                fila.add(new Candidata(aresta));
            }
        }
    }

    private void validarGrafo(IGrafo grafo) {
        if (grafo == null) {
            throw new IllegalArgumentException("O grafo nao pode ser nulo.");
        }
        if (grafo.tipo() != TipoGrafo.NAO_DIRECIONADO) {
            throw new IllegalArgumentException("Prim requer um grafo nao direcionado.");
        }
    }

    private void validarOrigem(IGrafo grafo, int origem) {
        if (origem < 0 || origem >= grafo.numVertices()) {
            throw new IndexOutOfBoundsException("Vertice de origem fora dos limites: " + origem);
        }
    }

    private void validarPeso(Aresta aresta) {
        if (!Double.isFinite(aresta.getPeso())) {
            throw new IllegalArgumentException("Prim requer pesos finitos.");
        }
    }
}
