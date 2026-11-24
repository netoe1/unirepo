package grafo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GrafoListaAdjacencia implements IGrafo {
    private final List<Vertice> vertices;
    private final List<Aresta> arestas;
    private final List<List<Aresta>> adjacencia;
    private final TipoGrafo tipo;

    public GrafoListaAdjacencia(int numVertices, TipoGrafo tipo) {
        if (numVertices < 0) {
            throw new IllegalArgumentException("O numero de vertices nao pode ser negativo: " + numVertices);
        }
        if (tipo == null) {
            throw new IllegalArgumentException("O tipo do grafo nao pode ser nulo.");
        }

        this.tipo = tipo;
        this.vertices = new ArrayList<>(numVertices);
        this.arestas = new ArrayList<>();
        this.adjacencia = new ArrayList<>(numVertices);

        for (int i = 0; i < numVertices; i++) {
            this.vertices.add(new Vertice(i));
            this.adjacencia.add(new ArrayList<>());
        }
    }

    @Override
    public TipoGrafo tipo() {
        return tipo;
    }

    @Override
    public List<Vertice> vertices() {
        return Collections.unmodifiableList(vertices);
    }

    @Override
    public List<Aresta> arestas() {
        return Collections.unmodifiableList(arestas);
    }

    @Override
    public void adicionarAresta(int u, int v) {
        adicionarAresta(u, v, 1.0);
    }

    @Override
    public void adicionarAresta(int u, int v, double peso) {
        validarVertice(u);
        validarVertice(v);

        Vertice verticeU = vertices.get(u);
        Vertice verticeV = vertices.get(v);
        Aresta arestaUV = new Aresta(verticeU, verticeV, peso);

        adjacencia.get(u).add(arestaUV);
        arestas.add(arestaUV);

        // Se nao for direcionado, adiciona tambem na lista de adjacencia de v
        if (tipo == TipoGrafo.NAO_DIRECIONADO) {
            Aresta arestaVU = new Aresta(verticeV, verticeU, peso);
            adjacencia.get(v).add(arestaVU);
        }
    }

    @Override
    public Aresta obterAresta(int u, int v) {
        validarVertice(u);
        validarVertice(v);

        for (Aresta a : adjacencia.get(u)) {
            if (a.getV().getId() == v) {
                return a;
            }
        }
        return null;
    }

    @Override
    public List<Aresta> adjacentes(int u) {
        validarVertice(u);
        return Collections.unmodifiableList(adjacencia.get(u));
    }

    @Override
    public int numVertices() {
        return vertices.size();
    }

    @Override
    public int numArestas() {
        return arestas.size();
    }

    private void validarVertice(int u) {
        if (u < 0 || u >= numVertices()) {
            throw new IndexOutOfBoundsException(
                "Indice de vertice fora dos limites: " + u + " (valido: 0.." + (numVertices() - 1) + ")"
            );
        }
    }
}