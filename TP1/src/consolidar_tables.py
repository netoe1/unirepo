#!/usr/bin/env python3
"""
Centraliza os CSVs de desempenho do TP1 sem calcular estatísticas ainda.

Entrada esperada:
  resultados/tp1_completo_<Algoritmo>_n<TAMANHO>.csv

Saídas:
  resultados/centralizados/tempos_consolidados.csv
  resultados/centralizados/por_algoritmo/<Algoritmo>.csv
  resultados/centralizados/integridade_configuracoes.csv
"""

import csv
import re
import sys
from collections import defaultdict
from pathlib import Path

BASE = Path(__file__).resolve().parent
RESULTADOS = BASE / "resultados"
SAIDA = RESULTADOS / "centralizados"
POR_ALGORITMO = SAIDA / "por_algoritmo"

COLUNAS = [
    "algoritmo", "n", "caso", "disposicao",
    "semente", "sessao", "repeticao", "t_ns"
]
PADRAO_ARQUIVO = re.compile(r"tp1_completo_(.+)_n(\d+)\.csv$")


def ler_csv(caminho):
    linhas = []
    with caminho.open("r", encoding="utf-8-sig", newline="") as arquivo:
        leitor = csv.DictReader(arquivo)
        if not leitor.fieldnames:
            print(f"[AVISO] CSV vazio ou sem cabeçalho: {caminho}", file=sys.stderr)
            return linhas

        campos = {campo.strip().strip('"'): campo for campo in leitor.fieldnames}
        faltantes = [c for c in COLUNAS if c not in campos]
        if faltantes:
            print(
                f"[AVISO] Ignorando {caminho}: faltam colunas {faltantes}; "
                f"cabeçalho encontrado: {leitor.fieldnames}",
                file=sys.stderr,
            )
            return linhas

        for numero, linha in enumerate(leitor, start=2):
            try:
                registro = {
                    coluna: (linha.get(campos[coluna]) or "").strip()
                    for coluna in COLUNAS
                }
                # O nome do arquivo é a fonte de validação para algoritmo e n.
                correspondencia = PADRAO_ARQUIVO.search(caminho.name)
                if correspondencia:
                    algoritmo_arquivo, n_arquivo = correspondencia.groups()
                    if not registro["algoritmo"]:
                        registro["algoritmo"] = algoritmo_arquivo
                    if not registro["n"]:
                        registro["n"] = n_arquivo

                registro["n"] = int(registro["n"])
                registro["semente"] = int(registro["semente"])
                registro["repeticao"] = int(registro["repeticao"])
                registro["t_ns"] = int(registro["t_ns"])
                if registro["t_ns"] < 0:
                    raise ValueError("tempo negativo")
                if not registro["algoritmo"]:
                    raise ValueError("algoritmo vazio")
                linhas.append(registro)
            except (ValueError, TypeError) as erro:
                print(
                    f"[AVISO] Linha inválida ignorada ({caminho.name}:{numero}): {erro}",
                    file=sys.stderr,
                )
    return linhas


def gravar_csv(caminho, colunas, linhas):
    caminho.parent.mkdir(parents=True, exist_ok=True)
    with caminho.open("w", encoding="utf-8", newline="") as arquivo:
        escritor = csv.DictWriter(arquivo, fieldnames=colunas, extrasaction="ignore")
        escritor.writeheader()
        escritor.writerows(linhas)


def chave_ordenacao(r):
    return (
        r["algoritmo"], int(r["n"]), r["disposicao"], r["caso"],
        int(r["semente"]), r["sessao"], int(r["repeticao"])
    )


def main():
    arquivos = sorted(RESULTADOS.glob("tp1_completo_*_n*.csv"))
    if not arquivos:
        print(f"Nenhum CSV encontrado em: {RESULTADOS}")
        print("Esperado: tp1_completo_<Algoritmo>_n<TAMANHO>.csv")
        return 1

    todos = []
    for arquivo in arquivos:
        todos.extend(ler_csv(arquivo))

    if not todos:
        print("Nenhum registro válido foi encontrado; confira os avisos acima.")
        return 1

    todos.sort(key=chave_ordenacao)
    SAIDA.mkdir(parents=True, exist_ok=True)
    POR_ALGORITMO.mkdir(parents=True, exist_ok=True)

    gravar_csv(SAIDA / "tempos_consolidados.csv", COLUNAS, todos)

    por_algoritmo = defaultdict(list)
    por_configuracao = defaultdict(list)
    for linha in todos:
        por_algoritmo[linha["algoritmo"]].append(linha)
        chave = (
            linha["algoritmo"], linha["n"], linha["caso"], linha["disposicao"],
            linha["semente"], linha["sessao"]
        )
        por_configuracao[chave].append(linha)

    for algoritmo, linhas in sorted(por_algoritmo.items()):
        linhas.sort(key=chave_ordenacao)
        gravar_csv(POR_ALGORITMO / f"{algoritmo}.csv", COLUNAS, linhas)

    colunas_integridade = [
        "algoritmo", "n", "caso", "disposicao", "semente", "sessao",
        "quantidade_medicoes", "tempos_zero", "tempos_negativos",
        "repeticoes_duplicadas", "repeticoes_encontradas"
    ]
    integridade = []
    for chave, linhas in sorted(por_configuracao.items()):
        algoritmo, n, caso, disposicao, semente, sessao = chave
        repeticoes = [int(l["repeticao"]) for l in linhas]
        integridade.append({
            "algoritmo": algoritmo,
            "n": n,
            "caso": caso,
            "disposicao": disposicao,
            "semente": semente,
            "sessao": sessao,
            "quantidade_medicoes": len(linhas),
            "tempos_zero": sum(int(l["t_ns"]) == 0 for l in linhas),
            "tempos_negativos": sum(int(l["t_ns"]) < 0 for l in linhas),
            "repeticoes_duplicadas": len(repeticoes) - len(set(repeticoes)),
            "repeticoes_encontradas": ";".join(map(str, sorted(repeticoes))),
        })

    gravar_csv(SAIDA / "integridade_configuracoes.csv", colunas_integridade, integridade)

    algoritmos = sorted(por_algoritmo)
    print(f"Arquivos de entrada lidos: {len(arquivos)}")
    print(f"Algoritmos encontrados: {len(algoritmos)} ({', '.join(algoritmos)})")
    print(f"Tempos individuais válidos: {len(todos)}")
    print(f"Configurações distintas (incluindo sessão): {len(por_configuracao)}")
    print(f"Consolidado: {SAIDA / 'tempos_consolidados.csv'}")
    print(f"Arquivos por algoritmo: {POR_ALGORITMO}")
    print(f"Relatório de integridade: {SAIDA / 'integridade_configuracoes.csv'}")
    print("\nObservação: este script não calcula médias nem remove registros.")
    print("Confira a integridade e a metodologia dos tempos antes de gerar estatísticas.")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
