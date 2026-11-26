# TP2 — Algoritmos e Classificação de Dados

Implementação em Java de representações de grafos por lista e matriz de adjacência, nove algoritmos e um experimento de desempenho de caminhos mínimos. O enunciado está em [`docs/TP2 - Trabalho Prático sobre  GRAFOS.md`](<docs/TP2 - Trabalho Prático sobre  GRAFOS.md>).

## Estrutura

```text
tp2/
	Main.java                       menu interativo
	App.java                        encaminha para Main
	algoritmos/                     implementações dos nove algoritmos
	grafo/                          interface, vértices, arestas e representações
	io/                             leitura .gr, impressão e exportação CSV
	samples/demo/                   exemplos pequenos do menu
	samples/                        15 grafos GTgraph do experimento
	test/                           testes Java e scripts de validação
	output/                         CSVs, gráficos e estatísticas
```

Os algoritmos disponíveis são BFS, DFS, ordenação topológica, Kruskal, Prim, Bellman-Ford, Dijkstra, Floyd-Warshall e Ford-Fulkerson. A interface de grafo permite que os algoritmos usem lista ou matriz de adjacência.

## Requisitos

- JDK compatível com switch expressions, com `java` e `javac` no `PATH` (Java 17 ou superior recomendado).
- Para gerar gráficos: Python 3 e `pandas`, `matplotlib` e `numpy`.

## Compilar e executar

Execute os comandos na raiz do repositório:

```bash
mkdir -p out/tp2
find tp2 -name '*.java' -print0 | xargs -0 javac -encoding UTF-8 -d out/tp2
java -cp out/tp2 Main
```

O menu pede um vértice de origem (padrão `0`) e apresenta as opções 1 a 9 para os algoritmos; a opção 10 altera a origem e 0 encerra. Em cada algoritmo, pressione Enter para usar o exemplo de `tp2/samples/demo/`, digite somente o nome de um `.gr` em `tp2/samples/` ou informe um caminho explícito. Os índices digitados no menu começam em zero.

Os arquivos GTgraph usam linhas `p sp V E` para declarar vértices e arestas e `a u v peso` para cada arco. Os identificadores de vértice no arquivo começam em 1; `LeituraGrafo` converte-os para índices Java começando em 0. O menu imprime uma visão resumida do grafo (até 20 vértices e 10 adjacências por vértice), o resultado e o tempo.

O menu acrescenta os resultados em `tp2/output/resultados_algoritmos.csv`. BFS e DFS não geram mais CSVs de percurso separados.

## Experimento de caminhos mínimos

O experimento principal compara Bellman-Ford e Dijkstra nas 15 amostras, nas representações por lista e matriz, com 10 repetições por configuração:

```bash
bash tp2/experimento_caminhos_minimos.sh
```

São 300 medições por algoritmo, 600 no total. O executor também faz um aquecimento não registrado por algoritmo e configuração. Cada chamada cronometrada mede somente o algoritmo; leitura e preparação ficam fora do intervalo. Os resultados vão para `tp2/output/<timestamp>/experimento_caminhos_minimos.csv`, sem substituir coletas anteriores.

A entrada do experimento é tratada como dígrafo e usa origem 0. Arestas paralelas entre o mesmo par dirigido são normalizadas pelo menor peso, preservando os caminhos mínimos. O CSV registra tanto as arestas/densidades declaradas no arquivo quanto as efetivamente carregadas.

Para validar rapidamente com um grafo e uma repetição:

```bash
TP2_LIMITE_GRAFOS=1 TP2_REPETICOES=1 bash tp2/experimento_caminhos_minimos.sh
```

## Boxplots

No Ubuntu, instale os pacotes Python necessários:

```bash
sudo apt update
sudo apt install python3-pandas python3-matplotlib python3-numpy
```

Depois gere os gráficos e a tabela estatística:

```bash
python3 tp2/output/boxplot.py
```

O script seleciona o CSV experimental mais recente, verifica as 600 medições e produz um boxplot separado para cada algoritmo, comparando lista e matriz por tamanho e densidade. O eixo mostra a densidade nominal e, entre parênteses, a densidade efetiva do grafo carregado. Os arquivos são salvos em `tp2/output/boxplots/`: PNG, PDF e `estatisticas_resumo_tp2.csv`.

As dependências também estão listadas em [`tp2/output/requirements.txt`](tp2/output/requirements.txt), para ambientes Python que usam `pip`.

## Testes

Os testes são classes Java sem JUnit. Cada script compila em uma pasta temporária e retorna erro se uma compilação ou verificação falhar. Para executar todas as suítes a partir da raiz:

```bash
set -e
for teste in tp2/test/corretude_*.sh; do bash "$teste"; done
```

As suítes cobrem as estruturas, os algoritmos, caminhos mínimos, fluxo máximo e a demonstração ponta a ponta do menu. Para executar apenas uma suíte, por exemplo:

```bash
bash tp2/test/corretude_caminhos_minimos.sh
```

## Referências

- [Enunciado do TP2](<docs/TP2 - Trabalho Prático sobre  GRAFOS.md>)
- [Documentação das estruturas de grafo](tp2/grafo/README.md)
- [Documentação dos testes](tp2/test/README.md)

Este repositório está sob a licença GNU General Public License v3.0. Consulte [`LICENSE`](LICENSE) para os termos completos.
