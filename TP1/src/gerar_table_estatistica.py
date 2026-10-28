#!/usr/bin/env python3
"""Calcula estatísticas e gera gráficos para os resultados do TP1 (ACD).

Entrada padrão:
    resultados/centralizados/tempos_consolidados.csv

Saída padrão:
    resultados/estatisticas/

Dependência para gráficos: matplotlib
    python3 -m pip install matplotlib
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

# Ordem usada nos gráficos e matriz de cobertura prevista para o TP1.
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


def numero(valor: float | int | None, casas: int = 3) -> str:
    """Formata números com ponto decimal e sem valores NaN/inf no CSV."""
    if valor is None or not math.isfinite(float(valor)):
        return ""
    return f"{valor:.{casas}f}"


def carregar_dados(caminho: Path) -> tuple[list[dict[str, Any]], list[dict[str, str]]]:
    """Carrega linhas válidas e registra, sem ocultar, linhas malformadas."""
    dados: list[dict[str, Any]] = []
    erros: list[dict[str, str]] = []

    with caminho.open("r", newline="", encoding="utf-8-sig") as arquivo:
        leitor = csv.DictReader(arquivo)
        campos = {campo.strip() for campo in (leitor.fieldnames or [])}
        faltantes = sorted(CAMPOS_OBRIGATORIOS - campos)
        if faltantes:
            raise ValueError(
                f"O CSV não possui as colunas obrigatórias: {', '.join(faltantes)}. "
                f"Colunas encontradas: {', '.join(leitor.fieldnames or [])}"
            )

        for numero_linha, linha in enumerate(leitor, start=2):
            try:
                registro: dict[str, Any] = {
                    "algoritmo": (linha.get("algoritmo") or "").strip(),
                    "n": int((linha.get("n") or "").strip()),
                    "caso": (linha.get("caso") or "").strip().upper(),
                    "disposicao": (linha.get("disposicao") or "").strip().upper(),
                    "semente": (linha.get("semente") or "").strip(),
                    "sessao": (linha.get("sessao") or "").strip(),
                    "repeticao": int((linha.get("repeticao") or "").strip()),
                    "t_ns": int((linha.get("t_ns") or "").strip()),
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
                dados.append(registro)
            except (TypeError, ValueError) as erro:
                erros.append({
                    "linha_csv": str(numero_linha),
                    "motivo": str(erro),
                    "algoritmo": str(linha.get("algoritmo", "")),
                    "n": str(linha.get("n", "")),
                    "caso": str(linha.get("caso", "")),
                    "disposicao": str(linha.get("disposicao", "")),
                    "repeticao": str(linha.get("repeticao", "")),
                    "t_ns": str(linha.get("t_ns", "")),
                })
    return dados, erros


def calcular_estatisticas(chaves: list[tuple], dados: list[dict[str, Any]]) -> list[dict[str, Any]]:
    grupos: dict[tuple, list[dict[str, Any]]] = defaultdict(list)
    for linha in dados:
        chave = tuple(linha[campo] for campo in chaves)
        grupos[chave].append(linha)

    saida: list[dict[str, Any]] = []
    for chave in sorted(grupos, key=lambda item: tuple(str(p) for p in item)):
        grupo = grupos[chave]
        tempos = [linha["t_ns"] for linha in grupo]
        media = statistics.fmean(tempos)
        mediana = statistics.median(tempos)
        minimo = min(tempos)
        maximo = max(tempos)
        desvio = statistics.stdev(tempos) if len(tempos) >= 2 else None
        cv = (desvio / media * 100) if desvio is not None and media != 0 else None

        registro: dict[str, Any] = {campo: valor for campo, valor in zip(chaves, chave)}
        registro.update({
            "quantidade_medicoes": len(tempos),
            "quantidade_sessoes": len({linha["sessao"] for linha in grupo if linha["sessao"]}),
            "quantidade_sementes": len({linha["semente"] for linha in grupo if linha["semente"]}),
            "media_ns": media,
            "mediana_ns": mediana,
            "minimo_ns": minimo,
            "maximo_ns": maximo,
            "desvio_padrao_ns": desvio,
            "media_ms": media / 1_000_000,
            "mediana_ms": mediana / 1_000_000,
            "minimo_ms": minimo / 1_000_000,
            "maximo_ms": maximo / 1_000_000,
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
            registro = {}
            for campo in campos:
                valor = linha.get(campo, "")
                if isinstance(valor, float):
                    valor = numero(valor, 6 if campo.endswith("_ns") else 3)
                elif valor is None:
                    valor = ""
                registro[campo] = valor
            escritor.writerow(registro)


def gerar_cobertura(dados: list[dict[str, Any]]) -> list[dict[str, Any]]:
    contagens = Counter(
        (linha["algoritmo"], linha["n"], linha["caso"], linha["disposicao"])
        for linha in dados
    )
    cobertura: list[dict[str, Any]] = []
    for algoritmo in ALGORITMOS_ESPERADOS:
        for n in TAMANHOS_ESPERADOS:
            for caso, disposicao in CENARIOS_ESPERADOS:
                chave = (algoritmo, n, caso, disposicao)
                quantidade = contagens.get(chave, 0)
                if quantidade == 0:
                    status = "AUSENTE"
                elif quantidade < REPETICOES_ESPERADAS:
                    status = "INCOMPLETA"
                else:
                    status = "COMPLETA"
                cobertura.append({
                    "algoritmo": algoritmo,
                    "n": n,
                    "caso": caso,
                    "disposicao": disposicao,
                    "medicoes_encontradas": quantidade,
                    "repeticoes_esperadas_minimas": REPETICOES_ESPERADAS,
                    "status": status,
                })
    return cobertura


def importar_matplotlib():
    try:
        import matplotlib
        matplotlib.use("Agg")
        import matplotlib.pyplot as plt
        return matplotlib, plt
    except ImportError as erro:
        raise RuntimeError(
            "A dependência matplotlib não está instalada. Instale-a com "
            "'python3 -m pip install matplotlib' (ou usando um ambiente virtual) e tente novamente."
        ) from erro


def ordem_algoritmos(dados: list[dict[str, Any]]) -> list[str]:
    existentes = {linha["algoritmo"] for linha in dados}
    ordem = [alg for alg in ALGORITMOS_ESPERADOS if alg in existentes]
    ordem.extend(sorted(existentes - set(ordem)))
    return ordem


def gerar_graficos(dados: list[dict[str, Any]], estatisticas: list[dict[str, Any]], pasta_saida: Path) -> int:
    _matplotlib, plt = importar_matplotlib()
    pasta_comparacao = pasta_saida / "graficos" / "comparacao_algoritmos"
    pasta_evolucao = pasta_saida / "graficos" / "evolucao_tamanho"
    pasta_comparacao.mkdir(parents=True, exist_ok=True)
    pasta_evolucao.mkdir(parents=True, exist_ok=True)

    ordem = ordem_algoritmos(dados)
    indice = {
        (linha["algoritmo"], int(linha["n"]), linha["disposicao"]): linha
        for linha in estatisticas
    }
    total_graficos = 0

    # Um gráfico por tamanho e disposição: mediana entre os algoritmos.
    for n in TAMANHOS_ESPERADOS:
        for _caso, disposicao in CENARIOS_ESPERADOS:
            nomes: list[str] = []
            valores_ms: list[float] = []
            for algoritmo in ordem:
                linha = indice.get((algoritmo, n, disposicao))
                if linha is None:
                    continue  # configuração ausente: não cria um zero artificial
                valor = float(linha["mediana_ms"])
                if valor <= 0:
                    continue  # escala logarítmica não representa zero
                nomes.append(algoritmo)
                valores_ms.append(valor)

            fig, eixo = plt.subplots(figsize=(max(10, len(nomes) * 0.9), 6))
            if nomes:
                eixo.bar(nomes, valores_ms)
                eixo.set_yscale("log")
                eixo.tick_params(axis="x", labelrotation=35)
                eixo.set_ylabel("Tempo mediano (ms) — escala logarítmica")
                eixo.set_title(f"Comparação de algoritmos — n={n:,} — {disposicao}".replace(",", "."))
                eixo.grid(True, axis="y", which="both", alpha=0.3)
            else:
                eixo.text(0.5, 0.5, "Não há medições positivas para esta configuração.",
                          ha="center", va="center", transform=eixo.transAxes)
                eixo.set_axis_off()
            fig.tight_layout()
            destino = pasta_comparacao / f"mediana_n{n}_{disposicao.lower()}.png"
            fig.savefig(destino, dpi=160, bbox_inches="tight")
            plt.close(fig)
            total_graficos += 1

    # Um gráfico por algoritmo: evolução da mediana conforme o tamanho e disposição.
    for algoritmo in ordem:
        fig, eixo = plt.subplots(figsize=(9, 6))
        plotou = False
        for _caso, disposicao in CENARIOS_ESPERADOS:
            xs: list[int] = []
            ys: list[float] = []
            for n in TAMANHOS_ESPERADOS:
                linha = indice.get((algoritmo, n, disposicao))
                xs.append(n)
                if linha is None or float(linha["mediana_ms"]) <= 0:
                    ys.append(math.nan)  # preserva lacunas onde não houve execução
                else:
                    ys.append(float(linha["mediana_ms"]))
            if any(math.isfinite(y) for y in ys):
                eixo.plot(xs, ys, marker="o", label=disposicao)
                plotou = True
        if plotou:
            eixo.set_xscale("log")
            eixo.set_yscale("log")
            eixo.set_xticks(TAMANHOS_ESPERADOS)
            eixo.set_xticklabels([f"{n:,}".replace(",", ".") for n in TAMANHOS_ESPERADOS])
            eixo.set_xlabel("Tamanho da entrada (n)")
            eixo.set_ylabel("Tempo mediano (ms) — escala logarítmica")
            eixo.set_title(f"Evolução do tempo — {algoritmo}")
            eixo.grid(True, which="both", alpha=0.3)
            eixo.legend(title="Disposição")
        else:
            eixo.text(0.5, 0.5, "Não há medições positivas para este algoritmo.",
                      ha="center", va="center", transform=eixo.transAxes)
            eixo.set_axis_off()
        fig.tight_layout()
        fig.savefig(pasta_evolucao / f"{algoritmo}.png", dpi=160, bbox_inches="tight")
        plt.close(fig)
        total_graficos += 1

    return total_graficos


def criar_resumo(
    caminho: Path,
    dados: list[dict[str, Any]],
    estatisticas: list[dict[str, Any]],
    cobertura: list[dict[str, Any]],
    erros: list[dict[str, str]],
    quantidade_graficos: int,
) -> None:
    status = Counter(linha["status"] for linha in cobertura)
    algoritmos_presentes = sorted({linha["algoritmo"] for linha in dados})
    tamanhos_presentes = sorted({linha["n"] for linha in dados})
    disposicoes_presentes = sorted({linha["disposicao"] for linha in dados})
    texto = [
        "RELATÓRIO DE PROCESSAMENTO — TP1 / ACD",
        "=" * 44,
        f"Medições válidas utilizadas: {len(dados)}",
        f"Configurações observadas (algoritmo, n, caso, disposição): {len(estatisticas)}",
        f"Algoritmos presentes: {', '.join(algoritmos_presentes)}",
        f"Tamanhos presentes: {', '.join(str(n) for n in tamanhos_presentes)}",
        f"Disposições presentes: {', '.join(disposicoes_presentes)}",
        f"Configurações previstas na matriz padrão: {len(cobertura)}",
        f"Configurações completas (pelo menos {REPETICOES_ESPERADAS} medições): {status['COMPLETA']}",
        f"Configurações incompletas: {status['INCOMPLETA']}",
        f"Configurações ausentes: {status['AUSENTE']}",
        f"Linhas inválidas excluídas: {len(erros)}",
        f"Gráficos gerados: {quantidade_graficos}",
        "",
        "Definições:",
        "- Média e mediana calculadas sobre todas as medições disponíveis para a configuração.",
        "- Desvio-padrão amostral (n-1); em grupos com menos de duas medições fica vazio.",
        "- Os tempos originais são mantidos em nanossegundos; também são exportados em milissegundos.",
        "- Gráficos usam mediana e escala logarítmica; configurações ausentes ficam sem ponto/barra.",
        "- Se houver medições em mais de uma sessão/semente, elas são agrupadas na estatística por configuração; a tabela por sessão mantém a separação.",
    ]
    caminho.write_text("\n".join(texto) + "\n", encoding="utf-8")


def main() -> int:
    base = Path(__file__).resolve().parent
    parser = argparse.ArgumentParser(
        description="Calcula estatísticas e gera gráficos para os CSVs de desempenho do TP1."
    )
    parser.add_argument(
        "--entrada", type=Path,
        default=base / "resultados" / "centralizados" / "tempos_consolidados.csv",
        help="Caminho do tempos_consolidados.csv (padrão: resultados/centralizados/tempos_consolidados.csv).",
    )
    parser.add_argument(
        "--saida", type=Path,
        default=base / "resultados" / "estatisticas",
        help="Diretório de saída (padrão: resultados/estatisticas).",
    )
    parser.add_argument(
        "--sem-graficos", action="store_true",
        help="Gera somente tabelas CSV e relatórios, sem importar matplotlib.",
    )
    args = parser.parse_args()

    entrada = args.entrada.expanduser().resolve()
    saida = args.saida.expanduser().resolve()
    if not entrada.is_file():
        print(f"ERRO: arquivo de entrada não encontrado: {entrada}", file=sys.stderr)
        print("Use --entrada para informar o caminho correto.", file=sys.stderr)
        return 2

    try:
        dados, erros = carregar_dados(entrada)
    except (OSError, ValueError) as erro:
        print(f"ERRO ao ler CSV: {erro}", file=sys.stderr)
        return 2
    if not dados:
        print("ERRO: nenhum registro válido foi encontrado no CSV.", file=sys.stderr)
        return 2

    # O agrupamento principal representa algoritmo × tamanho × caso × disposição.
    estatisticas = calcular_estatisticas(
        ["algoritmo", "n", "caso", "disposicao"], dados
    )
    estatisticas_sessao = calcular_estatisticas(
        ["algoritmo", "n", "caso", "disposicao", "semente", "sessao"], dados
    )
    cobertura = gerar_cobertura(dados)
    saida.mkdir(parents=True, exist_ok=True)

    salvar_csv(saida / "estatisticas_por_configuracao.csv", CAMPOS_ESTATISTICAS, estatisticas)
    salvar_csv(
        saida / "estatisticas_por_sessao.csv",
        ["algoritmo", "n", "caso", "disposicao", "semente", "sessao"] +
        [campo for campo in CAMPOS_ESTATISTICAS if campo not in {"algoritmo", "n", "caso", "disposicao"}],
        estatisticas_sessao,
    )
    salvar_csv(
        saida / "cobertura_configuracoes.csv",
        ["algoritmo", "n", "caso", "disposicao", "medicoes_encontradas", "repeticoes_esperadas_minimas", "status"],
        cobertura,
    )
    salvar_csv(
        saida / "anomalias_dados.csv",
        ["linha_csv", "motivo", "algoritmo", "n", "caso", "disposicao", "repeticao", "t_ns"],
        erros,
    )

    quantidade_graficos = 0
    if not args.sem_graficos:
        try:
            quantidade_graficos = gerar_graficos(dados, estatisticas, saida)
        except RuntimeError as erro:
            print(f"ERRO: {erro}", file=sys.stderr)
            print("As tabelas CSV foram geradas; os gráficos não foram concluídos.", file=sys.stderr)
            return 3

    criar_resumo(
        saida / "resumo_processamento.txt", dados, estatisticas, cobertura,
        erros, quantidade_graficos,
    )
    status = Counter(linha["status"] for linha in cobertura)
    print("Processamento concluído.")
    print(f"Entrada: {entrada}")
    print(f"Medições válidas: {len(dados)}; configurações observadas: {len(estatisticas)}")
    print(
        f"Cobertura prevista: {status['COMPLETA']} completas, "
        f"{status['INCOMPLETA']} incompletas, {status['AUSENTE']} ausentes."
    )
    print(f"Linhas inválidas excluídas e registradas: {len(erros)}")
    print(f"Saída: {saida}")
    print("CSV: estatisticas_por_configuracao.csv, estatisticas_por_sessao.csv, cobertura_configuracoes.csv, anomalias_dados.csv")
    print(f"Gráficos gerados: {quantidade_graficos}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
