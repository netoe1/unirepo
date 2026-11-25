package test;

import grafo.Aresta;
import grafo.GrafoMatrizAdjacencia;
import grafo.IGrafo;
import grafo.TipoGrafo;

public final class GrafoMatrizAdjacenciaTest {

    public static void main(String[] args) {
        testaGrafoDirigido();
        testaGrafoNaoDirigido();
        testaGrafoVazioEDeUmVertice();
        testaValidacoes();
        testaListasSomenteLeitura();
        System.out.println("GrafoMatrizAdjacenciaTest: todos os testes passaram.");
    }

    private static void testaGrafoDirigido() {
        IGrafo grafo = new GrafoMatrizAdjacencia(4, TipoGrafo.DIRECIONADO);

        grafo.adicionarAresta(0, 1);
        grafo.adicionarAresta(1, 2, 2.5);

        verifica(grafo.tipo() == TipoGrafo.DIRECIONADO, "o tipo dirigido deve ser preservado");
        verifica(grafo.numVertices() == 4, "a quantidade de vértices deve ser preservada");
        verifica(grafo.numArestas() == 2, "devem existir duas arestas dirigidas");
        verificaPeso(grafo.obterAresta(0, 1), 1.0, "aresta sem peso deve usar peso 1.0");
        verificaPeso(grafo.obterAresta(1, 2), 2.5, "aresta ponderada deve preservar seu peso");
        verifica(grafo.obterAresta(1, 0) == null, "aresta dirigida não deve criar a aresta inversa");
        verifica(grafo.adjacentes(1).size() == 1, "adjacentes deve listar apenas arestas de saída");

        grafo.adicionarAresta(1, 0, 7.0);
        grafo.adicionarAresta(0, 1, 4.0);

        verifica(grafo.numArestas() == 3, "substituir uma direção não deve remover a direção oposta");
        verificaPeso(grafo.obterAresta(0, 1), 4.0, "adicionar o mesmo par deve substituir o peso");
        verificaPeso(grafo.obterAresta(1, 0), 7.0, "a aresta inversa deve manter seu próprio peso");
    }

    private static void testaGrafoNaoDirigido() {
        IGrafo grafo = new GrafoMatrizAdjacencia(3, TipoGrafo.NAO_DIRECIONADO);

        grafo.adicionarAresta(0, 1, 2.0);
        verifica(grafo.numArestas() == 1, "aresta não dirigida deve ser contada uma vez");
        verificaPeso(grafo.obterAresta(0, 1), 2.0, "aresta deve estar disponível no sentido informado");
        verificaPeso(grafo.obterAresta(1, 0), 2.0, "aresta não dirigida deve estar disponível no sentido inverso");

        grafo.adicionarAresta(1, 0, 3.5);
        verifica(grafo.numArestas() == 1, "adicionar o mesmo par invertido deve substituir a aresta");
        verificaPeso(grafo.obterAresta(0, 1), 3.5, "substituição deve atualizar o peso nos dois sentidos");
        verificaPeso(grafo.obterAresta(1, 0), 3.5, "substituição deve atualizar o peso nos dois sentidos");

        grafo.adicionarAresta(2, 2, 5.0);
        verifica(grafo.numArestas() == 2, "laço deve contar como uma aresta lógica");
        verifica(grafo.adjacentes(2).size() == 1, "laço não dirigido deve aparecer uma vez na adjacência");
        grafo.adicionarAresta(2, 2, 6.0);
        verifica(grafo.numArestas() == 2, "substituir um laço não deve duplicá-lo");
        verifica(grafo.adjacentes(2).size() == 1, "substituir um laço não deve duplicar a adjacência");
        verificaPeso(grafo.obterAresta(2, 2), 6.0, "substituição do laço deve atualizar o peso");
    }

    private static void testaValidacoes() {
        esperaExcecao(IllegalArgumentException.class,
                () -> new GrafoMatrizAdjacencia(-1, TipoGrafo.DIRECIONADO));
        esperaExcecao(IllegalArgumentException.class,
                () -> new GrafoMatrizAdjacencia(1, null));

        IGrafo grafo = new GrafoMatrizAdjacencia(2, TipoGrafo.DIRECIONADO);
        esperaExcecao(IndexOutOfBoundsException.class, () -> grafo.adicionarAresta(0, 2));
        esperaExcecao(IndexOutOfBoundsException.class, () -> grafo.obterAresta(-1, 0));
        esperaExcecao(IndexOutOfBoundsException.class, () -> grafo.adjacentes(2));
    }

    private static void testaGrafoVazioEDeUmVertice() {
        IGrafo vazio = new GrafoMatrizAdjacencia(0, TipoGrafo.DIRECIONADO);
        verifica(vazio.numVertices() == 0, "grafo vazio deve ter zero vértices");
        verifica(vazio.numArestas() == 0, "grafo vazio deve ter zero arestas");

        IGrafo unico = new GrafoMatrizAdjacencia(1, TipoGrafo.NAO_DIRECIONADO);
        verifica(unico.numVertices() == 1, "grafo unitário deve ter um vértice");
        verifica(unico.numArestas() == 0, "grafo unitário começa sem arestas");
        unico.adicionarAresta(0, 0);
        verifica(unico.numArestas() == 1, "laço no único vértice deve contar uma vez");
        verifica(unico.adjacentes(0).size() == 1, "laço no único vértice deve aparecer uma vez");
    }

    private static void testaListasSomenteLeitura() {
        IGrafo grafo = new GrafoMatrizAdjacencia(2, TipoGrafo.DIRECIONADO);
        grafo.adicionarAresta(0, 1);

        esperaExcecao(UnsupportedOperationException.class, () -> grafo.vertices().clear());
        esperaExcecao(UnsupportedOperationException.class, () -> grafo.arestas().clear());
        esperaExcecao(UnsupportedOperationException.class, () -> grafo.adjacentes(0).clear());
    }

    private static void verificaPeso(Aresta aresta, double esperado, String mensagem) {
        verifica(aresta != null, mensagem + " (aresta não encontrada)");
        verifica(Double.compare(aresta.getPeso(), esperado) == 0, mensagem);
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
