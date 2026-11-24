package io;

import grafo.GrafoListaAdjacencia;
import grafo.GrafoMatrizAdjacencia;
import grafo.IGrafo;
import grafo.TipoGrafo;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class LeituraGrafo {

    public enum Representacao {
        LISTA_ADJACENCIA,
        MATRIZ_ADJACENCIA
    }

    public IGrafo lerArquivo(String caminhoArquivo, Representacao representacao, TipoGrafo tipoGrafo) throws IOException {
        if (caminhoArquivo == null || caminhoArquivo.trim().isEmpty()) {
            throw new IllegalArgumentException("O caminho do arquivo nao pode ser vazio.");
        }
        return lerArquivo(new File(caminhoArquivo), representacao, tipoGrafo);
    }

    public IGrafo lerArquivo(File arquivo, Representacao representacao, TipoGrafo tipoGrafo) throws IOException {
        if (arquivo == null || !arquivo.exists() || !arquivo.isFile()) {
            throw new IOException("Arquivo de grafo nao encontrado: " + (arquivo != null ? arquivo.getPath() : "null"));
        }
        if (representacao == null) {
            throw new IllegalArgumentException("A representacao do grafo nao pode ser nula.");
        }
        if (tipoGrafo == null) {
            throw new IllegalArgumentException("O tipo do grafo nao pode ser nulo.");
        }

        IGrafo grafo = null;
        int numVerticesDeclarado = -1;
        int numArestasDeclaradas = -1;  
        int arestasLidas = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(arquivo, StandardCharsets.UTF_8))) {
            String linha;
            int numeroLinha = 0;

            while ((linha = br.readLine()) != null) {
                numeroLinha++;
                linha = linha.trim();

                if (linha.isEmpty() || linha.startsWith("c")) {
                    continue;
                }

                String[] partes = linha.split("\\s+");

                if (partes[0].equals("p")) {
                    if (partes.length < 4) {
                        throw new IOException("Linha 'p' mal formatada na linha " + numeroLinha + ": " + linha);
                    }
                    numVerticesDeclarado = Integer.parseInt(partes[2]);
                    numArestasDeclaradas = Integer.parseInt(partes[3]);
                    grafo = criarGrafo(numVerticesDeclarado, representacao, tipoGrafo);
                } else if (partes[0].equals("a")) {
                    if (grafo == null) {
                        throw new IOException("Linha de aresta 'a' encontrada antes da declaracao 'p' na linha " + numeroLinha);
                    }
                    if (partes.length < 4) {
                        throw new IOException("Linha 'a' mal formatada na linha " + numeroLinha + ": " + linha);
                    }

                    // GTgraph usa indices comecando em 1; converte para 0-based
                    int u = Integer.parseInt(partes[1]) - 1;
                    int v = Integer.parseInt(partes[2]) - 1;
                    double peso = Double.parseDouble(partes[3]);

                    grafo.adicionarAresta(u, v, peso);
                    arestasLidas++;
                }
            }
        }

        if (grafo == null) {
            throw new IOException("Cabecalho 'p' nao encontrado no arquivo: " + arquivo.getPath());
        }

        if (numArestasDeclaradas >= 0 && arestasLidas != numArestasDeclaradas) {
            System.out.println(
                "Aviso: esperado " + numArestasDeclaradas + " arestas em " + arquivo.getName() +
                ", mas foram lidas " + arestasLidas + "."
        );
}

        return grafo;
    }

    private IGrafo criarGrafo(int numVertices, Representacao representacao, TipoGrafo tipoGrafo) {
        return switch (representacao) {
            case LISTA_ADJACENCIA -> new GrafoListaAdjacencia(numVertices, tipoGrafo);
            case MATRIZ_ADJACENCIA -> new GrafoMatrizAdjacencia(numVertices, tipoGrafo);
        };
    }
}