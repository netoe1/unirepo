
# Análise estatística dos resultados do TP1

> **Fonte analisada:** `tempos_consolidados.csv`, com os tempos individuais fornecidos. Os tempos foram convertidos de nanossegundos para milissegundos dividindo `t_ns` por 1.000.000. A análise é descritiva: não foram removidas observações nem estimadas configurações ausentes.

## 1. Resumo executivo

- **Medições:** 1.130 registros válidos, correspondentes a 10 algoritmos.
- **Configurações observadas:** 113 de 120 combinações esperadas de algoritmo × tamanho × disposição (94,2% de cobertura).
- **Repetições:** cada configuração observada possui exatamente 10 medições, numeradas de 1 a 10; 113 sessões únicas.
- **Qualidade básica:** 0 tempos nulos/negativos; 0 números de repetição duplicados dentro de uma configuração; 0 configurações com repetições diferentes de 1–10.
- **Configurações ausentes:** 7. Todas se referem a entradas de um milhão de elementos para Bubble Sort, Insertion Sort e Selection Sort.

A mediana é utilizada como medida principal para comparar desempenho, pois é menos sensível que a média a valores extremos. Média e coeficiente de variação são usados para avaliar estabilidade. O coeficiente de variação (CV) é o desvio-padrão amostral dividido pela média, multiplicado por 100.

## 2. Integridade e cobertura experimental

Cada combinação presente tem dez registros, com os números de repetição de 1 a 10. Os tempos são positivos e não foram detectadas repetições duplicadas por algoritmo, tamanho e disposição. Esta validação confirma a estrutura tabular, mas não prova que todos os algoritmos tenham cronometragem idêntica ou que o mesmo vetor tenha sido usado em todas as implementações.

| Tamanho (`n`) | Registros | Configurações completas |
| --------------: | --------: | ------------------------: |
|           1.000 |       300 |                        30 |
|          10.000 |       300 |                        30 |
|         100.000 |       300 |                        30 |
|       1.000.000 |       230 |                        23 |

### Configurações ausentes

| Algoritmo     |         n | Disposição |
| ------------- | --------: | ------------ |
| BubbleSort    | 1.000.000 | ALEATORIO    |
| BubbleSort    | 1.000.000 | DECRESCENTE  |
| InsertionSort | 1.000.000 | ALEATORIO    |
| InsertionSort | 1.000.000 | DECRESCENTE  |
| SelectionSort | 1.000.000 | ALEATORIO    |
| SelectionSort | 1.000.000 | CRESCENTE    |
| SelectionSort | 1.000.000 | DECRESCENTE  |

As ausências não foram preenchidas com zero, interpoladas ou estimadas. Gráficos e tabelas devem manter essas configurações em branco.

## 3. Medianas por algoritmo em entradas aleatórias

A Tabela 2 contém a mediana das dez execuções para `ALEATORIO`. Os travessões indicam que a configuração não aparece no CSV. Os valores são em milissegundos.

| Algoritmo     | n=1.000 |  n=10.000 |   n=100.000 | n=1.000.000 |
| ------------- | ------: | --------: | ----------: | ----------: |
| BubbleSort    |  35,703 | 3.256,275 | 325.089,550 |          — |
| InsertionSort |  13,938 | 1.302,483 | 121.652,268 |          — |
| SelectionSort |  16,452 | 1.637,438 | 162.589,079 |          — |
| ShellSort     |   0,918 |    14,140 |     208,443 |   3.492,487 |
| HeapSort      |   1,324 |    17,246 |     201,359 |   2.575,510 |
| MergeSort     |   1,422 |     3,447 |      52,796 |     668,228 |
| QuickSort     |   0,731 |     9,095 |     115,369 |   1.348,115 |
| CountingSort  |   0,281 |     1,045 |       7,220 |      88,443 |
| RadixSort     |   0,144 |     0,726 |       7,061 |      75,993 |
| BucketSort    |   0,771 |     2,207 |      18,279 |     211,707 |

### Comparação em n=100.000, disposição aleatória

Ordenando pela mediana, Radix Sort e Counting Sort têm os menores tempos observados nesta configuração, seguidos por Bucket Sort e Merge Sort. A diferença entre Radix Sort (7,061 ms) e Counting Sort (7,220 ms) é pequena em termos absolutos; esta análise descritiva não permite afirmar que a diferença seja estatisticamente significativa.

