#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")/../.." && pwd)"
OUTPUT_DIR="$ROOT_DIR/tp2/src/output"

find "$ROOT_DIR" \
    -type d -name .git -prune -o \
    -type f -name '*.class' -print0 |
    while IFS= read -r -d '' arquivo_class; do
        printf '%s\n' "$arquivo_class"
        rm -- "$arquivo_class"
    done

if [[ -d "$OUTPUT_DIR" ]]; then
    find "$OUTPUT_DIR" -maxdepth 1 -type f -name '*.csv' -print -delete
fi

printf 'Arquivos temporarios do TP2 removidos.\n'
