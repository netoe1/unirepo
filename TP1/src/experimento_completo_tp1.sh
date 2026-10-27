#!/usr/bin/env bash
# Script de Execução Completa dos Experimentos do TP1

set -euo pipefail
cd "$(dirname -- "${BASH_SOURCE[0]}")"

REPETICOES=10
SEMENTE=2026
PASTA_SAIDA="resultados"
PREFIXO_SAIDA="${PASTA_SAIDA}/tp1_completo"

# Garante que a pasta de resultados existe
mkdir -p "$PASTA_SAIDA"

# Verificação do ambiente Java
if ! command -v javac >/dev/null 2>&1 || ! command -v java >/dev/null 2>&1; then
    echo "Erro: instale um JDK e verifique se java e javac estao no PATH." >&2
    exit 127
fi

printf '\n[TP1] Compilando o projeto...\n'
javac -encoding UTF-8 ./*.java

printf '\n[TP1] Iniciando a bateria de experimentos...\n'

# Função auxiliar para invocar a Main.java de forma limpa e padronizada
rodar() {
    local algoritmo="$1"
    local n="$2"
    local disposicao="$3"
    local caso="$4"

    printf '\n[TP1] %s | n=%s | disposicao=%s | caso=%s\n' \
        "$algoritmo" "$n" "$disposicao" "$caso"

    java -Xms256m -Xmx2g -cp . Main \
        --repeticoes "$REPETICOES" \
        --caso "$caso" \
        --algoritmo "$algoritmo" \
        --disposicao "$disposicao" \
        --n "$n" \
        --semente "$SEMENTE" \
        --saida "$PREFIXO_SAIDA" \
        --no-color
}

DISPOSICOES=("CRESCENTE" "DECRESCENTE" "ALEATORIO")
ALGORITMOS=(
    "MergeSort" "QuickSort" "HeapSort" "ShellSort"  
    "CountingSort" "RadixSort" "BucketSort"  
    "InsertionSort" "BubbleSort" "SelectionSort" 
)
TAMANHOS=( 1000 10000 100000 1000000)

for alg in "${ALGORITMOS[@]}"; do
    for n in "${TAMANHOS[@]}"; do
        for disp in "${DISPOSICOES[@]}"; do

            # Determina o rótulo do caso teórico para o registo no CSV
            CASO="MEDIO"
            if [[ "$disp" == "CRESCENTE" ]]; then CASO="MELHOR"; fi
            if [[ "$disp" == "ALEATORIO" ]]; then CASO="MEDIO"; fi
            if [[ "$disp" == "DECRESCENTE" ]]; then CASO="PIOR"; fi

            # Chama a execução SEM NENHUMA RESTRIÇÃO
            rodar "$alg" "$n" "$disp" "$CASO"

        done
    done
done

echo
echo "[TP1] Todos os testes concluídos! Os CSVs foram gerados na pasta '${PASTA_SAIDA}/'."