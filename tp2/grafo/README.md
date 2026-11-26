# Estruturas de grafo

Este pacote contém o modelo de grafos usado pelo TP2. Ele implementa o modelo de classes do [diagrama UML](../../../docs/Graph.pdf) e fornece a base para os algoritmos e experimentos do projeto.

As classes deste pacote representam os dados e permitem consultá-los. Os algoritmos — como BFS, DFS, Dijkstra e Bellman-Ford — devem receber um `IGrafo`; assim, podem operar sobre lista ou matriz de adjacência sem depender da representação concreta.

## Classes

| Classe | Responsabilidade |
| --- | --- |
| `IGrafo` | Contrato comum para consultar e alterar um grafo. |
| `TipoGrafo` | Define se as arestas são `DIRECIONADO` ou `NAO_DIRECIONADO`. |
| `GrafoListaAdjacencia` | Guarda as arestas nas listas de vizinhos de cada vértice. |
| `GrafoMatrizAdjacencia` | Guarda as arestas em uma matriz de pares de vértices. |
| `Vertice` | Identifica um vértice e permite associar a ele um rótulo. |
| `Aresta` | Liga os vértices de origem e destino e armazena o peso. |

As duas implementações concretas aceitam a quantidade de vértices e o tipo do grafo no construtor. Ambas implementam `IGrafo`, portanto podem ser usadas pelo mesmo algoritmo.

## Qual representação usar

- **Lista de adjacência:** costuma ser mais econômica para grafos esparsos. Percorrer os vizinhos de um vértice acessa diretamente suas arestas.
- **Matriz de adjacência:** usa espaço proporcional a `V × V` e permite verificar diretamente se há aresta entre dois vértices. É adequada para comparar o comportamento em grafos densos.

Essa diferença é central no TP2: o projeto compara as representações em diferentes tamanhos e densidades de grafos. Ao comparar algoritmos, use o mesmo grafo de entrada e o mesmo tipo de grafo para ambas as representações.

## Exemplo de uso

Os índices dos vértices começam em `0`. O método sem peso cria uma aresta de peso `1.0`; use a sobrecarga com `double` para definir outro peso.

```java
import grafo.Aresta;
import grafo.GrafoListaAdjacencia;
import grafo.IGrafo;
import grafo.TipoGrafo;

public class ExemploGrafo {
    public static void main(String[] args) {
        IGrafo grafo = new GrafoListaAdjacencia(4, TipoGrafo.DIRECIONADO);

        grafo.adicionarAresta(0, 1);       // peso padrão: 1.0
        grafo.adicionarAresta(1, 2, 2.5); // aresta ponderada

        System.out.println("Vértices: " + grafo.numVertices());
        System.out.println("Arestas: " + grafo.numArestas());

        for (Aresta aresta : grafo.adjacentes(1)) {
            System.out.println(aresta);
        }
    }
}
```

Para usar a matriz, troque `GrafoListaAdjacencia` por `GrafoMatrizAdjacencia`. O restante do código pode continuar tipado como `IGrafo`.

```java
IGrafo grafo = new GrafoMatrizAdjacencia(4, TipoGrafo.NAO_DIRECIONADO);
grafo.adicionarAresta(0, 1, 8.0);
```

## Regras do modelo

- Em grafos **dirigidos**, `adicionarAresta(u, v, peso)` cria a ligação de `u` para `v`; ela não cria automaticamente a ligação de `v` para `u`.
- Em grafos **não dirigidos**, a ligação pode ser consultada pelos dois sentidos. `numArestas()` conta cada ligação lógica uma vez.
- Adicionar novamente uma aresta entre o mesmo par substitui seu peso anterior. Em grafo não dirigido, isso também vale se o par for informado na ordem inversa.
- Um laço (`u == v`) é permitido e aparece uma vez na lista de adjacências.
- As listas retornadas por `vertices()`, `arestas()` e `adjacentes(u)` não podem ser alteradas pelo chamador. Para mudar o grafo, use os métodos de `IGrafo`.
- Índices inválidos lançam `IndexOutOfBoundsException`. Uma quantidade negativa de vértices ou tipo nulo no construtor lança `IllegalArgumentException`.
- Para `Vertice`, o rótulo padrão é o próprio índice convertido em texto. Ele pode ser consultado ou alterado com `getRotulo()` e `setRotulo()`.

## Operações de `IGrafo`

| Método | Uso |
| --- | --- |
| `tipo()` | Consulta se o grafo é dirigido ou não dirigido. |
| `vertices()` / `arestas()` | Consulta as listas de vértices e de arestas lógicas. |
| `adicionarAresta(u, v)` | Adiciona aresta com peso padrão `1.0`. |
| `adicionarAresta(u, v, peso)` | Adiciona aresta com peso informado. |
| `obterAresta(u, v)` | Obtém a aresta entre os vértices ou `null` se não existir. |
| `adjacentes(u)` | Obtém as arestas que saem de `u` (ou suas vizinhas em grafo não dirigido). |
| `numVertices()` / `numArestas()` | Consulta a quantidade de vértices e arestas. |

## Leitura dos arquivos do experimento

Os arquivos de amostra do enunciado usam identificadores iniciando em `1`. A classe `io.LeituraGrafo` faz a conversão para os índices `0` a `V - 1` usados pelas estruturas deste pacote e permite escolher a representação e o tipo do grafo:

```java
IGrafo grafo = new io.LeituraGrafo().lerArquivo(
    "tp2/samples/sample100-1980.gr",
    io.LeituraGrafo.Representacao.LISTA_ADJACENCIA,
    TipoGrafo.DIRECIONADO
);
```

O tipo deve corresponder ao algoritmo e ao experimento em execução. Para o experimento principal de caminhos mínimos, o enunciado requer Bellman-Ford e Dijkstra nas duas representações, comparando grafos esparsos e densos.
