#!/usr/bin/env python3
"""Gera visualizações para o TP1 a partir das tabelas estatísticas.

Pré-requisito: execute estatisticas_tp1.py antes deste script.
Dependência: matplotlib (Debian/Ubuntu: sudo apt install python3-matplotlib)

Os gráficos comparam medianas observadas e mostram linhas teóricas normalizadas
como referência de crescimento, não como previsões exatas de tempo.
"""
from __future__ import annotations

import argparse
import csv
import math
import sys
from pathlib import Path
from typing import Any

ALGORITMOS_ESPERADOS = [
    "BubbleSort", "InsertionSort", "SelectionSort", "ShellSort", "HeapSort",
    "MergeSort", "QuickSort", "CountingSort", "RadixSort", "BucketSort",
]
TAMANHOS_ESPERADOS = [1_000, 10_000, 100_000, 1_000_000]
CENARIOS = [
    ("MELHOR", "CRESCENTE"),
    ("MEDIO", "ALEATORIO"),
    ("PIOR", "DECRESCENTE"),
]

# Notas teóricas assumindo implementações convencionais. ShellSort varia com gaps;
# CountingSort, RadixSort e BucketSort dependem de parâmetros/distribuição dos valores.
COMPLEXIDADE_TEORICA = [
    ("BubbleSort", "O(n)¹", "O(n²)", "O(n²)", "Melhor caso linear se há parada antecipada quando não ocorrem trocas."),
    ("InsertionSort", "O(n)", "O(n²)", "O(n²)", "A entrada crescente reduz o trabalho; aleatória/decrescente exigem mais deslocamentos."),
    ("SelectionSort", "Θ(n²)", "Θ(n²)", "Θ(n²)", "O número de comparações permanece quadrático mesmo em entrada ordenada."),
    ("ShellSort", "Depende", "Depende", "Depende", "A complexidade depende da sequência de intervalos (gaps) utilizada."),
    ("HeapSort", "Θ(n log n)", "Θ(n log n)", "Θ(n log n)", "Ordem inicial tende a ter influência limitada na classe assintótica."),
    ("MergeSort", "Θ(n log n)", "Θ(n log n)", "Θ(n log n)", "A divisão e intercalação mantêm a mesma classe nos casos convencionais."),
    ("QuickSort", "Θ(n log n)", "Θ(n log n) esperado", "Θ(n²)", "O comportamento depende da escolha do pivô e do particionamento."),
    ("CountingSort", "O(n+k)", "O(n+k)", "O(n+k)", "k é o intervalo de chaves; k não está registrado no CSV de tempos."),
    ("RadixSort", "O(d(n+b))", "O(d(n+b))", "O(d(n+b))", "d é o número de dígitos/passes e b é a base; depende da representação dos valores."),
    ("BucketSort", "O(n+k) esperado¹", "O(n+k) esperado¹", "O(n²) em pior caso", "Esperado sob distribuição favorável; buckets concentrados podem degradar o tempo."),
]


def ler_csv(caminho: Path) -> list[dict[str, str]]:
    if not caminho.is_file():
        raise FileNotFoundError(f"Arquivo não encontrado: {caminho}")
    with caminho.open("r", newline="", encoding="utf-8-sig") as f:
        return list(csv.DictReader(f))


def importar_matplotlib():
    try:
        import matplotlib
        matplotlib.use("Agg")
        import matplotlib.pyplot as plt
        return plt
    except ImportError as erro:
        raise RuntimeError(
            "Matplotlib não está instalado. No Debian/Ubuntu, execute: sudo apt install python3-matplotlib"
        ) from erro


def para_int(valor: Any, padrao: int = 0) -> int:
    try:
        return int(float(str(valor).strip()))
    except (ValueError, TypeError):
        return padrao


def para_float(valor: Any) -> float | None:
    try:
        valor_num = float(str(valor).strip())
        if math.isfinite(valor_num):
            return valor_num
    except (ValueError, TypeError):
        pass
    return None


