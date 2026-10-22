#!/usr/bin/env bash
set -Eeuo pipefail

# Orquestrador parametrizavel para os experimentos do TP1.
# Compila uma unica vez e executa cada combinacao em processo Java separado.
# Isso evita acumular estado entre testes e permite manter os limites de JVM
# definidos pelo TesteGenerator.

SCRIPT_DIR="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

REPETICOES=10
ALGORITMOS_RAW="all"
TAMANHOS_RAW="1000,10000,100000,1000000"
CONFIGS_RAW="MELHOR:CRESCENTE,PIOR:DECRESCENTE,MEDIO:ALEATORIO"
CASO_SINGLE=""
DISPOSICAO_SINGLE=""
CONFIGS_EXPLICIT=0
SAIDA="resultados/tp1"
DEBUG=0
NO_COLOR=0
DRY_RUN=0
CONTINUAR_ERROS=0

ALGORITMOS_TODOS=(BubbleSort InsertionSort SelectionSort ShellSort HeapSort MergeSort QuickSort CountingSort RadixSort BucketSort)

usage() {
    cat <<'USAGE'
Uso:
  ./executar_suite_tp1.sh [opcoes]

Opcoes:
  -r, --repeticoes N         Numero de medicoes por configuracao (padrao: 10)
  -a, --algoritmos LISTA     all ou nomes separados por virgula
                             Ex.: MergeSort,QuickSort,RadixSort
  -n, --tamanhos LISTA       Tamanhos separados por virgula
                             Padrao: 1000,10000,100000,1000000
  -c, --configuracoes LISTA  Pares CASO:DISPOSICAO separados por virgula
                             Padrao: MELHOR:CRESCENTE,PIOR:DECRESCENTE,MEDIO:ALEATORIO
                             Casos: MELHOR, PIOR, MEDIO
                             Disposicoes: CRESCENTE, DECRESCENTE, ALEATORIO, REPETIDOS
  -o, --saida PREFIXO        Prefixo dos CSVs (padrao: resultados/tp1)
      --debug                Ativa os logs detalhados do TesteGenerator
      --no-color              Desativa as cores ANSI
      --dry-run               Mostra o plano sem compilar ou executar testes
      --continuar-erros       Continua para a proxima combinacao se uma falhar
  -h, --help                 Mostra esta ajuda

Exemplos:
  # Plano padrao: todos algoritmos, tamanhos e casos/disposicoes padrao
  ./executar_suite_tp1.sh

  # Apenas tres algoritmos e dois tamanhos
  ./executar_suite_tp1.sh -r 10 -a MergeSort,QuickSort,RadixSort \
    -n 1000,10000 -c MELHOR:CRESCENTE,PIOR:DECRESCENTE,MEDIO:ALEATORIO \
    -o resultados/experimento1 --debug

  # Incluir dados repetidos como configuracao adicional
  ./executar_suite_tp1.sh -a CountingSort,RadixSort -n 1000,10000 \
    -c MEDIO:REPETIDOS -o resultados/repetidos

  # Ver quais testes seriam executados, sem iniciar a JVM
  ./executar_suite_tp1.sh --dry-run

Notas:
  * Cada par CASO:DISPOSICAO e uma configuracao distinta. A correspondencia
    entre caso teorico e disposicao depende do algoritmo e da implementacao.
  * A suite executa os testes sequencialmente, nunca em paralelo, para reduzir
    interferencia entre medicoes de desempenho.
  * Configuracoes que ultrapassam limites preventivos do TesteGenerator sao
    ignoradas e listadas antes da execucao.
  * JAVA_OPTS pode ajustar a heap, por exemplo:
      JAVA_OPTS='-Xms128m -Xmx2g' ./executar_suite_tp1.sh ...
USAGE
}

