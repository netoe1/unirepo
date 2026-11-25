package test;

import algoritmos.Kruskal;
import algoritmos.ResultadoArvoreGeradoraMinima;
import grafo.GrafoListaAdjacencia;
import grafo.GrafoMatrizAdjacencia;
import grafo.IGrafo;
import grafo.TipoGrafo;

public final class KruskalTest {

    public static void main(String[] args) {
        for (boolean usarMatriz : new boolean[] {false, true}) {
            testaArvoreGeradoraMinima(usarMatriz);
            testaFlorestaGeradoraMinima(usarMatriz);
            testaGrafoUnitarioEIsolados(usarMatriz);
            testaGrafoVazio(usarMatriz);
            testaLaco(usarMatriz);
        }
        testaValidacoes();
        System.out.println("KruskalTest: todos os testes passaram.");
    }

    private static void testaArvoreGeradoraMinima(boolean usarMatriz) {
        IGrafo grafo = novoGrafo(usarMatriz, 5);
        adicionarArestasConectadas(grafo);
        ResultadoArvoreGeradoraMinima resultado = new Kruskal().executar(grafo);
        verifica(resultado.isConexo(), "grafo conectado deve produzir uma arvore");
        verifica(resultado.getArestas().size() == 4, "arvore deve ter n-1 arestas");
        verifica(resultado.getPesoTotal() == 18.0, "peso da MST deve ser 18");
    }

    private static void testaFlorestaGeradoraMinima(boolean usarMatriz) {
        IGrafo grafo = novoGrafo(usarMatriz, 6);
        grafo.adicionarAresta(0, 1, 3.0);
        grafo.adicionarAresta(3, 4, 2.0);
        grafo.adicionarAresta(4, 5, 7.0);

        ResultadoArvoreGeradoraMinima resultado = new Kruskal().executar(grafo);
        verifica(!resultado.isConexo(), "grafo desconexo deve produzir uma floresta");
        verifica(resultado.getNumeroComponentes() == 3, "devem existir tres componentes");
        verifica(resultado.getArestas().size() == 3, "floresta deve selecionar tres arestas");
        verifica(resultado.getPesoTotal() == 12.0, "peso da floresta deve ser 12");
    }

    private static void testaGrafoUnitarioEIsolados(boolean usarMatriz) {
        IGrafo unitario = novoGrafo(usarMatriz, 1);
        ResultadoArvoreGeradoraMinima resultadoUnitario = new Kruskal().executar(unitario);
        verifica(resultadoUnitario.isConexo(), "grafo unitario deve ser conexo");
        verifica(resultadoUnitario.getArestas().isEmpty(), "grafo unitario nao deve ter arestas");

        IGrafo isolados = novoGrafo(usarMatriz, 3);
        ResultadoArvoreGeradoraMinima resultadoIsolados = new Kruskal().executar(isolados);
        verifica(resultadoIsolados.getNumeroComponentes() == 3, "vertices isolados devem ser componentes");
    }

    private static void testaGrafoVazio(boolean usarMatriz) {
        ResultadoArvoreGeradoraMinima resultado = new Kruskal().executar(novoGrafo(usarMatriz, 0));
        verifica(resultado.getArestas().isEmpty(), "grafo vazio nao deve ter arestas");
        verifica(resultado.getPesoTotal() == 0.0, "peso de grafo vazio deve ser zero");
        verifica(resultado.getNumeroComponentes() == 0, "grafo vazio nao deve ter componentes");
    }

    private static void testaLaco(boolean usarMatriz) {
        IGrafo grafo = novoGrafo(usarMatriz, 3);
        grafo.adicionarAresta(0, 0, -5.0);
        grafo.adicionarAresta(0, 1, 1.0);
        ResultadoArvoreGeradoraMinima resultado = new Kruskal().executar(grafo);
        verifica(resultado.getArestas().size() == 1, "laco nao deve ser selecionado");
    }

    private static void adicionarArestasConectadas(IGrafo grafo) {
        grafo.adicionarAresta(0, 1, 4.0);
        grafo.adicionarAresta(0, 2, 1.0);
        grafo.adicionarAresta(1, 2, 2.0);
        grafo.adicionarAresta(1, 3, 5.0);
        grafo.adicionarAresta(2, 3, 8.0);
        grafo.adicionarAresta(3, 4, 10.0);
    }

    private static void testaValidacoes() {
        Kruskal algoritmo = new Kruskal();
        esperaExcecao(IllegalArgumentException.class, () -> algoritmo.executar(null));
        esperaExcecao(IllegalArgumentException.class,
                () -> algoritmo.executar(new GrafoListaAdjacencia(2, TipoGrafo.DIRECIONADO)));

        IGrafo pesoInvalido = new GrafoListaAdjacencia(2, TipoGrafo.NAO_DIRECIONADO);
        pesoInvalido.adicionarAresta(0, 1, Double.NaN);
        esperaExcecao(IllegalArgumentException.class, () -> algoritmo.executar(pesoInvalido));
    }

    private static IGrafo novoGrafo(boolean usarMatriz, int vertices) {
        return usarMatriz
                ? new GrafoMatrizAdjacencia(vertices, TipoGrafo.NAO_DIRECIONADO)
                : new GrafoListaAdjacencia(vertices, TipoGrafo.NAO_DIRECIONADO);
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
