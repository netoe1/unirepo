# TestesGenerator — Experimentos de ordenação (TP1)

A classe `TestesGenerator.java` automatiza os experimentos de desempenho dos algoritmos de ordenação do Trabalho Prático 1 (TP1), da disciplina Algoritmos e Classificação de Dados (ACD).

Ela percorre os valores de `EnumAlgoritmos`, cria uma instância do algoritmo correspondente, prepara os dados de entrada, realiza aquecimentos, mede o tempo de ordenação e registra os resultados em um arquivo CSV.

> **Importante:** esta classe mede desempenho. Ela **não verifica automaticamente a corretude do resultado**, ou seja, não compara o vetor final com uma versão ordenada esperada. Os testes pequenos de corretude devem ser feitos separadamente ou adicionados ao gerador.

## Algoritmos contemplados

O código possui mapeamento para estes dez algoritmos:

- Bubble Sort (`BUBBLE`)
- Insertion Sort (`INSERCTION` ou `INSERTION`)
- Selection Sort (`SELECTION`)
- Shell Sort (`SHELL`)
- Heap Sort (`HEAP`)
- Merge Sort (`MERGE`)
- Quick Sort (`QUICK`)
- Bucket Sort (`BUCKET`)
- Counting Sort (`COUNTING`)
- Radix Sort (`RADIX`)

Para que todos sejam executados com sucesso, os valores de `EnumAlgoritmos` devem corresponder a esses nomes e as classes correspondentes devem compilar e cumprir a interface usada pelo projeto: construtor sem argumentos, herança de `Ordenador<Integer>`, `setVetor(...)` e `sort()`.

A classe **tenta executar todos os algoritmos listados no enum**, mas isso não significa que toda combinação de algoritmo, tamanho e caso será medida. Algumas combinações são ignoradas conforme os limites descritos abaixo. Se uma implementação der erro, o status é registrado no CSV.

## Configuração padrão

### Tamanhos dos vetores

O código usa estes tamanhos:

- 1.000 elementos
- 10.000 elementos
- 100.000 elementos
- 1.000.000 de elementos

### Casos de entrada

Para tamanhos de até 100.000 elementos, são considerados quatro casos:

- **CRESCENTE:** valores em ordem crescente.
- **DECRESCENTE:** valores em ordem decrescente.
- **ALEATORIO:** permutação dos valores, embaralhada com uma semente fixa para tornar a entrada reproduzível.
- **REPETIDOS:** todos os valores são iguais a `7`.

Para o vetor de 1.000.000 de elementos, somente o caso `ALEATORIO` é executado, a fim de reduzir o custo total dos experimentos.

### Aquecimentos e repetições

Antes de medir cada configuração, o programa executa **2 aquecimentos**. Os tempos dessas execuções não são registrados como medições oficiais.

Depois, são feitas **10 repetições medidas** por padrão. Cada repetição recebe uma cópia independente do vetor original, para que a ordenação anterior não altere a próxima entrada.

### Combinações ignoradas

Para reduzir o risco de execuções excessivamente demoradas:

- Bubble Sort, Insertion Sort e Selection Sort são ignorados para tamanhos maiores que 1.000 elementos.
- Quick Sort é ignorado em casos crescentes, decrescentes e repetidos quando o tamanho ultrapassa 1.000 elementos. Isso considera que a implementação do projeto usa o último elemento como pivô, o que pode gerar recursão muito profunda em entradas desse tipo.
- Para o tamanho de 1.000.000, somente o caso aleatório é gerado para todos os algoritmos.

Esses casos não medidos aparecem no CSV com o status `IGNORADO_LIMITE_DE_TEMPO`. Essa decisão é uma proteção prática do experimento; não substitui a análise teórica nem significa que esses algoritmos não façam parte do TP1.

## Como executar

Execute os comandos no terminal, dentro da pasta `TP1/src`:

```bash
javac *.java
java TestesGenerator
```

Por padrão, o programa cria `resultados_tp1.csv` no diretório de execução.

Também é possível informar o nome/caminho do CSV e o número de repetições:

```bash
java TestesGenerator resultados_tp1.csv 10
```

Por exemplo, para fazer 30 repetições:

```bash
java TestesGenerator resultados_tp1_30_repeticoes.csv 30
```

O segundo argumento precisa ser um número inteiro maior que zero.

> **Atenção:** o arquivo CSV informado é criado ou sobrescrito quando o programa inicia. Use um nome diferente para preservar resultados anteriores.

## Formato do CSV

O arquivo usa uma linha por medição ou situação não medida, com estas colunas:

| Coluna | Significado |
|---|---|
| `algoritmo` | Nome do valor de `EnumAlgoritmos`. |
| `tamanho` | Quantidade de elementos no vetor. |
| `caso` | Configuração dos dados (`CRESCENTE`, `DECRESCENTE`, `ALEATORIO` ou `REPETIDOS`). |
| `repeticao` | Número da repetição medida; `0` é usado para aquecimento com erro ou configuração ignorada. |
| `tempo_ns` | Tempo de execução da ordenação em nanossegundos. Fica vazio quando não há medição válida. |
| `status` | `OK`, motivo de erro ou `IGNORADO_LIMITE_DE_TEMPO`. |

A cópia do vetor e a criação do objeto ordenador são feitas **fora do intervalo cronometrado**. O intervalo medido envolve a chamada `sort()`.

Os erros durante o aquecimento e durante a medição são registrados no CSV. Quando uma configuração falha durante uma medição, o código interrompe as repetições restantes daquela configuração e continua o experimento.

## Como interpretar os resultados

- Considere para os cálculos de média e dispersão apenas as linhas com `status` igual a `OK` e um valor válido em `tempo_ns`.
- Não trate as linhas `IGNORADO_LIMITE_DE_TEMPO` como tempos iguais a zero: elas representam configurações não medidas.
- Investigue as linhas com `ERRO` antes de usar seus dados para comparação.
- Compare os algoritmos somente sob configurações de entrada equivalentes.
- O CSV preserva tempos individuais; estatísticas como média, mediana, mínimo, máximo e desvio-padrão podem ser calculadas posteriormente.

## Limitações conhecidas

1. A classe não contém uma asserção de corretude que confirme se o vetor terminou ordenado.
2. A entrada aleatória é determinística e gerada como uma permutação sem duplicatas, enquanto o caso `REPETIDOS` contém apenas o valor `7`. Portanto, a configuração de dados deve ser descrita no relatório.
3. As combinações ignoradas reduzem a abrangência das comparações e precisam ser mencionadas na metodologia do relatório.
4. Todos os resultados dependem da implementação concreta dos algoritmos, da JVM, do hardware e da carga do sistema durante as medições.