log() { printf '[%s] %s\n' "$1" "$2"; }
trim() {
    local s="$1"
    s="${s#"${s%%[![:space:]]*}"}"
    s="${s%"${s##*[![:space:]]}"}"
    printf '%s' "$s"
}

fail() { log ERRO "$1" >&2; exit "${2:-2}"; }

canonical_algorithm() {
    local raw="$1" key
    key="$(printf '%s' "$raw" | tr '[:upper:]' '[:lower:]' | tr -cd '[:alnum:]')"
    case "$key" in
        all|todos) printf 'all' ;;
        bubble|bubblesort) printf 'BubbleSort' ;;
        insertion|insertionsort|inserction|inserctionsort) printf 'InsertionSort' ;;
        selection|selectionsort) printf 'SelectionSort' ;;
        shell|shellsort) printf 'ShellSort' ;;
        heap|heapsort) printf 'HeapSort' ;;
        merge|mergesort) printf 'MergeSort' ;;
        quick|quicksort) printf 'QuickSort' ;;
        counting|countingsort) printf 'CountingSort' ;;
        radix|radixsort) printf 'RadixSort' ;;
        bucket|bucketsort) printf 'BucketSort' ;;
        *) return 1 ;;
    esac
}

canonical_case() {
    local key
    key="$(printf '%s' "$1" | tr '[:lower:]' '[:upper:]')"
    key="$(printf '%s' "$key" | tr -d '[:space:]')"
    case "$key" in
        MELHOR|MELHORCASO) printf 'MELHOR' ;;
        PIOR|PIORCASO) printf 'PIOR' ;;
        MEDIO|MEDIOCASO|MÉDIO) printf 'MEDIO' ;;
        *) return 1 ;;
    esac
}

canonical_disposition() {
    local key
    key="$(printf '%s' "$1" | tr '[:upper:]' '[:lower:]' | tr -cd '[:alnum:]')"
    case "$key" in
        crescente|ordemcrescente|ascending) printf 'CRESCENTE' ;;
        decrescente|ordemdecrescente|descending) printf 'DECRESCENTE' ;;
        aleatorio|ordemaleatoria|random) printf 'ALEATORIO' ;;
        repetidos|valoresrepetidos|repeated) printf 'REPETIDOS' ;;
        *) return 1 ;;
    esac
}

valid_integer() { [[ "$1" =~ ^[0-9]+$ ]]; }

