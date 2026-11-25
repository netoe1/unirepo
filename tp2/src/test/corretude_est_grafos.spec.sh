#!/usr/bin/env bash
set -euo pipefail

TP2_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
BUILD_DIR="$(mktemp -d "${TMPDIR:-/tmp}/tp2-regressao.XXXXXX")"
trap 'rm -rf "$BUILD_DIR"' EXIT

find "$TP2_DIR/src" -name '*.java' -print0 \
    | xargs -0 javac -encoding UTF-8 -d "$BUILD_DIR"

java -cp "$BUILD_DIR" test.GrafoListaAdjacenciaTest
java -cp "$BUILD_DIR" test.GrafoMatrizAdjacenciaTest
java -cp "$BUILD_DIR" test.RepresentacoesGrafoTest
