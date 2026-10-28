# Gráficos do TP1 com Gnuplot

Este script lê diretamente `tempos_consolidados.csv`, calcula estatísticas descritivas usando o comando `stats` do próprio Gnuplot e gera figuras PDF para o relatório SBC/Overleaf.

## Requisitos

- Gnuplot 5.4 ou superior;
- terminal `pdfcairo` habilitado;
- CSV consolidado com cabeçalho e colunas nesta ordem:

```text
algoritmo,n,caso,disposicao,semente,sessao,repeticao,t_ns
```

## Estrutura esperada

Salve `graficos_tp1.gnuplot` dentro de `TP1/src`. O CSV deve estar em:

```text
TP1/src/resultados/centralizados/tempos_consolidados.csv
```

## Executar

No Debian/Ubuntu, instale o Gnuplot (se necessário):

```bash
sudo apt install gnuplot-nox
```

Depois execute a partir de `TP1/src`:

```bash
gnuplot graficos_tp1.gnuplot
```

Ou informe caminhos diferentes:

```bash
gnuplot -e "DATA='resultados/centralizados/tempos_consolidados.csv'; OUTDIR='figuras'" graficos_tp1.gnuplot
```

## Arquivos gerados

A pasta `figuras/` conterá:

- `resumo_estatistico_gnuplot.csv`: média, mediana, mínimo, máximo, desvio-padrão amostral e coeficiente de variação por algoritmo/tamanho/disposição; a cobertura de cada configuração aparece na coluna `n_medicoes`;
- `10_matriz_cobertura_configuracoes.pdf`: cobertura de todas as configurações;
- `01_comparacao_n100000_aleatorio.pdf`: comparação de medianas usada no relatório LaTeX;
- outros 11 gráficos de comparação por tamanho/disposição;
- `02_evolucao_BubbleSort.pdf`, `03_evolucao_SelectionSort.pdf`, `04_evolucao_MergeSort.pdf`, `05_evolucao_QuickSort.pdf`, `06_evolucao_CountingSort.pdf`, `07_evolucao_RadixSort.pdf`, `08_evolucao_BucketSort.pdf`;
- `11_evolucao_InsertionSort.pdf`, `12_evolucao_HeapSort.pdf`, `13_evolucao_ShellSort.pdf`;
- `09_boxplot_n100000_aleatorio.pdf` com as observações individuais do CSV bruto.

As sete configurações ausentes ficam explícitas com `n_medicoes=0` e `NaN` nas estatísticas. O script não inventa tempos para completar a matriz. O gráfico de cobertura e os gráficos de evolução são produzidos a partir das mesmas estatísticas. Se algumas medições vierem de versões antigas dos algoritmos, regenere os dados antes do relatório final.

## Usar no Overleaf

Copie os PDFs para a pasta `figuras/` do projeto Overleaf. Os nomes dos arquivos principais são compatíveis com os `\includegraphics` do relatório SBC compartilhado na conversa.

> Nota: o Gnuplot gera estatísticas com base no comando `stats`; o script utiliza `S_ssd` (desvio-padrão amostral) para a coluna de dispersão. Confira as tabelas acadêmicas finais contra o script estatístico do projeto caso os resultados sejam processados também por outra ferramenta.
