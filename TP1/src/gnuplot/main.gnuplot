# ============================================================================
# TP1 - Algoritmos e Classificacao de Dados
# Geracao de graficos a partir de tempos_consolidados.csv
# Requer Gnuplot 5.4+ (terminal pdfcairo e estilo boxplot).
#
# Execucao a partir de TP1/src:
#   gnuplot graficos_tp1.gnuplot
#
# Caminhos customizados:
#   gnuplot -e "DATA='resultados/centralizados/tempos_consolidados.csv'; OUTDIR='figuras'" graficos_tp1.gnuplot
#
# Saidas:
#   OUTDIR/resumo_estatistico_gnuplot.csv
#   OUTDIR/cobertura_gnuplot.csv
#   PDFs para comparacao, evolucao, boxplot e cobertura.
# ============================================================================

if (!exists("DATA")) DATA = "resultados/centralizados/tempos_consolidados.csv"
if (!exists("OUTDIR")) OUTDIR = "figuras"

system sprintf("mkdir -p '%s'", OUTDIR)

# Dados: algoritmo,n,caso,disposicao,semente,sessao,repeticao,t_ns
set datafile separator comma
set datafile commentschars "#"
set encoding utf8

ALGS = "BubbleSort InsertionSort SelectionSort ShellSort HeapSort MergeSort QuickSort CountingSort RadixSort BucketSort"
DISPS = "CRESCENTE ALEATORIO DECRESCENTE"
SIZES = "1000 10000 100000 1000000"

# Estatisticas para graficos: mediana em ms e cobertura por configuracao.
SUMMARY = sprintf("%s/resumo_estatistico_gnuplot.csv", OUTDIR)

set print SUMMARY
print "algoritmo,algoritmo_id,n,tamanho_id,disposicao,disposicao_id,configuracao_id,n_medicoes,media_ms,mediana_ms,minimo_ms,maximo_ms,desvio_padrao_ms,cv_pct"

do for [ia=1:words(ALGS)] {
    alg = word(ALGS, ia)
    do for [id=1:words(DISPS)] {
        disp = word(DISPS, id)
        do for [is=1:words(SIZES)] {
            n = int(word(SIZES, is))
            config_id = (is - 1) * words(DISPS) + id

            # A coluna t_ns (coluna 8) e convertida de nanossegundos para ms.
            # Reinicializa o contador para que uma configuracao ausente nao herde
            # a contagem da configuracao anterior caso stats nao encontre pontos.
            S_records = 0
            stats DATA using ((strcol(1) eq alg && $2 == n && strcol(4) eq disp) ? $8/1000000.0 : 1/0) name "S" nooutput

            if (S_records > 0) {
                cv = (S_mean != 0 ? 100.0 * S_ssd / S_mean : 0.0)
                print sprintf("%s,%d,%d,%d,%s,%d,%d,%d,%.9f,%.9f,%.9f,%.9f,%.9f,%.6f", alg, ia, n, is, disp, id, config_id, S_records, S_mean, S_median, S_min, S_max, S_ssd, cv)
            } else {
                # Mantem as configuracoes ausentes explicitas; NaN cria lacunas nos graficos.
                print sprintf("%s,%d,%d,%d,%s,%d,%d,0,NaN,NaN,NaN,NaN,NaN,NaN", alg, ia, n, is, disp, id, config_id)
            }
        }
    }
}
set print

# ---------------------------------------------------------------------------
# 1) Matriz de cobertura: cada combinacao algoritmo x tamanho/disposicao.
# ---------------------------------------------------------------------------
set terminal pdfcairo enhanced color size 20cm, 11cm font "Arial,9"
set output sprintf("%s/10_matriz_cobertura_configuracoes.pdf", OUTDIR)
set title "Cobertura das configuracoes experimentais"
set xlabel "Tamanho e disposicao da entrada"
set ylabel "Algoritmo"
set xrange [0.5:12.5]
set yrange [10.5:0.5]
set xtics ("1k\nCresc." 1, "1k\nAleat." 2, "1k\nDecresc." 3, \
           "10k\nCresc." 4, "10k\nAleat." 5, "10k\nDecresc." 6, \
           "100k\nCresc." 7, "100k\nAleat." 8, "100k\nDecresc." 9, \
           "1M\nCresc." 10, "1M\nAleat." 11, "1M\nDecresc." 12)
