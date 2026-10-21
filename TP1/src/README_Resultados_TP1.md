# README — Resultados e consolidação dos experimentos do TP1

## 1. Objetivo

O `TestesGenerator` executa os algoritmos de ordenação do TP1, registra cada medição em CSVs separados e permite reunir esses arquivos em um único CSV consolidado. Este documento explica como gerar os resultados, interpretar cada coluna e consolidar as medições para análise.

## 2. Arquivos CSV individuais

Cada arquivo individual corresponde a uma combinação de **algoritmo + tamanho do vetor (`n`)**. O nome segue o padrão:

```text
<prefixo>_<Algoritmo>_n<Tamanho>.csv
```

Por exemplo, ao utilizar `resultados_tp1.csv` como prefixo, o programa gera arquivos como:

```text
resultados_tp1_MergeSort_n1000.csv
resultados_tp1_InsertionSort_n10000.csv
resultados_tp1_RadixSort_n100000.csv
resultados_tp1_BucketSort_n1000000.csv
```

Cada CSV individual reúne os casos de entrada e as repetições daquele algoritmo e tamanho. Os dados são gravados no formato:

```csv
algoritmo,n,caso,repeticao,t_ns
InsertionSort,10000,CRESCENTE,1,597300
InsertionSort,10000,CRESCENTE,2,912200
InsertionSort,10000,CRESCENTE,3,678100
```

### Interpretação de cada coluna

| Coluna | O que representa | Como interpretar |
|---|---|---|
| `algoritmo` | Nome do algoritmo executado. | Identifica qual implementação produziu a medição, por exemplo `InsertionSort`. |
| `n` | Quantidade de elementos do vetor. | `10000` significa que o algoritmo recebeu 10.000 elementos. Nos arquivos individuais, esse valor é o mesmo nas linhas. |
| `caso` | Configuração da entrada. | Permite comparar o comportamento do algoritmo com diferentes disposições ou distribuições dos dados. |
| `repeticao` | Número da medição dentro da rodada atual. | `1`, `2` e `3` são execuções repetidas da mesma configuração, e não tamanhos diferentes. O contador volta a 1 quando o programa é executado novamente. |
| `t_ns` | Duração da chamada a `sort()`, em nanossegundos. | É o tempo da ordenação. Para converter para milissegundos, divida por 1.000.000. Por exemplo, `597300 ns` corresponde a `0,5973 ms`. |

### Interpretação do exemplo

Considere estas linhas:

```csv
algoritmo,n,caso,repeticao,t_ns
InsertionSort,10000,CRESCENTE,1,597300
InsertionSort,10000,CRESCENTE,2,912200
InsertionSort,10000,CRESCENTE,3,678100
```

As três medições foram feitas com `InsertionSort`, em um vetor de 10.000 elementos no caso `CRESCENTE`. Somente a repetição e o tempo medido mudam. A variação entre os tempos é esperada em medições de programas, por influência da JVM e das condições do computador. Use o conjunto de repetições para calcular estatísticas, em vez de tirar conclusões de uma execução isolada.

## 3. Interpretação dos casos de entrada

| Caso | Interpretação | Cuidados na análise |
|---|---|---|
| `CRESCENTE` | Vetor gerado em ordem crescente. | Pode favorecer algoritmos que já trabalham bem com dados ordenados, como Insertion Sort, mas não significa automaticamente o melhor caso de todos os algoritmos. |
| `DECRESCENTE` | Vetor gerado em ordem decrescente. | Pode representar uma situação desfavorável para algumas implementações; o efeito depende do algoritmo e de seus detalhes. |
| `ALEATORIO` | Vetor gerado com elementos em ordem aleatória. | Serve para estudar o comportamento em uma entrada não previamente ordenada. Os dados devem ser comparáveis entre algoritmos na mesma configuração. |
| `REPETIDOS` | Vetor gerado com muitas ocorrências repetidas. | Ajuda a observar se a quantidade de valores distintos influencia o desempenho. O comportamento depende da implementação e dos dados gerados. |

Esses nomes descrevem a configuração do vetor. Não devem ser tratados como rótulos universais de melhor, médio ou pior caso para todos os algoritmos. A classificação depende da implementação concreta e da análise teórica de cada método.

## 4. Como os tempos são medidos

O vetor original é gerado antes da medição e cada algoritmo recebe uma cópia. O tempo `t_ns` é calculado envolvendo somente a chamada ao método `sort()`:

```java
long inicio = System.nanoTime();
ordenador.sort();
long fim = System.nanoTime();
long tempo = fim - inicio;
```

Assim, a geração do vetor, sua cópia e a gravação da linha no CSV não fazem parte do tempo registrado. O número de repetições pode ser informado na execução; o padrão é 10. Para o relatório, o enunciado recomenda pelo menos 10 medições por configuração.