def indice_estatisticas(linhas: list[dict[str, str]]) -> dict[tuple[str, int, str], dict[str, str]]:
    # A tabela estatística tem uma única linha por algoritmo × n × caso × disposição.
    # O índice usa algoritmo, n, disposição para os três cenários esperados.
    indice: dict[tuple[str, int, str], dict[str, str]] = {}
    for linha in linhas:
        chave = (linha.get("algoritmo", "").strip(), para_int(linha.get("n")), linha.get("disposicao", "").strip().upper())
        indice[chave] = linha
    return indice


def texto_n(n: int) -> str:
    return f"{n:,}".replace(",", ".")


def curva_teorica(nome: str, ns: list[int], n0: int) -> list[float]:
    def f(n: int) -> float:
        if nome == "O(n)":
            return float(n)
        if nome == "O(n log n)":
            return n * math.log2(max(n, 2))
        if nome == "O(n²)":
            return float(n * n)
        raise ValueError(nome)
    valor_base = f(n0)
    return [f(n) / valor_base for n in ns]


def referencias_por_algoritmo(algoritmo: str) -> tuple[list[str], str]:
    if algoritmo in {"BubbleSort", "InsertionSort"}:
        return ["O(n)", "O(n²)"], "Referências: entrada crescente tende ao melhor caso O(n); aleatória/decrescente, O(n²)."
    if algoritmo == "SelectionSort":
        return ["O(n²)"], "Referência teórica: Θ(n²) nas três disposições."
    if algoritmo in {"HeapSort", "MergeSort"}:
        return ["O(n log n)"], "Referência teórica: Θ(n log n) nos casos convencionais."
    if algoritmo == "QuickSort":
        return ["O(n log n)", "O(n²)"], "As referências mostram o caso esperado e o pior caso; o pivô implementado afeta o resultado."
    if algoritmo == "CountingSort":
        return ["O(n)"], "Referência O(n) apenas como aproximação quando k (intervalo de chaves) permanece limitado; k não foi medido no CSV."
    if algoritmo == "RadixSort":
        return ["O(n)"], "Referência aproximadamente linear quando a quantidade de dígitos d e a base b permanecem limitadas."
    if algoritmo == "BucketSort":
        return ["O(n)", "O(n²)"], "Referências de crescimento médio (distribuição favorável) e pior caso; depende da distribuição nos buckets."
    return [], "A classe assintótica do ShellSort depende da sequência de gaps; exibe-se apenas o crescimento observado."


def desenhar_comparacoes(plt, indice, ordem, pasta: Path) -> int:
    pasta.mkdir(parents=True, exist_ok=True)
    quantidade = 0
    for n in TAMANHOS_ESPERADOS:
        for _caso, disposicao in CENARIOS:
            valores: list[tuple[str, float]] = []
            for algoritmo in ordem:
                linha = indice.get((algoritmo, n, disposicao))
                if linha is None:
                    continue
                mediana = para_float(linha.get("mediana_ms"))
                if mediana is not None and mediana > 0:
                    valores.append((algoritmo, mediana))
            valores.sort(key=lambda par: par[1])
            fig, ax = plt.subplots(figsize=(10.5, 6.5))
            if valores:
                nomes = [v[0] for v in valores]
                tempos = [v[1] for v in valores]
                ax.barh(nomes, tempos)
                ax.set_xscale("log")
                ax.set_xlabel("Tempo mediano (ms) — escala logarítmica")
                ax.set_ylabel("Algoritmo")
                ax.set_title(f"Comparação por algoritmo — n={texto_n(n)} — {disposicao.title()}")
                ax.grid(True, axis="x", which="both", alpha=0.25)
                ax.invert_yaxis()
                ax.text(0.01, -0.15, "Barras ausentes indicam que não há medição válida para a configuração.",
                        transform=ax.transAxes, fontsize=8, va="top")
            else:
                ax.text(0.5, 0.5, "Nenhuma medição positiva disponível para esta configuração.", ha="center", va="center", transform=ax.transAxes)
                ax.set_axis_off()
            fig.tight_layout()
            fig.savefig(pasta / f"comparacao_n{n}_{disposicao.lower()}.png", dpi=180, bbox_inches="tight")
            plt.close(fig)
            quantidade += 1
    return quantidade