set ytics ("BubbleSort" 1, "InsertionSort" 2, "SelectionSort" 3, "ShellSort" 4, "HeapSort" 5, "MergeSort" 6, "QuickSort" 7, "CountingSort" 8, "RadixSort" 9, "BucketSort" 10)
set grid xtics ytics
set key off
set colorbox
set cbrange [0:1]
set cbtics ("Ausente" 0, "Completa" 1)
set palette defined (0 "#c85b55", 1 "#4b956c")
plot SUMMARY using 7:2:(($8 == 10) ? 1 : 0) with points pointtype 5 pointsize 2.5 palette notitle
unset output

# ---------------------------------------------------------------------------
# 2) Comparacao de medianas entre algoritmos para cada tamanho/disposicao.
#    E usado grafico de pontos, pois escala logaritmica nao combina com barras
#    que partem de zero. O arquivo para n=100000 aleatorio segue o nome do LaTeX.
# ---------------------------------------------------------------------------
set terminal pdfcairo enhanced color size 17cm, 9.5cm font "Arial,10"
set logscale y 10
set format y "10^{%L}"
set xlabel "Algoritmo"
set ylabel "Mediana do tempo (ms) - escala logaritmica"
set xrange [0.4:10.6]
set xtics ("Bubble" 1, "Insertion" 2, "Selection" 3, "Shell" 4, "Heap" 5, "Merge" 6, "Quick" 7, "Counting" 8, "Radix" 9, "Bucket" 10) rotate by -25
set grid ytics
set key off

do for [is=1:words(SIZES)] {
    n = int(word(SIZES, is))
    do for [id=1:words(DISPS)] {
        disp = word(DISPS, id)
        if (n == 100000 && disp eq "ALEATORIO") {
            fname = sprintf("%s/01_comparacao_n100000_aleatorio.pdf", OUTDIR)
        } else {
            fname = sprintf("%s/comparacao_n%d_%s.pdf", OUTDIR, n, disp)
        }
        set output fname
        set title sprintf("Comparacao dos algoritmos - n=%d - %s", n, disp)
        plot SUMMARY using ((($3 == n && strcol(5) eq disp) ? $2 : 1/0)):((($3 == n && strcol(5) eq disp) ? $10 : 1/0)) with points pointtype 7 pointsize 1.5 notitle
        unset output
    }
}
unset logscale y
set format y "%g"

# ---------------------------------------------------------------------------
# 3) Evolucao das medianas para cada algoritmo, por disposicao.
#    NaN nas configuracoes ausentes evita inventar tempos ou interpolar dados.
# ---------------------------------------------------------------------------
set terminal pdfcairo enhanced color size 16cm, 9cm font "Arial,10"
set logscale x 10
set logscale y 10
set format x "10^{%L}"
set format y "10^{%L}"
set xlabel "Tamanho da entrada (n)"
set ylabel "Mediana do tempo (ms) - escala logaritmica"
set xrange [800:1300000]
set grid xtics ytics mxtics mytics
set key outside top center horizontal

# Os nomes dos arquivos Bubble, Selection, Merge, Quick, Counting, Radix e
# Bucket correspondem aos includes ja usados no documento SBC do projeto.
do for [ia=1:words(ALGS)] {
    alg = word(ALGS, ia)
    if (alg eq "BubbleSort") {
        fname = sprintf("%s/02_evolucao_BubbleSort.pdf", OUTDIR)
    } else if (alg eq "SelectionSort") {
        fname = sprintf("%s/03_evolucao_SelectionSort.pdf", OUTDIR)
    } else if (alg eq "MergeSort") {
        fname = sprintf("%s/04_evolucao_MergeSort.pdf", OUTDIR)
    } else if (alg eq "QuickSort") {
        fname = sprintf("%s/05_evolucao_QuickSort.pdf", OUTDIR)
    } else if (alg eq "CountingSort") {
        fname = sprintf("%s/06_evolucao_CountingSort.pdf", OUTDIR)
    } else if (alg eq "RadixSort") {
        fname = sprintf("%s/07_evolucao_RadixSort.pdf", OUTDIR)
    } else if (alg eq "BucketSort") {
        fname = sprintf("%s/08_evolucao_BucketSort.pdf", OUTDIR)
    } else if (alg eq "InsertionSort") {
        fname = sprintf("%s/11_evolucao_InsertionSort.pdf", OUTDIR)
    } else if (alg eq "HeapSort") {
        fname = sprintf("%s/12_evolucao_HeapSort.pdf", OUTDIR)
    } else {
        fname = sprintf("%s/13_evolucao_ShellSort.pdf", OUTDIR)
    }

    set output fname
    set title sprintf("Evolucao do tempo - %s", alg)
    plot SUMMARY using ((strcol(1) eq alg && strcol(5) eq "CRESCENTE") ? $3 : 1/0):((strcol(1) eq alg && strcol(5) eq "CRESCENTE") ? $10 : 1/0) with linespoints linewidth 2 pointtype 7 title "Crescente", \
         SUMMARY using ((strcol(1) eq alg && strcol(5) eq "ALEATORIO") ? $3 : 1/0):((strcol(1) eq alg && strcol(5) eq "ALEATORIO") ? $10 : 1/0) with linespoints linewidth 2 pointtype 5 title "Aleatoria", \
         SUMMARY using ((strcol(1) eq alg && strcol(5) eq "DECRESCENTE") ? $3 : 1/0):((strcol(1) eq alg && strcol(5) eq "DECRESCENTE") ? $10 : 1/0) with linespoints linewidth 2 pointtype 9 title "Decrescente"
    unset output
}
unset logscale x
unset logscale y
set format x "%g"
set format y "%g"

