# TP1 — Algoritmos e Classificação de Dados (ACD)

Este projeto implementa e avalia dez algoritmos de ordenação em Java. O repositório contém os códigos dos algoritmos, testes de corretude, ferramentas de execução experimental e scripts auxiliares para organizar e analisar os resultados.

## 1. Algoritmos implementados

- Bubble Sort
- Insertion Sort
- Selection Sort
- Shell Sort
- Heap Sort
- Merge Sort
- Quick Sort
- Counting Sort
- Radix Sort
- Bucket Sort

As implementações estão em `TP1/src/`.

## 2. Requisitos

- Linux ou ambiente compatível com Bash.
- JDK instalado, com `java` e `javac` disponíveis no `PATH`.
- GNU Make, caso prefira utilizar o `makefile`.
- Python 3 para os scripts auxiliares de análise.
- Matplotlib para gerar os gráficos com `gerar_graficos.py`.

O ambiente usado na coleta documentada foi: Intel Core i5-12400F, 16 GB de RAM DDR4, Ubuntu 24.04.5 LTS e OpenJDK 21.0.12.1. Os resultados de desempenho podem variar em outros ambientes.

Verifique as ferramentas instaladas:

```bash
java -version
javac -version
python3 --version
make --version
```

## 3. Entrar na pasta do projeto

Execute os comandos a partir da raiz do repositório:

```bash
cd TP1/src
```

Os exemplos seguintes pressupõem que o diretório atual é `TP1/src`.

## 4. Compilar e executar os testes de corretude

A maneira recomendada é usar o script de testes:

```bash
chmod +x testes_tp1.sh
./testes_tp1.sh corretude
```

O script compila os arquivos Java com `javac -encoding UTF-8` e executa `TestesCorretude`. A suíte verifica os dez algoritmos com vetores de tamanhos 10, 11 e 15, nas disposições crescente, decrescente, aleatória e com elementos repetidos. Além de verificar a ordenação crescente, os testes confirmam que os elementos da entrada foram preservados.

Como alternativa, use o `makefile`:

```bash
make test
```

Para compilar sem executar os testes:

```bash
make all
```

## 5. Consultar a ajuda do programa experimental

Depois de compilar, consulte as opções disponíveis:

```bash
java -cp . Main --help
```

A aplicação é executada por argumentos de linha de comando e não solicita dados interativamente. Os parâmetros principais são:

| Parâmetro       | Descrição                               | Valores usuais                                                      |
| ---------------- | ----------------------------------------- | ------------------------------------------------------------------- |
| `--repeticoes` | Número de medições registradas         | Inteiro entre 1 e 1000; use pelo menos 10 para o experimento do TP1 |
| `--caso`       | Rótulo do cenário experimental          | `MELHOR`, `MEDIO` ou `PIOR`                                   |
| `--algoritmo`  | Algoritmo a executar                      | Nome da classe, por exemplo `MergeSort`                           |
| `--disposicao` | Ordem dos dados de entrada                | `CRESCENTE`, `ALEATORIO`, `DECRESCENTE` ou `REPETIDOS`      |
| `--n`          | Quantidade de elementos                   | Entre 1 e 1.000.000                                                 |
| `--semente`    | Semente para gerar entrada aleatória     | Padrão: `2026`                                                   |
| `--saida`      | Prefixo dos arquivos CSV de saída        | Padrão: `resultados_tp1`                                         |
| `--debug`      | Mostra detalhes e progresso da execução | Flag opcional                                                       |
| `--no-color`   | Desativa cores ANSI nos logs              | Flag opcional                                                       |

O rótulo `--caso` e a disposição real são registrados separadamente. Uma entrada crescente ou decrescente não corresponde necessariamente ao melhor ou pior caso teórico de todos os algoritmos.

## 6. Executar uma medição de exemplo

O comando abaixo executa dez medições do Merge Sort com 1.000 elementos em ordem aleatória. O nome da pasta inclui a data e hora para evitar misturar a execução com resultados anteriores:

```bash
SAIDA="resultados/verificacao_$(date +%Y%m%d_%H%M%S)/medicao"

java -cp . Main \
  --repeticoes 10 \
  --caso MEDIO \
  --algoritmo MergeSort \
  --disposicao ALEATORIO \
  --n 1000 \
  --semente 2026 \
  --saida "$SAIDA" \
  --no-color
```

O programa cria um arquivo semelhante a:

```text
resultados/verificacao_AAAAMMDD_HHMMSS/medicao_MergeSort_n1000.csv
```

O nome efetivo da pasta usa a data e a hora locais da execução.

## 7. Executar os experimentos de desempenho

### Bateria reduzida

Para executar a bateria de desempenho predefinida, rode:

```bash
./testes_tp1.sh desempenho
```

O script executa dez repetições por configuração selecionada e grava os arquivos com o prefixo `resultados/tp1_`. A bateria é parcial: limita os cenários de maior custo para os algoritmos quadráticos e seleciona configurações específicas para alguns algoritmos. Consulte o próprio `testes_tp1.sh` para ver a matriz executada.

### Bateria completa — execução opcional

O script `experimento_completo_tp1.sh` percorre os dez algoritmos, quatro tamanhos (`1000`, `10000`, `100000` e `1000000`) e três disposições (crescente, aleatória e decrescente), com dez repetições por configuração:

```bash
chmod +x experimento_completo_tp1.sh
./experimento_completo_tp1.sh
```

