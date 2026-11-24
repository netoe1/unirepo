package io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class ExportadorCSV {

    private final File pastaOutput;

    public ExportadorCSV() {
        this("output");
    }

    public ExportadorCSV(String nomePasta) {
        this.pastaOutput = new File(nomePasta);
    }

    public File registrarExecucao(
            String arquivoOrigem,
            String algoritmo,
            String representacao,
            String tipoGrafo,
            int numVertices,
            int numArestas,
            int verticeOrigem,
            double tempoMs,
            String resumoSaida
    ) throws IOException {
        if (!pastaOutput.exists() && !pastaOutput.mkdirs()) {
            throw new IOException("Nao foi possivel criar a pasta de saida: " + pastaOutput.getPath());
        }

        File arquivoCsv = new File(pastaOutput, "resultados_algoritmos.csv");
        boolean arquivoNovo = !arquivoCsv.exists() || arquivoCsv.length() == 0;

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(arquivoCsv, StandardCharsets.UTF_8, true))) {
            if (arquivoNovo) {
                bw.write("arquivo_grafo;algoritmo;representacao;tipo_grafo;vertices;arestas;origem;tempo_ms;resumo_saida");
                bw.newLine();
            }

            String linha = String.join(";",
                    escaparCsv(arquivoOrigem),
                    escaparCsv(algoritmo),
                    escaparCsv(representacao),
                    escaparCsv(tipoGrafo),
                    String.valueOf(numVertices),
                    String.valueOf(numArestas),
                    String.valueOf(verticeOrigem),
                    String.format(java.util.Locale.US, "%.6f", tempoMs),
                    escaparCsv(resumoSaida)
            );
            bw.write(linha);
            bw.newLine();
        }

        return arquivoCsv;
    }

    public File salvarPercursoVertices(
            String nomeArquivo,
            String algoritmo,
            String representacao,
            String tipoGrafo,
            List<Integer> ordemVisitacao,
            int[] distanciaOuDescoberta,
            int[] paiOuFinalizacao
    ) throws IOException {
        if (!pastaOutput.exists() && !pastaOutput.mkdirs()) {
            throw new IOException("Nao foi possivel criar a pasta de saida: " + pastaOutput.getPath());
        }

        File arquivoCsv = new File(pastaOutput, nomeArquivo);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(arquivoCsv, StandardCharsets.UTF_8))) {
            bw.write("algoritmo;representacao;tipo_grafo;ordem_visita;vertice;valor1;pai_ou_valor2");
            bw.newLine();

            for (int v = 0; v < distanciaOuDescoberta.length; v++) {
                int ordem = ordemVisitacao.indexOf(v);
                String linha = String.join(";",
                        escaparCsv(algoritmo),
                        escaparCsv(representacao),
                        escaparCsv(tipoGrafo),
                        String.valueOf(ordem),
                        String.valueOf(v),
                        String.valueOf(distanciaOuDescoberta[v]),
                        String.valueOf(paiOuFinalizacao[v])
                );
                bw.write(linha);
                bw.newLine();
            }
        }

        return arquivoCsv;
    }

    private String escaparCsv(String valor) {
        if (valor == null) {
            return "";
        }
        String limpo = valor.replace("\r", " ").replace("\n", " ");
        if (limpo.contains(";") || limpo.contains("\"")) {
            return "\"" + limpo.replace("\"", "\"\"") + "\"";
        }
        return limpo;
    }
}