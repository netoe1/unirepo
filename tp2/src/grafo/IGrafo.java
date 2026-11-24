package grafo;

import java.util.List;

public interface IGrafo {
    TipoGrafo tipo();
    List<Vertice> vertices();
    List<Aresta> arestas();
    void adicionarAresta(int u, int v);
    void adicionarAresta(int u, int v, double peso);
    Aresta obterAresta(int u, int v);
    List<Aresta> adjacentes(int u);
    int numVertices();
    int numArestas();
}