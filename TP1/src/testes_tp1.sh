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

# Experimentos: 10 medicoes por configuracao, conforme o minimo recomendado no TP1.
# Altere REPETICOES se precisar; mantenha pelo menos 10 para os resultados oficiais.
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
    java -Xms128m -Xmx1g -cp . TesteGenerator \
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
# Em n=10000, roda somente o caso crescente para evitar uma bateria quadratica muito demorada.
for algoritmo in BubbleSort InsertionSort; do
    rodar MELHOR "$algoritmo" CRESCENTE 1000
    rodar MEDIO  "$algoritmo" ALEATORIO 1000
    rodar PIOR   "$algoritmo" DECRESCENTE 1000
    rodar MELHOR "$algoritmo" CRESCENTE 10000
done

# Selection faz quantidade de comparacoes quadratica em todas as disposicoes.
rodar MEDIO SelectionSort CRESCENTE 1000
rodar MEDIO SelectionSort DECRESCENTE 1000
rodar MEDIO SelectionSort ALEATORIO 1000

# Quick Sort usa o ultimo elemento como pivo: entradas ordenadas sao limitadas a n=1000.
rodar PIOR  QuickSort CRESCENTE 1000
rodar PIOR  QuickSort DECRESCENTE 1000
rodar MEDIO QuickSort ALEATORIO 1000
rodar MEDIO QuickSort ALEATORIO 10000
rodar MEDIO QuickSort ALEATORIO 100000

# Algoritmos adequados a entradas maiores: tres disposicoes ate 100000 e aleatorio em 1 milhao.
# Para estes algoritmos, 'MEDIO' e um rotulo experimental; a disposicao real fica registrada separadamente.
for algoritmo in ShellSort MergeSort HeapSort CountingSort RadixSort BucketSort; do
    for n in 1000 10000 100000; do
        rodar MEDIO "$algoritmo" CRESCENTE "$n"
        rodar MEDIO "$algoritmo" DECRESCENTE "$n"
        rodar MEDIO "$algoritmo" ALEATORIO "$n"
    done
    rodar MEDIO "$algoritmo" ALEATORIO 1000000
done

echo
echo "[TP1] Testes e experimentos concluidos. Os CSVs estao em resultados/."
