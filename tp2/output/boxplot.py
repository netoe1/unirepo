import pandas as pd
import matplotlib.pyplot as plt
import numpy as np
from pathlib import Path

PASTA_SCRIPT = Path(__file__).resolve().parent
PASTA_BOXPLOTS = PASTA_SCRIPT / "boxplots"
PASTA_BOXPLOTS.mkdir(parents=True, exist_ok=True)


def localizar_csv_experimento():
    candidatos = list(PASTA_SCRIPT.glob("*/experimento_caminhos_minimos.csv"))
    csv_direto = PASTA_SCRIPT / "experimento_caminhos_minimos.csv"
    if csv_direto.is_file():
        candidatos.append(csv_direto)
    if not candidatos:
        raise SystemExit(f"Nenhum CSV experimental encontrado em: {PASTA_SCRIPT}")
    return max(candidatos, key=lambda caminho: caminho.stat().st_mtime)


ARQUIVO_CSV = localizar_csv_experimento()

COL_ALGORITMO = "algoritmo"          # ex.: "DIJKSTRA", "BELLMAN_FORD"
COL_REPRESENTACAO = "representacao"  # ex.: "LISTA_ADJACENCIA", "MATRIZ_ADJACENCIA"
COL_VERTICES = "vertices"            # ex.: 100, 200, 500
COL_DENSIDADE_ARQUIVO = "densidade_arquivo"
COL_DENSIDADE_GRAFO = "densidade_grafo"
COL_TEMPO = "tempo_ms"               # tempo de execução em milissegundos

df = pd.read_csv(ARQUIVO_CSV, sep=";")

colunas_obrigatorias = {
    "arquivo_grafo",
    COL_ALGORITMO,
    COL_REPRESENTACAO,
    "tipo_grafo",
    COL_VERTICES,
    COL_DENSIDADE_ARQUIVO,
    COL_DENSIDADE_GRAFO,
    "repeticao",
    COL_TEMPO,
}
faltando = colunas_obrigatorias.difference(df.columns)
if faltando:
    raise SystemExit(f"Colunas ausentes no CSV: {', '.join(sorted(faltando))}")

# Normaliza strings para evitar problemas com maiúsculas/minúsculas ou espaços
df[COL_ALGORITMO] = df[COL_ALGORITMO].astype(str).str.strip().str.upper()
df[COL_REPRESENTACAO] = df[COL_REPRESENTACAO].astype(str).str.strip().str.upper()
df["tipo_grafo"] = df["tipo_grafo"].astype(str).str.strip().str.upper()

# Mapeia variações comuns de nomes para rótulos padronizados
mapa_alg = {
    "DIJKSTRA": "Dijkstra",
    "BELLMAN_FORD": "Bellman-Ford",
    "BELLMAN-FORD": "Bellman-Ford"
}
mapa_rep = {
    "LISTA": "LA",
    "LISTA_ADJACENCIA": "LA",
    "LA": "LA",
    "MATRIZ": "MA",
    "MATRIZ_ADJACENCIA": "MA",
    "MA": "MA"
}

df["alg_padrao"] = df[COL_ALGORITMO].map(mapa_alg)
df["rep_padrao"] = df[COL_REPRESENTACAO].map(mapa_rep)

# Filtra apenas as 600 execuções de Dijkstra e Bellman-Ford
df_exp = df.dropna(subset=["alg_padrao", "rep_padrao"]).copy()
df_exp = df_exp[df_exp["tipo_grafo"] == "DIRECIONADO"].copy()
df_exp[COL_VERTICES] = df_exp[COL_VERTICES].astype(int)
df_exp[COL_DENSIDADE_ARQUIVO] = pd.to_numeric(df_exp[COL_DENSIDADE_ARQUIVO], errors="coerce")
df_exp[COL_DENSIDADE_GRAFO] = pd.to_numeric(df_exp[COL_DENSIDADE_GRAFO], errors="coerce")
df_exp["repeticao"] = pd.to_numeric(df_exp["repeticao"], errors="coerce")
df_exp[COL_TEMPO] = pd.to_numeric(df_exp[COL_TEMPO], errors="coerce")
df_exp = df_exp.dropna(
    subset=[COL_VERTICES, COL_DENSIDADE_ARQUIVO, COL_DENSIDADE_GRAFO, "repeticao", COL_TEMPO]
)
if len(df_exp) != 600:
    raise SystemExit(f"Esperadas 600 execucoes dos dois algoritmos; encontradas {len(df_exp)}.")
if not np.isfinite(df_exp[[COL_DENSIDADE_ARQUIVO, COL_DENSIDADE_GRAFO, COL_TEMPO]].to_numpy()).all():
    raise SystemExit("O CSV contem densidades ou tempos nao finitos.")
if (df_exp[COL_TEMPO] <= 0).any():
    raise SystemExit("O CSV contem tempos nao positivos.")

colunas_grupo = [
    "arquivo_grafo",
    "alg_padrao",
    "rep_padrao",
]
contagens = df_exp.groupby(colunas_grupo).size()
if len(contagens) != 60 or (contagens != 10).any():
    raise SystemExit("Cada algoritmo/representacao/grafo deve conter exatamente 10 repeticoes.")

print(f"Total de registros filtrados para o experimento: {len(df_exp)}")
print(f"CSV utilizado: {ARQUIVO_CSV}")

