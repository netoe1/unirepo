
# Análise preliminar dos dados experimentais — TP1 (ACD)

**Projeto:** Trabalho Prático 1 — Algoritmos e Classificação de Dados (ACD)**Data da análise:** 28/10/2026**Versão:** 1.0**Status:** análise preliminar dos CSVs disponíveis; requer validação metodológica antes da versão definitiva do relatório.

> **Objetivo deste documento.** Consolidar uma leitura estatística dos resultados atuais, relacionar os padrões observados às complexidades teóricas e registrar limitações que precisam ser tratadas antes de transpor a análise para o relatório em Overleaf. Os valores abaixo vêm dos CSVs consolidados e das tabelas estatísticas associadas; não são tempos estimados.

## 1. Síntese dos resultados

- Foram analisadas **1.130 medições individuais**, distribuídas em **113 configurações completas**, cada uma com dez repetições.
- A matriz planejada contém 120 configurações: 10 algoritmos, quatro tamanhos (`1.000`, `10.000`, `100.000` e `1.000.000`) e três disposições de entrada. Sete configurações de `n = 1.000.000` estão ausentes.
- Não foram identificadas, nos arquivos verificados, linhas inválidas, tempos iguais a zero ou negativos, nem números de repetição duplicados. Isso comprova consistência estrutural básica, mas não demonstra por si só que todas as implementações e fronteiras de cronometragem estejam metodologicamente corretas.
- Os resultados são, em geral, coerentes com a teoria: Bubble Sort e Insertion Sort ficam muito mais lentos em entradas aleatórias/decrescentes do que em entradas crescentes; Selection Sort apresenta tempos semelhantes entre disposições; algoritmos com crescimento próximo de `n log n` aumentam muito menos que os quadráticos; Radix Sort e Counting Sort têm os menores tempos entre os algoritmos completos em grandes entradas aleatórias, sob a distribuição de chaves usada.
- **Há ressalvas importantes antes da conclusão final:** a implementação de Bucket Sort chama `Collections.sort`, a fronteira de cronometragem não é uniforme entre Counting Sort, Radix Sort e Bucket Sort, e os nomes `MELHOR`/`MEDIO`/`PIOR` são atribuídos principalmente pela disposição do vetor, não por uma demonstração de que cada entrada seja realmente o melhor, médio ou pior caso de todos os algoritmos.

## 2. Escopo experimental e origem dos dados

O enunciado do TP1 exige implementar dez algoritmos, verificar a corretude, coletar tempos de execução, organizar estatísticas e comparar os resultados com a teoria. Para cada configuração, recomenda pelo menos dez repetições, preservação dos tempos individuais e apresentação de medidas como média, mediana e dispersão.

### 2.1 Algoritmos considerados

| Família                                   | Algoritmos                                                                                  |
| ------------------------------------------ | ------------------------------------------------------------------------------------------- |
| Baseados em comparação                   | Bubble Sort, Insertion Sort, Selection Sort, Shell Sort, Heap Sort, Merge Sort e Quick Sort |
| Não baseados em comparação convencional | Counting Sort, Radix Sort e Bucket Sort                                                     |

### 2.2 Configurações registradas

| Dimensão                                  | Valores observados                                                        |
| ------------------------------------------ | ------------------------------------------------------------------------- |
| Tamanho do vetor (`n`)                   | 1.000; 10.000; 100.000; 1.000.000                                         |
| Disposição                               | `CRESCENTE`, `ALEATORIO`, `DECRESCENTE`                             |
| Repetições por configuração encontrada | 10                                                                        |
| Semente registrada                         | 2026                                                                      |
| Campos principais no CSV bruto             | algoritmo, n, caso, disposição, semente, sessão, repetição, `t_ns` |
| Unidade original                           | nanossegundos; as tabelas resumidas também apresentam milissegundos      |

