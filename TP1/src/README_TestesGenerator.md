# TestesGenerator — Execução dos experimentos do TP1

## Objetivo

A classe `TestesGenerator` automatiza os experimentos de desempenho dos algoritmos de ordenação do TP1. Ela prepara os vetores de entrada, executa os algoritmos selecionados, mede o tempo de `sort()` com `System.nanoTime()` e registra cada medição em CSV.

## CSVs separados por algoritmo e tamanho

Os resultados são organizados em **um arquivo CSV para cada combinação de algoritmo e tamanho do vetor**. O argumento posicional de arquivo define o prefixo e a pasta de saída, não um único CSV.

Por exemplo, executando com o prefixo `resultados_tp1.csv`, são criados arquivos como:

```text
resultados_tp1_MergeSort_n1000.csv
resultados_tp1_MergeSort_n10000.csv
resultados_tp1_RadixSort_n1000.csv
resultados_tp1_RadixSort_n1000000.csv
```

Cada arquivo contém os casos e as repetições correspondentes àquele algoritmo e tamanho. O cabeçalho é:

```csv
algoritmo,n,caso,repeticao,t_ns
```

- `algoritmo`: nome do algoritmo executado;
- `n`: quantidade de elementos do vetor;
- `caso`: configuração dos dados de entrada;
- `repeticao`: índice da repetição dentro da execução atual;
- `t_ns`: tempo de ordenação em nanossegundos.

### Acumulação dos resultados

Se o CSV já existir, novas medições são **acrescentadas ao final do arquivo**. O conteúdo existente não é apagado e o cabeçalho não é repetido. Se o arquivo não existir ou estiver vazio, ele é criado com o cabeçalho.

Ao executar o programa novamente com o mesmo prefixo, algoritmo e tamanho, as medições novas são acumuladas nos arquivos correspondentes. Como o número da repetição volta a 1 em cada execução do programa, podem aparecer índices de repetição repetidos quando os resultados são acumulados de várias sessões.

## Algoritmos disponíveis

- `MergeSort`
- `BubbleSort`
- `InsertionSort`
- `QuickSort`
- `ShellSort`
- `SelectionSort`
- `HeapSort`
- `CountingSort`
- `RadixSort`
- `BucketSort`

Por padrão, todos são executados. É possível selecionar um algoritmo individualmente usando `--algoritmo`.

## Compilar e executar

Execute os comandos dentro da pasta `TP1/src`, onde estão as classes Java do projeto:

```bash
javac *.java
```

Executar todos os algoritmos, usando 10 repetições por configuração:

```bash
java TestesGenerator resultados_tp1.csv 10
```

Executar somente Radix Sort:

```bash
java TestesGenerator resultados_tp1.csv 10 --algoritmo RadixSort
```

Executar somente Counting Sort, ativando os logs:

```bash
java TestesGenerator resultados_tp1.csv 10 --algoritmo CountingSort --debug
```

Também são aceitos nomes curtos, como `radix`, `counting`, `merge`, `bubble`, `insertion`, `quick`, `shell`, `selection`, `heap` e `bucket`.

Ver todas as opções:

```bash
java TestesGenerator --help
```

### Argumentos

```text
java TestesGenerator [prefixo.csv] [repeticoes] [opções]
```

- `prefixo.csv`: prefixo e pasta para os arquivos de saída. Se omitido, usa `resultados_tp1.csv` no diretório atual.
- `repeticoes`: número de medições por combinação. Se omitido ou inválido, usa 10.
- `--algoritmo <nome>`: executa somente o algoritmo informado ou `todos`.
- `--debug`: mostra o progresso detalhado.
- `--help`: exibe a ajuda.

Para gravar em uma pasta específica, informe um caminho como `resultados/resultado.csv`. A pasta é criada automaticamente quando necessário.

## Tamanhos e configurações de entrada

Os tamanhos definidos atualmente são:

- 1.000 elementos;
- 10.000 elementos;
- 100.000 elementos;
- 1.000.000 de elementos.

Os casos gerados são:

- `CRESCENTE`;
- `DECRESCENTE`;
- `ALEATORIO`;
- `REPETIDOS`.

Para `n = 1.000.000`, a configuração atual executa somente `ALEATORIO`. Essa regra está implementada para reduzir a duração dos experimentos; portanto, não haverá linhas dos outros casos nos CSVs desse tamanho.

## Debug, progresso e estimativa de tempo

Com `--debug`, o programa mostra:

- qual algoritmo, tamanho, caso e repetição estão começando;
- qual CSV recebe a medição;
- o tempo de ordenação daquela execução;
- o número de medições concluídas e o percentual de progresso;
- o tempo decorrido e uma estimativa aproximada do tempo restante (ETA).

A estimativa é recalculada com base na duração observada até então e pode variar bastante quando a execução passa para vetores maiores ou algoritmos mais lentos.

## Como a medição é feita

A cópia do vetor original é preparada antes do início do cronômetro. O tempo registrado envolve somente a chamada a `ordenador.sort()`:

```java
long inicio = System.nanoTime();
ordenador.sort();
long fim = System.nanoTime();
long tempo = fim - inicio;
```

Para cada combinação experimental, os algoritmos recebem cópias do mesmo vetor original gerado para aquele tamanho e caso. O tempo de geração do vetor, a cópia e a gravação do CSV não fazem parte de `t_ns`.

## Observações para a análise do TP1

1. Preserve os CSVs: eles contêm as medições individuais necessárias para calcular estatísticas e construir gráficos.
2. Registre no relatório as configurações que não foram executadas, inclusive a restrição para vetores de um milhão de elementos.
3. Counting Sort, Radix Sort e Bucket Sort dependem de características da entrada. Documente o domínio e o intervalo de valores utilizado ao comparar seus resultados.
4. Os tempos acumulados podem conter o mesmo número de `repeticao` em sessões distintas. Para análises que precisem distinguir sessões de execução, mantenha um controle externo das rodadas ou execute com um prefixo novo.
5. Antes da coleta definitiva, faça uma rodada piloto para confirmar que todas as implementações compilam, ordenam corretamente e terminam em tempo aceitável.

## Logs formatados

A classe `Logs.java` centraliza a saída do terminal em um estilo inspirado nos logs de aplicações Node.js: horário, nível, contexto e mensagem, com cores ANSI quando suportadas pelo terminal. Ela oferece os níveis `INFO`, `DEBUG`, `SUCCESS`, `WARN`, `ERROR` e `PROGRESS`.

Mantenha `Logs.java` na mesma pasta das classes do TP1 e compile normalmente:

```bash
javac *.java
```

Com `--debug`, o `TestesGenerator` mostra cada medição e uma barra de progresso com percentual, quantidade concluída, tempo decorrido e estimativa restante. Sem `--debug`, os detalhes de cada medição e a barra são ocultados; as mensagens de início, resumo e erro continuam disponíveis.

Exemplo ilustrativo de saída:

```text
[20:15:08] INFO    [TestesGenerator] Iniciando experimentos com DEBUG ativado.
[20:15:08] DEBUG   [TestesGenerator] Iniciando RadixSort  n=1000  caso=ALEATORIO  repetição=1/10
[20:15:08] PROGRESS [TestesGenerator] [########................]  33.0%  33/100
[20:15:08] SUCCESS [TestesGenerator] Experimentos finalizados com sucesso.
```

Os horários e tempos acima são apenas exemplos. Se necessário, as cores podem ser desligadas programaticamente com `Logs.setCores(false)`, ou ativadas com `Logs.setCores(true)`.
