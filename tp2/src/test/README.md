# Testes de regressão do TP2

Esta pasta contém testes automatizados para as estruturas de grafo e para os algoritmos de busca já implementados. Os testes são Java simples, sem JUnit ou outras dependências: cada classe de teste tem um `main` e encerra com erro (`AssertionError`) quando uma condição falha.

Execute os comandos a partir da raiz do repositório:

```bash
bash tp2/src/test/corretude_est_grafos.spec.sh
bash tp2/src/test/corretude_algoritmos_grafos.sh
bash tp2/src/test/corretude_caminhos_minimos.sh
```

Cada script compila os arquivos Java de `tp2/src` em uma pasta temporária e remove essa pasta ao terminar. Se a compilação ou algum teste falhar, o script encerra com código diferente de zero.

## Testes das estruturas de grafo

O script `corretude_est_grafos.spec.sh` executa:

- `GrafoListaAdjacenciaTest`: verifica grafos dirigidos e não dirigidos, pesos, adjacências, substituição de arestas, laços, grafos vazio e unitário, validação de índices e listas somente leitura.
- `GrafoMatrizAdjacenciaTest`: verifica os mesmos comportamentos para a matriz de adjacência.
- `RepresentacoesGrafoTest`: aplica as mesmas operações às duas representações e compara tipo, contagens, arestas, pesos e vizinhos.

## Testes dos algoritmos

O script `corretude_algoritmos_grafos.sh` executa:

- `BFSTest`: verifica distâncias em número de arestas, vértices visitados e desconectados, reconstrução de caminho, grafo unitário, validação da origem e uso com ambas as representações e tipos de grafo.
- `DFSTest`: verifica busca a partir de uma origem e busca completa, componentes desconectados, vértices isolados, pais, tempos de descoberta/finalização, detecção de ciclos e validações. Também executa os cenários em lista e matriz, dirigidos e não dirigidos.
- `OrdenacaoTopologicaTest`: verifica DAG, ciclo, desconexão, grafo vazio/unitário e validações nas duas representações.
- `KruskalTest`: verifica MST, floresta geradora mínima, laços, grafos vazios/unitários e pesos inválidos nas duas representações.
- `PrimTest`: verifica MST, origem alternativa, floresta geradora mínima, laços, grafos vazios/unitários e validações nas duas representações.

Esses testes cobrem BFS e DFS; não substituem testes para os demais algoritmos que ainda serão implementados.

## Testes de caminhos mínimos

O script `corretude_caminhos_minimos.sh` executa:

- `BellmanFordTest`: verifica caminhos mínimos com peso negativo, vértices desconectados, ciclos negativos alcançáveis e inalcançáveis, validações e as duas representações de grafo.
- `DkjistraTest`: verifica caminhos mínimos com pesos não negativos, vértices desconectados, rejeição de pesos negativos ou infinitos, validações e as duas representações de grafo.