**Atenção:** essa bateria pode levar muito tempo, sobretudo para Bubble Sort, Insertion Sort e Selection Sort com um milhão de elementos. Recomenda-se executar primeiro os testes de corretude e a bateria reduzida. Planeje a execução completa somente quando houver tempo e recursos disponíveis.

O programa acrescenta as medições aos CSVs que já existam se o cabeçalho for compatível. Antes de repetir uma bateria, faça cópia dos resultados anteriores ou escolha um prefixo de saída novo. Não misture sessões diferentes sem registrá-las e identificá-las na análise.

## 8. Arquivos de resultados

Cada execução da `Main` gera um CSV cujo nome segue o padrão:

```text
<prefixo>_<Algoritmo>_n<Tamanho>.csv
```

O arquivo contém uma linha por medição e as seguintes colunas:

| Coluna         | Significado                                           |
| -------------- | ----------------------------------------------------- |
| `algoritmo`  | Algoritmo executado                                   |
| `n`          | Tamanho da entrada                                    |
| `caso`       | Rótulo informado (`MELHOR`, `MEDIO` ou `PIOR`) |
| `disposicao` | Disposição real da entrada                          |
| `semente`    | Semente da geração de dados                         |
| `sessao`     | Identificador da sessão de execução                |
| `repeticao`  | Número da repetição                                |
| `t_ns`       | Tempo medido em nanossegundos                         |

A medição usa `System.nanoTime()` por meio do tempo devolvido pelo método `sort()`. A geração do vetor e a cópia externa para cada repetição são realizadas fora do trecho cronometrado. O programa realiza duas execuções de aquecimento antes das medições registradas.

Os dados de desempenho atualmente documentados contêm 1.130 medições em 113 configurações completas de 120 planejadas. Faltam sete configurações para um milhão de elementos: entradas aleatória e decrescente de Bubble Sort; entradas aleatória e decrescente de Insertion Sort; e as três disposições de Selection Sort. Não interprete valores ausentes como tempo zero.

## 9. Estatísticas e gráficos

O projeto inclui estes scripts auxiliares:

- `consolidar_tables.py`: reúne CSVs brutos em um arquivo consolidado e gera um relatório de integridade.
- `gerar_table_estatistica.py`: calcula média, mediana, mínimo, máximo, desvio-padrão amostral e coeficiente de variação.
- `gerar_graficos.py`: gera gráficos de comparação entre algoritmos, evolução por tamanho, referências teóricas e cobertura experimental.

A sequência pretendida, depois que o CSV consolidado estiver disponível, é:

```bash
python3 gerar_table_estatistica.py
python3 gerar_graficos.py
```

O script estatístico espera, por padrão, o arquivo `resultados/centralizados/tempos_consolidados.csv`; o script de gráficos espera as tabelas em `resultados/estatisticas/`. Para instalar o Matplotlib no Ubuntu/Debian:

```bash
sudo apt install python3-matplotlib
```

**Nota sobre a versão atual:** antes de executar o fluxo automatizado completo, verifique `consolidar_tables.py`: na versão documentada, a variável `SAIDA` é declarada como texto, mas depois é utilizada como um objeto `Path`. A inicialização deve ser ajustada para usar caminhos `Path` de forma consistente, com a saída em `resultados/centralizados/`, antes de executar a centralização. Os scripts estatísticos e de gráficos dependem da existência desses arquivos de entrada.

Os gráficos representam as medições disponíveis. Configurações não executadas devem permanecer ausentes; linhas teóricas normalizadas servem para comparar tendências de crescimento, não para prever tempos absolutos.

## 10. Cuidados metodológicos

- Use exatamente a mesma entrada para os algoritmos que participam da mesma configuração experimental.
- Faça uma cópia limpa do vetor antes de cada ordenação.
- Exclua geração, cópia externa e impressão do intervalo medido.
- Faça aquecimentos da JVM antes de registrar os resultados; o programa atual executa dois aquecimentos.
- Preserve todas as medições individuais e documente qualquer critério de exclusão.
- Não associe automaticamente `CRESCENTE`, `ALEATORIO` e `DECRESCENTE` aos casos teóricos de melhor, médio e pior de todos os algoritmos.
- A complexidade de Shell Sort depende da sequência de intervalos; Counting Sort, Radix Sort e Bucket Sort dependem das chaves e da implementação concreta.
- Confirme a implementação e a fronteira temporal utilizadas antes de comparar diretamente os resultados de versões diferentes dos algoritmos.

## 11. Limpeza dos arquivos compilados

Para remover apenas os arquivos `.class` gerados na compilação:

```bash
make clean
```

Evite `make clean_all` quando quiser preservar resultados experimentais: esse alvo remove arquivos CSV diretamente na pasta `resultados/`.

## 12. Estrutura relevante

```text
unirepo/
├── README.md
├── docs/
│   ├── EnunciadoTrabalho.md
│   ├── TestesGenerator.md
│   └── TestesTP1.md
└── TP1/
    └── src/
        ├── *Sort.java
        ├── Ordenador.java
        ├── Main.java
        ├── TestesCorretude.java
        ├── TestesGenerator.java
        ├── testes_tp1.sh
        ├── experimento_completo_tp1.sh
        ├── consolidar_tables.py
        ├── gerar_table_estatistica.py
        └── resultados/
```

Para detalhes do enunciado e da análise, consulte `docs/EnunciadoTrabalho.md` e `docs/AnaliseDados.md`.
