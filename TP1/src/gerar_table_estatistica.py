#!/usr/bin/env python3
"""Calcula tabelas estatísticas para o TP1 (ACD), sem dependências externas.

Entrada padrão: resultados/centralizados/tempos_consolidados.csv
Saída padrão:   resultados/estatisticas/

Este script NÃO gera gráficos. Execute graficos_tp1.py depois dele.
"""
from __future__ import annotations

import argparse
import csv
import math
import statistics
import sys
from collections import Counter, defaultdict
from pathlib import Path
from typing import Any

CAMPOS_OBRIGATORIOS = {
    "algoritmo", "n", "caso", "disposicao", "semente", "sessao", "repeticao", "t_ns"
}
ALGORITMOS_ESPERADOS = [
    "BubbleSort", "InsertionSort", "SelectionSort", "ShellSort", "HeapSort",
    "MergeSort", "QuickSort", "CountingSort", "RadixSort", "BucketSort",
]
TAMANHOS_ESPERADOS = [1_000, 10_000, 100_000, 1_000_000]
CENARIOS_ESPERADOS = [
    ("MELHOR", "CRESCENTE"),
    ("MEDIO", "ALEATORIO"),
    ("PIOR", "DECRESCENTE"),
]
REPETICOES_ESPERADAS = 10
CAMPOS_ESTATISTICAS = [
    "algoritmo", "n", "caso", "disposicao", "quantidade_medicoes",
    "quantidade_sessoes", "quantidade_sementes", "media_ns", "mediana_ns",
    "minimo_ns", "maximo_ns", "desvio_padrao_ns", "media_ms", "mediana_ms",
    "minimo_ms", "maximo_ms", "desvio_padrao_ms", "coeficiente_variacao_pct",
]
CAMPOS_ANOMALIAS = [
    "tipo", "linha_csv", "motivo", "algoritmo", "n", "caso", "disposicao",
    "semente", "sessao", "repeticao", "t_ns",
]


def formatar_numero(valor: Any, casas: int = 6) -> Any:
    if valor is None:
        return ""
    if isinstance(valor, float):
        if not math.isfinite(valor):
            return ""
        return f"{valor:.{casas}f}"
    return valor


def carregar_dados(caminho: Path) -> tuple[list[dict[str, Any]], list[dict[str, str]]]:
    dados: list[dict[str, Any]] = []
    anomalias: list[dict[str, str]] = []
    chaves_repeticao: dict[tuple[str, int, str, str, str, str, int], int] = {}

    with caminho.open("r", newline="", encoding="utf-8-sig") as arquivo:
        leitor = csv.DictReader(arquivo)
        campos = {campo.strip() for campo in (leitor.fieldnames or [])}
        faltantes = sorted(CAMPOS_OBRIGATORIOS - campos)
        if faltantes:
            raise ValueError(
                "O CSV não possui as colunas obrigatórias: " + ", ".join(faltantes)
                + ". Colunas encontradas: " + ", ".join(leitor.fieldnames or [])
            )

        for numero_linha, original in enumerate(leitor, start=2):
            linha = {str(k).strip(): (v if v is not None else "") for k, v in original.items() if k is not None}
            try:
                registro: dict[str, Any] = {
                    "algoritmo": str(linha.get("algoritmo", "")).strip(),
                    "n": int(str(linha.get("n", "")).strip()),
                    "caso": str(linha.get("caso", "")).strip().upper(),
                    "disposicao": str(linha.get("disposicao", "")).strip().upper(),
                    "semente": str(linha.get("semente", "")).strip(),
                    "sessao": str(linha.get("sessao", "")).strip(),
                    "repeticao": int(str(linha.get("repeticao", "")).strip()),
                    "t_ns": int(str(linha.get("t_ns", "")).strip()),
                }
                if not registro["algoritmo"]:
                    raise ValueError("campo algoritmo vazio")
                if registro["n"] <= 0:
                    raise ValueError("n deve ser maior que zero")
                if registro["repeticao"] <= 0:
                    raise ValueError("repeticao deve ser maior que zero")
                if registro["t_ns"] < 0:
                    raise ValueError("t_ns não pode ser negativo")
                if not registro["caso"] or not registro["disposicao"]:
                    raise ValueError("caso ou disposicao vazio")

                identificador = (
                    registro["algoritmo"], registro["n"], registro["caso"],
                    registro["disposicao"], registro["semente"], registro["sessao"],
                    registro["repeticao"],
                )
                if identificador in chaves_repeticao:
                    anomalias.append({
                        "tipo": "REPETICAO_DUPLICADA",
                        "linha_csv": str(numero_linha),
                        "motivo": f"mesma chave de medição já apareceu na linha {chaves_repeticao[identificador]}; registro mantido",
                        **{k: str(registro.get(k, "")) for k in ["algoritmo", "n", "caso", "disposicao", "semente", "sessao", "repeticao", "t_ns"]},
                    })
                else:
                    chaves_repeticao[identificador] = numero_linha
                if registro["t_ns"] == 0:
                    anomalias.append({
                        "tipo": "TEMPO_ZERO", "linha_csv": str(numero_linha),
                        "motivo": "tempo zero é válido numericamente, mas não pode ser exibido em escala logarítmica",
                        **{k: str(registro.get(k, "")) for k in ["algoritmo", "n", "caso", "disposicao", "semente", "sessao", "repeticao", "t_ns"]},
                    })
                dados.append(registro)
            except (TypeError, ValueError) as erro:
                anomalias.append({
                    "tipo": "LINHA_INVALIDA", "linha_csv": str(numero_linha), "motivo": str(erro),
                    **{k: str(linha.get(k, "")) for k in ["algoritmo", "n", "caso", "disposicao", "semente", "sessao", "repeticao", "t_ns"]},
                })
    return dados, anomalias


