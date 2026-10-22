# TesteGenerator — CLI do TP1 de ACD

`TesteGenerator.java` oferece uma interface de linha de comando (CLI), sem perguntas interativas. Todos os parâmetros são informados como argumentos, então o programa pode ser executado diretamente pelo shell, por scripts `.sh`, cron ou outros automatizadores.

## Requisitos

- JDK instalado (`java` e `javac` disponíveis no `PATH`).
- `TesteGenerator.java`, `Logs.java`, `Ordenador.java`, `CasosAlgoritmo.java` e as classes dos algoritmos na mesma pasta `src`.
- O script `executar_testes.sh` deve ter permissão de execução.

## Uso básico

```bash
chmod +x executar_testes.sh
./executar_testes.sh \
  --repeticoes 10 \
  --caso MEDIO \
  --algoritmo InsertionSort \
  --disposicao ALEATORIO \
  --n 1000 \
  --saida resultados/tp1 \
  --debug
```

A execução não lê nada de `stdin` e não exige confirmação do usuário. O script compila os arquivos Java e encaminha todos os argumentos para `TesteGenerator`.

Se nenhum argumento for fornecido, o script exibe a ajuda em vez de entrar num modo interativo:

```bash
./executar_testes.sh --help
```

## Parâmetros obrigatórios

| Parâmetro | Valores | Significado |
|---|---|---|
| `--repeticoes` | `1` a `1000` | Número de medições oficiais depois dos aquecimentos. |
| `--caso` | `MELHOR`, `PIOR`, `MEDIO` | Rótulo do caso teórico que será registrado no CSV. |
| `--algoritmo` | Nome de um dos dez algoritmos | Algoritmo executado nesta chamada. |
| `--disposicao` | `CRESCENTE`, `DECRESCENTE`, `ALEATORIO`, `REPETIDOS` | Como o vetor de entrada será gerado. |
| `--n` | `1` a `1000000` | Número de elementos do vetor. |

Algoritmos aceitos: `BubbleSort`, `InsertionSort`, `SelectionSort`, `ShellSort`, `HeapSort`, `MergeSort`, `QuickSort`, `CountingSort`, `RadixSort` e `BucketSort`. Os nomes também aceitam abreviações sem o sufixo `Sort`, por exemplo `--algoritmo Radix`.

## Parâmetros opcionais

| Parâmetro | Efeito |
|---|---|
| `--saida PREFIXO` | Pasta/prefixo dos arquivos gerados. Padrão: `resultados_tp1`. |
| `--debug` | Ativa logs detalhados e progresso percentual com estimativa de tempo restante. |
| `--no-color` | Desativa as cores ANSI nos logs. |
| `--help` ou `-h` | Exibe a ajuda e termina. |

## CSV gerado

O nome segue o padrão:

```text
<PREFIXO>_<Algoritmo>_n<TAMANHO>.csv
```

Por exemplo, usando `--saida resultados/tp1`, algoritmo `InsertionSort` e `n=1000`:

```text
resultados/tp1_InsertionSort_n1000.csv
```

O cabeçalho é:

```csv
algoritmo,n,caso,disposicao,sessao,repeticao,t_ns
```

| Coluna | Interpretação |
|---|---|
| `algoritmo` | Algoritmo que foi executado. |
| `n` | Número de elementos no vetor. |
| `caso` | Rótulo experimental solicitado (`MELHOR`, `PIOR` ou `MEDIO`). |
| `disposicao` | Disposição usada para gerar a entrada. |
| `sessao` | Identificador único da execução do programa, para distinguir execuções acumuladas. |
| `repeticao` | Número da medição oficial dentro daquela sessão, de 1 até o valor de `--repeticoes`. |
| `t_ns` | Tempo da chamada `sort()`, em nanossegundos. |

Se o CSV já existir e tiver o cabeçalho esperado, as medições serão acrescentadas ao final. O conteúdo anterior é preservado. Se o cabeçalho for incompatível, a execução falha para evitar misturar formatos diferentes.

## Automatização com shell

### Exemplo: executar um algoritmo por vez

```bash
./executar_testes.sh --repeticoes 10 --caso PIOR \
  --algoritmo InsertionSort --disposicao DECRESCENTE --n 1000 \
  --saida resultados/tp1 --debug
```

### Exemplo: executar várias configurações em sequência

O shell pode percorrer uma lista de tamanhos e algoritmos sem interação. Compile uma vez e chame a classe Java diretamente dentro do loop para evitar recompilar a cada configuração:

```bash
javac -encoding UTF-8 *.java

for algoritmo in MergeSort QuickSort CountingSort RadixSort BucketSort; do
  for n in 1000 10000 100000; do
    java -Xms128m -Xmx1g TesteGenerator \
      --repeticoes 10 \
      --caso MEDIO \
      --algoritmo "$algoritmo" \
      --disposicao ALEATORIO \
      --n "$n" \
      --saida resultados/tp1 \
      --no-color
  done
done
```

O exemplo usa algoritmos adequados, em geral, para vetores maiores. Ao criar loops mais abrangentes, respeite os limites específicos descritos abaixo e confira o código de cada implementação. O `set -e` pode ser usado em um script batch para interromper a execução se algum experimento falhar.

## Aquecimento e medição

O programa executa duas passagens de aquecimento, que não são registradas no CSV. A geração dos dados e a cópia do vetor ficam fora do intervalo cronometrado. A coluna `t_ns` mede apenas a chamada `sort()`.

O progresso e o ETA são estimativas. Os tempos podem variar quando o programa troca de tamanho do vetor ou por causa de atividade externa na máquina.

## Limites preventivos de execução

Para reduzir riscos de execuções demoradas ou falha por recursão/memória:

- `n` máximo: `1.000.000`.
- `BubbleSort`, `InsertionSort` e `SelectionSort`: `n <= 10.000`.
- `QuickSort`: `n <= 100.000`; com disposição crescente ou decrescente, `n <= 1.000`, porque certas estratégias de pivô podem degenerar para `O(n²)` e aprofundar demais a recursão.
- Para algoritmos quadráticos, configurações com `n >= 5.000` aceitam no máximo 10 repetições.
- O script usa por padrão `-Xms128m -Xmx1g`; é possível ajustar a heap:

```bash
JAVA_OPTS='-Xms64m -Xmx512m' ./executar_testes.sh \
  --repeticoes 10 --caso MEDIO --algoritmo RadixSort \
  --disposicao ALEATORIO --n 100000 --saida resultados/tp1
```

Aumentar a heap não resolve complexidade quadrática nem garante que um Quick Sort recursivo evite `StackOverflowError`.

## Importante para o relatório

`caso` e `disposicao` são registrados separadamente. Escolher `--caso MELHOR` não transforma a entrada em melhor caso automaticamente: a relação entre o rótulo e a disposição depende do algoritmo e da implementação. Registre e justifique essa correspondência na metodologia do TP1.
