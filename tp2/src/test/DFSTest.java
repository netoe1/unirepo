package test;

import algoritmos.DFS;
import algoritmos.DFS.ResultadoDFS;
import grafo.GrafoListaAdjacencia;
import grafo.GrafoMatrizAdjacencia;
import grafo.IGrafo;
import grafo.TipoGrafo;
import java.util.Arrays;
import java.util.List;

public final class DFSTest {

    public static void main(String[] args) {
        for (boolean usarMatriz : new boolean[] {false, true}) {
            for (TipoGrafo tipo : TipoGrafo.values()) {
                testaFlorestaTemposEPais(usarMatriz, tipo);
                testaDeteccaoDeCiclo(usarMatriz, tipo);
            }
        }
        testaValidacoes();
        testaGrafoVazio();
        System.out.println("DFSTest: todos os testes passaram.");
    }

    private static void testaFlorestaTemposEPais(boolean usarMatriz, TipoGrafo tipo) {
        IGrafo grafo = novoGrafo(usarMatriz, 6, tipo);
        grafo.adicionarAresta(0, 1);
        grafo.adicionarAresta(0, 2);
        grafo.adicionarAresta(1, 3);
        grafo.adicionarAresta(3, 4);

        DFS dfs = new DFS();
        ResultadoDFS parcial = dfs.executarDeOrigem(grafo, 0);
        verifica(Arrays.equals(parcial.getVisitado(), new boolean[] {true, true, true, true, true, false}),
                "DFS a partir da origem deve visitar somente os vértices alcançáveis");
        verifica(!parcial.isTemCiclo(), "árvore sem ciclos não deve ser marcada cíclica");
        verificaTemposEArvore(grafo, parcial);

        ResultadoDFS completo = dfs.executarCompleto(grafo);
        verifica(Arrays.equals(completo.getVisitado(), new boolean[] {true, true, true, true, true, true}),
                "DFS completo deve incluir componentes desconectados e vértices isolados");
        verifica(completo.getOrdemPreOrdem().size() == 6,
                "pré-ordem completa deve conter cada vértice uma vez");
        verifica(!completo.isTemCiclo(), "floresta sem ciclos não deve ser marcada cíclica");
        verificaTemposEArvore(grafo, completo);
    }

    private static void testaDeteccaoDeCiclo(boolean usarMatriz, TipoGrafo tipo) {
        IGrafo cíclico = novoGrafo(usarMatriz, 3, tipo);
        cíclico.adicionarAresta(0, 1);
        cíclico.adicionarAresta(1, 2);
        cíclico.adicionarAresta(2, 0);

        DFS dfs = new DFS();
        verifica(dfs.executarCompleto(cíclico).isTemCiclo(),
                "DFS deve detectar ciclo dirigido ou não dirigido");

        IGrafo acíclico = novoGrafo(usarMatriz, 3, tipo);
        acíclico.adicionarAresta(0, 1);
        acíclico.adicionarAresta(0, 2);
        if (tipo == TipoGrafo.DIRECIONADO) {
            acíclico.adicionarAresta(1, 2); // aresta para vértice já finalizado não forma ciclo
        }
        verifica(!dfs.executarCompleto(acíclico).isTemCiclo(),
                "resultado de ciclo não deve vazar para a próxima execução");
    }

    private static void testaValidacoes() {
        DFS dfs = new DFS();
        esperaExcecao(IllegalArgumentException.class, () -> dfs.executarDeOrigem(null, 0));
        esperaExcecao(IllegalArgumentException.class, () -> dfs.executarCompleto(null));

        IGrafo grafo = new GrafoListaAdjacencia(2, TipoGrafo.DIRECIONADO);
        esperaExcecao(IndexOutOfBoundsException.class, () -> dfs.executarDeOrigem(grafo, -1));
        esperaExcecao(IndexOutOfBoundsException.class, () -> dfs.executarDeOrigem(grafo, 2));
    }

    private static void testaGrafoVazio() {
        ResultadoDFS resultado = new DFS().executarCompleto(
                new GrafoMatrizAdjacencia(0, TipoGrafo.DIRECIONADO));
        verifica(resultado.getOrdemPreOrdem().isEmpty(), "DFS completo em grafo vazio deve retornar ordem vazia");
        verifica(!resultado.isTemCiclo(), "grafo vazio não deve conter ciclo");
    }

    private static void verificaTemposEArvore(IGrafo grafo, ResultadoDFS resultado) {
        boolean[] visitado = resultado.getVisitado();
        int[] pai = resultado.getPai();
        int[] descoberta = resultado.getDescoberta();
        int[] finalizacao = resultado.getFinalizacao();
        List<Integer> ordem = resultado.getOrdemPreOrdem();
        boolean[] tempoUsado = new boolean[2 * visitado.length + 1];

        verifica(ordem.size() == contar(visitado), "pré-ordem deve ter um registro por vértice visitado");
        int tempoAnteriorNaOrdem = 0;
        for (int u : ordem) {
            verifica(visitado[u], "pré-ordem não pode conter vértice não visitado");
            verifica(descoberta[u] > tempoAnteriorNaOrdem,
                    "ordem pré-fixada deve seguir os tempos de descoberta");
            tempoAnteriorNaOrdem = descoberta[u];
        }

        for (int u = 0; u < visitado.length; u++) {
            if (!visitado[u]) {
                verifica(pai[u] == -1 && descoberta[u] == -1 && finalizacao[u] == -1,
                        "vértice não visitado deve manter pai e tempos sentinela");
                continue;
            }

            verifica(descoberta[u] > 0 && finalizacao[u] > descoberta[u],
                    "tempos de descoberta/finalização devem ser válidos");
            verifica(descoberta[u] < tempoUsado.length && finalizacao[u] < tempoUsado.length,
                    "tempos devem ficar dentro de 1..2V");
            verifica(!tempoUsado[descoberta[u]] && !tempoUsado[finalizacao[u]],
                    "cada evento DFS deve ter um tempo exclusivo");
            tempoUsado[descoberta[u]] = true;
            tempoUsado[finalizacao[u]] = true;

            if (pai[u] != -1) {
                int p = pai[u];
                verifica(grafo.obterAresta(p, u) != null,
                        "pai deve estar ligado ao vértice pela direção válida");
                verifica(descoberta[p] < descoberta[u] && finalizacao[u] < finalizacao[p],
                        "intervalos de descoberta/finalização devem ser aninhados pela árvore DFS");
            }
        }
    }

    private static int contar(boolean[] valores) {
        int quantidade = 0;
        for (boolean valor : valores) {
            if (valor) {
                quantidade++;
            }
        }
        return quantidade;
    }

    private static IGrafo novoGrafo(boolean usarMatriz, int numVertices, TipoGrafo tipo) {
        return usarMatriz
                ? new GrafoMatrizAdjacencia(numVertices, tipo)
                : new GrafoListaAdjacencia(numVertices, tipo);
    }

    private static void verifica(boolean condicao, String mensagem) {
        if (!condicao) {
            throw new AssertionError(mensagem);
        }
    }

    private static void esperaExcecao(Class<? extends Throwable> tipo, Runnable acao) {
        try {
            acao.run();
        } catch (Throwable erro) {
            if (tipo.isInstance(erro)) {
                return;
            }
            throw new AssertionError("Esperada " + tipo.getSimpleName() + ", recebida " + erro, erro);
        }
        throw new AssertionError("Esperada exceção " + tipo.getSimpleName() + ", mas nenhuma foi lançada");
    }
}