De acordo com a implementação de geração de entrada registrada no contexto do projeto, os vetores crescentes contêm os valores de `0` a `n-1`, os decrescentes contêm os mesmos valores em ordem inversa, e os aleatórios são permutações reprodutíveis desses valores. Portanto, **a entrada aleatória é uma permutação sem duplicatas**, e não uma amostra aleatória com valores repetidos. Os testes de corretude com repetidos constituem um cenário separado.

A mesma semente e o mesmo tamanho/disposição são utilizados para gerar entradas comparáveis entre algoritmos. Para a versão final do relatório, recomenda-se preservar também a identificação da revisão do código e, se possível, uma confirmação automatizada de que a entrada inicial é idêntica em todas as execuções correspondentes.

## 3. Integridade e cobertura

| Indicador                                                        | Resultado |
| ---------------------------------------------------------------- | --------: |
| Medições individuais válidas                                  |     1.130 |
| Configurações previstas                                        |       120 |
| Configurações presentes e completas                            |       113 |
| Configurações ausentes                                         |         7 |
| Repetições em cada configuração presente                     |        10 |
| Linhas consideradas inválidas pelo processamento                |         0 |
| Configurações com tempo zero/negativo ou repetição duplicada |         0 |

A cobertura observada é de **94,2%** das configurações previstas (`113 / 120`). As sete ausências são:

| Algoritmo      |     `n` | Disposição ausente |
| -------------- | --------: | -------------------- |
| Bubble Sort    | 1.000.000 | `ALEATORIO`        |
| Bubble Sort    | 1.000.000 | `DECRESCENTE`      |
| Insertion Sort | 1.000.000 | `ALEATORIO`        |
| Insertion Sort | 1.000.000 | `DECRESCENTE`      |
| Selection Sort | 1.000.000 | `CRESCENTE`        |
| Selection Sort | 1.000.000 | `ALEATORIO`        |
| Selection Sort | 1.000.000 | `DECRESCENTE`      |

Os tempos dessas configurações devem permanecer ausentes nos gráficos e tabelas; não devem ser preenchidos com zero, interpolados ou estimados como se fossem medições. O padrão é compatível com a dificuldade de completar a bateria de algoritmos quadráticos em um milhão de elementos, mas os CSVs, isoladamente, não comprovam o motivo de cada ausência.

Para comparar **todos os dez algoritmos** sob uma matriz comum, o tamanho de `100.000` é atualmente a escolha mais completa: existem as três disposições para todos os algoritmos. Para `n = 1.000.000`, as comparações devem usar apenas os algoritmos com medição disponível para a disposição correspondente.

## 4. Estatística descritiva: entradas aleatórias

A tabela abaixo apresenta a **mediana dos dez tempos**, em milissegundos, para cada algoritmo e tamanho com uma configuração aleatória registrada. A mediana foi selecionada como referência principal para a comparação, pois algumas configurações pequenas contêm valores extremos; a média e as medidas de dispersão continuam necessárias no relatório.

| Algoritmo      | `n = 1.000` (ms) | `n = 10.000` (ms) | `n = 100.000` (ms) | `n = 1.000.000` (ms) |
| -------------- | -----------------: | ------------------: | -------------------: | ---------------------: |
| Bubble Sort    |             35,703 |           3.256,275 |          325.089,550 |                     — |
| Insertion Sort |             13,938 |           1.302,483 |          121.652,268 |                     — |
| Selection Sort |             16,452 |           1.637,438 |          162.589,079 |                     — |
| Shell Sort     |              0,918 |              14,140 |              208,443 |              3.492,487 |
| Heap Sort      |              1,324 |              17,246 |              201,359 |              2.575,510 |
| Merge Sort     |              1,422 |               3,447 |               52,796 |                668,228 |
| Quick Sort     |              0,731 |               9,095 |              115,369 |              1.348,115 |
| Counting Sort  |              0,281 |               1,045 |                7,220 |                 88,443 |
| Radix Sort     |              0,144 |               0,726 |                7,061 |                 75,993 |
| Bucket Sort    |              0,771 |               2,207 |               18,279 |                211,707 |

`—` indica que não existe medição para a configuração; não significa tempo igual a zero.

