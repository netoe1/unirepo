# Repositório de trabalhos — Algoritmos e Classificação de Dados

Este repositório é um monorepo para os trabalhos da disciplina **Algoritmos e Classificação de Dados (ACD)**. Cada trabalho é desenvolvido em uma branch própria. Consulte a branch ativa para identificar o conteúdo em desenvolvimento; não presuma que todos os trabalhos estarão presentes ao mesmo tempo na mesma branch.

## Trabalho atual

Na branch `acd/tp2/main`, o foco é o **TP2**, sobre representações de grafos e algoritmos de grafos em Java, com experimentos para comparar desempenho.

- Código do TP2: [`tp2/`](tp2/)
- Enunciado: [`docs/TP2 - Trabalho Prático sobre  GRAFOS.md`](<docs/TP2 - Trabalho Prático sobre  GRAFOS.md>)
- Grafos de amostra: [`tp2/src/samples/`](tp2/src/samples/)
- Exemplos pequenos para demonstração: [`tp2/src/samples/demo/`](tp2/src/samples/demo/)
- Documentação das estruturas: [`tp2/src/grafo/README.md`](tp2/src/grafo/README.md)
- Documentação dos testes: [`tp2/src/test/README.md`](tp2/src/test/README.md)
- Instruções para contribuir e revisar: [`AGENTS.md`](AGENTS.md)

O projeto implementa grafos dirigidos e não dirigidos nas representações por lista e matriz de adjacência. Os algoritmos disponíveis são:

| Funcionalidade | Status |
| --- | --- |
| Busca em largura (BFS) | Implementada e testada |
| Busca em profundidade (DFS) | Implementada e testada |
| Ordenação topológica | Implementada e testada |
| Kruskal | Implementado e testado |
| Prim | Implementado e testado |
| Bellman-Ford | Implementado e testado |
| Dijkstra | Implementado e testado |
| Floyd-Warshall | Implementado e testado |
| Ford-Fulkerson | Implementado e testado |
| Experimento completo com boxplots e artigo | Em desenvolvimento |

## Requisitos

- JDK com `java` e `javac` disponíveis no `PATH`.
- Git para obter o repositório e alternar entre branches.

## Compilar e executar

Na raiz do repositório, compile todo o código Java do TP2:

```bash
mkdir -p out/tp2
find tp2/src -name '*.java' -print0 | xargs -0 javac -encoding UTF-8 -d out/tp2
```

Execute o programa interativo:

```bash
java -cp out/tp2 Main
```

O menu permite executar cada algoritmo com um exemplo pequeno recomendado. Pressione Enter no prompt de arquivo para usar o exemplo sugerido ou informe outro caminho GTgraph. Os vértices informados pelo programa usam índices Java começando em zero. A estrutura é exibida com até 20 vértices e 10 adjacências por vértice; itens omitidos são indicados. Remova `out/` quando quiser limpar os arquivos compilados.

## Testes

O TP2 usa testes Java simples, sem JUnit ou dependências externas. Execute os scripts a partir da raiz do repositório:

```bash
bash tp2/src/test/corretude_est_grafos.spec.sh
bash tp2/src/test/corretude_algoritmos_grafos.sh
bash tp2/src/test/corretude_ordenacao_topologica.sh
bash tp2/src/test/corretude_caminhos_minimos.sh
bash tp2/src/test/corretude_fluxo_maximo.sh
bash tp2/src/test/corretude_exemplos_demonstracao.sh
```

Os scripts compilam o projeto em uma pasta temporária e encerram com código diferente de zero quando uma compilação ou validação falha.

O teste `corretude_exemplos_demonstracao.sh` funciona como E2E: percorre o menu, executa os nove algoritmos com os exemplos sugeridos, seleciona um arquivo GTgraph personalizado e verifica a saída e os CSVs gerados em diretório temporário.

## Licença

Este repositório está sob a licença GNU General Public License v3.0. Consulte [`LICENSE`](LICENSE) para ver o texto completo e as condições de uso, modificação e redistribuição.