def desenhar_evolucao_empirica(plt, indice, ordem, pasta: Path) -> int:
    pasta.mkdir(parents=True, exist_ok=True)
    quantidade = 0
    for algoritmo in ordem:
        fig, ax = plt.subplots(figsize=(9.5, 6.5))
        plotou = False
        for _caso, disposicao in CENARIOS:
            xs: list[int] = []
            ys: list[float] = []
            for n in TAMANHOS_ESPERADOS:
                linha = indice.get((algoritmo, n, disposicao))
                tempo = para_float(linha.get("mediana_ms")) if linha else None
                xs.append(n)
                ys.append(tempo if tempo is not None and tempo > 0 else math.nan)
            if any(math.isfinite(y) for y in ys):
                ax.plot(xs, ys, marker="o", linewidth=2, label=disposicao.title())
                plotou = True
        if plotou:
            ax.set_xscale("log")
            ax.set_yscale("log")
            ax.set_xticks(TAMANHOS_ESPERADOS)
            ax.set_xticklabels([texto_n(n) for n in TAMANHOS_ESPERADOS])
            ax.set_xlabel("Tamanho da entrada (n)")
            ax.set_ylabel("Tempo mediano (ms) — escala logarítmica")
            ax.set_title(f"Crescimento empírico — {algoritmo}")
            ax.grid(True, which="both", alpha=0.23)
            ax.legend(title="Disposição")
            ax.text(0.01, -0.16, "Pontos representam medianas observadas; lacunas indicam configurações sem medição.",
                    transform=ax.transAxes, fontsize=8, va="top")
        else:
            ax.text(0.5, 0.5, "Sem medições positivas para este algoritmo.", ha="center", va="center", transform=ax.transAxes)
            ax.set_axis_off()
        fig.tight_layout()
        fig.savefig(pasta / f"evolucao_{algoritmo}.png", dpi=180, bbox_inches="tight")
        plt.close(fig)
        quantidade += 1
    return quantidade


def desenhar_crescimento_teorico(plt, indice, ordem, pasta: Path) -> int:
    pasta.mkdir(parents=True, exist_ok=True)
    quantidade = 0
    ns = TAMANHOS_ESPERADOS
    for algoritmo in ordem:
        fig, ax = plt.subplots(figsize=(9.5, 6.5))
        baseline_n = next((n for n in ns if (linha := indice.get((algoritmo, n, "CRESCENTE"))) and (para_float(linha.get("mediana_ms")) or 0) > 0), None)
        if baseline_n is None:
            ax.text(0.5, 0.5, "Sem medição crescente positiva para normalizar o gráfico.", ha="center", va="center", transform=ax.transAxes)
            ax.set_axis_off()
        else:
            # Normalize each observed disposition to its own first measured value; this makes relative growth visible.
            for _caso, disposicao in CENARIOS:
                observados: list[tuple[int, float]] = []
                primeiro_tempo: float | None = None
                for n in ns:
                    linha = indice.get((algoritmo, n, disposicao))
                    tempo = para_float(linha.get("mediana_ms")) if linha else None
                    if tempo is not None and tempo > 0:
                        if primeiro_tempo is None:
                            primeiro_tempo = tempo
                        observados.append((n, tempo))
                if primeiro_tempo is not None and observados:
                    ax.plot([x for x, _ in observados], [y / primeiro_tempo for _, y in observados],
                            marker="o", linewidth=2, label=f"Observado — {disposicao.title()}")

            refs, nota = referencias_por_algoritmo(algoritmo)
            for ref in refs:
                ax.plot(ns, curva_teorica(ref, ns, ns[0]), linestyle="--", linewidth=1.8,
                        label=f"Referência teórica {ref}")
            ax.set_xscale("log")
            ax.set_yscale("log")
            ax.set_xticks(ns)
            ax.set_xticklabels([texto_n(n) for n in ns])
            ax.set_xlabel("Tamanho da entrada (n)")
            ax.set_ylabel("Crescimento relativo do tempo (baseline = 1)")
            ax.set_title(f"Crescimento observado × referências assintóticas — {algoritmo}")
            ax.grid(True, which="both", alpha=0.23)
            ax.legend(fontsize=8, loc="best")
            ax.text(0.01, -0.20,
                    "Curvas teóricas normalizadas são referências de crescimento, não tempos previstos.\n" + nota,
                    transform=ax.transAxes, fontsize=8, va="top", wrap=True)
        fig.tight_layout()
        fig.savefig(pasta / f"crescimento_teorico_{algoritmo}.png", dpi=180, bbox_inches="tight")
        plt.close(fig)
        quantidade += 1
    return quantidade