### 4.1 Leitura dos resultados da tabela

1. **Os três algoritmos quadráticos mais tradicionais degradam rapidamente.** Entre `n = 1.000` e `n = 10.000`, Bubble Sort aleatório passa de aproximadamente 35,7 ms para 3,26 s; entre `n = 10.000` e `n = 100.000`, chega a aproximadamente 325 s. Insertion Sort e Selection Sort também exibem crescimento próximo de duas ordens de grandeza por aumento de dez vezes no tamanho.
2. **Merge Sort mantém crescimento mais contido.** Para a entrada aleatória, a mediana passa de 3,447 ms em `n = 10.000` para 52,796 ms em `n = 100.000` e 668,228 ms em `n = 1.000.000`. O padrão é compatível com crescimento da família `n log n`, sem que a medição seja uma prova formal da complexidade.
3. **Quick Sort apresenta crescimento compatível com `n log n` nas configurações observadas.** A mediana aleatória aumenta de 9,095 ms (`n = 10.000`) para 115,369 ms (`n = 100.000`) e 1.348,115 ms (`n = 1.000.000`). O resultado depende, contudo, da estratégia de escolha do pivô da implementação.
4. **Counting Sort e Radix Sort apresentam os menores tempos entre os algoritmos completamente medidos para entradas aleatórias grandes.** Em `n = 1.000.000`, as medianas são 88,443 ms e 75,993 ms, respectivamente. A conclusão é específica à distribuição de chaves, à implementação e à região cronometrada; não representa uma classificação universal de algoritmos.
5. **Bucket Sort fica atrás de Counting Sort e Radix Sort neste conjunto.** Sua mediana é 211,707 ms para um milhão de elementos aleatórios. A interpretação precisa ser cautelosa porque a implementação usa uma ordenação pronta dentro dos buckets e, assim, não representa uma implementação independente de Bucket Sort conforme a restrição do enunciado.

## 5. Efeito da disposição da entrada

### 5.1 Bubble Sort e Insertion Sort

Os dados exibem claramente a sensibilidade desses algoritmos à ordem inicial dos elementos. Em `n = 100.000`:

| Algoritmo      | Crescente (ms) | Aleatória (ms) | Decrescente (ms) |
| -------------- | -------------: | --------------: | ---------------: |
| Bubble Sort    |          3,538 |     325.089,550 |      486.220,017 |
| Insertion Sort |          5,309 |     121.652,268 |      243.172,255 |

Em entrada crescente, o Bubble Sort pode encerrar após uma passagem sem trocas; o Insertion Sort faz poucas movimentações. Esse comportamento explica o crescimento observado próximo de linear no melhor cenário da implementação. Em entradas aleatórias e decrescentes, o número de comparações e, principalmente, de trocas/deslocamentos cresce substancialmente, aproximando-se do comportamento quadrático.

Os tempos de um milhão de elementos reforçam o ponto apenas para o cenário crescente: Bubble Sort tem mediana de 34,860 ms e Insertion Sort de 53,009 ms. Não há dados para os seus cenários aleatório e decrescente nesse tamanho.

### 5.2 Selection Sort

Em `n = 100.000`, as medianas são próximas nas três disposições: 162.006,956 ms em ordem crescente, 162.589,079 ms em ordem aleatória e 162.072,307 ms em ordem decrescente. Esse padrão é esperado: a implementação percorre repetidamente a parte não ordenada para encontrar o menor elemento, mantendo aproximadamente o mesmo número de comparações independentemente da disposição inicial. O resultado é compatível com `Θ(n²)` em melhor, médio e pior caso.

### 5.3 Algoritmos da família `n log n`

Heap Sort, Merge Sort e Quick Sort permanecem em escalas de tempo muito inferiores às dos algoritmos quadráticos aleatórios/decrescentes nas maiores entradas disponíveis. Heap Sort mostra pouca variação entre as três disposições em `n = 1.000.000` (medianas de aproximadamente 2,44 a 2,58 s), comportamento compatível com sua classe `Θ(n log n)`. Merge Sort também apresenta tempos próximos entre disposições (aproximadamente 630 a 668 ms).

