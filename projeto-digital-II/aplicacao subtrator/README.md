# Projeto VHDL com GHDL

Este repositório contém o desenvolvimento e a simulação de um circuito digital escrito em **VHDL**, utilizando o compilador de código aberto **GHDL** e a ferramenta **GTKWave** para visualização das formas de onda.

## 🚀 Pré-requisitos

Antes de começar, você precisa ter instalado em sua máquina:

- **GHDL** (Compilador VHDL)
- **GTKWave** (Visualizador de formas de onda)

### Instalação rápida

- **Ubuntu/Debian:**
  ```bash
  sudo apt update
  sudo apt install ghdl gtkwave
  ```
- **macOS (Homebrew):**
  ```bash
  brew install ghdl gtkwave
  ```

## 🛠️ Como Compilar e Simular

O fluxo de trabalho no terminal segue três passos principais: análise, elaboração e execução.

Substitua `meu_arquivo.vhd` pelo nome do seu arquivo fonte e `nome_tb` pelo nome da sua entidade de teste (_Testbench_).

### 1. Analisar (Compilar)

Verifica a sintaxe do arquivo VHDL:

```bash
ghdl -a meu_arquivo.vhd
```

### 2. Elaborar

Gera o executável da simulação com base na entidade principal do seu Testbench:

```bash
ghdl -e nome_tb
```

### 3. Executar (Simular)

Roda a simulação e exporta as transições de sinais para um arquivo `.vcd`:

```bash
ghdl -r nome_tb --vcd=resultado.vcd
```

## 📊 Visualizando as Formas de Onda

Após gerar o arquivo `resultado.vcd`, abra o GTKWave para analisar os gráficos temporais dos sinais:

```bash
gtkwave resultado.vcd
```

## 📂 Estrutura do Projeto

- `src/` — Arquivos de código fonte do circuito (`.vhd`).
- `sim/` — Arquivos de testbench e simulação (`.vhd`).
- `resultado.vcd` — Arquivo gerado para leitura de ondas no GTKWave (gerado após simulação).