def desenhar_cobertura(plt, cobertura: list[dict[str, str]], pasta: Path) -> int:
    if not cobertura:
        return 0
    pasta.mkdir(parents=True, exist_ok=True)
    algoritmo_ordem = {a: i for i, a in enumerate(ALGORITMOS_ESPERADOS)}
    linhas = sorted(cobertura, key=lambda r: (algoritmo_ordem.get(r.get("algoritmo", ""), 999), para_int(r.get("n")), r.get("disposicao", "")))
    nomes = [f"{r.get('algoritmo', '')} — {texto_n(para_int(r.get('n')))}" for r in linhas]
    contagens = [para_int(r.get("medicoes_encontradas")) for r in linhas]
    fig_h = max(10, len(linhas) * 0.19)
    fig, ax = plt.subplots(figsize=(11, fig_h))
    barras = ax.barh(nomes, contagens)
    ax.set_xlim(0, max(10, max(contagens, default=0) + 0.5))
    ax.set_xticks(range(0, 11, 2))
    ax.set_xlabel("Medições encontradas (10 esperadas por configuração)")
    ax.set_title("Cobertura das configurações experimentais")
    ax.grid(True, axis="x", alpha=0.25)
    for barra, r, qtd in zip(barras, linhas, contagens):
        ax.text(max(qtd, 0) + 0.08, barra.get_y() + barra.get_height() / 2,
                f"{qtd}/10 — {r.get('disposicao', '')}", va="center", fontsize=6.5)
    ax.invert_yaxis()
    fig.tight_layout()
    fig.savefig(pasta / "cobertura_configuracoes.png", dpi=170, bbox_inches="tight")
    plt.close(fig)
    return 1


def desenhar_tabela_teorica(plt, pasta: Path) -> int:
    pasta.mkdir(parents=True, exist_ok=True)
    fig, ax = plt.subplots(figsize=(16, 7.8))
    ax.axis("off")
    colunas = ["Algoritmo", "Melhor caso", "Caso médio", "Pior caso", "Condição / interpretação"]
    tabela = ax.table(cellText=COMPLEXIDADE_TEORICA, colLabels=colunas, cellLoc="left", colLoc="left",
                      colWidths=[0.13, 0.12, 0.18, 0.14, 0.43], loc="center")
    tabela.auto_set_font_size(False)
    tabela.set_fontsize(8.5)
    tabela.scale(1, 2.0)
    ax.set_title("Referência teórica das complexidades de ordenação", fontsize=15, pad=22)
    fig.text(0.02, 0.045,
             "¹ Depende da implementação e das condições de entrada. k = intervalo de chaves; d = número de dígitos/passes; b = base/radix.\n"
             "As classes são referências acadêmicas convencionais; confirme a sequência de gaps, pivô e representação usados no código do TP1.",
             fontsize=8, va="bottom")
    fig.tight_layout(rect=[0, 0.08, 1, 1])
    fig.savefig(pasta / "complexidade_teorica.png", dpi=180, bbox_inches="tight")
    plt.close(fig)
    return 1


