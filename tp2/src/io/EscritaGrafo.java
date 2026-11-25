package io;

import algoritmos.BFS.ResultadoBFS;
import algoritmos.BellmanFord.ResultadoBellmanFord;
import algoritmos.DFS.ResultadoDFS;
import algoritmos.Dkjistra.ResultadoDkjistra;
import algoritmos.OrdenacaoTopologica.ResultadoOrdenacaoTopologica;
import algoritmos.ResultadoArvoreGeradoraMinima;
import grafo.Aresta;
import grafo.IGrafo;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class EscritaGrafo {

    public void imprimirResumoGrafo(IGrafo grafo) {
        System.out.println("--------------------------------");
        System.out.println("Estrutura do Grafo");
        System.out.println("--------------------------------");
        System.out.println("Representacao : " + grafo.getClass().getSimpleName());
        System.out.println("Tipo          : " + grafo.tipo());
        System.out.println("Vertices (|V|): " + grafo.numVertices());
        System.out.println("Arestas  (|E|): " + grafo.numArestas());
    }

    public void imprimirListaAdjacencia(IGrafo grafo, int limiteVertices) {
        imprimirResumoGrafo(grafo);
        int limite = Math.min(grafo.numVertices(), limiteVertices);
        for (int u = 0; u < limite; u++) {
            StringBuilder sb = new StringBuilder();
            sb.append("  ").append(u).append(" -> ");
            List<Aresta> adj = grafo.adjacentes(u);
            if (adj.isEmpty()) {
                sb.append("[]");
            } else {
                for (int i = 0; i < adj.size(); i++) {
                    Aresta a = adj.get(i);
                    sb.append("(").append(a.getV().getId()).append(", peso=").append(a.getPeso()).append(")");
                    if (i < adj.size() - 1) {
                        sb.append(", ");
                    }
                }
            }
            System.out.println(sb);
        }
        if (grafo.numVertices() > limite) {
            System.out.println("  ... (" + (grafo.numVertices() - limite) + " vertices omitidos)");
        }
    }

    public void imprimirResultadoBFS(ResultadoBFS resultado, double tempoMs) {
        System.out.println("\n----------------------");
        System.out.println("Resultado BFS (Origem: " + resultado.getOrigem() + ")");
        System.out.println("----------------------");
        System.out.println("Ordem de visitacao : " + resultado.getOrdemVisitacao());
        int[] dist = resultado.getDistancia();
        for (int v = 0; v < dist.length; v++) {
            System.out.println("  Vertice " + v + " | Distancia: " + dist[v] + " | Pai: " + resultado.getPai()[v]);
        }
        System.out.printf("Tempo de execucao  : %.4f ms%n", tempoMs);
    }

    public void imprimirResultadoDFS(ResultadoDFS resultado, double tempoMs) {
        System.out.println("\n----------------------");
        System.out.println("Resultado DFS");
        System.out.println("----------------------");
        System.out.println("Ordem pre-ordem    : " + resultado.getOrdemPreOrdem());
        System.out.println("Ciclo detectado    : " + (resultado.isTemCiclo() ? "Sim" : "Nao"));
        int[] desc = resultado.getDescoberta();
        int[] fin = resultado.getFinalizacao();
        for (int v = 0; v < desc.length; v++) {
            System.out.println(
                "  Vertice " + v +
                " | d/f: " + desc[v] + "/" + fin[v] +
                " | Pai: " + resultado.getPai()[v]
            );
        }
        System.out.printf("Tempo de execucao  : %.4f ms%n", tempoMs);
    }

    public void imprimirResultadoOrdenacaoTopologica(
            ResultadoOrdenacaoTopologica resultado,
            double tempoMs
    ) {
        System.out.println("\n----------------------");
        System.out.println("Resultado da ordenacao topologica");
        System.out.println("----------------------");
        System.out.println("Ordem: " + (resultado.isTemCiclo() ? "indefinida (ciclo detectado)" : resultado.getOrdem()));
        System.out.println("Ciclo detectado: " + (resultado.isTemCiclo() ? "Sim" : "Nao"));
        System.out.printf("Tempo de execucao: %.4f ms%n", tempoMs);
    }

    public void imprimirResultadoArvoreGeradoraMinima(
            ResultadoArvoreGeradoraMinima resultado,
            String algoritmo,
            double tempoMs
    ) {
        System.out.println("\n----------------------");
        System.out.println("Resultado " + algoritmo);
        System.out.println("----------------------");
        System.out.println("Arestas selecionadas: " + resultado.getArestas());
        System.out.printf("Peso total: %.4f%n", resultado.getPesoTotal());
        System.out.println("Componentes: " + resultado.getNumeroComponentes());
        System.out.println("Grafo conexo: " + (resultado.isConexo() ? "Sim" : "Nao"));
        System.out.printf("Tempo de execucao: %.4f ms%n", tempoMs);
    }

    public void imprimirResultadoBellmanFord(
            ResultadoBellmanFord resultado,
            double tempoMs
    ) {
        System.out.println("\n----------------------");
        System.out.println("Resultado Bellman-Ford (Origem: " + resultado.getOrigem() + ")");
        System.out.println("----------------------");
        System.out.println("Ciclo negativo alcançavel: " + (resultado.isTemCicloNegativo() ? "Sim" : "Nao"));
        for (int v = 0; v < resultado.getDistancia().length; v++) {
            double distancia = resultado.getDistancia()[v];
            String textoDistancia = Double.isInfinite(distancia) ? "INF" : String.format("%.4f", distancia);
            System.out.println(
                "  Vertice " + v +
                " | Distancia: " + textoDistancia +
                " | Pai: " + resultado.getPai()[v]
            );
        }
        System.out.printf("Tempo de execucao: %.4f ms%n", tempoMs);
    }

    public void imprimirResultadoDijkstra(
            ResultadoDkjistra resultado,
            double tempoMs
    ) {
        System.out.println("\n----------------------");
        System.out.println("Resultado Dijkstra (Origem: " + resultado.getOrigem() + ")");
        System.out.println("----------------------");
        for (int v = 0; v < resultado.getDistancia().length; v++) {
            double distancia = resultado.getDistancia()[v];
            String textoDistancia = Double.isInfinite(distancia) ? "INF" : String.format("%.4f", distancia);
            System.out.println(
                "  Vertice " + v +
                " | Distancia: " + textoDistancia +
                " | Pai: " + resultado.getPai()[v]
            );
        }
        System.out.printf("Tempo de execucao: %.4f ms%n", tempoMs);
    }

    public void salvarTexto(String caminhoArquivo, String conteudo) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(caminhoArquivo, StandardCharsets.UTF_8))) {
            bw.write(conteudo);
        }
    }
}