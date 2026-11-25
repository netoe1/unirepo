#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")/../.." && pwd)"
SOURCE_DIR="$ROOT_DIR/tp2/src"
BUILD_DIR="$(mktemp -d "${TMPDIR:-/tmp}/tp2-main.XXXXXX")"
trap 'rm -rf "$BUILD_DIR"' EXIT

find "$SOURCE_DIR" -name '*.java' -print0 \
    | xargs -0 javac -encoding UTF-8 -d "$BUILD_DIR"

cd "$ROOT_DIR"
java -cp "$BUILD_DIR" Main