def calcular_estatisticas(chaves: list[str], dados: list[dict[str, Any]]) -> list[dict[str, Any]]:
    grupos: dict[tuple[Any, ...], list[dict[str, Any]]] = defaultdict(list)
    for linha in dados:
        grupos[tuple(linha[campo] for campo in chaves)].append(linha)

    saida: list[dict[str, Any]] = []
    for chave, grupo in sorted(grupos.items()):
        tempos = [int(linha["t_ns"]) for linha in grupo]
        media = statistics.fmean(tempos)
        mediana = statistics.median(tempos)
        minimo, maximo = min(tempos), max(tempos)
        desvio = statistics.stdev(tempos) if len(tempos) >= 2 else None
        cv = (desvio / media * 100) if desvio is not None and media != 0 else None
        registro = {campo: valor for campo, valor in zip(chaves, chave)}
        registro.update({
            "quantidade_medicoes": len(tempos),
            "quantidade_sessoes": len({linha["sessao"] for linha in grupo if linha["sessao"]}),
            "quantidade_sementes": len({linha["semente"] for linha in grupo if linha["semente"]}),
            "media_ns": media, "mediana_ns": mediana, "minimo_ns": minimo,
            "maximo_ns": maximo, "desvio_padrao_ns": desvio,
            "media_ms": media / 1_000_000, "mediana_ms": mediana / 1_000_000,
            "minimo_ms": minimo / 1_000_000, "maximo_ms": maximo / 1_000_000,
            "desvio_padrao_ms": desvio / 1_000_000 if desvio is not None else None,
            "coeficiente_variacao_pct": cv,
        })
        saida.append(registro)
    return saida


def salvar_csv(caminho: Path, campos: list[str], linhas: list[dict[str, Any]]) -> None:
    caminho.parent.mkdir(parents=True, exist_ok=True)
    with caminho.open("w", newline="", encoding="utf-8") as arquivo:
        escritor = csv.DictWriter(arquivo, fieldnames=campos, extrasaction="ignore")
        escritor.writeheader()
        for linha in linhas:
            escritor.writerow({campo: formatar_numero(linha.get(campo, ""), 6) for campo in campos})


def gerar_cobertura(dados: list[dict[str, Any]]) -> list[dict[str, Any]]:
    contagens = Counter(
        (linha["algoritmo"], linha["n"], linha["caso"], linha["disposicao"])
        for linha in dados
    )
    cobertura: list[dict[str, Any]] = []
    for algoritmo in ALGORITMOS_ESPERADOS:
        for n in TAMANHOS_ESPERADOS:
            for caso, disposicao in CENARIOS_ESPERADOS:
                quantidade = contagens.get((algoritmo, n, caso, disposicao), 0)
                if quantidade == 0:
                    status = "AUSENTE"
                elif quantidade < REPETICOES_ESPERADAS:
                    status = "INCOMPLETA"
                elif quantidade == REPETICOES_ESPERADAS:
                    status = "COMPLETA"
                else:
                    status = "ACIMA_DO_ESPERADO"
                cobertura.append({
                    "algoritmo": algoritmo, "n": n, "caso": caso, "disposicao": disposicao,
                    "medicoes_encontradas": quantidade,
                    "repeticoes_esperadas": REPETICOES_ESPERADAS, "status": status,
                })
    return cobertura