| Posição por mediana | Algoritmo     | Mediana (ms) | Média (ms) |    CV |
| --------------------: | ------------- | -----------: | ----------: | ----: |
|                     1 | RadixSort     |        7,061 |       7,099 | 2,76% |
|                     2 | CountingSort  |        7,220 |       7,579 | 9,40% |
|                     3 | BucketSort    |       18,279 |      18,431 | 7,41% |
|                     4 | MergeSort     |       52,796 |      52,966 | 5,69% |
|                     5 | QuickSort     |      115,369 |     116,111 | 1,35% |
|                     6 | HeapSort      |      201,359 |     203,114 | 1,58% |
|                     7 | ShellSort     |      208,443 |     210,022 | 1,77% |
|                     8 | InsertionSort |  121.652,268 | 121.952,489 | 0,83% |
|                     9 | SelectionSort |  162.589,079 | 162.643,572 | 0,08% |
|                    10 | BubbleSort    |  325.089,550 | 325.159,441 | 0,06% |

## 4. Influência da disposição dos dados

Para n=100.000, a mediana por disposição mostra padrões coerentes com as características dos algoritmos:

| Algoritmo     | Crescente (ms) | Aleatória (ms) | Decrescente (ms) |
| ------------- | -------------: | --------------: | ---------------: |
| BubbleSort    |          3,538 |     325.089,550 |      486.220,017 |
| InsertionSort |          5,309 |     121.652,268 |      243.172,255 |
| SelectionSort |    162.006,956 |     162.589,079 |      162.072,307 |
| ShellSort     |         80,026 |         208,443 |          115,629 |
| HeapSort      |        214,715 |         201,359 |          194,175 |
| MergeSort     |         54,000 |          52,796 |           54,317 |
| QuickSort     |         86,342 |         115,369 |           85,859 |
| CountingSort  |          7,844 |           7,220 |            7,265 |
| RadixSort     |          7,013 |           7,061 |            7,211 |
| BucketSort    |         10,549 |          18,279 |           10,501 |

### Interpretação dos padrões

- **Bubble Sort:** a mediana passa de 3,538 ms em ordem crescente para 325.089,550 ms em ordem aleatória e 486.220,017 ms em ordem decrescente. A entrada crescente permite a parada antecipada quando não há trocas; a ordem decrescente exige muitas trocas.
- **Insertion Sort:** passa de 5,309 ms em crescente para 121.652,268 ms em aleatória e 243.172,255 ms em decrescente. Isso é compatível com o número de deslocamentos necessário para inserir cada elemento na região ordenada.
- **Selection Sort:** os valores são muito próximos (aproximadamente 162 s) para as três disposições. Isso corresponde ao seu padrão de Θ(n²) comparações, pouco sensível à ordem inicial.
- **Merge Sort:** os tempos permanecem próximos de 53–54 ms nas três disposições, compatíveis com seu comportamento Θ(n log n) pouco sensível à ordem inicial.
- **Quick Sort:** os resultados são menores nas entradas crescentes/decrescentes do que na aleatória nesta implementação. Esse comportamento depende da estratégia concreta de pivô e partição; não deve ser generalizado para todas as implementações.
- **Counting Sort e Radix Sort:** as medianas ficam próximas entre disposições, coerente com o fato de que o custo depende sobretudo do tamanho e dos parâmetros das chaves/passagens, não da ordem inicial.
- **Bucket Sort e Shell Sort:** mostram sensibilidade à disposição e aos detalhes internos de implementação; para Buckets, a distribuição entre baldes e a forma de ordenação interna importam. A complexidade de Shell Sort depende da sequência de gaps.

## 5. Escalabilidade: fator de crescimento para cada aumento de 10× em n

A tabela usa medianas de entrada aleatória e calcula a razão entre medianas consecutivas: tempo em n seguinte dividido pelo tempo no tamanho anterior. Se n aumenta dez vezes, uma razão próxima de 100 é um sinal compatível com comportamento quadrático; razões próximas de 10–15 podem ser compatíveis com famílias lineares ou n log n, conforme parâmetros e custos fixos. São referências descritivas, não uma prova da complexidade.

| Algoritmo     | 1.000→10.000 | 10.000→100.000 | 100.000→1.000.000 |
| ------------- | ------------: | --------------: | -----------------: |
| BubbleSort    |       91,21× |         99,83× |                 — |
| InsertionSort |       93,45× |         93,40× |                 — |
| SelectionSort |       99,53× |         99,29× |                 — |
| ShellSort     |       15,40× |         14,74× |            16,76× |
| HeapSort      |       13,02× |         11,68× |            12,79× |
| MergeSort     |        2,42× |         15,31× |            12,66× |
| QuickSort     |       12,43× |         12,69× |            11,69× |
| CountingSort  |        3,72× |          6,91× |            12,25× |
| RadixSort     |        5,04× |          9,73× |            10,76× |
| BucketSort    |        2,86× |          8,28× |            11,58× |

Observações sobre os fatores:

