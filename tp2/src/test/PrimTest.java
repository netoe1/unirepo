package test;

import algoritmos.Prim;
import algoritmos.ResultadoArvoreGeradoraMinima;
import grafo.GrafoListaAdjacencia;
import grafo.GrafoMatrizAdjacencia;
import grafo.IGrafo;
import grafo.TipoGrafo;

public final class PrimTest {

    public static void main(String[] args) {
        for (boolean usarMatriz : new boolean[] {false, true}) {
            testaArvoreGeradoraMinima(usarMatriz);
            testaOrigemAlternativa(usarMatriz);
            testaFlorestaGeradoraMinima(usarMatriz);
            testaGrafoVazioEUnitario(usarMatriz);
            testaLaco(usarMatriz);
        }
        testaValidacoes();
        System.out.println("PrimTest: todos os testes passaram.");
    }

    private static void testaArvoreGeradoraMinima(boolean usarMatriz) {
        IGrafo grafo = novoGrafo(usarMatriz, 5);
        adicionarArestasConectadas(grafo);
        ResultadoArvoreGeradoraMinima resultado = new Prim().executar(grafo, 0);
        verifica(resultado.isConexo(), "grafo conectado deve produzir uma arvore");
        verifica(resultado.getArestas().size() == 4, "arvore deve ter n-1 arestas");
        verifica(resultado.getPesoTotal() == 22.0, "peso da MST deve ser 22");
    }

    private static void testaOrigemAlternativa(boolean usarMatriz) {
        IGrafo grafo = novoGrafo(usarMatriz, 5);
        adicionarArestasConectadas(grafo);
        ResultadoArvoreGeradoraMinima resultado = new Prim().executar(grafo, 4);
        verifica(resultado.isConexo(), "grafo conectado deve ser conexo independentemente da origem");
        verifica(resultado.getPesoTotal() == 22.0, "peso da MST nao deve depender da origem");
    }

    private static void testaFlorestaGeradoraMinima(boolean usarMatriz) {
        IGrafo grafo = novoGrafo(usarMatriz, 6);
        grafo.adicionarAresta(0, 1, 3.0);
        grafo.adicionarAresta(3, 4, 2.0);
        grafo.adicionarAresta(4, 5, 7.0);

        ResultadoArvoreGeradoraMinima resultado = new Prim().executar(grafo, 0);
        verifica(!resultado.isConexo(), "grafo desconexo deve produzir uma floresta");
        verifica(resultado.getNumeroComponentes() == 3, "devem existir tres componentes");
        verifica(resultado.getArestas().size() == 3, "floresta deve selecionar tres arestas");
        verifica(resultado.getPesoTotal() == 12.0, "peso da floresta deve ser 12");
    }

    private static void testaGrafoVazioEUnitario(boolean usarMatriz) {
        ResultadoArvoreGeradoraMinima vazio = new Prim().executar(novoGrafo(usarMatriz, 0));
        verifica(vazio.getArestas().isEmpty(), "grafo vazio nao deve ter arestas");
        verifica(vazio.getNumeroComponentes() == 0, "grafo vazio nao deve ter componentes");

        ResultadoArvoreGeradoraMinima unitario = new Prim().executar(novoGrafo(usarMatriz, 1));
        verifica(unitario.isConexo(), "grafo unitario deve ser conexo");
        verifica(unitario.getArestas().isEmpty(), "grafo unitario nao deve ter arestas");
    }

    private static void testaLaco(boolean usarMatriz) {
        IGrafo grafo = novoGrafo(usarMatriz, 3);
        grafo.adicionarAresta(0, 0, -5.0);
        grafo.adicionarAresta(0, 1, 1.0);
        ResultadoArvoreGeradoraMinima resultado = new Prim().executar(grafo);
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
        Prim algoritmo = new Prim();
        esperaExcecao(IllegalArgumentException.class, () -> algoritmo.executar(null));
        esperaExcecao(IllegalArgumentException.class,
                () -> algoritmo.executar(new GrafoListaAdjacencia(2, TipoGrafo.DIRECIONADO)));
        esperaExcecao(IndexOutOfBoundsException.class,
                () -> algoritmo.executar(new GrafoListaAdjacencia(2, TipoGrafo.NAO_DIRECIONADO), 2));

        IGrafo pesoInvalido = new GrafoListaAdjacencia(2, TipoGrafo.NAO_DIRECIONADO);
        pesoInvalido.adicionarAresta(0, 1, Double.POSITIVE_INFINITY);
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