Quick Sort apresenta mediana de 992,370 ms na entrada crescente, 1.348,115 ms na aleatória e 1.033,301 ms na decrescente, todas para `n = 1.000.000`. Na versão do código analisada, o pivô é escolhido pelo elemento da posição central antes de ser movido para o fim da partição. Assim, não se deve assumir automaticamente que a entrada crescente ou decrescente corresponda ao pior caso teórico de Quick Sort.

### 5.4 Counting Sort, Radix Sort e Bucket Sort

Em `n = 1.000.000`, as medianas registradas por disposição são:

| Algoritmo     | Crescente (ms) | Aleatória (ms) | Decrescente (ms) |
| ------------- | -------------: | --------------: | ---------------: |
| Counting Sort |         76,970 |          88,443 |           77,397 |
| Radix Sort    |         72,011 |          75,993 |           71,765 |
| Bucket Sort   |        110,830 |         211,707 |          115,232 |

Counting Sort e Radix Sort variam relativamente pouco entre as disposições. Isso é coerente com algoritmos cujo trabalho depende principalmente do tamanho do vetor e das propriedades das chaves, e não da ordem original. Neste experimento, os valores são `0` a `n-1`, portanto o intervalo de chaves do Counting Sort é proporcional a `n`. No Radix Sort atual, a transformação para lidar com inteiros com sinal faz com que a quantidade de passagens seja aproximadamente constante para os tipos utilizados; a preparação dessa transformação está fora da região cronometrada no código registrado.

Bucket Sort é mais lento na entrada aleatória do que nas entradas crescente e decrescente. Isso pode estar ligado ao trabalho realizado dentro dos buckets e à ordenação auxiliar usada por sua implementação. Por causa do uso de `Collections.sort`, os resultados não devem ser interpretados como evidência isolada da complexidade de uma implementação manual convencional de Bucket Sort.

## 6. Comparação com a teoria assintótica

A tabela sintetiza as complexidades convencionais relevantes para orientar a discussão. A complexidade concreta pode variar conforme detalhes da implementação e, nos algoritmos não baseados em comparação, conforme os parâmetros dos valores de entrada.

| Algoritmo      | Melhor caso                                 | Médio/esperado                            | Pior caso                                                             | Relação com os dados observados                                                                                                                                                  |
| -------------- | ------------------------------------------- | ------------------------------------------ | --------------------------------------------------------------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Bubble Sort    | `O(n)` com parada antecipada              | `O(n²)`                                 | `O(n²)`                                                            | Crescente cresce próximo de linear; aleatório/decrescente crescem muito mais rapidamente.                                                                                        |
| Insertion Sort | `O(n)`                                    | `O(n²)`                                 | `O(n²)`                                                            | Forte diferença entre crescente e as demais disposições.                                                                                                                        |
| Selection Sort | `Θ(n²)`                                 | `Θ(n²)`                                | `Θ(n²)`                                                           | Tempos próximos nas três disposições e crescimento aproximadamente quadrático.                                                                                                |
| Shell Sort     | Depende dos intervalos                      | Depende dos intervalos                     | Depende dos intervalos                                                | A implementação usa gaps que começam em `n/2` e são divididos por dois; não se deve atribuir uma ordem única sem especificar essa sequência e a análise aplicável.      |
| Heap Sort      | `Θ(n log n)`                             | `Θ(n log n)`                            | `Θ(n log n)`                                                       | Crescimento semelhante entre disposições e escalas compatíveis com `n log n`.                                                                                                 |
| Merge Sort     | `Θ(n log n)`                             | `Θ(n log n)`                            | `Θ(n log n)`                                                       | Crescimento consistente com a família `n log n`.                                                                                                                                |
| Quick Sort     | `Θ(n log n)` em partições equilibradas | `Θ(n log n)` esperado                   | `Θ(n²)`                                                           | As medições observadas crescem de forma próxima a `n log n`; a escolha de pivô e o particionamento importam.                                                                 |
| Counting Sort  | `O(n+k)`                                  | `O(n+k)`                                 | `O(n+k)`                                                            | `k` é o intervalo de chaves. Como `k` é proporcional a `n` neste experimento, espera-se crescimento aproximadamente linear.                                                |
| Radix Sort     | `O(d(n+b))` na formulação por passagens | `O(d(n+b))`                              | `O(d(n+b))` para a configuração de dígitos/passagens considerada | Com base fixa e número de passagens limitado, o crescimento pode ser aproximadamente linear. `d` e a base devem ser descritos.                                                  |
| Bucket Sort    | Dependente da distribuição                | Linear esperado sob hipóteses favoráveis | Pode chegar a `O(n²)`                                              | Os resultados mostram dependência da disposição, mas a implementação usa ordenação pronta dentro dos buckets; é necessário corrigir isso antes de uma comparação final. |