# ---------------------------------------------------------------------------
# 4) Boxplot das 10 repeticoes para n=100000 aleatorio, diretamente do CSV bruto.
# ---------------------------------------------------------------------------
set terminal pdfcairo enhanced color size 19cm, 10cm font "Arial,9"
set output sprintf("%s/09_boxplot_n100000_aleatorio.pdf", OUTDIR)
set title "Distribuicao das medicoes - n=100000 - entrada aleatoria"
set xlabel "Algoritmo"
set ylabel "Tempo de execucao (ms) - escala logaritmica"
set logscale y 10
set format y "10^{%L}"
set xrange [0.5:10.5]
set xtics ("Bubble" 1, "Insertion" 2, "Selection" 3, "Shell" 4, "Heap" 5, "Merge" 6, "Quick" 7, "Counting" 8, "Radix" 9, "Bucket" 10) rotate by -25
set grid ytics
set key off
set style data boxplot
set style boxplot outliers pointtype 7
set style boxplot range 1.5
plot DATA using ((strcol(1) eq "BubbleSort" && $2 == 100000 && strcol(4) eq "ALEATORIO") ? 1 : 1/0):($8/1000000.0) with boxplot, \
     DATA using ((strcol(1) eq "InsertionSort" && $2 == 100000 && strcol(4) eq "ALEATORIO") ? 2 : 1/0):($8/1000000.0) with boxplot, \
     DATA using ((strcol(1) eq "SelectionSort" && $2 == 100000 && strcol(4) eq "ALEATORIO") ? 3 : 1/0):($8/1000000.0) with boxplot, \
     DATA using ((strcol(1) eq "ShellSort" && $2 == 100000 && strcol(4) eq "ALEATORIO") ? 4 : 1/0):($8/1000000.0) with boxplot, \
     DATA using ((strcol(1) eq "HeapSort" && $2 == 100000 && strcol(4) eq "ALEATORIO") ? 5 : 1/0):($8/1000000.0) with boxplot, \
     DATA using ((strcol(1) eq "MergeSort" && $2 == 100000 && strcol(4) eq "ALEATORIO") ? 6 : 1/0):($8/1000000.0) with boxplot, \
     DATA using ((strcol(1) eq "QuickSort" && $2 == 100000 && strcol(4) eq "ALEATORIO") ? 7 : 1/0):($8/1000000.0) with boxplot, \
     DATA using ((strcol(1) eq "CountingSort" && $2 == 100000 && strcol(4) eq "ALEATORIO") ? 8 : 1/0):($8/1000000.0) with boxplot, \
     DATA using ((strcol(1) eq "RadixSort" && $2 == 100000 && strcol(4) eq "ALEATORIO") ? 9 : 1/0):($8/1000000.0) with boxplot, \
     DATA using ((strcol(1) eq "BucketSort" && $2 == 100000 && strcol(4) eq "ALEATORIO") ? 10 : 1/0):($8/1000000.0) with boxplot
unset output
unset logscale y
set format y "%g"

print sprintf("Graficos gerados em: %s", OUTDIR)
print sprintf("Resumo estatistico: %s", SUMMARY)
print sprintf("Fonte de dados: %s", DATA)
