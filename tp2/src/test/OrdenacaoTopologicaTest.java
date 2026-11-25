package test;

import algoritmos.OrdenacaoTopologica;
import algoritmos.OrdenacaoTopologica.ResultadoOrdenacaoTopologica;
import grafo.GrafoListaAdjacencia;
import grafo.GrafoMatrizAdjacencia;
import grafo.IGrafo;
import grafo.TipoGrafo;
import grafo.Aresta;
import java.util.List;

public final class OrdenacaoTopologicaTest {

    public static void main(String[] args) {
        for (boolean usarMatriz : new boolean[] {false, true}) {
            testaOrdemValida(usarMatriz);
            testaGrafoDesconexo(usarMatriz);
            testaCiclo(usarMatriz);
            testaLaco(usarMatriz);
            testaCicloDesconectadoDaArvore(usarMatriz);
            testaGrafoVazio(usarMatriz);
            testaGrafoUnitario(usarMatriz);
        }
        testaValidacoes();
        System.out.println("OrdenacaoTopologicaTest: todos os testes passaram.");
    }

    private static void testaOrdemValida(boolean usarMatriz) {
        IGrafo grafo = novoGrafo(usarMatriz, 6);
        grafo.adicionarAresta(5, 2);
        grafo.adicionarAresta(5, 0);
        grafo.adicionarAresta(4, 0);
        grafo.adicionarAresta(4, 1);
        grafo.adicionarAresta(2, 3);
        grafo.adicionarAresta(3, 1);

        ResultadoOrdenacaoTopologica resultado = new OrdenacaoTopologica().executar(grafo);
        verifica(!resultado.isTemCiclo(), "grafo aciclico nao deve detectar ciclo");
        verifica(resultado.getOrdem().size() == 6, "todos os vertices devem aparecer na ordenacao");
        verificaTodasAsDependencias(resultado.getOrdem(), grafo);
    }

    private static void testaGrafoDesconexo(boolean usarMatriz) {
        IGrafo grafo = novoGrafo(usarMatriz, 5);
        grafo.adicionarAresta(0, 1);
        grafo.adicionarAresta(3, 4);

        ResultadoOrdenacaoTopologica resultado = new OrdenacaoTopologica().executar(grafo);
        verifica(!resultado.isTemCiclo(), "grafo desconexo aciclico nao deve detectar ciclo");
        verifica(resultado.getOrdem().size() == 5, "todos os vertices desconexos devem aparecer");
        verificaTodasAsDependencias(resultado.getOrdem(), grafo);
    }

    private static void testaCiclo(boolean usarMatriz) {
        IGrafo grafo = novoGrafo(usarMatriz, 3);
        grafo.adicionarAresta(0, 1);
        grafo.adicionarAresta(1, 2);
        grafo.adicionarAresta(2, 0);

        ResultadoOrdenacaoTopologica resultado = new OrdenacaoTopologica().executar(grafo);
        verifica(resultado.isTemCiclo(), "ciclo deve ser detectado");
        verifica(resultado.getOrdem().size() < 3, "vertices do ciclo nao podem formar uma ordenacao completa");
    }

    private static void testaLaco(boolean usarMatriz) {
        IGrafo grafo = novoGrafo(usarMatriz, 2);
        grafo.adicionarAresta(0, 1);
        grafo.adicionarAresta(1, 1);

        ResultadoOrdenacaoTopologica resultado = new OrdenacaoTopologica().executar(grafo);
        verifica(resultado.isTemCiclo(), "laco deve ser detectado como ciclo");
        verifica(resultado.getOrdem().equals(List.of(0)), "vertice conectado ao laco nao deve entrar na ordenacao completa");
    }

    private static void testaCicloDesconectadoDaArvore(boolean usarMatriz) {
        IGrafo grafo = novoGrafo(usarMatriz, 5);
        grafo.adicionarAresta(0, 1);
        grafo.adicionarAresta(2, 3);
        grafo.adicionarAresta(3, 4);
        grafo.adicionarAresta(4, 2);

        ResultadoOrdenacaoTopologica resultado = new OrdenacaoTopologica().executar(grafo);
        verifica(resultado.isTemCiclo(), "ciclo desconectado deve ser detectado");
        verifica(resultado.getOrdem().contains(0) && resultado.getOrdem().contains(1),
                "componentes aciclicos devem ser processados antes da deteccao do ciclo");
        verifica(resultado.getOrdem().size() == 2,
                "a ordem parcial deve conter apenas vertices fora do componente ciclico");
    }

    private static void testaGrafoVazio(boolean usarMatriz) {
        IGrafo grafo = novoGrafo(usarMatriz, 0);
        ResultadoOrdenacaoTopologica resultado = new OrdenacaoTopologica().executar(grafo);
        verifica(!resultado.isTemCiclo(), "grafo vazio nao deve ter ciclo");
        verifica(resultado.getOrdem().isEmpty(), "ordenacao de grafo vazio deve ser vazia");
    }

    private static void testaGrafoUnitario(boolean usarMatriz) {
        IGrafo grafo = novoGrafo(usarMatriz, 1);
        ResultadoOrdenacaoTopologica resultado = new OrdenacaoTopologica().executar(grafo);
        verifica(!resultado.isTemCiclo(), "grafo unitario nao deve ter ciclo");
        verifica(resultado.getOrdem().equals(List.of(0)), "grafo unitario deve ter ordem [0]");
    }

    private static void verificaTodasAsDependencias(List<Integer> ordem, IGrafo grafo) {
        int[] posicao = new int[grafo.numVertices()];
        java.util.Arrays.fill(posicao, -1);
        for (int i = 0; i < ordem.size(); i++) {
            posicao[ordem.get(i)] = i;
        }
        for (int u = 0; u < grafo.numVertices(); u++) {
            for (Aresta aresta : grafo.adjacentes(u)) {
                int v = aresta.getV().getId();
                verifica(posicao[u] >= 0 && posicao[v] >= 0,
                        "todos os vertices devem possuir posicao na ordenacao");
                verifica(posicao[u] < posicao[v],
                        "origem deve preceder destino em toda aresta");
            }
        }
    }

    private static void testaValidacoes() {
        OrdenacaoTopologica algoritmo = new OrdenacaoTopologica();
        esperaExcecao(IllegalArgumentException.class, () -> algoritmo.executar(null));
        esperaExcecao(IllegalArgumentException.class,
                () -> algoritmo.executar(new GrafoListaAdjacencia(2, TipoGrafo.NAO_DIRECIONADO)));
    }

    private static IGrafo novoGrafo(boolean usarMatriz, int vertices) {
        return usarMatriz
                ? new GrafoMatrizAdjacencia(vertices, TipoGrafo.DIRECIONADO)
                : new GrafoListaAdjacencia(vertices, TipoGrafo.DIRECIONADO);
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
        throw new AssertionError("Esperada excecao " + tipo.getSimpleName() + ", mas nenhuma foi lancada");
    }
}
