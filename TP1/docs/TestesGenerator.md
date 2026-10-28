# `Main.java`, `TestesGenerator.java` e os testes do TP1

Há duas classes de propósito diferente no diretório; use a indicada para a tarefa.

## Executor recomendado para a coleta atual: `Main.java`

`Main.java` executa **uma configuração experimental por chamada da CLI**, sem solicitar dados pelo `stdin`. Seus argumentos são explícitos, o que permite chamá-lo em scripts Bash e automatizar a coleta.

```bash
java -cp . Main --help
```

Exemplo:

```bash
java -cp . Main --repeticoes 10 --caso MEDIO \
  --algoritmo MergeSort --disposicao ALEATORIO --n 1000 \
  --saida resultados/tp1 --semente 2026 --debug
```

Os resultados são acrescentados a arquivos CSV por algoritmo e tamanho, com campos para algoritmo, tamanho, caso, disposição, semente, sessão, repetição e tempo em nanossegundos. Dois aquecimentos não entram nas medições. A região cronometrada envolve apenas `sort()`.

## Bateria automatizada: `testes_tp1.sh`

O script executa primeiro a compilação e os testes de corretude. Quando chamado com `desempenho`, dispara uma matriz conservadora de configurações e usa `Main.java` para registrar os tempos:

```bash
./testes_tp1.sh corretude
./testes_tp1.sh desempenho
```

A matriz, os tamanhos e os limites preventivos estão documentados em [`testes_tp1.md`](testes_tp1.md).

## Testes de corretude: `TestesCorretude.java`

Verifica se cada algoritmo ordena vetores pequenos (10, 11 e 15 elementos) em ordem crescente, decrescente, aleatória e com valores repetidos. Confere tanto a ordenação quanto a preservação dos elementos.

## Classe antiga: `TestesGenerator.java`

`TestesGenerator.java` é um executor de lote legado com configurações fixas e argumentos posicionais próprios. **Não é a classe chamada pelo script atualizado de desempenho e não implementa os mesmos parâmetros `--caso`, `--algoritmo`, `--disposicao` e `--n`.** Para os experimentos atuais, use `Main.java` diretamente ou `testes_tp1.sh desempenho`, evitando confundir os dois executores.

## Significado de `caso` e `disposicao`

O rótulo `MELHOR`, `MEDIO` ou `PIOR` é registrado separadamente da disposição de entrada. A relação é específica do algoritmo e da implementação. Para comparar os algoritmos, mantenha o mesmo tamanho, disposição e semente, e descreva no relatório as condições usadas nos algoritmos de tempo linear.