- **Bubble, Insertion e Selection Sort:** os fatores de aproximadamente 91–100× entre 1.000, 10.000 e 100.000 elementos em entradas aleatórias são compatíveis com o crescimento quadrático esperado. A ausência das execuções aleatórias em n=1.000.000 impede avaliar a mesma razão no último intervalo.
- **Heap Sort e Quick Sort:** os fatores ficam em geral entre 11,7× e 13×, compatíveis com crescimento da ordem de n log n nos intervalos avaliados.
- **Merge Sort:** a razão de 2,42× entre 1.000 e 10.000 elementos é muito menor que a esperada para uma escala n log n. As medições de n=10.000 também têm grande dispersão (CV de 75,55%) e uma observação máxima de 15,179 ms; essa transição deve ser investigada antes da conclusão final. Para os dois intervalos maiores, as razões são 15,31× e 12,66×.
- **Counting Sort e Radix Sort:** nos tamanhos maiores, as razões se aproximam de uma escala linear, sobretudo de n=100.000 a 1.000.000. Nos tamanhos pequenos há sinais de custos fixos e variação entre repetições.
- **Bucket Sort:** a progressão se aproxima de escala linear no maior intervalo, mas a interpretação depende da distribuição entre buckets e da implementação da ordenação interna.
- **Shell Sort:** o crescimento medido ficou na faixa aproximada de 14,7× a 16,8× por fator 10 em n aleatório. A complexidade não tem uma única ordem universal: depende dos gaps implementados.

## 6. Resultados em n=1.000.000, entrada aleatória

Esta comparação inclui somente os algoritmos com medições nessa configuração. Os três algoritmos quadráticos não foram executados para entrada aleatória nesse tamanho, conforme a cobertura registrada.

| Algoritmo    | Mediana (ms) | Mediana (s) |    CV |
| ------------ | -----------: | ----------: | ----: |
| RadixSort    |       75,993 |       0,076 | 7,65% |
| CountingSort |       88,443 |       0,088 | 6,38% |
| BucketSort   |      211,707 |       0,212 | 4,33% |
| MergeSort    |      668,228 |       0,668 | 2,20% |
| QuickSort    |    1.348,115 |       1,348 | 1,03% |
| HeapSort     |    2.575,510 |       2,576 | 0,45% |
| ShellSort    |    3.492,487 |       3,492 | 0,08% |

Radix Sort apresentou a menor mediana observada nessa configuração (75,993 ms), seguido por Counting Sort (88,443 ms) e Bucket Sort (211,707 ms). Entre os algoritmos baseados em comparação disponíveis, Merge Sort apresentou 668,228 ms, seguido por Quick Sort (1.348,115 ms), Heap Sort (2.575,510 ms) e Shell Sort (3.492,487 ms). O ranking se refere somente às implementações e às entradas avaliadas, não a uma superioridade universal dos algoritmos.

## 7. Dispersão e aquecimento das execuções

Algumas configurações apresentam CV elevado e média acima da mediana. Os maiores exemplos são:

| Algoritmo    |      n | Disposição | Mediana (ms) | Média (ms) | Mínimo (ms) | Máximo (ms) |     CV |
| ------------ | -----: | ------------ | -----------: | ----------: | -----------: | -----------: | -----: |
| MergeSort    | 10.000 | ALEATORIO    |        3,447 |       5,049 |        2,990 |       15,179 | 75,55% |
| RadixSort    |  1.000 | CRESCENTE    |        0,128 |       0,209 |        0,122 |        0,496 | 70,94% |
| MergeSort    | 10.000 | DECRESCENTE  |        3,084 |       4,158 |        2,839 |       12,331 | 70,43% |
| RadixSort    |  1.000 | DECRESCENTE  |        0,137 |       0,208 |        0,124 |        0,485 | 70,42% |
| RadixSort    |  1.000 | ALEATORIO    |        0,144 |       0,236 |        0,121 |        0,480 | 69,59% |
| MergeSort    | 10.000 | CRESCENTE    |        3,246 |       4,436 |        2,965 |       12,476 | 66,61% |
| CountingSort | 10.000 | ALEATORIO    |        1,045 |       1,500 |        0,849 |        3,005 | 50,86% |
| BucketSort   | 10.000 | CRESCENTE    |        1,496 |       2,096 |        1,274 |        4,266 | 49,63% |
| BucketSort   | 10.000 | DECRESCENTE  |        1,524 |       2,073 |        1,264 |        4,138 | 49,12% |
| ShellSort    |  1.000 | DECRESCENTE  |        0,638 |       0,895 |        0,610 |        1,520 | 47,58% |

