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

printf '\n1\n\n2\n\n3\n\n4\n\n5\n\n6\n\n7\n\n8\n\n9\n\n\n0\n' \
    | (cd "$RUN_DIR" && java -cp "$BUILD_DIR" Main) > "$BUILD_DIR/main-output.txt"

for algoritmo in BFS DFS "Ordenacao Topologica" Kruskal Prim "Bellman-Ford" Dijkstra "Floyd-Warshall" "Ford-Fulkerson"; do
    grep -q "ALGORITMO: $algoritmo" "$BUILD_DIR/main-output.txt"
done
grep -q "Estrutura do Grafo" "$BUILD_DIR/main-output.txt"
grep -q "Tempo de execucao" "$BUILD_DIR/main-output.txt"