def main() -> int:
    base = Path(__file__).resolve().parent
    parser = argparse.ArgumentParser(description="Gera gráficos a partir das estatísticas já calculadas.")
    parser.add_argument("--entrada", type=Path, default=base / "resultados" / "estatisticas" / "estatisticas_por_configuracao.csv",
                        help="CSV de estatísticas por configuração.")
    parser.add_argument("--cobertura", type=Path, default=base / "resultados" / "estatisticas" / "cobertura_configuracoes.csv",
                        help="CSV de cobertura gerado por estatisticas_tp1.py.")
    parser.add_argument("--saida", type=Path, default=base / "resultados" / "estatisticas" / "graficos",
                        help="Pasta de saída para imagens.")
    args = parser.parse_args()
    entrada, cobertura_path, saida = args.entrada.expanduser().resolve(), args.cobertura.expanduser().resolve(), args.saida.expanduser().resolve()
    try:
        estatisticas = ler_csv(entrada)
        cobertura = ler_csv(cobertura_path) if cobertura_path.is_file() else []
        plt = importar_matplotlib()
    except (OSError, ValueError, RuntimeError) as erro:
        print(f"ERRO: {erro}", file=sys.stderr)
        return 2
    if not estatisticas:
        print("ERRO: a tabela de estatísticas está vazia.", file=sys.stderr)
        return 2

    indice = indice_estatisticas(estatisticas)
    presentes = {r.get("algoritmo", "").strip() for r in estatisticas}
    ordem = [a for a in ALGORITMOS_ESPERADOS if a in presentes] + sorted(presentes - set(ALGORITMOS_ESPERADOS))
    saida.mkdir(parents=True, exist_ok=True)
    total = 0
    total += desenhar_comparacoes(plt, indice, ordem, saida / "comparacao_algoritmos")
    total += desenhar_evolucao_empirica(plt, indice, ordem, saida / "evolucao_empirica")
    total += desenhar_crescimento_teorico(plt, indice, ordem, saida / "crescimento_observado_vs_teoria")
    total += desenhar_cobertura(plt, cobertura, saida / "cobertura")
    total += desenhar_tabela_teorica(plt, saida / "teoria")
    instrucoes = [
        "GUIA DOS GRÁFICOS — TP1 / ACD", "=" * 34,
        "comparacao_algoritmos/: barras horizontais da mediana, em escala logarítmica, para comparar algoritmos com o mesmo n e disposição.",
        "evolucao_empirica/: medianas medidas por tamanho e disposição; lacunas significam que não existe medição registrada.",
        "crescimento_observado_vs_teoria/: tempos observados normalizados e referências O(n), O(n log n) e/ou O(n²), conforme a família do algoritmo.",
        "cobertura/: quantidade de medições por algoritmo, tamanho e disposição; o esperado é 10 por configuração.",
        "teoria/complexidade_teorica.png: resumo das complexidades convencionais e das condições importantes.",
        "IMPORTANTE: curvas teóricas normalizadas comparam tendências de crescimento; não são previsões exatas de tempo. Parâmetros como k, d, b, gaps e política de pivô afetam a interpretação.",
        "Mediana usada para reduzir o efeito de medições atípicas. Os gráficos de comparação usam escala logarítmica porque as magnitudes variam bastante.",
    ]
    (saida / "LEIA-ME_GRAFICOS.txt").write_text("\n".join(instrucoes) + "\n", encoding="utf-8")
    print("Gráficos gerados com sucesso.")
    print(f"Estatísticas lidas: {entrada}")
    print(f"Imagens geradas: {total}")
    print(f"Saída: {saida}")
    return 0

if __name__ == "__main__":
    raise SystemExit(main())