## 5. Acumulação das medições individuais

Ao executar novamente o `TestesGenerator` com o mesmo prefixo, as medições novas são acrescentadas aos CSVs individuais correspondentes. O conteúdo antigo é preservado e o cabeçalho não é repetido.

Como o número de `repeticao` reinicia em 1 a cada nova execução do programa, um CSV acumulado pode ter linhas com o mesmo número de repetição em rodadas diferentes. Para o trabalho, registre no relatório que os dados foram acumulados entre sessões ou, se for importante distinguir cada rodada, mantenha essa informação separadamente.

## 6. Juntar todos os CSVs em um único arquivo

Depois de gerar os arquivos individuais, use a opção `--merge`. Essa operação **não executa novamente os algoritmos**; apenas lê os CSVs individuais que correspondem ao prefixo informado e combina seus registros.

### Comando básico

Dentro de `TP1/src`:

```bash
java TestesGenerator resultados_tp1.csv --merge
```

O arquivo consolidado criado será:

```text
resultados_tp1_consolidado.csv
```

Se os arquivos individuais estiverem em uma pasta, informe o prefixo com o caminho. Por exemplo:

```bash
java TestesGenerator resultados/resultados_tp1.csv --merge
```

Nesse caso, o arquivo consolidado fica na pasta `resultados`.

### Escolher o nome do arquivo consolidado

```bash
java TestesGenerator resultados_tp1.csv --merge --merge-output tp1_completo.csv
```

O nome `tp1_completo.csv` será criado na pasta do prefixo, desde que seja informado apenas um nome de arquivo. Também é possível fornecer um caminho explícito para a saída.

### Comportamento da consolidação

- O programa procura arquivos no padrão `<prefixo>_<Algoritmo>_n<Tamanho>.csv`.
- O consolidado contém um único cabeçalho, seguido das linhas de dados dos arquivos individuais.
- Os CSVs individuais são preservados.
- O consolidado é **recriado a cada operação de merge**, e não recebe append. Isso evita duplicar todas as linhas simplesmente por executar a consolidação novamente.
- A consolidação não deduplica medições que já estejam repetidas nos arquivos individuais; ela preserva todas as linhas de dados encontradas.
- Arquivos que não correspondam ao prefixo e ao padrão de tamanho não são incluídos.

## 7. Interpretar o CSV consolidado

O arquivo consolidado utiliza as mesmas cinco colunas dos arquivos individuais, mas reúne diferentes algoritmos e tamanhos. Por isso, **cada linha continua sendo uma única medição**; ela não é uma média nem um resumo estatístico.

Para uma comparação justa:

1. compare algoritmos com o mesmo `n` e `caso`;
2. examine todas as repetições antes de resumir os resultados;
3. calcule pelo menos a média por grupo de `algoritmo`, `n` e `caso`;
4. considere também mediana e medidas de dispersão, como desvio-padrão;
5. use os dados individuais para produzir gráficos e conservar a evidência experimental.

Não compare diretamente tempos de `n` diferentes como se fossem a mesma configuração. Aumentar `n` muda o tamanho do problema e, portanto, a pergunta experimental.

## 8. Fluxo recomendado

1. Compile as classes na pasta `TP1/src`:

   ```bash
   javac *.java
   ```

2. Execute os experimentos (exemplo com 10 repetições):

   ```bash
   java TestesGenerator resultados_tp1.csv 10 --debug
   ```

3. Após concluir as medições, consolide os CSVs:

   ```bash
   java TestesGenerator resultados_tp1.csv --merge
   ```

4. Utilize `resultados_tp1_consolidado.csv` para análise, estatísticas e gráficos. Preserve também os CSVs individuais como dados originais.

## 9. Seleção de algoritmo e debug

Exemplo para executar somente Radix Sort:

```bash
java TestesGenerator resultados_tp1.csv 10 --algoritmo RadixSort --debug
```

A opção `--debug` mostra as configurações em execução, o progresso e uma estimativa de tempo restante. Para ver as opções disponíveis:

```bash
java TestesGenerator --help
```

## 10. Observação sobre tamanhos e resultados ausentes

Os tamanhos configurados são 1.000, 10.000, 100.000 e 1.000.000. Na implementação atual, para `n = 1.000.000`, somente o caso `ALEATORIO` é executado. Portanto, a ausência de linhas para os outros casos nesse tamanho decorre da configuração do programa e deve ser mencionada na metodologia do relatório.

Para os algoritmos de tempo linear — Counting Sort, Radix Sort e Bucket Sort — documente também as características do domínio e intervalo de valores utilizados. Isso ajuda a interpretar corretamente os resultados e as limitações de comparação com algoritmos baseados em comparação.
