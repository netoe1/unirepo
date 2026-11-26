package algoritmos;

import grafo.Aresta;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Resultado de uma árvore geradora mínima ou floresta geradora mínima. */
public final class ResultadoArvoreGeradoraMinima {
    private final List<Aresta> arestas;
    private final double pesoTotal;
    private final int numeroComponentes;

    ResultadoArvoreGeradoraMinima(List<Aresta> arestas, double pesoTotal, int numeroComponentes) {
        this.arestas = Collections.unmodifiableList(new ArrayList<>(arestas));
        this.pesoTotal = pesoTotal;
        this.numeroComponentes = numeroComponentes;
    }

    public List<Aresta> getArestas() {
        return arestas;
    }

    public double getPesoTotal() {
        return pesoTotal;
    }

    public int getNumeroComponentes() {
        return numeroComponentes;
    }

    public boolean isConexo() {
        return numeroComponentes <= 1;
    }
}