def main() -> int:
    base = Path(__file__).resolve().parent
    parser = argparse.ArgumentParser(description="Calcula estatísticas do TP1 sem gerar gráficos.")
    parser.add_argument("--entrada", type=Path, default=base / "resultados" / "centralizados" / "tempos_consolidados.csv",
                        help="CSV consolidado de tempos (padrão: resultados/centralizados/tempos_consolidados.csv).")
    parser.add_argument("--saida", type=Path, default=base / "resultados" / "estatisticas",
                        help="Pasta de saída (padrão: resultados/estatisticas).")
    args = parser.parse_args()
    entrada, saida = args.entrada.expanduser().resolve(), args.saida.expanduser().resolve()
    if not entrada.is_file():
        print(f"ERRO: arquivo de entrada não encontrado: {entrada}", file=sys.stderr)
        return 2
    try:
        dados, anomalias = carregar_dados(entrada)
    except (OSError, ValueError) as erro:
        print(f"ERRO ao ler CSV: {erro}", file=sys.stderr)
        return 2
    if not dados:
        print("ERRO: nenhum registro válido foi encontrado no CSV.", file=sys.stderr)
        return 2

    estatisticas = calcular_estatisticas(["algoritmo", "n", "caso", "disposicao"], dados)
    por_sessao = calcular_estatisticas(
        ["algoritmo", "n", "caso", "disposicao", "semente", "sessao"], dados
    )
    cobertura = gerar_cobertura(dados)
    saida.mkdir(parents=True, exist_ok=True)
    salvar_csv(saida / "estatisticas_por_configuracao.csv", CAMPOS_ESTATISTICAS, estatisticas)
    campos_sessao = ["algoritmo", "n", "caso", "disposicao", "semente", "sessao"] + [
        c for c in CAMPOS_ESTATISTICAS if c not in {"algoritmo", "n", "caso", "disposicao"}
    ]
    salvar_csv(saida / "estatisticas_por_sessao.csv", campos_sessao, por_sessao)
    salvar_csv(saida / "cobertura_configuracoes.csv",
               ["algoritmo", "n", "caso", "disposicao", "medicoes_encontradas", "repeticoes_esperadas", "status"], cobertura)
    salvar_csv(saida / "anomalias_dados.csv", CAMPOS_ANOMALIAS, anomalias)

    status = Counter(linha["status"] for linha in cobertura)
    resumo = [
        "RELATÓRIO ESTATÍSTICO — TP1 / ACD", "=" * 40,
        f"Arquivo de entrada: {entrada}", f"Medições válidas utilizadas: {len(dados)}",
        f"Configurações observadas: {len(estatisticas)}",
        f"Configurações completas (10 medições): {status['COMPLETA']}",
        f"Configurações incompletas: {status['INCOMPLETA']}",
        f"Configurações ausentes: {status['AUSENTE']}",
        f"Configurações acima de 10 medições: {status['ACIMA_DO_ESPERADO']}",
        f"Anomalias registradas: {len(anomalias)}",
        "", "Definições e limitações:",
        "- Estatísticas calculadas por algoritmo, tamanho, caso e disposição.",
        "- Desvio-padrão amostral (n-1); fica vazio quando há menos de duas medições.",
        "- O CSV por sessão separa as medições por semente e sessão de execução.",
        "- Linhas inválidas não entram nas estatísticas; repetições duplicadas válidas são sinalizadas e mantidas.",
        "- A cobertura usa a matriz prevista de 10 algoritmos, 4 tamanhos e 3 cenários (melhor/médio/pior).",
        "- Este script não gera gráficos; use graficos_tp1.py após a geração destes arquivos.",
    ]
    (saida / "resumo_estatistico.txt").write_text("\n".join(resumo) + "\n", encoding="utf-8")
    print("Estatísticas calculadas com sucesso.")
    print(f"Medições válidas: {len(dados)} | configurações observadas: {len(estatisticas)}")
    print(f"Cobertura: {status['COMPLETA']} completas, {status['INCOMPLETA']} incompletas, {status['AUSENTE']} ausentes, {status['ACIMA_DO_ESPERADO']} acima do esperado.")
    print(f"Anomalias registradas: {len(anomalias)}")
    print(f"Saída: {saida}")
    return 0

if __name__ == "__main__":
    raise SystemExit(main())