`k` representa o intervalo de chaves em Counting Sort; `d`, o número de dígitos/passagens em Radix Sort; `b`, a base utilizada. Para Shell Sort, a complexidade é particularmente dependente da sequência de gaps. Para Bucket Sort, o comportamento esperado depende de como os elementos se distribuem pelos buckets e do método usado para ordenar cada bucket.

## 7. Variação entre repetições

A análise das dez repetições mostra que a estabilidade depende do tamanho e da configuração. Em algumas medições pequenas, poucos tempos altos afetam bastante a média e o desvio-padrão; nas grandes entradas, as repetições de vários algoritmos têm variação relativa menor.

| Configuração                          | Média (ms) | Mediana (ms) | Mínimo (ms) | Máximo (ms) | Desvio-padrão amostral (ms) |      CV |
| --------------------------------------- | ----------: | -----------: | -----------: | -----------: | ---------------------------: | ------: |
| Merge Sort, `n=10.000`, aleatória    |       5,049 |        3,447 |        2,990 |       15,179 |                        3,814 | 75,552% |
| Radix Sort, `n=1.000`, crescente      |       0,209 |        0,128 |        0,122 |        0,496 |                        0,148 | 70,939% |
| Counting Sort, `n=10.000`, aleatória |       1,500 |        1,045 |        0,849 |        3,005 |                        0,763 | 50,862% |
| Heap Sort, `n=1.000.000`, aleatória  |   2.571,961 |    2.575,510 |    2.555,215 |    2.585,119 |                       11,561 |  0,449% |
| Quick Sort, `n=1.000.000`, aleatória |   1.351,083 |    1.348,115 |    1.335,979 |    1.382,101 |                       13,952 |  1,033% |
| Bubble Sort, `n=100.000`, aleatória  | 325.159,441 |  325.089,550 |  325.005,083 |  325.634,692 |                      187,695 |  0,058% |

O coeficiente de variação (CV) é o desvio-padrão dividido pela média, expresso em porcentagem. Por exemplo, no Merge Sort aleatório de 10.000 elementos, a média supera a mediana porque há uma medição máxima de 15,179 ms, muito acima das demais. Por isso:

- use **mediana** como resumo principal nos gráficos de comparação;
- apresente também **média, desvio-padrão, mínimo e máximo** em tabelas, atendendo ao enunciado;
- interprete CVs muito elevados como indício de alta variação/outlier ou de execução ainda sensível a fatores externos;
- não descreva diferenças pequenas entre algoritmos como estatisticamente significativas sem teste estatístico apropriado e desenho experimental adequado.

O TP1 exige pelo menos dez repetições e recomenda trinta ou mais quando viável. A base atual tem dez repetições por configuração, atendendo ao mínimo informado, mas uma coleta adicional pode melhorar a avaliação da variabilidade, sobretudo para tempos curtos.

## 8. Limitações metodológicas que precisam ser resolvidas

Esta seção é importante para evitar que o relatório atribua à teoria diferenças que também podem ser provocadas pela implementação ou pela medição.

