# TP1 - Graficos de melhor, medio e pior caso para cada algoritmo
# Fonte: CSV bruto com as colunas:
# algoritmo,n,caso,disposicao,semente,sessao,repeticao,t_ns
#
# Uso a partir de TP1/src:
#   gnuplot graficos_casos_tp1.gnuplot
#
# Se o caminho for diferente:
#   gnuplot -e "DATA='caminho/tempos_consolidados.csv'; OUTDIR='figuras'" graficos_casos_tp1.gnuplot
#
# Cada algoritmo gera um PDF e um PNG.
# As linhas suaves sao interpolacoes cubicas entre as medianas medidas;
# os pontos marcam as medianas reais. Nao sao novas medicoes.

DATA = "tempos_consolidados.csv"
OUTDIR = "figuras_casos"

# Criar a pasta de saida (Linux/macOS).
system sprintf("mkdir -p '%s'", OUTDIR)

set datafile separator ","
set datafile commentschars "#"
set encoding utf8

# Definicoes dos algoritmos e tamanhos do experimento.
ALGORITMOS = "BubbleSort InsertionSort SelectionSort ShellSort HeapSort MergeSort QuickSort CountingSort RadixSort BucketSort"
DISPOSICOES = "CRESCENTE ALEATORIO DECRESCENTE"
TAMANHOS = "1000 10000 100000 1000000"

# Primeiro, calcular a mediana de cada configuracao a partir das repeticoes.
# O tempo original esta em nanossegundos; dividimos por 1.000.000 para ms.
RESUMO = sprintf("%s/medianas_por_configuracao.csv", OUTDIR)
set print RESUMO
print "algoritmo,n,disposicao,mediana_ms"

do for [ia=1:words(ALGORITMOS)] {
    algoritmo = word(ALGORITMOS, ia)

    do for [it=1:words(TAMANHOS)] {
        tamanho = int(word(TAMANHOS, it))

        do for [id=1:words(DISPOSICOES)] {
            disposicao = word(DISPOSICOES, id)

            # Zera o contador antes de cada chamada para tratar configuracoes ausentes.
            S_records = 0
            stats DATA using ((strcol(1) eq algoritmo && $2 == tamanho && strcol(4) eq disposicao) ? $8/1000000.0 : 1/0) name "S" nooutput

            if (S_records > 0) {
                print sprintf("%s,%d,%s,%.9f", algoritmo, tamanho, disposicao, S_median)
            } else {
                # Mantem a configuracao ausente explicita; nao inventa um tempo.
                print sprintf("%s,%d,%s,NaN", algoritmo, tamanho, disposicao)
            }
        }
    }
}
set print

# Os eixos usam log10 para mostrar bem tempos que diferem por muitas ordens
# de magnitude. Os rotulos continuam mostrando unidades compreensiveis.
set xlabel "Tamanho da entrada (n)"
set ylabel "Mediana do tempo (ms, escala logaritmica)"
set xtics ("1.000" 3, "10.000" 4, "100.000" 5, "1.000.000" 6)
set ytics ("0,1" -1, "1" 0, "10" 1, "100" 2, "1.000" 3, "10.000" 4, "100.000" 5, "1.000.000" 6)
set xrange [2.8:6.2]
set yrange [-1.2:5.9]
set grid xtics ytics lc rgb "#dddddd" lw 1
set border linewidth 1
set key outside bottom center horizontal maxcols 3
set pointsize 1.2

# Cores solicitadas:
# verde = crescente (rotulado melhor caso)
# amarelo = aleatorio (rotulado caso medio)
# vermelho = decrescente (rotulado pior caso)
COR_MELHOR = "#16803c"
COR_MEDIO  = "#e0ad00"
COR_PIOR   = "#d62728"

# Criar um par de arquivos (PDF e PNG) para cada algoritmo.
do for [ia=1:words(ALGORITMOS)] {
    algoritmo = word(ALGORITMOS, ia)
    set title sprintf("%s - melhor, medio e pior caso", algoritmo)

    # PDF vetorial: recomendado para incluir no Overleaf.
    set terminal pdfcairo enhanced color size 16cm, 9cm font "Arial,10"
    set output sprintf("%s/%s_casos.pdf", OUTDIR, algoritmo)

    plot \
        RESUMO using (strcol(1) eq algoritmo && strcol(3) eq "CRESCENTE" && $4 > 0 ? log10($2) : 1/0):(strcol(1) eq algoritmo && strcol(3) eq "CRESCENTE" && $4 > 0 ? log10($4) : 1/0) smooth csplines with lines lc rgb COR_MELHOR lw 2.5 title "Melhor (crescente)", \
        '' using (strcol(1) eq algoritmo && strcol(3) eq "CRESCENTE" && $4 > 0 ? log10($2) : 1/0):(strcol(1) eq algoritmo && strcol(3) eq "CRESCENTE" && $4 > 0 ? log10($4) : 1/0) with points pt 7 ps 1.1 lc rgb COR_MELHOR notitle, \
        '' using (strcol(1) eq algoritmo && strcol(3) eq "ALEATORIO" && $4 > 0 ? log10($2) : 1/0):(strcol(1) eq algoritmo && strcol(3) eq "ALEATORIO" && $4 > 0 ? log10($4) : 1/0) smooth csplines with lines lc rgb COR_MEDIO lw 2.5 title "Medio (aleatorio)", \
        '' using (strcol(1) eq algoritmo && strcol(3) eq "ALEATORIO" && $4 > 0 ? log10($2) : 1/0):(strcol(1) eq algoritmo && strcol(3) eq "ALEATORIO" && $4 > 0 ? log10($4) : 1/0) with points pt 7 ps 1.1 lc rgb COR_MEDIO notitle, \
        '' using (strcol(1) eq algoritmo && strcol(3) eq "DECRESCENTE" && $4 > 0 ? log10($2) : 1/0):(strcol(1) eq algoritmo && strcol(3) eq "DECRESCENTE" && $4 > 0 ? log10($4) : 1/0) smooth csplines with lines lc rgb COR_PIOR lw 2.5 title "Pior (decrescente)", \
        '' using (strcol(1) eq algoritmo && strcol(3) eq "DECRESCENTE" && $4 > 0 ? log10($2) : 1/0):(strcol(1) eq algoritmo && strcol(3) eq "DECRESCENTE" && $4 > 0 ? log10($4) : 1/0) with points pt 7 ps 1.1 lc rgb COR_PIOR notitle

    unset output

    # PNG para visualizacao rapida fora do Overleaf.
    set terminal pngcairo enhanced color size 1800, 1000 font "DejaVu Sans,14"
    set output sprintf("%s/%s_casos.png", OUTDIR, algoritmo)
    replot
    unset output
}

print sprintf("Graficos gerados em: %s", OUTDIR)
print sprintf("Resumo das medianas: %s", RESUMO)
