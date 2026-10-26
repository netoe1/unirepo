#!/usr/bin/env bash
# Testes do TP1: corretude em vetores pequenos e, opcionalmente, experimentos de desempenho.
# Uso: ./testes_tp1.sh [corretude|desempenho]

set -euo pipefail
cd "$(dirname -- "${BASH_SOURCE[0]}")"

MODO="${1:-corretude}"
if [[ "$MODO" != "corretude" && "$MODO" != "desempenho" ]]; then
    echo "Uso: $0 [corretude|desempenho]" >&2
    exit 2
fi

if ! command -v javac >/dev/null 2>&1 || ! command -v java >/dev/null 2>&1; then
    echo "Erro: instale um JDK e verifique se java e javac estao no PATH." >&2
    exit 127
fi

printf '\n[TP1] Compilando o projeto...\n'
javac -encoding UTF-8 ./*.java

printf '\n[TP1] Executando testes de corretude...\n'
java -cp . TestesCorretude

if [[ "$MODO" == "corretude" ]]; then
    echo "[TP1] Testes de corretude concluidos. Para medir desempenho: $0 desempenho"
    exit 0
fi

# O enunciado pede no minimo 10 medicoes por configuracao.
REPETICOES=10
SEMENTE=2026
SAIDA="resultados/tp1"
mkdir -p resultados

rodar() {
    local caso="$1"
    local algoritmo="$2"
    local disposicao="$3"
    local n="$4"

    printf '\n[TP1] %s | n=%s | disposicao=%s | caso=%s\n' \
        "$algoritmo" "$n" "$disposicao" "$caso"
    java -Xms128m -Xmx1g -cp . Main \
        --repeticoes "$REPETICOES" \
        --caso "$caso" \
        --algoritmo "$algoritmo" \
        --disposicao "$disposicao" \
        --n "$n" \
        --semente "$SEMENTE" \
        --saida "$SAIDA" \
        --no-color
}

# Bubble e Insertion: melhor, medio e pior caso em n=1000.
# Em n=10000, mede somente a entrada crescente para limitar o custo quadratico.
for algoritmo in BubbleSort InsertionSort; do
    rodar MELHOR "$algoritmo" CRESCENTE 1000
    rodar MEDIO  "$algoritmo" ALEATORIO 1000
    rodar PIOR   "$algoritmo" DECRESCENTE 1000
    rodar MELHOR "$algoritmo" CRESCENTE 10000
done

# Selection tem custo quadratico nas tres disposicoes; n=1000.
rodar MEDIO SelectionSort CRESCENTE 1000
rodar MEDIO SelectionSort DECRESCENTE 1000
rodar MEDIO SelectionSort ALEATORIO 1000

# Quick Sort usa o ultimo elemento como pivo; entradas ordenadas limitadas a n=1000.
rodar PIOR  QuickSort CRESCENTE 1000
rodar PIOR  QuickSort DECRESCENTE 1000
rodar MEDIO QuickSort ALEATORIO 1000
rodar MEDIO QuickSort ALEATORIO 10000
rodar MEDIO QuickSort ALEATORIO 100000

# Algoritmos adequados a entradas maiores: tres disposicoes ate 100000 e aleatorio em 1 milhao.
# Para esses algoritmos, MEDIO e um rótulo experimental; a disposicao real tambem fica registrada.
for algoritmo in ShellSort MergeSort HeapSort CountingSort RadixSort BucketSort; do
    for n in 1000 10000 100000; do
        rodar MEDIO "$algoritmo" CRESCENTE "$n"
        rodar MEDIO "$algoritmo" DECRESCENTE "$n"
        rodar MEDIO "$algoritmo" ALEATORIO "$n"
    done
    rodar MEDIO "$algoritmo" ALEATORIO 1000000
done

echo
echo "[TP1] Testes e experimentos concluidos. Os CSVs foram gravados com prefixo $SAIDA."
