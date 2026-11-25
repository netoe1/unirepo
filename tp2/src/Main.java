import algoritmos.BFS;
import algoritmos.BFS.ResultadoBFS;
import algoritmos.BellmanFord;
import algoritmos.BellmanFord.ResultadoBellmanFord;
import algoritmos.DFS;
import algoritmos.DFS.ResultadoDFS;
import algoritmos.Dkjistra;
import algoritmos.Dkjistra.ResultadoDkjistra;
import io.ExportadorCSV;
import io.EscritaGrafo;
import io.LeituraGrafo;
import io.LeituraGrafo.Representacao;
import algoritmos.Kruskal;
import algoritmos.OrdenacaoTopologica;
import algoritmos.OrdenacaoTopologica.ResultadoOrdenacaoTopologica;
import algoritmos.Prim;
import algoritmos.ResultadoArvoreGeradoraMinima;
import grafo.IGrafo;
import grafo.TipoGrafo;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        EscritaGrafo impressora = new EscritaGrafo();
        LeituraGrafo leitor = new LeituraGrafo();
        ExportadorCSV exportador = new ExportadorCSV("output");

        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Caminho do arquivo do grafo (ex: sample.gr): ");
            String caminhoArquivo = scanner.nextLine().trim();

            System.out.print("Vertice de origem (padrao 0): ");
            int origem = lerInteiroComPadrao(scanner, 0);

            int opcao;
            do {
                System.out.println("\nEXECUTAR ALGORITMO EM TODAS AS COMBINACOES");
                System.out.println("1 - Rodar BFS");
                System.out.println("2 - Rodar DFS");
                System.out.println("3 - Rodar Ordenacao Topologica");
                System.out.println("4 - Rodar Kruskal");
                System.out.println("5 - Rodar Prim");
                System.out.println("6 - Rodar Bellman-Ford");
                System.out.println("7 - Rodar Dijkstra");
                System.out.println("8 - Trocar arquivo / vertice de origem");
                System.out.println("0 - Sair");
                System.out.print("Escolha: ");
                opcao = lerInteiroComPadrao(scanner, -1);

                switch (opcao) {
                    case 1 -> executarCombinacoesBFS(caminhoArquivo, origem, leitor, impressora, exportador);
                    case 2 -> executarCombinacoesDFS(caminhoArquivo, origem, leitor, impressora, exportador);
                    case 3 -> executarOrdenacaoTopologica(caminhoArquivo, leitor, impressora, exportador);
                    case 4 -> executarKruskal(caminhoArquivo, leitor, impressora, exportador);
                    case 5 -> executarPrim(caminhoArquivo, origem, leitor, impressora, exportador);
                    case 6 -> executarBellmanFord(caminhoArquivo, origem, leitor, impressora, exportador);
                    case 7 -> executarDijkstra(caminhoArquivo, origem, leitor, impressora, exportador);
                    case 8 -> {
                        System.out.print("Novo caminho do arquivo: ");
                        caminhoArquivo = scanner.nextLine().trim();
                        System.out.print("Novo vertice de origem: ");
                        origem = lerInteiroComPadrao(scanner, 0);
                    }
                    case 0 -> System.out.println("Encerrando...");
                    default -> System.out.println("Opcao invalida.");
                }
            } while (opcao != 0);

        } catch (IOException | IllegalArgumentException | IndexOutOfBoundsException e) {
            System.err.println("Erro durante a execucao: " + e.getMessage());
        }
    }

    private static void executarCombinacoesBFS(
            String caminhoArquivo,
            int origem,
            LeituraGrafo leitor,
            EscritaGrafo impressora,
            ExportadorCSV exportador
    ) throws IOException {
        BFS bfs = new BFS();
        String nomeBaseArquivo = new File(caminhoArquivo).getName();

        for (Representacao rep : Representacao.values()) {
            for (TipoGrafo tipo : TipoGrafo.values()) {
                IGrafo grafo = leitor.lerArquivo(caminhoArquivo, rep, tipo);

                System.out.println("\n--------------------------------------------------");
                System.out.println("ALGORITMO: BFS | REP: " + rep + " | TIPO: " + tipo);
                impressora.imprimirResumoGrafo(grafo);

                long inicio = System.nanoTime();
                ResultadoBFS resultado = bfs.executar(grafo, origem);
                double tempoMs = (System.nanoTime() - inicio) / 1_000_000.0;

                impressora.imprimirResultadoBFS(resultado, tempoMs);

                exportador.registrarExecucao(
                        nomeBaseArquivo,
                        "BFS",
                        rep.name(),
                        tipo.name(),
                        grafo.numVertices(),
                        grafo.numArestas(),
                        origem,
                        tempoMs,
                        "ordem=" + resultado.getOrdemVisitacao()
                );

                String nomeDetalhe = String.format("bfs_%s_%s.csv", rep.name().toLowerCase(), tipo.name().toLowerCase());
                exportador.salvarPercursoVertices(
                        nomeDetalhe,
                        "BFS",
                        rep.name(),
                        tipo.name(),
                        resultado.getOrdemVisitacao(),
                        resultado.getDistancia(),
                        resultado.getPai()
                );
            }
        }
        System.out.println("\nArquivos CSV salvos na pasta output/");
    }

    private static void executarCombinacoesDFS(
            String caminhoArquivo,
            int origem,
            LeituraGrafo leitor,
            EscritaGrafo impressora,
            ExportadorCSV exportador
    ) throws IOException {
        DFS dfs = new DFS();
        String nomeBaseArquivo = new File(caminhoArquivo).getName();

        for (Representacao rep : Representacao.values()) {
            for (TipoGrafo tipo : TipoGrafo.values()) {
                IGrafo grafo = leitor.lerArquivo(caminhoArquivo, rep, tipo);

                System.out.println("\n--------------------------------------------------");
                System.out.println("ALGORITMO: DFS | REP: " + rep + " | TIPO: " + tipo);
                impressora.imprimirResumoGrafo(grafo);

                long inicio = System.nanoTime();
                ResultadoDFS resultado = dfs.executarDeOrigem(grafo, origem);
                double tempoMs = (System.nanoTime() - inicio) / 1_000_000.0;

                impressora.imprimirResultadoDFS(resultado, tempoMs);

                exportador.registrarExecucao(
                        nomeBaseArquivo,
                        "DFS",
                        rep.name(),
                        tipo.name(),
                        grafo.numVertices(),
                        grafo.numArestas(),
                        origem,
                        tempoMs,
                        "ordem=" + resultado.getOrdemPreOrdem() + " | ciclo=" + resultado.isTemCiclo()
                );

                String nomeDetalhe = String.format("dfs_%s_%s.csv", rep.name().toLowerCase(), tipo.name().toLowerCase());
                exportador.salvarPercursoVertices(
                        nomeDetalhe,
                        "DFS",
                        rep.name(),
                        tipo.name(),
                        resultado.getOrdemPreOrdem(),
                        resultado.getDescoberta(),
                        resultado.getFinalizacao()
                );
            }
        }
        System.out.println("\nArquivos CSV salvos na pasta output/");
    }

    private static void executarOrdenacaoTopologica(
            String caminhoArquivo,
            LeituraGrafo leitor,
            EscritaGrafo impressora,
            ExportadorCSV exportador
    ) throws IOException {
        String nomeBaseArquivo = new File(caminhoArquivo).getName();
        for (Representacao rep : Representacao.values()) {
            IGrafo grafo = leitor.lerArquivo(caminhoArquivo, rep, TipoGrafo.DIRECIONADO);
            System.out.println("\n--------------------------------------------------");
            System.out.println("ALGORITMO: Ordenacao Topologica | REP: " + rep + " | TIPO: DIRECIONADO");
            impressora.imprimirResumoGrafo(grafo);

            long inicio = System.nanoTime();
            ResultadoOrdenacaoTopologica resultado = new OrdenacaoTopologica().executar(grafo);
            double tempoMs = (System.nanoTime() - inicio) / 1_000_000.0;
            impressora.imprimirResultadoOrdenacaoTopologica(resultado, tempoMs);

            exportador.registrarExecucao(
                    nomeBaseArquivo,
                    "ORDENACAO_TOPOLOGICA",
                    rep.name(),
                    TipoGrafo.DIRECIONADO.name(),
                    grafo.numVertices(),
                    grafo.numArestas(),
                    -1,
                    tempoMs,
                    "ordem=" + resultado.getOrdem() + " | ciclo=" + resultado.isTemCiclo()
            );
        }
        System.out.println("\nArquivos CSV salvos na pasta output/");
    }

    private static void executarKruskal(
            String caminhoArquivo,
            LeituraGrafo leitor,
            EscritaGrafo impressora,
            ExportadorCSV exportador
    ) throws IOException {
        String nomeBaseArquivo = new File(caminhoArquivo).getName();
        for (Representacao rep : Representacao.values()) {
            IGrafo grafo = leitor.lerArquivo(caminhoArquivo, rep, TipoGrafo.NAO_DIRECIONADO);
            System.out.println("\n--------------------------------------------------");
            System.out.println("ALGORITMO: Kruskal | REP: " + rep + " | TIPO: NAO_DIRECIONADO");
            impressora.imprimirResumoGrafo(grafo);

            long inicio = System.nanoTime();
            ResultadoArvoreGeradoraMinima resultado = new Kruskal().executar(grafo);
            double tempoMs = (System.nanoTime() - inicio) / 1_000_000.0;
            impressora.imprimirResultadoArvoreGeradoraMinima(resultado, "Kruskal", tempoMs);

            exportador.registrarExecucao(
                    nomeBaseArquivo,
                    "KRUSKAL",
                    rep.name(),
                    TipoGrafo.NAO_DIRECIONADO.name(),
                    grafo.numVertices(),
                    grafo.numArestas(),
                    -1,
                    tempoMs,
                    "peso=" + resultado.getPesoTotal() + " | componentes=" + resultado.getNumeroComponentes()
            );
        }
        System.out.println("\nArquivos CSV salvos na pasta output/");
    }

    private static void executarPrim(
            String caminhoArquivo,
            int origem,
            LeituraGrafo leitor,
            EscritaGrafo impressora,
            ExportadorCSV exportador
    ) throws IOException {
        String nomeBaseArquivo = new File(caminhoArquivo).getName();
        for (Representacao rep : Representacao.values()) {
            IGrafo grafo = leitor.lerArquivo(caminhoArquivo, rep, TipoGrafo.NAO_DIRECIONADO);
            System.out.println("\n--------------------------------------------------");
            System.out.println("ALGORITMO: Prim | REP: " + rep + " | TIPO: NAO_DIRECIONADO");
            impressora.imprimirResumoGrafo(grafo);

            long inicio = System.nanoTime();
            ResultadoArvoreGeradoraMinima resultado = new Prim().executar(grafo, origem);
            double tempoMs = (System.nanoTime() - inicio) / 1_000_000.0;
            impressora.imprimirResultadoArvoreGeradoraMinima(resultado, "Prim", tempoMs);

            exportador.registrarExecucao(
                    nomeBaseArquivo,
                    "PRIM",
                    rep.name(),
                    TipoGrafo.NAO_DIRECIONADO.name(),
                    grafo.numVertices(),
                    grafo.numArestas(),
                    origem,
                    tempoMs,
                    "peso=" + resultado.getPesoTotal() + " | componentes=" + resultado.getNumeroComponentes()
            );
        }
        System.out.println("\nArquivos CSV salvos na pasta output/");
    }

    private static void executarBellmanFord(
            String caminhoArquivo,
            int origem,
            LeituraGrafo leitor,
            EscritaGrafo impressora,
            ExportadorCSV exportador
    ) throws IOException {
        String nomeBaseArquivo = new File(caminhoArquivo).getName();
        for (Representacao rep : Representacao.values()) {
            IGrafo grafo = leitor.lerArquivo(caminhoArquivo, rep, TipoGrafo.DIRECIONADO);
            System.out.println("\n--------------------------------------------------");
            System.out.println("ALGORITMO: Bellman-Ford | REP: " + rep + " | TIPO: DIRECIONADO");
            impressora.imprimirResumoGrafo(grafo);

            long inicio = System.nanoTime();
            ResultadoBellmanFord resultado = new BellmanFord().executar(grafo, origem);
            double tempoMs = (System.nanoTime() - inicio) / 1_000_000.0;
            impressora.imprimirResultadoBellmanFord(resultado, tempoMs);

            exportador.registrarExecucao(
                    nomeBaseArquivo,
                    "BELLMAN_FORD",
                    rep.name(),
                    TipoGrafo.DIRECIONADO.name(),
                    grafo.numVertices(),
                    grafo.numArestas(),
                    origem,
                    tempoMs,
                    "ciclo_negativo=" + resultado.isTemCicloNegativo()
            );
        }
        System.out.println("\nArquivos CSV salvos na pasta output/");
    }

    private static void executarDijkstra(
            String caminhoArquivo,
            int origem,
            LeituraGrafo leitor,
            EscritaGrafo impressora,
            ExportadorCSV exportador
    ) throws IOException {
        String nomeBaseArquivo = new File(caminhoArquivo).getName();
        for (Representacao rep : Representacao.values()) {
            IGrafo grafo = leitor.lerArquivo(caminhoArquivo, rep, TipoGrafo.DIRECIONADO);
            System.out.println("\n--------------------------------------------------");
            System.out.println("ALGORITMO: Dijkstra | REP: " + rep + " | TIPO: DIRECIONADO");
            impressora.imprimirResumoGrafo(grafo);

            long inicio = System.nanoTime();
            ResultadoDkjistra resultado = new Dkjistra().executar(grafo, origem);
            double tempoMs = (System.nanoTime() - inicio) / 1_000_000.0;
            impressora.imprimirResultadoDijkstra(resultado, tempoMs);

            exportador.registrarExecucao(
                    nomeBaseArquivo,
                    "DIJKSTRA",
                    rep.name(),
                    TipoGrafo.DIRECIONADO.name(),
                    grafo.numVertices(),
                    grafo.numArestas(),
                    origem,
                    tempoMs,
                    "distancias=" + java.util.Arrays.toString(resultado.getDistancia())
            );
        }
        System.out.println("\nArquivos CSV salvos na pasta output/");
    }

    private static int lerInteiroComPadrao(Scanner scanner, int padrao) {
        if (!scanner.hasNextLine()) {
            return padrao;
        }
        String linha = scanner.nextLine().trim();
        if (linha.isEmpty()) {
            return padrao;
        }
        try {
            return Integer.parseInt(linha);
        } catch (NumberFormatException e) {
            return padrao;
        }
    }
}