#!/bin/bash
# Executa a bateria de experimentos do TP1 chamando o TesteGenerator diretamente.
#
# Uso:
#   ./executar_testes.sh                  # roda todos os algoritmos
#   ./executar_testes.sh merge heap       # roda so os algoritmos citados
#
# Algoritmos: bubble inserction selection shell merge quick heap counting radix bucket
#
# Variaveis opcionais (ambiente):
#   REPETICOES (padrao 10)   CASO (padrao MEDIO)   SEMENTE (padrao 42)
#   SAIDA (padrao resultados/tp1)   JAVA_OPTS (padrao -Xms128m -Xmx1g)

cd "$(dirname "${BASH_SOURCE[0]}")" || exit 1

REPETICOES="${REPETICOES:-10}"
CASO="${CASO:-MEDIO}"
SEMENTE="${SEMENTE:-42}"
SAIDA="${SAIDA:-resultados/tp1}"
JAVA_OPTS="${JAVA_OPTS:--Xms128m -Xmx1g}"
read -r -a OPCOES_JVM <<< "$JAVA_OPTS"

FALHAS=0

# ---------------------------------------------------------------- compilacao
function compilar(){
    if ! command -v java >/dev/null 2>&1; then
        echo "ERRO: 'java' nao encontrado no PATH." >&2
        exit 1
    fi
    local precisa=0
    if [[ ! -f TesteGenerator.class ]]; then
        precisa=1
    else
        local fonte
        for fonte in *.java; do
            if [[ "$fonte" -nt TesteGenerator.class ]]; then
                precisa=1
                break
            fi
        done
    fi
    if [[ $precisa -eq 1 ]]; then
        if ! command -v javac >/dev/null 2>&1; then
            echo "ERRO: 'javac' nao encontrado no PATH (instale um JDK)." >&2
            exit 1
        fi
        echo "Compilando os arquivos .java..."
        javac -encoding UTF-8 *.java || exit 1
    fi
}

# Executa UMA configuracao: rodar <Algoritmo> <DISPOSICAO> <n>
function rodar(){
    local algoritmo="$1" disposicao="$2" n="$3"
    echo ">>> $algoritmo | $disposicao | n=$n | repeticoes=$REPETICOES | semente=$SEMENTE"
    if ! java "${OPCOES_JVM[@]}" -cp . TesteGenerator \
            --repeticoes "$REPETICOES" --caso "$CASO" \
            --algoritmo "$algoritmo" --disposicao "$disposicao" \
            --n "$n" --semente "$SEMENTE" --saida "$SAIDA" --no-color; then
        echo "!!! FALHOU: $algoritmo | $disposicao | n=$n" >&2
        FALHAS=$((FALHAS + 1))
    fi
}

# ------------------------------------------------------------- algoritmos
function bubble(){
    rodar BubbleSort CRESCENTE 1000
    rodar BubbleSort DECRESCENTE 1000
    rodar BubbleSort ALEATORIO 1000
    rodar BubbleSort CRESCENTE 10000
    rodar BubbleSort DECRESCENTE 10000
    rodar BubbleSort ALEATORIO 10000
}

function inserction(){
    rodar InsertionSort CRESCENTE 1000
    rodar InsertionSort DECRESCENTE 1000
    rodar InsertionSort ALEATORIO 1000
    rodar InsertionSort CRESCENTE 10000
    rodar InsertionSort DECRESCENTE 10000
    rodar InsertionSort ALEATORIO 10000
}

function selection(){
    rodar SelectionSort CRESCENTE 1000
    rodar SelectionSort DECRESCENTE 1000
    rodar SelectionSort ALEATORIO 1000
    rodar SelectionSort CRESCENTE 10000
    rodar SelectionSort DECRESCENTE 10000
    rodar SelectionSort ALEATORIO 10000
}

function shell(){
    rodar ShellSort CRESCENTE 1000
    rodar ShellSort DECRESCENTE 1000
    rodar ShellSort ALEATORIO 1000
    rodar ShellSort CRESCENTE 10000
    rodar ShellSort DECRESCENTE 10000
    rodar ShellSort ALEATORIO 10000
    rodar ShellSort CRESCENTE 100000
    rodar ShellSort DECRESCENTE 100000
    rodar ShellSort ALEATORIO 100000
    rodar ShellSort CRESCENTE 1000000
    rodar ShellSort DECRESCENTE 1000000
    rodar ShellSort ALEATORIO 1000000
}

