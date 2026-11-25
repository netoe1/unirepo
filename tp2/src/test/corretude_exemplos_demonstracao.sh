#!/usr/bin/env bash
set -euo pipefail

TP2_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
BUILD_DIR="$(mktemp -d "${TMPDIR:-/tmp}/tp2-exemplos-demonstracao.XXXXXX")"
trap 'rm -rf "$BUILD_DIR"' EXIT

find "$TP2_DIR/src" -name '*.java' -print0 \
    | xargs -0 javac -encoding UTF-8 -d "$BUILD_DIR"

cd "$TP2_DIR/.."
java -cp "$BUILD_DIR" test.ExemplosDemonstracaoTest

RUN_DIR="$BUILD_DIR/run"
mkdir -p "$RUN_DIR/tp2/src/samples"
ln -s "$TP2_DIR/src/samples/demo" "$RUN_DIR/tp2/src/samples/demo"

cat > "$RUN_DIR/custom-e2e.gr" <<'EOF'
c Custom E2E graph
p sp 3 2
a 1 2 4
a 2 3 5
EOF

{
    printf '\n'
    for opcao in 1 2 3 4 5 6 7 8 9; do
        printf '%s\n\n' "$opcao"
        if [[ "$opcao" == 9 ]]; then
            printf '\n'
        fi
    done
    printf '1\n%s\n0\n' "$RUN_DIR/custom-e2e.gr"
} | (cd "$RUN_DIR" && java -cp "$BUILD_DIR" Main) > "$BUILD_DIR/main-output.txt"

for algoritmo in BFS DFS "Ordenacao Topologica" Kruskal Prim "Bellman-Ford" Dijkstra "Floyd-Warshall" "Ford-Fulkerson"; do
    grep -q "ALGORITMO: $algoritmo" "$BUILD_DIR/main-output.txt"
done

test "$(grep -c "ALGORITMO:" "$BUILD_DIR/main-output.txt")" -eq 28
test "$(grep -c "Estrutura do Grafo" "$BUILD_DIR/main-output.txt")" -eq 28
test "$(grep -c "Tempo de execucao" "$BUILD_DIR/main-output.txt")" -eq 28
grep -q "0 -> (1, peso=1.0)" "$BUILD_DIR/main-output.txt"
grep -q "Peso total: 10.0000" "$BUILD_DIR/main-output.txt"
grep -q "Fluxo maximo             : 23.0000" "$BUILD_DIR/main-output.txt"
grep -q "Arquivo para BFS" "$BUILD_DIR/main-output.txt"

CSV="$RUN_DIR/output/resultados_algoritmos.csv"
test -f "$CSV"
test "$(wc -l < "$CSV")" -eq 29
test "$(awk -F';' 'NR > 1 && $2 == "BFS" { count++ } END { print count+0 }' "$CSV")" -eq 8
test "$(awk -F';' 'NR > 1 && $2 == "FORD_FULKERSON" { count++ } END { print count+0 }' "$CSV")" -eq 2
grep -q 'custom-e2e.gr;BFS;' "$CSV"