O padrão de várias sequências sugere efeito de aquecimento da JVM/JIT ou outra fonte de variação durante as repetições. Por exemplo, Radix Sort com n=1.000 aleatório tem média 0,236 ms e mediana 0,144 ms; Merge Sort com n=10.000 aleatório tem média 5,049 ms e mediana 3,447 ms. Isso é consistente com algumas medições iniciais bem mais lentas, mas o CSV isoladamente não prova a causa. Como as repetições individuais foram preservadas, não é recomendado excluir automaticamente esses valores: é melhor reforçar o aquecimento, repetir a coleta em sessões controladas e documentar os critérios antes de remover qualquer observação.

### Implicação metodológica

Os resultados usam dez repetições por configuração. Para melhorar a estabilidade, recomenda-se executar mais aquecimentos antes das repetições registradas e, se possível, realizar 30 ou mais medições como sugere o enunciado. Idealmente, manter o mesmo processo JVM durante um conjunto balanceado de algoritmos ou documentar como cada sessão é iniciada; alternar a ordem dos algoritmos também reduz o risco de confundir diferenças de aquecimento com diferenças de desempenho.

## 8. Limitações interpretativas

1. **Caso teórico versus disposição:** `MELHOR`, `MEDIO` e `PIOR` são valores da coluna `caso`. Eles estão associados a crescente, aleatória e decrescente no CSV, mas não significam necessariamente melhor, médio e pior caso para todo algoritmo. A análise deve priorizar a coluna `disposicao` e discutir cada algoritmo individualmente.
2. **Comparabilidade da medição:** o CSV registra os tempos, mas não descreve a fronteira exata do cronômetro. O contexto do projeto identificou diferenças na preparação interna medida entre Counting Sort, Bucket Sort e Radix Sort. Antes de afirmar comparação rigorosa entre esses métodos, confirme que o CSV foi coletado com as mesmas regras de cronometragem nas versões finais.
3. **Proveniência das implementações:** o CSV não identifica hash ou versão do código que produziu cada sessão. Como houve alterações recentes nas implementações, confirme se estes tempos correspondem à versão corrigida; se não, regenere os dados afetados.
4. **Bucket Sort:** o contexto do projeto registra o uso de `Collections.sort` dentro dos buckets na versão analisada anteriormente. Se essa for a versão que gerou estes dados, os tempos não representam uma implementação totalmente própria da ordenação interna e devem ser considerados provisórios para avaliação acadêmica.
5. **Ambiente experimental:** não há informação suficiente no CSV sobre processador, RAM, sistema operacional, JDK e parâmetros da JVM. Esses dados devem ser documentados separadamente.
6. **Entrada aleatória:** a semente 2026 aparece registrada, mas o CSV não contém hash dos vetores. A igualdade exata entre entradas usadas por algoritmos distintos depende do gerador e da forma como a semente é aplicada.

## 9. Conclusões

A base reúne 1.130 medições válidas e 113 configurações completas de 120 previstas, com dez repetições em cada configuração presente. Os resultados apoiam três conclusões descritivas principais: (i) Bubble Sort, Insertion Sort e Selection Sort apresentam crescimento compatível com custo quadrático em entradas aleatórias nos tamanhos disponíveis; (ii) Heap Sort e Quick Sort crescem, em geral, na faixa esperada para n log n; e (iii) Counting Sort e Radix Sort têm menores tempos observados para a família de entradas testada, com custos que crescem aproximadamente com o tamanho e os parâmetros das chaves. Merge Sort apresenta crescimento coerente nos intervalos maiores, mas a transição entre n=1.000 e n=10.000 merece investigação devido à baixa razão observada e à grande dispersão nessa última configuração.

Os dados também mostram grande sensibilidade à disposição em Bubble Sort e Insertion Sort, e comportamento mais estável em relação à disposição em Selection Sort, Merge Sort, Counting Sort e Radix Sort. Antes da redação definitiva do relatório, devem ser confirmadas a versão dos códigos utilizada, a consistência dos cronômetros e a origem dos altos valores nas primeiras repetições de algumas sessões. As sete configurações ausentes devem continuar explicitamente identificadas, sem estimativa.

## 10. Arquivos derivados

- `estatisticas_descritivas_tp1.csv`: estatísticas por algoritmo, tamanho e disposição, com média, mediana, mínimo, máximo, desvio-padrão e CV.
- `analise_estatistica_csv_tp1.md`: este documento de análise.

### Fórmulas utilizadas

- Conversão: $t_{ms}=t_{ns}/10^6$.
- Média amostral: $ar{x}=rac{1}{n}\sum_{i=1}^{n}x_i$.
- Desvio-padrão amostral: $s=\sqrt{rac{1}{n-1}\sum_{i=1}^{n}(x_i-ar{x})^2}$.
- Coeficiente de variação: $CV=100\cdot s/ar{x}$.