function merge(){
    rodar MergeSort CRESCENTE 1000
    rodar MergeSort DECRESCENTE 1000
    rodar MergeSort ALEATORIO 1000
    rodar MergeSort CRESCENTE 10000
    rodar MergeSort DECRESCENTE 10000
    rodar MergeSort ALEATORIO 10000
    rodar MergeSort CRESCENTE 100000
    rodar MergeSort DECRESCENTE 100000
    rodar MergeSort ALEATORIO 100000
    rodar MergeSort CRESCENTE 1000000
    rodar MergeSort DECRESCENTE 1000000
    rodar MergeSort ALEATORIO 1000000
}

# QuickSort (pivo no ultimo elemento): crescente/decrescente degeneram para O(n^2)
# e recursao profunda, por isso so ate n=1000; aleatorio vai ate 100000.
function quick(){
    rodar QuickSort CRESCENTE 1000
    rodar QuickSort DECRESCENTE 1000
    rodar QuickSort ALEATORIO 1000
    rodar QuickSort ALEATORIO 10000
    rodar QuickSort ALEATORIO 100000
}

function heap(){
    rodar HeapSort CRESCENTE 1000
    rodar HeapSort DECRESCENTE 1000
    rodar HeapSort ALEATORIO 1000
    rodar HeapSort CRESCENTE 10000
    rodar HeapSort DECRESCENTE 10000
    rodar HeapSort ALEATORIO 10000
    rodar HeapSort CRESCENTE 100000
    rodar HeapSort DECRESCENTE 100000
    rodar HeapSort ALEATORIO 100000
    rodar HeapSort CRESCENTE 1000000
    rodar HeapSort DECRESCENTE 1000000
    rodar HeapSort ALEATORIO 1000000
}

function counting(){
    rodar CountingSort CRESCENTE 1000
    rodar CountingSort DECRESCENTE 1000
    rodar CountingSort ALEATORIO 1000
    rodar CountingSort CRESCENTE 10000
    rodar CountingSort DECRESCENTE 10000
    rodar CountingSort ALEATORIO 10000
    rodar CountingSort CRESCENTE 100000
    rodar CountingSort DECRESCENTE 100000
    rodar CountingSort ALEATORIO 100000
    rodar CountingSort CRESCENTE 1000000
    rodar CountingSort DECRESCENTE 1000000
    rodar CountingSort ALEATORIO 1000000
}

function radix(){
    rodar RadixSort CRESCENTE 1000
    rodar RadixSort DECRESCENTE 1000
    rodar RadixSort ALEATORIO 1000
    rodar RadixSort CRESCENTE 10000
    rodar RadixSort DECRESCENTE 10000
    rodar RadixSort ALEATORIO 10000
    rodar RadixSort CRESCENTE 100000
    rodar RadixSort DECRESCENTE 100000
    rodar RadixSort ALEATORIO 100000
    rodar RadixSort CRESCENTE 1000000
    rodar RadixSort DECRESCENTE 1000000
    rodar RadixSort ALEATORIO 1000000
}

function bucket(){
    rodar BucketSort CRESCENTE 1000
    rodar BucketSort DECRESCENTE 1000
    rodar BucketSort ALEATORIO 1000
    rodar BucketSort CRESCENTE 10000
    rodar BucketSort DECRESCENTE 10000
    rodar BucketSort ALEATORIO 10000
    rodar BucketSort CRESCENTE 100000
    rodar BucketSort DECRESCENTE 100000
    rodar BucketSort ALEATORIO 100000
    rodar BucketSort CRESCENTE 1000000
    rodar BucketSort DECRESCENTE 1000000
    rodar BucketSort ALEATORIO 1000000
}

function todos(){
    bubble
    inserction
    selection
    shell
    merge
    quick
    heap
    counting
    radix
    bucket
}

# --------------------------------------------------------------- despacho
compilar

if [[ $# -eq 0 ]]; then
    todos
else
    for alvo in "$@"; do
        case "$alvo" in
            bubble)                bubble ;;
            inserction|insertion)  inserction ;;
            selection)             selection ;;
            shell)                 shell ;;
            merge)                 merge ;;
            quick)                 quick ;;
            heap)                  heap ;;
            counting)              counting ;;
            radix)                 radix ;;
            bucket)                bucket ;;
            todos|all)             todos ;;
            *)
                echo "Algoritmo desconhecido: $alvo" >&2
                echo "Use: bubble inserction selection shell merge quick heap counting radix bucket" >&2
                exit 2
                ;;
        esac
    done
fi

echo
if [[ $FALHAS -gt 0 ]]; then
    echo "Concluido com $FALHAS configuracao(oes) com falha." >&2
    exit 1
fi
echo "Concluido sem falhas. CSVs em: $SAIDA*"