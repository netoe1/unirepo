package grafo;

public class Aresta {
    private final Vertice u;
    private final Vertice v;
    private final double peso;

    // Construtor para grafos nao ponderados
    public Aresta(Vertice u, Vertice v) {
        this.u = u;
        this.v = v;
        this.peso = 1.0;
    }

    // Construtor para grafos ponderados
    public Aresta(Vertice u, Vertice v, double peso) {
        this.u = u;
        this.v = v;
        this.peso = peso;
    }

    public Vertice getU() {
        return u;
    }

    public Vertice getV() {
        return v;
    }

    public double getPeso() {
        return peso;
    }

    @Override
    public String toString() {
        return "(" + u + " -> " + v + ", peso=" + peso + ")";
    }
}