resumo = (
    df_exp.groupby([
        "arquivo_grafo",
        "alg_padrao",
        COL_VERTICES,
        COL_DENSIDADE_ARQUIVO,
        COL_DENSIDADE_GRAFO,
        "rep_padrao",
    ])[COL_TEMPO]
    .agg(
        n="count",
        media="mean",
        desvio_padrao="std",
        mediana="median",
        q1=lambda x: np.percentile(x, 25),
        q3=lambda x: np.percentile(x, 75),
        minimo="min",
        maximo="max",
    )
    .reset_index()
)
resumo["iqr"] = resumo["q3"] - resumo["q1"]
arquivo_resumo = PASTA_BOXPLOTS / "estatisticas_resumo_tp2.csv"
resumo.to_csv(arquivo_resumo, index=False, float_format="%.6f")
print(f"Tabela estatistica salva em: {arquivo_resumo}")

plt.rcParams.update({
    "font.size": 10,
    "axes.titlesize": 11,
    "axes.labelsize": 10,
    "legend.fontsize": 9,
})

tamanhos_v = sorted(df_exp[COL_VERTICES].unique())

for algoritmo in ["Dijkstra", "Bellman-Ford"]:
    sub_alg = df_exp[df_exp["alg_padrao"] == algoritmo]
    if sub_alg.empty:
        continue

    fig, axes = plt.subplots(1, len(tamanhos_v), figsize=(15, 4.8), sharey=False)
    if len(tamanhos_v) == 1:
        axes = [axes]

    cor_la = "#4C72B0"  # Azul para Lista de Adjacência
    cor_ma = "#DD8452"  # Laranja para Matriz de Adjacência

    for ax, v in zip(axes, tamanhos_v):
        sub_v = sub_alg[sub_alg[COL_VERTICES] == v]
        amostras = (
            sub_v[["arquivo_grafo", COL_DENSIDADE_ARQUIVO, COL_DENSIDADE_GRAFO]]
            .drop_duplicates()
            .sort_values(COL_DENSIDADE_ARQUIVO)
            .reset_index(drop=True)
        )
        posicoes = np.arange(len(amostras))
        largura = 0.34

        dados_la = []
        dados_ma = []
        for _, amostra in amostras.iterrows():
            sub_amostra = sub_v[sub_v["arquivo_grafo"] == amostra["arquivo_grafo"]]
            dados_la.append(sub_amostra[sub_amostra["rep_padrao"] == "LA"][COL_TEMPO].values)
            dados_ma.append(sub_amostra[sub_amostra["rep_padrao"] == "MA"][COL_TEMPO].values)

        bp_la = ax.boxplot(
            dados_la,
            positions=posicoes - largura / 2,
            widths=largura * 0.85,
            patch_artist=True,
            showfliers=True,
            flierprops=dict(marker="o", markersize=3.5, alpha=0.6, markeredgecolor=cor_la),
            medianprops=dict(color="black", linewidth=1.3),
        )
        bp_ma = ax.boxplot(
            dados_ma,
            positions=posicoes + largura / 2,
            widths=largura * 0.85,
            patch_artist=True,
            showfliers=True,
            flierprops=dict(marker="s", markersize=3.5, alpha=0.6, markeredgecolor=cor_ma),
            medianprops=dict(color="black", linewidth=1.3),
        )

        for patch in bp_la["boxes"]:
            patch.set_facecolor(cor_la)
            patch.set_alpha(0.8)
        for patch in bp_ma["boxes"]:
            patch.set_facecolor(cor_ma)
            patch.set_alpha(0.8)

        ax.set_title(f"|V| = {v} vértices")
        ax.set_xlabel("D nominal (efetiva entre parênteses)")
        ax.set_xticks(posicoes)
        ax.set_xticklabels([
            f"{linha[COL_DENSIDADE_ARQUIVO]:.1f}\n({linha[COL_DENSIDADE_GRAFO]:.2f})"
            for _, linha in amostras.iterrows()
        ])
        ax.grid(axis="y", linestyle="--", alpha=0.4)

    axes[0].set_ylabel("Tempo de Execução (ms)")

    # Legenda única no topo da figura
    handles = [
        plt.Rectangle((0, 0), 1, 1, facecolor=cor_la, alpha=0.8, edgecolor="black"),
        plt.Rectangle((0, 0), 1, 1, facecolor=cor_ma, alpha=0.8, edgecolor="black"),
    ]
    fig.legend(
        handles,
        ["Lista de Adjacência (LA)", "Matriz de Adjacência (MA)"],
        loc="upper center",
        ncol=2,
        frameon=True,
        bbox_to_anchor=(0.5, 1.0),
    )

    fig.suptitle(f"Desempenho experimental — algoritmo de {algoritmo}", y=1.13, fontweight="bold")
    plt.tight_layout(rect=(0, 0, 1, 0.88))

    nome_base = f"boxplot_{algoritmo.lower().replace('-', '_')}"
    plt.savefig(PASTA_BOXPLOTS / f"{nome_base}.pdf", bbox_inches="tight")
    plt.savefig(PASTA_BOXPLOTS / f"{nome_base}.png", dpi=300, bbox_inches="tight")
    plt.close(fig)
    print(f"Gráficos salvos: {nome_base}.pdf e {nome_base}.png")