package io;

import algoritmos.BFS.ResultadoBFS;
import algoritmos.BellmanFord.ResultadoBellmanFord;
import algoritmos.DFS.ResultadoDFS;
import algoritmos.Dijkstra.ResultadoDijkstra;
import algoritmos.FloydWarshall.ResultadoFloydWarshall;
import algoritmos.FordFulkerson.ResultadoFordFulkerson;
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
            ResultadoDijkstra resultado,
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

    public void imprimirResultadoFloydWarshall(
        ResultadoFloydWarshall resultado, 
        double tempoMs
    ) {
        System.out.println("\n----------------------");
        System.out.println("Resultado Floyd-Warshall");
        System.out.println("----------------------");
        System.out.println("Ciclo negativo detectado : " + (resultado.isTemCicloNegativo() ? "Sim" : "Nao"));

        double[][] dist = resultado.getDistancias();
        int n = dist.length;
        int limite = Math.min(n, 10);

        System.out.println("Matriz de distancias (primeiros " + limite + "x" + limite + " vertices):");
        for (int i = 0; i < limite; i++) {
            StringBuilder linha = new StringBuilder("  [" + i + "] ");
            for (int j = 0; j < limite; j++) {
                if (Double.isInfinite(dist[i][j])) {
                    linha.append("INF\t");
                } else {
                    linha.append(String.format(java.util.Locale.US, "%.1f\t", dist[i][j]));
                }
            }
            System.out.println(linha);
        }

        if (n > limite) {
            System.out.println("  ... (" + n + "x" + n + " total; exibindo apenas os primeiros " + limite + " vertices)");
        }

        if (n >= 2) {
            List<Integer> caminho = resultado.reconstruirCaminho(0, n - 1);
            if (caminho.isEmpty()) {
                System.out.println("Caminho minimo (0 -> " + (n - 1) + ") : indisponivel / sem caminho");
            } else {
                System.out.println("Caminho minimo (0 -> " + (n - 1) + ") : " + caminho);
            }
        }

        System.out.printf("Tempo de execucao: %.4f ms%n", tempoMs);
    }

    public void imprimirResultadoFordFulkerson(
        ResultadoFordFulkerson resultado, 
        double tempoMs
    ) {
        System.out.println("\n----------------------");
        System.out.println("Resultado Ford-Fulkerson");
        System.out.println("----------------------");
        System.out.println("Origem (s)               : " + resultado.getOrigem());
        System.out.println("Sumidouro (t)            : " + resultado.getSumidouro());
        System.out.printf(java.util.Locale.US, "Fluxo maximo             : %.4f%n", resultado.getFluxoMaximo());

        double[][] fluxo = resultado.getFluxoPorAresta();
        int n = fluxo.length;
        int exibidas = 0;
        int maxLinhas = 25;

        System.out.println("Fluxo enviado por aresta (fluxo > 0):");
        for (int u = 0; u < n; u++) {
            for (int v = 0; v < n; v++) {
                if (fluxo[u][v] > 1e-12) {
                    if (exibidas < maxLinhas) {
                        System.out.printf(java.util.Locale.US, "  %d -> %d : %.4f%n", u, v, fluxo[u][v]);
                    }
                    exibidas++;
                }
            }
        }

        if (exibidas == 0) {
            System.out.println("  (nenhuma aresta com fluxo positivo)");
        } else if (exibidas > maxLinhas) {
            System.out.println("  ... (" + (exibidas - maxLinhas) + " arestas adicionais omitidas)");
        }

        System.out.printf("Tempo de execucao        : %.4f ms%n", tempoMs);
    }

    public void salvarTexto(String caminhoArquivo, String conteudo) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(caminhoArquivo, StandardCharsets.UTF_8))) {
            bw.write(conteudo);
        }
    }
}