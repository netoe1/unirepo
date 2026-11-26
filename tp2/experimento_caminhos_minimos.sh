#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT_DIR="$(cd "$SCRIPT_DIR/.." && pwd)"
BUILD_DIR="$(mktemp -d "${TMPDIR:-/tmp}/tp2-experimento.XXXXXX")"
trap 'rm -rf "$BUILD_DIR"' EXIT

REPETICOES="${TP2_REPETICOES:-10}"
LIMITE_GRAFOS="${TP2_LIMITE_GRAFOS:-15}"

command -v javac >/dev/null || {
    echo "Erro: javac nao encontrado no PATH." >&2
    exit 1
}
command -v java >/dev/null || {
    echo "Erro: java nao encontrado no PATH." >&2
    exit 1
}

find "$SCRIPT_DIR" -name '*.java' -print0 \
    | xargs -0 javac -encoding UTF-8 -d "$BUILD_DIR"

java -cp "$BUILD_DIR" ExperimentoCaminhosMinimos "$ROOT_DIR" "$REPETICOES" "$LIMITE_GRAFOS"