### 8.1 Fronteira de cronometragem

O enunciado determina que a medição não inclua geração da entrada, cópias ou impressão. A metodologia definida no projeto também pretende padronizar quais etapas de preparação entram no tempo. No código registrado no contexto:

- **Radix Sort** cria e transforma arrays auxiliares e determina a maior chave antes de iniciar o timer; a ordenação por dígitos e a escrita dos valores de volta ao vetor ficam dentro da região cronometrada.
- **Counting Sort** inicia o timer antes de obter mínimo/máximo e determinar o intervalo, de modo que a busca desses extremos fica dentro do tempo registrado.
- **Bucket Sort** também inicia o timer antes da busca de mínimo/máximo e inclui essas operações.

Se a regra adotada para o experimento for excluir a preparação de extremos/chaves, esses limites não são equivalentes. Antes da versão definitiva, a equipe deve padronizar a decisão, ajustar os timers e **refazer as medições afetadas**. Se for decidido que a identificação de extremos faz parte do algoritmo medido, isso deve ser declarado e aplicado de maneira consistente.

### 8.2 Implementação de Bucket Sort

O código de Bucket Sort utiliza `Collections.sort` para ordenar os elementos de cada bucket. O enunciado afirma que não se pode usar métodos prontos de ordenação da API Java para substituir os algoritmos solicitados. É necessário implementar manualmente a ordenação interna de cada bucket ou discutir outra solução com o docente. Até essa correção, os tempos de Bucket Sort devem ser tratados como provisórios e não devem fundamentar uma conclusão definitiva sobre a complexidade do algoritmo implementado pelo grupo.

### 8.3 Significado de `MELHOR`, `MEDIO` e `PIOR`

No script experimental, os rótulos estão associados às disposições: crescente, aleatória e decrescente, respectivamente. Essa correspondência é adequada para alguns algoritmos, como Bubble Sort e Insertion Sort, mas não pode ser generalizada. Selection Sort realiza trabalho quadrático em qualquer disposição; Quick Sort depende da escolha do pivô; Counting Sort e Radix Sort dependem principalmente das chaves e parâmetros específicos.

No relatório, prefira nomear os cenários como **entrada crescente**, **entrada aleatória** e **entrada decrescente**. A expressão “melhor caso” ou “pior caso” deve ser usada somente quando a análise do algoritmo e da implementação justificar essa classificação.

### 8.4 Cobertura incompleta em `n = 1.000.000`

Não compare todos os algoritmos como se a matriz de um milhão estivesse completa. Para comparações abrangentes, utilize `n = 100.000`, que possui as trinta configurações esperadas (dez algoritmos × três disposições). Nas análises de um milhão, informe explicitamente quais algoritmos não foram medidos.

### 8.5 Número de sessões e sementes

As estatísticas atuais se baseiam em dez repetições por configuração e, conforme os CSVs, uma sessão e uma semente por configuração. Isso avalia a variabilidade entre repetições nessa sessão, mas não captura completamente variação entre sessões, reinicializações, condições de carga do sistema ou diferentes sementes. Uma extensão útil seria repetir o experimento em mais de uma sessão e, quando pertinente, com sementes adicionais.

### 8.6 Ambiente e reprodutibilidade

O CSV contém dados suficientes para agrupar as medições, mas não documenta por si só o hardware e todo o ambiente de execução. O relatório técnico precisa registrar, no mínimo:

- processador e memória RAM;
- sistema operacional e versão;
- versão do JDK/JVM;
- parâmetros usados ao executar a JVM;
- commit ou versão do código avaliado;
- comandos de execução, aquecimentos, número de repetições e semente.

Esses dados devem ser obtidos do ambiente real em que o experimento foi executado; não devem ser preenchidos por suposição.

## 9. Recomendações para a análise final e para o Overleaf