while (($#)); do
    case "$1" in
        -r|--repeticoes) (($# >= 2)) || fail "Falta valor para $1"; REPETICOES="$2"; shift 2 ;;
        -a|--algoritmos) (($# >= 2)) || fail "Falta valor para $1"; ALGORITMOS_RAW="$2"; shift 2 ;;
        -n|--tamanhos) (($# >= 2)) || fail "Falta valor para $1"; TAMANHOS_RAW="$2"; shift 2 ;;
        -c|--configuracoes) (($# >= 2)) || fail "Falta valor para $1"; CONFIGS_RAW="$2"; CONFIGS_EXPLICIT=1; shift 2 ;;
        --caso) (($# >= 2)) || fail "Falta valor para $1"; CASO_SINGLE="$2"; shift 2 ;;
        --disposicao) (($# >= 2)) || fail "Falta valor para $1"; DISPOSICAO_SINGLE="$2"; shift 2 ;;
        -o|--saida) (($# >= 2)) || fail "Falta valor para $1"; SAIDA="$2"; shift 2 ;;
        --debug) DEBUG=1; shift ;;
        --no-color) NO_COLOR=1; shift ;;
        --dry-run) DRY_RUN=1; shift ;;
        --continuar-erros) CONTINUAR_ERROS=1; shift ;;
        -h|--help) usage; exit 0 ;;
        *) fail "Opcao desconhecida: $1 (use --help)" ;;
    esac
done

if (( CONFIGS_EXPLICIT )) && { [[ -n "$CASO_SINGLE" ]] || [[ -n "$DISPOSICAO_SINGLE" ]]; }; then
    fail "Use --configuracoes ou o par --caso/--disposicao, nao ambos."
fi
if [[ -n "$CASO_SINGLE" || -n "$DISPOSICAO_SINGLE" ]]; then
    [[ -n "$CASO_SINGLE" && -n "$DISPOSICAO_SINGLE" ]] || fail "--caso e --disposicao precisam ser informados juntos."
    CONFIGS_RAW="$CASO_SINGLE:$DISPOSICAO_SINGLE"
fi

valid_integer "$REPETICOES" || fail "--repeticoes deve ser um inteiro."
(( REPETICOES >= 1 && REPETICOES <= 1000 )) || fail "--repeticoes deve estar entre 1 e 1000."

# Parseia algoritmos.
ALGORITMOS=()
if [[ "$(printf '%s' "$ALGORITMOS_RAW" | tr '[:upper:]' '[:lower:]')" == "all" || "$(printf '%s' "$ALGORITMOS_RAW" | tr '[:upper:]' '[:lower:]')" == "todos" ]]; then
    ALGORITMOS=("${ALGORITMOS_TODOS[@]}")
else
    IFS=',' read -r -a _algos <<< "$ALGORITMOS_RAW"
    for item in "${_algos[@]}"; do
        item="$(trim "$item")"
        [[ -n "$item" ]] || fail "Lista de algoritmos contem item vazio."
        canon="$(canonical_algorithm "$item")" || fail "Algoritmo desconhecido: $item"
        [[ "$canon" != "all" ]] || fail "Use 'all' sozinho em --algoritmos."
        # Evita executar duplicados na mesma suite.
        found=0
        for seen in "${ALGORITMOS[@]:-}"; do [[ "$seen" == "$canon" ]] && found=1; done
        (( found == 1 )) || ALGORITMOS+=("$canon")
    done
fi

# Parseia tamanhos.
TAMANHOS=()
IFS=',' read -r -a _sizes <<< "$TAMANHOS_RAW"
for item in "${_sizes[@]}"; do
    item="$(trim "$item")"
    valid_integer "$item" || fail "Tamanho invalido em --tamanhos: $item"
    n=$((10#$item))
    (( n >= 1 && n <= 1000000 )) || fail "Tamanhos precisam estar entre 1 e 1000000: $item"
    found=0
    for seen in "${TAMANHOS[@]:-}"; do [[ "$seen" == "$n" ]] && found=1; done
    (( found == 1 )) || TAMANHOS+=("$n")
done
((${#TAMANHOS[@]} > 0)) || fail "Informe ao menos um tamanho."

# Parseia pares caso:disposicao. O uso de pares evita um produto cartesiano
# involuntario entre todos os casos e todas as disposicoes.
CASOS=()
DISPOSICOES=()
IFS=',' read -r -a _configs <<< "$CONFIGS_RAW"
for item in "${_configs[@]}"; do
    item="$(trim "$item")"
    [[ "$item" == *:* ]] || fail "Configuracao '$item' invalida. Use CASO:DISPOSICAO."
    caso_raw="$(trim "${item%%:*}")"
    disp_raw="$(trim "${item#*:}")"
    caso="$(canonical_case "$caso_raw")" || fail "Caso invalido em configuracao: $caso_raw"
    disp="$(canonical_disposition "$disp_raw")" || fail "Disposicao invalida em configuracao: $disp_raw"
    CASOS+=("$caso")
    DISPOSICOES+=("$disp")
done
((${#CASOS[@]} > 0)) || fail "Informe ao menos uma configuracao."

# Configurações que o TesteGenerator.java também rejeita. O script filtra antes
# da execução para não iniciar a JVM para um teste que será imediatamente negado.
is_quadratic() {
    case "$1" in BubbleSort|InsertionSort|SelectionSort) return 0 ;; *) return 1 ;; esac
}
supported_config() {
    local algo="$1" n="$2" disp="$3" reason=""
    if is_quadratic "$algo" && (( n > 10000 )); then
        reason="$algo limitado a n <= 10000";
    elif is_quadratic "$algo" && (( n >= 5000 && REPETICOES > 10 )); then
        reason="$algo com n >= 5000 aceita no maximo 10 repeticoes";
    elif [[ "$algo" == "QuickSort" ]] && (( n > 100000 )); then
        reason="QuickSort limitado a n <= 100000";
    elif [[ "$algo" == "QuickSort" && ( "$disp" == "CRESCENTE" || "$disp" == "DECRESCENTE" ) ]] && (( n > 1000 )); then
        reason="QuickSort com dados crescentes/decrescentes limitado a n <= 1000";
    fi
    if [[ -n "$reason" ]]; then
        SKIPPED+=("$algo|$n|$disp|$reason")
        return 1
    fi
    return 0
}

RUN_ALGO=()
RUN_N=()
RUN_CASO=()
RUN_DISP=()
SKIPPED=()
for algo in "${ALGORITMOS[@]}"; do
    for n in "${TAMANHOS[@]}"; do
        for idx in "${!CASOS[@]}"; do
            caso="${CASOS[$idx]}"
            disp="${DISPOSICOES[$idx]}"
            if supported_config "$algo" "$n" "$disp"; then
                RUN_ALGO+=("$algo")
                RUN_N+=("$n")
                RUN_CASO+=("$caso")
                RUN_DISP+=("$disp")
            fi
        done
    done
done

TOTAL=${#RUN_ALGO[@]}
(( TOTAL > 0 )) || fail "Nenhuma configuracao compativel com os limites foi selecionada."

log INFO "Plano TP1: ${#ALGORITMOS[@]} algoritmo(s), ${#TAMANHOS[@]} tamanho(s), ${#CASOS[@]} configuracao(oes), $REPETICOES repeticao(oes)."
log INFO "Experimentos planejados: $TOTAL; combinacoes ignoradas por limites: ${#SKIPPED[@]}."
log INFO "Prefixo CSV: $SAIDA"

if ((${#SKIPPED[@]} > 0)); then
    for skip in "${SKIPPED[@]}"; do
        IFS='|' read -r algo n disp why <<< "$skip"
        log WARN "Ignorado: $algo n=$n disposicao=$disp ($why)."
    done
fi

if (( DRY_RUN )); then
    for ((i=0; i<TOTAL; i++)); do
        printf 'PLANO %03d/%03d: algoritmo=%s n=%s caso=%s disposicao=%s repeticoes=%s\n' \
            "$((i+1))" "$TOTAL" "${RUN_ALGO[$i]}" "${RUN_N[$i]}" \
            "${RUN_CASO[$i]}" "${RUN_DISP[$i]}" "$REPETICOES"
    done
    log SUCCESS "Dry-run concluido; nenhum arquivo foi compilado ou executado."
    exit 0
fi

command -v javac >/dev/null 2>&1 || fail "javac nao encontrado. Instale um JDK." 127
command -v java >/dev/null 2>&1 || fail "java nao encontrado no PATH." 127

shopt -s nullglob
FONTES=( *.java )
((${#FONTES[@]} > 0)) || fail "Nenhum arquivo .java encontrado em $SCRIPT_DIR."
log INFO "Compilando ${#FONTES[@]} arquivo(s) Java uma vez..."
javac -encoding UTF-8 "${FONTES[@]}"
log SUCCESS "Compilacao concluida."

if [[ -n "${JAVA_OPTS:-}" ]]; then
    read -r -a JVM_OPCOES <<< "$JAVA_OPTS"
else
    JVM_OPCOES=( -Xms128m -Xmx1g )
fi

INICIO_SUITE=$(date +%s)
SUCESSOS=0
FALHAS=0
for ((i=0; i<TOTAL; i++)); do
    algo="${RUN_ALGO[$i]}"
    n="${RUN_N[$i]}"
    caso="${RUN_CASO[$i]}"
    disp="${RUN_DISP[$i]}"

    log INFO "[$((i+1))/$TOTAL] $algo | n=$n | caso=$caso | disposicao=$disp | repeticoes=$REPETICOES"
    args=( --repeticoes "$REPETICOES" --caso "$caso" --algoritmo "$algo"
           --disposicao "$disp" --n "$n" --saida "$SAIDA" )
    (( DEBUG )) && args+=( --debug )
    (( NO_COLOR )) && args+=( --no-color )

    if java "${JVM_OPCOES[@]}" -cp "$SCRIPT_DIR" TesteGenerator "${args[@]}"; then
        ((SUCESSOS+=1))
    else
        rc=$?
        ((FALHAS+=1))
        log ERROR "Falhou: $algo n=$n caso=$caso disposicao=$disp (codigo $rc)."
        if (( ! CONTINUAR_ERROS )); then
            log ERROR "Interrompendo a suite. Use --continuar-erros para tentar as demais combinacoes."
            exit "$rc"
        fi
    fi

done

FIM_SUITE=$(date +%s)
DURACAO=$((FIM_SUITE - INICIO_SUITE))
log SUCCESS "Suite encerrada: $SUCESSOS sucesso(s), $FALHAS falha(s), duracao ${DURACAO}s."
if (( FALHAS > 0 )); then exit 1; fi
