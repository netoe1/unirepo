package test;

import grafo.Aresta;
import grafo.GrafoListaAdjacencia;
import grafo.GrafoMatrizAdjacencia;
import grafo.IGrafo;
import grafo.TipoGrafo;
import java.util.HashMap;
import java.util.Map;

public final class RepresentacoesGrafoTest {

    public static void main(String[] args) {
        comparaRepresentacoes(TipoGrafo.DIRECIONADO);
        comparaRepresentacoes(TipoGrafo.NAO_DIRECIONADO);
        System.out.println("RepresentacoesGrafoTest: lista e matriz produziram resultados equivalentes.");
    }

    private static void comparaRepresentacoes(TipoGrafo tipo) {
        IGrafo lista = new GrafoListaAdjacencia(4, tipo);
        IGrafo matriz = new GrafoMatrizAdjacencia(4, tipo);

        adicionarEmAmbos(lista, matriz, 0, 1, 2.0);
        adicionarEmAmbos(lista, matriz, 1, 2, 3.0);
        adicionarEmAmbos(lista, matriz, 2, 2, 4.0);
        adicionarEmAmbos(lista, matriz, 1, 0, 5.0); // substitui a aresta não dirigida
        adicionarEmAmbos(lista, matriz, 0, 1, 6.0); // substitui o par na ordem original

        verifica(lista.tipo() == matriz.tipo(), tipo + ": tipo divergente");
        verifica(lista.numVertices() == matriz.numVertices(), tipo + ": número de vértices divergente");
        verifica(lista.numArestas() == matriz.numArestas(), tipo + ": número de arestas divergente");

        for (int u = 0; u < lista.numVertices(); u++) {
            verifica(mapaAdjacencias(lista, u).equals(mapaAdjacencias(matriz, u)),
                    tipo + ": adjacências divergentes no vértice " + u);
            for (int v = 0; v < lista.numVertices(); v++) {
                Aresta arestaLista = lista.obterAresta(u, v);
                Aresta arestaMatriz = matriz.obterAresta(u, v);
                verifica((arestaLista == null) == (arestaMatriz == null),
                        tipo + ": existência divergente da aresta " + u + " -> " + v);
                if (arestaLista != null) {
                    verifica(Double.compare(arestaLista.getPeso(), arestaMatriz.getPeso()) == 0,
                            tipo + ": peso divergente na aresta " + u + " -> " + v);
                }
            }
        }
    }

    private static void adicionarEmAmbos(IGrafo lista, IGrafo matriz, int u, int v, double peso) {
        lista.adicionarAresta(u, v, peso);
        matriz.adicionarAresta(u, v, peso);
    }

    private static Map<Integer, Double> mapaAdjacencias(IGrafo grafo, int origem) {
        Map<Integer, Double> adjacencias = new HashMap<>();
        for (Aresta aresta : grafo.adjacentes(origem)) {
            adjacencias.put(aresta.getV().getId(), aresta.getPeso());
        }
        return adjacencias;
    }

    private static void verifica(boolean condicao, String mensagem) {
        if (!condicao) {
            throw new AssertionError(mensagem);
        }
    }
}
