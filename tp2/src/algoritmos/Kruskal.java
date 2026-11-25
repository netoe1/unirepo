package algoritmos;

import grafo.Aresta;
import grafo.IGrafo;
import grafo.TipoGrafo;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/** Constrói uma árvore geradora mínima usando o algoritmo de Kruskal. */
public class Kruskal {

    public ResultadoArvoreGeradoraMinima executar(IGrafo grafo) {
        validarGrafo(grafo);

        List<Aresta> candidatas = new ArrayList<>(grafo.arestas());
        candidatas.sort(Comparator.comparingDouble(Aresta::getPeso));

        ConjuntosDisjuntos conjuntos = new ConjuntosDisjuntos(grafo.numVertices());
        List<Aresta> selecionadas = new ArrayList<>();
        double pesoTotal = 0.0;

        for (Aresta aresta : candidatas) {
            validarPeso(aresta);
            int u = aresta.getU().getId();
            int v = aresta.getV().getId();
            if (conjuntos.unir(u, v)) {
                selecionadas.add(aresta);
                pesoTotal += aresta.getPeso();
            }
        }

        return new ResultadoArvoreGeradoraMinima(
                selecionadas,
                pesoTotal,
                conjuntos.getNumeroComponentes()
        );
    }

    private void validarGrafo(IGrafo grafo) {
        if (grafo == null) {
            throw new IllegalArgumentException("O grafo nao pode ser nulo.");
        }
        if (grafo.tipo() != TipoGrafo.NAO_DIRECIONADO) {
            throw new IllegalArgumentException("Kruskal requer um grafo nao direcionado.");
        }
    }

    private void validarPeso(Aresta aresta) {
        if (!Double.isFinite(aresta.getPeso())) {
            throw new IllegalArgumentException("Kruskal requer pesos finitos.");
        }
    }

    private static class ConjuntosDisjuntos {
        private final int[] pai;
        private final int[] tamanho;
        private int numeroComponentes;

        private ConjuntosDisjuntos(int tamanho) {
            this.pai = new int[tamanho];
            this.tamanho = new int[tamanho];
            this.numeroComponentes = tamanho;
            for (int i = 0; i < tamanho; i++) {
                pai[i] = i;
                this.tamanho[i] = 1;
            }
        }

        private boolean unir(int u, int v) {
            int raizU = encontrar(u);
            int raizV = encontrar(v);
            if (raizU == raizV) {
                return false;
            }
            if (tamanho[raizU] < tamanho[raizV]) {
                int temporario = raizU;
                raizU = raizV;
                raizV = temporario;
            }
            pai[raizV] = raizU;
            tamanho[raizU] += tamanho[raizV];
            numeroComponentes--;
            return true;
        }

        private int encontrar(int vertice) {
            if (pai[vertice] != vertice) {
                pai[vertice] = encontrar(pai[vertice]);
            }
            return pai[vertice];
        }

        private int getNumeroComponentes() {
            return numeroComponentes;
        }
    }
}