1. **Resolver primeiro as limitações metodológicas**, especialmente a ordenação pronta em Bucket Sort e a uniformidade dos timers. Registrar qual versão do código produziu os CSVs atuais.
2. **Regenerar a base final** a partir dos CSVs brutos preservados; depois recalcular as estatísticas e gerar os gráficos novamente, evitando misturar medições de versões diferentes.
3. Para comparação que inclua todos os dez algoritmos, priorizar os gráficos de `n = 100.000` por disposição. Para estudar o crescimento com `n`, usar curvas separadas por disposição e deixar lacunas onde não existem resultados.
4. Usar mediana para comparar tempos em gráficos, mas apresentar média e dispersão nas tabelas. Escala logarítmica é adequada quando os tempos diferem por várias ordens de grandeza; o eixo deve estar identificado com a unidade `ms`.
5. Incluir pelo menos: (a) tabela de complexidades teóricas; (b) tabela de estatísticas descritivas por configuração; (c) gráfico comparativo dos dez algoritmos em `n=100.000`; (d) gráficos de crescimento de Bubble/Insertion, Selection, família `n log n` e algoritmos dependentes das chaves; (e) uma nota clara sobre as configurações ausentes.
6. Discutir os resultados, não apenas apresentar rankings: explicar a relação entre tamanho da entrada, disposição inicial, parâmetros `k`/`d`, sequência de gaps e escolhas da implementação.

## 10. Texto-base preliminar para adaptar ao relatório

> Os experimentos disponíveis reúnem 1.130 medições, organizadas em 113 configurações completas, com dez repetições por configuração. Foram analisados vetores de 1.000, 10.000, 100.000 e 1.000.000 de elementos, em ordem crescente, aleatória e decrescente. A matriz de um milhão de elementos está incompleta para os algoritmos quadráticos: sete configurações não possuem medições e, portanto, foram mantidas como ausentes, sem estimativa de seus tempos.
>
> Os resultados indicam forte sensibilidade do Bubble Sort e do Insertion Sort à disposição dos dados. Para 100.000 elementos, ambos apresentam tempos de poucos milissegundos na entrada crescente, mas passam a dezenas ou centenas de milhares de milissegundos nos casos aleatório e decrescente. Em contrapartida, Selection Sort apresenta tempos semelhantes entre as três disposições, em consonância com seu custo quadrático independente da ordem inicial. Merge Sort, Heap Sort e Quick Sort exibem crescimento mais contido nas faixas observadas, compatível com a família `n log n` nas configurações analisadas.
>
> Counting Sort e Radix Sort registram os menores tempos entre os algoritmos com a matriz completa para entradas aleatórias grandes. Essa observação deve ser interpretada sob as condições de entrada utilizadas — permutações dos valores de `0` a `n-1` — e considerando as diferenças de implementação e de delimitação dos tempos medidos. O resultado de Bucket Sort é provisório, uma vez que a versão analisada utiliza um método de ordenação pronto dentro dos buckets. Assim, as conclusões definitivas devem ser estabelecidas após padronizar a cronometragem e validar a implementação, mantendo os tempos individuais como evidência experimental.

**Nota de uso:** o texto acima é um rascunho analítico e não deve ser apresentado como conclusão definitiva até que as ressalvas da Seção 8 sejam resolvidas ou explicitamente assumidas na metodologia.

## 11. Arquivos de origem usados nesta análise

- Enunciado: `docs/EnunciadoTrabalho.md`.
- Tempos individuais: `TP1/src/resultados/centralizados/tempos_consolidados.csv`.
- Integridade das configurações: `TP1/src/resultados/centralizados/integridade_configuracoes.csv`.
- Estatísticas resumidas: `TP1/src/resultados/estatisticas/estatisticas_por_configuracao.csv`.
- Estatísticas por sessão: `TP1/src/resultados/estatisticas/estatisticas_por_sessao.csv`.
- Cobertura experimental: `TP1/src/resultados/estatisticas/cobertura_configuracoes.csv`.
- Anomalias de leitura: `TP1/src/resultados/estatisticas/anomalias_dados.csv`.
- Implementações Java e scripts de execução do TP1, conforme o snapshot de contexto fornecido.
