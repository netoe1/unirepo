package grafo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GrafoMatrizAdjacencia implements IGrafo {
    private final List<Vertice> vertices;
    private final List<Aresta> arestas;
    private final Aresta[][] matriz;
    private final TipoGrafo tipo;

    public GrafoMatrizAdjacencia(int numVertices, TipoGrafo tipo) {
        if (numVertices < 0) {
            throw new IllegalArgumentException("O numero de vertices nao pode ser negativo: " + numVertices);
        }
        if (tipo == null) {
            throw new IllegalArgumentException("O tipo do grafo nao pode ser nulo.");
        }

        this.tipo = tipo;
        this.vertices = new ArrayList<>(numVertices);
        this.arestas = new ArrayList<>();
        this.matriz = new Aresta[numVertices][numVertices];

        for (int i = 0; i < numVertices; i++) {
            this.vertices.add(new Vertice(i));
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

        removerArestaExistente(u, v);

        Vertice verticeU = vertices.get(u);
        Vertice verticeV = vertices.get(v);
        Aresta arestaUV = new Aresta(verticeU, verticeV, peso);
        matriz[u][v] = arestaUV;
        arestas.add(arestaUV);

        // Se nao for direcionado, preenche tambem a posicao simetrica (v, u)
        if (tipo == TipoGrafo.NAO_DIRECIONADO && u != v) {
            Aresta arestaVU = new Aresta(verticeV, verticeU, peso);
            matriz[v][u] = arestaVU;
        }
    }

    @Override
    public Aresta obterAresta(int u, int v) {
        validarVertice(u);
        validarVertice(v);
        return matriz[u][v];
    }

    @Override
    public List<Aresta> adjacentes(int u) {
        validarVertice(u);
        List<Aresta> listaAdjacentes = new ArrayList<>();
        for (int v = 0; v < matriz.length; v++) {
            if (matriz[u][v] != null) {
                listaAdjacentes.add(matriz[u][v]);
            }
        }
        return Collections.unmodifiableList(listaAdjacentes);
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

    private void removerArestaExistente(int u, int v) {
        Aresta existente = matriz[u][v];
        if (existente != null) {
            matriz[u][v] = null;
            arestas.remove(existente);
        }
        if (tipo == TipoGrafo.NAO_DIRECIONADO && u != v) {
            Aresta inversa = matriz[v][u];
            if (inversa != null) {
                matriz[v][u] = null;
                arestas.remove(inversa);
            }
        }
    }
}
