# RNN — Contexto completo do projeto e backlog executável do H3

**Projeto:** Robot With No Name (RNN)**Componente curricular:** Projeto Integrador III — Grupo 1**Equipe indicada na documentação:** Ely Neto, Leonardo Manzke e Sara Henssler**Data desta consolidação:** 26/10/2026**Versão do documento:** 1.0**Finalidade:** servir como contexto de continuidade para a equipe e para outros chats/agentes que auxiliarão no desenvolvimento do compilador.

> **Como interpretar este documento:** ele combina o contexto da DSL e os requisitos acadêmicos disponíveis, a implementação H3 reconstruída no pacote atual e um backlog proposto com passos executáveis. Elementos de documentação histórica que contradizem o front-end atual são identificados como históricos. Decisões que ainda dependem do simulador oficial são marcadas como premissas, e não como fatos confirmados.

---

# Parte I — Contexto do projeto

## 1. Visão geral

O **Robot With No Name (RNN)** é uma linguagem de domínio específico (DSL) destinada a descrever o comportamento de um robô de entrega em um ambiente discreto bidimensional. A linguagem utiliza um paradigma imperativo: o programa descreve ações, leituras de sensores, condições, repetições e desvios de fluxo.

O objetivo da DSL é permitir que o programador descreva o comportamento do robô sem escrever diretamente código de baixo nível, registradores e instruções de máquina. O compilador deve traduzir a linguagem RNN para Assembly e, depois, para código binário compatível com a MiniMIPS.

O projeto foi dividido em horizontes:

- **H1 — Especificação da linguagem:** definição do domínio, das construções, da sintaxe e da semântica da DSL.
- **H2 — Front-end:** implementação da análise léxica, análise sintática e construção da AST.
- **H3 — Compilação até binário:** implementação da análise semântica, geração de Assembly, encoder/assembler, integração e testes.
- **H4 — Integração e validação comportamental:** integração com o simulador oficial, execução dos programas, análise de traces e validação do comportamento do robô.

O documento original de requisitos de H3 é `ContextoH3.txt`; uma cópia está incluída no pacote atual em `docs/h3/requisitos-h3-original.txt`.

## 2. Escopo funcional da DSL

### 2.1 Domínio e modelo conceitual

O domínio original é um robô de entrega que opera em um grid 2D com coordenadas `(x, y)`. O ambiente pode conter células livres, obstáculos, um pacote e um ponto de entrega. O robô consulta sensores locais e executa ações para se deslocar, coletar e entregar o pacote.

O estado conceitual do robô é descrito por:

- **Posição:** coordenadas X e Y.
- **Carregando pacote:** valor booleano que indica se o robô está transportando o pacote.
- **Última ação falhou:** valor booleano que indica se a última ação tentada falhou.
- **Orientação:** representação cardinal usada na documentação de compatibilidade com a MiniMIPS.

As regras operacionais históricas descrevem movimento para células transitáveis, falha ao colidir com obstáculos ou ultrapassar os limites do grid, `pickup` quando há um pacote na célula e `drop` quando o robô carrega o pacote e está no destino. Essas regras conceituais precisam ser validadas no ambiente oficial durante H4.

### 2.2 Construções suportadas pelo front-end atual do pacote H3

A gramática atual do pacote trabalha com o subconjunto abaixo:

**Ações**

- `move_north`
- `move_south`
- `move_east`
- `move_west`
- `pickup`
- `drop`

**Consulta**

- `where_am_i` — aceita como comando isolado na versão atual. Não há ainda mecanismo na DSL atual para atribuir ou consumir a posição retornada.

**Sensores**

- `north_blocked`
- `south_blocked`
- `east_blocked`
- `west_blocked`
- `has_package`
- `at_destination`
- `is_carrying`
- `last_failed`

**Controle de fluxo**

- `if`
- `while`
- `goto nome_rotulo`
- declaração de rótulo no formato `nome_rotulo:`
- `break`

**Operadores**

- Unário: `!`
- Binários: `&`, `|`, `==`, `!=`

**Regras de sintaxe relevantes**

- A indentação define os blocos de `if` e `while`.
- A versão atual não possui `else`.
- Comentários de linha começam com `--`.
- Os blocos usam espaços para indentação; tabulações devem ser evitadas conforme a implementação atual.
- A versão atual não possui variáveis DSL, atribuições, literais booleanos, operadores aritméticos, comparações `<`, `>`, `<=`, `>=`, nem comandos de configuração do ambiente.
- `where_am_i` não é uma condição booleana e não deve ser utilizado como expressão de `if` ou `while` nesta versão.

Exemplo representativo:

```text
inicio:
while !has_package
    if north_blocked
        move_east
    if !north_blocked
        move_north
goto inicio
```

Este exemplo é ilustrativo da sintaxe suportada. A execução física e o resultado semântico do programa dependem do cenário e do protocolo de E/S.

### 2.3 Divergências entre documentos históricos e o front-end atual

Alguns documentos antigos de H1 descrevem nomes e funcionalidades que não fazem parte do subconjunto atualmente implementado. Exemplos históricos:

- Ações antigas: `move_up`, `move_down`, `move_right`, `move_left`.
- Comandos de configuração do ambiente: `def_grid`, `def_pack`, `def_drop_point`, `crt_obstacle`.
- Possíveis argumentos numéricos, literais e validações de coordenadas.
- Comparações e construções adicionais sugeridas por versões antigas de gramática.

**Não acrescente automaticamente esses recursos ao parser do H3.** A implementação atual usa `move_north`, `move_south`, `move_east`, `move_west` e não implementa os comandos de inicialização do ambiente, variáveis ou atribuições. Caso a equipe decida reintroduzir algum recurso, deve revisar a especificação, a gramática, a semântica, o mapeamento e os testes em conjunto.

## 3. Estado e protocolo de sensores/atuadores

A documentação histórica define a interface entre o programa e o ambiente por meio do registrador especial `$r7`.

### 3.1 Bits de sensores documentados historicamente

| Bit | Sensor             | Interpretação histórica  |
| --: | ------------------ | --------------------------- |
|   0 | `north_blocked`  | obstáculo/limite ao norte  |
|   1 | `east_blocked`   | obstáculo/limite a leste   |
|   2 | `west_blocked`   | obstáculo/limite a oeste   |
|   3 | `south_blocked`  | obstáculo/limite ao sul    |
|   4 | `has_package`    | pacote na célula atual     |
|   5 | `at_destination` | robô na célula de destino |
|   6 | `is_carrying`    | robô carregando pacote     |
|   7 | `last_failed`    | última ação falhou       |

A documentação histórica afirma que o ambiente atualiza as informações de sensores depois de uma ação. Esse protocolo ainda precisa ser confirmado na execução com o simulador oficial.

### 3.2 Códigos de ações documentados historicamente

| Ação atual da DSL | Código histórico escrito em `$r7` |
| ------------------- | ------------------------------------: |
| `move_north`      |                          1 (`0x01`) |
| `move_south`      |                          2 (`0x02`) |
| `move_east`       |                          3 (`0x03`) |
| `move_west`       |                          4 (`0x04`) |
| `pickup`          |                          5 (`0x05`) |
| `drop`            |                          6 (`0x06`) |

A convenção de tradução adotada no pacote atual escreve estes valores usando instruções como `addi $r7, $r0, código`. A aceitação desse mecanismo e dos códigos pelo simulador oficial **não está confirmada**.

## 4. Arquitetura-alvo MiniMIPS

A referência técnica disponível é o artigo *MiniMIPS: An 8-Bit MIPS in an FPGA for Educational Purposes*, de Cesar Ortega-Sanchez (2011), DOI `10.1109/ReConFig.2011.62`, fornecido como `mini-mips.pdf`.

### 4.1 Características e formatos

- Dados e endereços de dados: 8 bits, conforme a arquitetura descrita.
- Instruções: 16 bits.
- Campos de registrador: 3 bits; índices codificáveis de `$r0` a `$r7`.
- Opcode: 4 bits.
- Formatos R, I e J.
- Campo imediato de 6 bits com sinal, intervalo `-32` a `31`.
- Campo de endereço J de 12 bits.
- A documentação histórica do projeto informa até 128 instruções de 16 bits na memória de instruções.

**Formato R** (`opcode[15:12] + RS[11:9] + RT[8:6] + RD[5:3] + funct[2:0]`)

**Formato I** (`opcode[15:12] + RS[11:9] + RT[8:6] + imediato[5:0]`)

**Formato J** (`opcode[15:12] + endereço[11:0]`)

### 4.2 Instruções permitidas

| Instrução            | Formato | Opcode   | Funct   | Operação resumida                   |
| ---------------------- | ------- | -------- | ------- | ------------------------------------- |
| `add $rd, $rs, $rt`  | R       | `0000` | `000` | soma registradores                    |
| `sub $rd, $rs, $rt`  | R       | `0000` | `010` | subtrai registradores                 |
| `and $rd, $rs, $rt`  | R       | `0000` | `100` | AND bit a bit                         |
| `or $rd, $rs, $rt`   | R       | `0000` | `101` | OR bit a bit                          |
| `addi $rt, $rs, imm` | I       | `0100` | —      | soma imediato                         |
| `lw $rt, imm($rs)`   | I       | `1011` | —      | leitura de memória                   |
| `sw $rt, imm($rs)`   | I       | `1111` | —      | escrita de memória                   |
| `beq $rt, $rs, imm`  | I       | `1000` | —      | desvio se iguais                      |
| `j address`          | J       | `0010` | —      | salto absoluto conforme a arquitetura |

Não introduza novas instruções para simplificar o gerador. Estratégias de tradução devem ser construídas a partir das nove instruções acima.

### 4.3 Registradores

| Registrador | Uso definido na convenção de compilação do RNN |
| ----------- | -------------------------------------------------- |
| `$r0`     | zero constante, somente leitura                    |
| `$r1`     | coordenada X                                       |
| `$r2`     | coordenada Y                                       |
| `$r3`     | orientação: 0=norte, 1=leste, 2=sul, 3=oeste     |
| `$r4`     | estado de carga: 0=falso, 1=verdadeiro             |
| `$r5`     | temporário primário                              |
| `$r6`     | temporário secundário/máscara                   |
| `$r7`     | registrador especial de E/S                        |

Este mapeamento vem da documentação histórica do RNN e é distinto do hardware físico (switches/LEDs) citado no artigo. Deve ser confirmado na integração com o ambiente do robô.

### 4.4 Limites e regras ainda sensíveis

- Imediatos assinados de 6 bits: `-32..31`, em complemento de dois.
- Campo J codifica 12 bits, `0..4095` como valor de campo; a memória de instruções documentada tem apenas 128 instruções, então o limite operacional do programa é menor.
- O comportamento exato do PC em `beq` e a unidade dos endereços devem ser validados no modelo/simulador de referência.
- As políticas completas para overflow não são confirmadas apenas pelo contexto disponível.
- A especificação do H3 não esclarece se `.bin` deve ser bytes crus, texto binário, hexadecimal ou outro formato. O pacote atual usa texto ASCII com uma palavra de 16 caracteres por linha.

## 5. Pipeline de compilação

O fluxo final do H3 é:

```text
Fonte .rnn
   ↓
Lexer
   ↓
Parser
   ↓
AST
   ↓
Análise semântica + tabela de rótulos
   ↓
Gerador de Assembly MiniMIPS
   ↓
Assembler/encoder + resolução de rótulos e desvios
   ↓
program.asm + program.bin
```

A análise semântica deve impedir que programas inválidos avancem para a geração final. O gerador produz Assembly simbólico; a resolução de rótulos e a codificação de instruções são responsabilidade do assembler/encoder.

## 6. Estado dos arquivos e implementação atual

O pacote H3 disponível foi reconstruído com base no snapshot exportado do H2 e nos requisitos do H3. No snapshot original, `gramar.lark` não estava presente na árvore exportada; por isso, a gramática foi reconstruída a partir das regras descritas pelo parser e pela documentação do front-end. O código atual deve ser entendido como uma base funcional de H3, e não como uma confirmação de que houve integração com a versão remota completa do repositório.

Estrutura do pacote:

```text
rnn-h3/
├── README.md
├── requirements.txt
├── main.py
├── src/
│   ├── __init__.py
│   ├── analise_lexica.py
│   ├── analise_sintatica.py
│   ├── gramar.lark
│   ├── analise_semantica.py
│   ├── gerador_assembly.py
│   ├── assembler.py
│   └── compilador.py
├── docs/h3/
│   ├── isa-minimips.md
│   ├── mapeamento-ast-assembly.md
│   ├── requisitos-h3-original.txt
│   └── validacao-local.md
├── examples/
│   ├── busca.rnn
│   └── sensores.rnn
└── tests/
    ├── test_semantica.py
    ├── test_assembler.py
    └── test_compilacao.py
```

### 6.1 Responsabilidades dos módulos

- `analise_lexica.py`: tokenização do código e tratamento de indentação/comentários.
- `analise_sintatica.py` e `gramar.lark`: parsing e produção da AST.
- `analise_semantica.py`: tabela global de rótulos e validações semânticas.
- `gerador_assembly.py`: tradução da AST para Assembly simbólico.
- `assembler.py`: validação do Assembly, resolução de rótulos/desvios e codificação de 16 bits.
- `compilador.py`: coordenação do pipeline e escrita das saídas.
- `main.py`: interface de linha de comando.

### 6.2 Como preparar e usar

Requisitos declarados no pacote: Python 3.10+ e dependência `lark`.

Linux/macOS:

```bash
python -m venv .venv
source .venv/bin/activate
python -m pip install -r requirements.txt
python main.py examples/busca.rnn --out-dir build
```

Testes:

```bash
python -m unittest discover -s tests -v
```

Saídas esperadas para `examples/busca.rnn`:

- `build/busca.ast.json`
- `build/busca.asm`
- `build/busca.bin`

O `.bin` atual é texto ASCII com uma palavra binária de 16 caracteres por linha, não bytes crus.

### 6.3 Estado de validação no momento desta consolidação

A execução local de `python -m unittest discover -s tests -v` em 26/10/2026 terminou com **19 testes aprovados e nenhum erro**. Os testes cobrem codificação das nove instruções, limites de operandos, resolução de rótulos, referências semânticas, uso de `break`, compilação de exemplos e gravação de AST/Assembly/binário.

Esse resultado demonstra testes locais do pacote atual. **Não demonstra** compatibilidade comportamental com o simulador oficial; a validação no simulador e a confirmação do protocolo de E/S são trabalho de H4.

## 7. Premissas implementadas e pontos pendentes

1. **Gramática:** reconstruída para o subconjunto atual. Conferir contra o repositório oficial antes de substituir arquivos da equipe.
2. **Escopo de rótulos:** global, incluindo rótulos dentro de blocos `if` e `while`.
3. **Referências futuras:** são permitidas porque a análise registra rótulos antes de validar `goto`.
4. **`break`:** válido dentro de `while`; o gerador desvia para a saída do laço mais interno.
5. **Branch `beq`:** o assembler assume atualmente `offset = índice_do_destino - índice_da_instrução_branch`, a partir da regra documental `PC = PC + imm * 2`. Validar contra o simulador/implementação original.
6. **Branch distante:** a implementação do assembler tem teste para expansão de branch distante; verificar a estratégia e sua compatibilidade com o simulador na validação comportamental.
7. **Protocolo `$r7`:** leitura de sensores por uso como fonte, códigos de ação 1 a 6 em escrita e bits de sensor conforme a documentação histórica. Validar oficialmente.
8. **`where_am_i`:** a versão atual da DSL o aceita como comando isolado, mas não há mecanismo para consumir o valor de retorno; a saída gerada registra essa limitação.
9. **Expressões:** o front-end atual não oferece variáveis nem atribuições. O gerador utiliza `$r5` e `$r6` e, em expressões binárias compostas, pode usar offsets de memória temporária 0..31. Rever a estratégia se variáveis DSL forem incorporadas.
10. **Formato `.bin`:** texto ASCII de palavras de 16 bits por linha; confirmar o formato esperado pela ferramenta/simulador oficial.
11. **Memória e overflow:** não assumir comportamento adicional sem uma referência técnica ou decisão documentada pela equipe.
12. **H4:** integração com o simulador oficial, validação de comportamento, análise de traces e correção de incompatibilidades pertencem ao próximo horizonte.

---

# Parte II — Backlog executável do Horizonte 3

## Objetivo do backlog

Ao terminar o H3, deverá existir um compilador Python integrado, capaz de receber um programa `.rnn`, validar sua semântica, gerar Assembly MiniMIPS, resolver rótulos e desvios, codificar instruções de 16 bits e produzir os arquivos de saída para programas válidos. Erros léxicos, sintáticos, semânticos e de montagem devem ser comunicados de modo compreensível.

Os IDs abaixo são uma **proposta de organização**. Antes de criar issues definitivas, conciliá-los com os IDs já existentes no board e evitar duplicações. A revisão de documentação semântica, se for mantida, deve ser uma subtarefa própria e não duplicar H3-02.

## H3-01 — Consolidar a especificação da ISA MiniMIPS

**Prioridade:** P0 — Crítica
**Estimativa:** a definir

### Descrição

Consolidar e versionar a especificação MiniMIPS utilizada pelo RNN, incluindo instruções, formatos de codificação, registradores, limites de operandos, endereçamento, desvios e convenções de E/S necessárias ao compilador. O documento deverá ser a fonte de verdade para o gerador e o encoder, diferenciando o que vem do artigo de referência das convenções próprias do RNN.

### Passos de execução

1. Revisar o artigo MiniMIPS e os documentos históricos do RNN.
2. Documentar as nove instruções e seus formatos R/I/J.
3. Registrar opcodes, functs e posições dos campos.
4. Formalizar registradores e limites de operandos.
5. Definir a unidade de endereço e a fórmula dos destinos de `beq` e `j`.
6. Formalizar a convenção de registradores e o protocolo de E/S do RNN.
7. Definir o formato do arquivo `.bin`.
8. Registrar o tratamento esperado para operandos inválidos e decisões de overflow, sem inventar regras não confirmadas.
9. Acrescentar exemplos de codificação para cada instrução.
10. Versionar o documento em `docs/h3/isa-minimips.md`.

### Critérios de aceite

- [ ] Nove instruções documentadas.
- [ ] Formatos R, I e J e campos definidos.
- [ ] Registradores, limites e restrições documentados.
- [ ] Endereçamento e desvios definidos ou bloqueios explicitamente registrados.
- [ ] Convenção de registradores/E/S documentada.
- [ ] Formato `.bin` documentado.
- [ ] Exemplos de codificação de todas as instruções.
- [ ] Documento versionado e sem decisões críticas ocultas.

### Entrega

`docs/h3/isa-minimips.md` ou documento equivalente aprovado pela equipe.

### Dependências

Artigo de referência, documentação da DSL e decisões da equipe.

---

## H3-02 — Implementar o analisador semântico

**Prioridade:** P0 — Crítica
**Estimativa:** a definir

### Descrição

Implementar um módulo Python que percorra a AST real do H2 e verifique as regras semânticas da DSL antes da geração de Assembly. A primeira versão deverá registrar declarações de rótulos, detectar duplicidades, verificar referências em `goto` e validar o contexto de `break`, emitindo diagnósticos compreensíveis.

### Passos de execução

1. Inspecionar os módulos do H2 e a estrutura dos nós reais da AST.
2. Criar o módulo semântico conforme a organização atual do código.
3. Definir a interface de análise que recebe a AST.
4. Implementar tabela de símbolos de rótulos com nome, declaração e campo para posição futura.
5. Fazer uma primeira passagem para registrar rótulos e detectar duplicatas.
6. Fazer uma segunda passagem para validar referências a `goto`, permitindo declarações posteriores.
7. Acompanhar a profundidade/contexto de `while` durante o percurso e validar `break`.
8. Validar tipos de nós e expressões do subconjunto atualmente suportado.
9. Definir erros com códigos e mensagens legíveis, incluindo localização se disponível.
10. Integrar o resultado à compilação para impedir que erros avancem para o gerador.
11. Escrever testes para programas válidos e inválidos.
12. Executar e registrar os resultados.

### Critérios de aceite

- [ ] A AST do H2 é recebida e percorrida.
- [ ] Os rótulos ficam registrados numa tabela consultável.
- [ ] Rótulos duplicados são rejeitados.
- [ ] Referências inexistentes em `goto` são rejeitadas.
- [ ] Destinos declarados posteriormente são aceitos.
- [ ] `break` só é permitido nos contextos definidos para laços.
- [ ] Nós ou expressões não suportados são diagnosticados.
- [ ] O pipeline interrompe a compilação diante de erro semântico.
- [ ] Testes semânticos foram executados e têm resultados registrados.

### Entrega

Módulo de análise semântica, tabela de símbolos, erros semânticos e testes.

### Dependências

Front-end/AST do H2, regras semânticas da DSL e H3-01 para convenções pertinentes.

---

## H3-03 — Definir o mapeamento da AST para Assembly MiniMIPS

**Prioridade:** P1 — Alta
**Estimativa:** a definir

### Descrição

Definir formalmente como cada construção suportada da AST RNN será representada por instruções MiniMIPS, documentando estratégias de tradução, utilização de registradores, avaliação de expressões e fluxo de controle. Esta tarefa produz a especificação usada em H3-04; não é a implementação do gerador.

### Passos de execução

1. Inspecionar a gramática e listar os tipos de nós AST implementados.
2. Criar uma tabela com construção DSL, nó AST, estratégia e instruções utilizadas.
3. Definir o mapeamento de ações para códigos de atuadores via `$r7` segundo o protocolo documentado.
4. Definir a extração e normalização dos bits de sensores.
5. Definir estratégias para `!`, `&`, `|`, `==` e `!=` usando somente as instruções da ISA.
6. Definir registradores temporários e estratégia para valores intermediários/spill.
7. Definir a geração de desvios para `if` e `while`.
8. Definir referências de `goto`, declarações de rótulos e destinos de `break`.
9. Definir rótulos auxiliares e garantir que não colidam com nomes do usuário.
10. Verificar a viabilidade de cada tradução dentro das restrições da MiniMIPS.
11. Documentar decisões pendentes e incluir exemplos de AST/DSL → Assembly.
12. Versionar `docs/h3/mapeamento-ast-assembly.md`.

### Critérios de aceite

- [ ] Tabela de mapeamento cobre todas as construções implementadas.
- [ ] Cada regra lista a sequência/estratégia de instruções permitidas.
- [ ] `if`, `while`, `goto`, rótulos e `break` estão especificados.
- [ ] Ações, sensores e operadores estão especificados.
- [ ] Registradores e memória temporária estão documentados.
- [ ] Não são introduzidas instruções inexistentes na ISA.
- [ ] Há exemplos suficientes para orientar a implementação.
- [ ] O documento pode ser utilizado diretamente por H3-04.

### Entrega

Documento versionado de mapeamento AST → Assembly.

### Dependências

AST/DSL do H2 e H3-01. Pode avançar em paralelo com H3-02 após a inspeção da AST.

---

## H3-04 — Implementar o gerador de Assembly MiniMIPS

**Prioridade:** P0 — Crítica
**Estimativa inicial:** 8 horas; revisar depois de inspecionar AST e regras de tradução.

### Descrição

Implementar o módulo Python que percorre uma AST semanticamente validada e produz uma sequência ordenada de Assembly MiniMIPS. Cada tipo de nó suportado deverá ser traduzido conforme H3-03, preservando a semântica e a ordem de execução do programa. Rótulos simbólicos serão mantidos para resolução posterior no assembler/encoder.

### Passos de execução

1. Inspecionar interfaces reais de AST e análise semântica.
2. Criar o módulo gerador e sua interface pública.
3. Definir a representação do resultado como linhas de Assembly formatadas.
4. Implementar a visita/despacho dos nós AST.
5. Implementar as ações do robô.
6. Implementar `where_am_i` de acordo com a limitação documentada.
7. Implementar sensores e normalização de resultados.
8. Implementar operadores lógicos e comparações conforme H3-03.
9. Implementar `if` com os desvios e rótulos auxiliares necessários.
10. Implementar `while`, incluindo início, condição, saída e retorno ao início.
11. Implementar `goto` como referência simbólica e `break` para a saída do laço correto.
12. Gerenciar `$r5` e `$r6` e os valores intermediários conforme a estratégia documentada.
13. Garantir unicidade dos rótulos gerados.
14. Rejeitar nós não suportados com erro explícito.
15. Criar testes unitários comparando Assembly real com o esperado.
16. Executar os testes e corrigir as diferenças encontradas.

### Critérios de aceite

- [ ] Recebe AST semanticamente válida.
- [ ] Cada construção suportada tem regra implementada.
- [ ] Só utiliza instruções da ISA.
- [ ] Respeita a convenção de registradores e E/S.
- [ ] Mantém referências simbólicas de destinos corretas.
- [ ] Rótulos auxiliares são únicos.
- [ ] A saída é determinística.
- [ ] Nó desconhecido resulta em erro compreensível.
- [ ] Testes unitários cobrem as categorias implementadas e passaram.

### Entrega

Módulo gerador, testes unitários e exemplos com saídas Assembly esperadas.

### Dependências

H3-01, H3-02 e H3-03.

### Fora do escopo

Resolução definitiva de endereços, codificação binária e execução no simulador.

---

## H3-05 — Implementar o encoder/assembler MiniMIPS

**Prioridade:** P0 — Crítica
**Estimativa:** a definir

### Descrição

Implementar o componente que recebe o Assembly, valida sua sintaxe, registra e resolve rótulos, calcula os operandos de desvio/salto e codifica cada instrução válida numa palavra binária de 16 bits. Não é necessária uma ferramenta independente: o assembler pode ser um módulo interno do compilador.

### Passos de execução

1. Definir a sintaxe exata de Assembly emitida por H3-04.
2. Criar a interface do assembler/encoder.
3. Implementar parser/validador de instruções e operandos.
4. Fazer a primeira passagem pelo Assembly para registrar os rótulos e suas posições.
5. Rejeitar rótulos duplicados e referências inexistentes.
6. Fazer a segunda passagem para resolver `j` e `beq` com a regra de endereço definida em H3-01.
7. Implementar codificação R (`add`, `sub`, `and`, `or`).
8. Implementar codificação I (`addi`, `lw`, `sw`, `beq`).
9. Implementar codificação J (`j`).
10. Validar registradores, quantidade/ordem de operandos, imediatos e endereços.
11. Definir e implementar a estratégia para branches cujo offset não caiba no campo, apenas se compatível com a especificação aprovada.
12. Criar testes com vetores das nove instruções e testes de limite.
13. Criar testes para labels, branches e saltos.
14. Expor o resultado ao pipeline e documentar o formato de saída.

### Critérios de aceite

- [ ] Nove instruções codificadas.
- [ ] Campos R/I/J posicionados conforme a especificação.
- [ ] Imediatos e endereços fora dos limites rejeitados.
- [ ] Sintaxe, operandos e instruções desconhecidas invalidados com mensagens claras.
- [ ] Rótulos duplicados e destinos inexistentes rejeitados.
- [ ] `beq` e `j` resolvidos conforme a regra oficial documentada.
- [ ] Cada palavra de instrução tem 16 bits.
- [ ] Vetores de referência e testes de limite passam.
- [ ] Interface pode ser chamada pelo compilador integrado.

### Entrega

Assembler/encoder Python, resolução de rótulos e desvios, testes e saída de palavras de 16 bits.

### Dependências

H3-01 e H3-04.

---

## H3-06 — Integrar as etapas do compilador

**Prioridade:** P0 — Crítica
**Estimativa:** a definir

### Descrição

Conectar lexer, parser, AST, análise semântica, gerador de Assembly e assembler/encoder num único fluxo de compilação, acessível por uma interface principal. Programas válidos devem produzir os artefatos esperados; programas inválidos devem terminar com mensagens de erro e sem ser apresentados como compilações bem-sucedidas.

### Passos de execução

1. Inspecionar o `main.py` e a CLI atual do H2.
2. Identificar as interfaces de cada módulo implementado.
3. Definir o ponto de entrada único e os argumentos necessários.
4. Encadear lexer, parser e geração da AST.
5. Executar a análise semântica e interromper se houver erro.
6. Executar a geração de Assembly somente após validação semântica bem-sucedida.
7. Executar o assembler/encoder e interromper se houver erro.
8. Gravar a AST JSON, o `.asm` e o `.bin` conforme o contrato documentado.
9. Tratar erros de leitura, compilação e gravação.
10. Documentar o comando principal de execução.
11. Criar testes de integração para entrada válida e inválida.
12. Executar testes e registrar resultados.

### Critérios de aceite

- [ ] Pipeline completo funciona por uma única interface.
- [ ] O front-end H2 é reutilizado.
- [ ] Erros lexicais, sintáticos ou semânticos interrompem a compilação.
- [ ] `.asm` e `.bin` correspondem ao mesmo programa de origem.
- [ ] Falhas são comunicadas com clareza.
- [ ] Comando de execução está documentado.
- [ ] Testes de integração passaram.

### Entrega

Fluxo principal integrado, CLI e geração dos artefatos para programas válidos.

### Dependências

H3-02, H3-04 e H3-05.

---

## H3-07 — Implementar testes de integração e regressão

**Prioridade:** P0 — Crítica
**Estimativa:** a definir

### Descrição

Consolidar uma suíte que demonstre que o compilador integrado identifica erros e produz Assembly e binário coerentes com as especificações. Os testes unitários permanecem junto dos módulos; esta tarefa concentra os cenários de ponta a ponta e de regressão.

### Passos de execução

1. Revisar e organizar os testes existentes.
2. Separar exemplos válidos e inválidos.
3. Cobrir rótulos duplicados, destinos inexistentes e uso incorreto de `break`.
4. Criar programas que exercitem todas as ações, sensores, operadores e estruturas suportadas.
5. Fixar saídas Assembly esperadas para os programas de referência.
6. Fixar palavras binárias esperadas com base na ISA aprovada.
7. Compilar programas completos e comparar as saídas obtidas.
8. Verificar que programas inválidos não geram saídas finais válidas.
9. Adicionar regressões para defeitos encontrados durante o desenvolvimento.
10. Definir um único comando de execução de testes.
11. Executar toda a suíte e registrar os resultados reais.
12. Documentar falhas que permanecerem abertas.

### Critérios de aceite

- [ ] Há exemplos válidos e inválidos.
- [ ] Os testes cobrem as construções DSL implementadas.
- [ ] A análise semântica é verificada.
- [ ] Assembly e binário são comparados com resultados esperados.
- [ ] Há testes de ponta a ponta DSL → AST → Assembly → binário.
- [ ] Existe um comando único de testes.
- [ ] Resultados foram executados e registrados.
- [ ] Falhas não são reportadas como aprovadas.

### Entrega

Suíte de testes, exemplos de referência e relatório dos resultados de execução.

### Dependências

H3-02 a H3-06; a criação dos testes começa durante as outras tarefas.

---

## H3-08 — Documentar e consolidar os artefatos de entrega

**Prioridade:** P1 — Alta
**Estimativa:** a definir

### Descrição

Consolidar a documentação técnica e operacional e preparar os artefatos do H3. O material deverá permitir a outro membro da equipe instalar, executar, testar e compreender o compilador, além de demonstrar o caminho completo de compilação. Documentar apenas funcionalidades e resultados efetivamente implementados/verificados.

### Passos de execução

1. Revisar o README e documentos técnicos existentes.
2. Documentar Python, dependências e preparação do ambiente.
3. Documentar o comando de compilação e seus argumentos.
4. Descrever o pipeline e a responsabilidade dos módulos.
5. Referenciar a especificação da ISA e o mapeamento AST → Assembly.
6. Registrar convenções de registradores, E/S, desvios e formato binário.
7. Explicar como executar os testes e interpretar o resultado.
8. Organizar exemplos DSL e saídas esperadas.
9. Documentar limitações e itens pendentes de H4.
10. Conferir quais artefatos são exigidos no repositório institucional.
11. Verificar se código, testes e documentos estão versionados.
12. Revisar a documentação comparando-a com o comportamento real do código.

### Critérios de aceite

- [ ] README explica preparação e execução.
- [ ] Pipeline e módulos estão documentados.
- [ ] ISA e mapeamento estão referenciados.
- [ ] Decisões arquiteturais estão registradas.
- [ ] Comando de testes está documentado.
- [ ] Exemplos e saídas esperadas estão disponíveis.
- [ ] Limitações conhecidas estão listadas.
- [ ] Documentação confere com o código.
- [ ] Código e artefatos de entrega estão no repositório.

### Entrega

README/documentação final, exemplos, resultados de teste e artefatos exigidos para H3.

### Dependências

H3-01 a H3-07. A documentação deve ser mantida incrementalmente, sem esperar o fim do projeto.

---

# Parte III — Dependências e organização do trabalho

## 8. Ordem recomendada

1. **H3-01 — ISA:** resolver os contratos que afetam todos os componentes.
2. **H3-02 — Semântica:** implementar a tabela de rótulos e as validações básicas.
3. **H3-03 — Mapeamento:** definir as estratégias de tradução. Pode avançar em paralelo com H3-02 depois de confirmar a AST.
4. **H3-04 — Gerador:** implementar o contrato definido em H3-03.
5. **H3-05 — Assembler/encoder:** resolver rótulos/endereço e codificar as instruções.
6. **H3-06 — Integração:** ligar as etapas e gravar saídas.
7. **H3-07 — Integração/regressão:** executar cenários completos; escrever testes unitários desde o início.
8. **H3-08 — Documentação/entrega:** manter documentos atualizados durante todo o H3 e fazer revisão final.

## 9. Definição geral de concluído (Definition of Done)

Uma issue só deve ser marcada como concluída quando:

- A implementação ou o documento previsto foi incorporado ao repositório.
- Todos os critérios de aceite específicos foram verificados.
- Os testes aplicáveis foram executados e os resultados registrados.
- As decisões necessárias foram documentadas.
- Os erros conhecidos e limitações estão explicitados.
- A saída está pronta para ser consumida pela tarefa seguinte.
- Nenhuma compatibilidade com o simulador é afirmada sem teste ou fonte técnica que a confirme.

## 10. Itens que continuam para H4

- Integração com o simulador oficial.
- Confirmação do protocolo `$r7` e dos códigos de sensores/atuadores.
- Confirmação prática da regra de PC, `beq` e unidade de endereçamento.
- Confirmação do formato de `.bin` esperado pelo simulador.
- Execução de programas em cenários de ambiente e análise de traces.
- Validação do comportamento completo do robô.

---

# Parte IV — Fluxo Git e contribuição

A documentação original de contribuição define o fluxo básico de trabalho:

1. Criar uma issue no board.
2. Criar uma branch vinculada à issue.
3. Usar branches dos tipos `feature`, `fix` ou `docs`.
4. Atualizar a branch principal e mudar para a branch da tarefa.
5. Implementar e fazer commits conforme a convenção da equipe.
6. Enviar a branch para o remoto (`git push origin nome_da_branch`).
7. Abrir Merge Request, revisar e integrar.

Formato de commit documentado:

```text
#<id_da_issue>: <verbo> + <mensagem>
```

Exemplos documentados:

```bash
git commit -m "#13: adicionando arquivo tal"
git commit -m "#14: fazendo documentação do esquema"
git commit -m "#7: arrumando bug em tal tarefa"
```

Exemplos de nomes de branch presentes na documentação:

```text
feature/16_def_pop_padrao
fix/666_tratando_erro_compilador
docs/14_documentacao_esquema
```

Conferir as convenções definitivas no `CONTRIBUTING.md` do repositório institucional antes de criar branches reais, pois os exemplos históricos apresentam pequenas variações de formatação.

---

# Parte V — Instruções para o próximo chat ou desenvolvedor

1. Usar este documento como contexto inicial, mas conferir o código presente no repositório que estiver efetivamente sendo trabalhado.
2. Não confundir os documentos históricos do H1 com o subconjunto da DSL implementado pelo parser atual.
3. Não acrescentar `else`, variáveis, atribuições, aritmética ou comandos `def_*`/`crt_*` sem decisão explícita de projeto.
4. Não introduzir novas instruções MiniMIPS.
5. Tratar o contrato de E/S, a base do PC de `beq` e o formato do `.bin` como pontos a validar se o simulador oficial ainda não tiver sido executado.
6. Distinguir testes locais do pacote H3 de validação no simulador oficial.
7. Ao modificar AST ou gramática, atualizar semântica, gerador, testes e documentação afetados.
8. Executar `python -m unittest discover -s tests -v` e relatar os resultados reais.
9. Não substituir arquivos do repositório oficial sem comparar primeiro com a versão atual da equipe.
10. O objetivo final de H3 é gerar `.asm` e `.bin` para programas DSL válidos, com detecção de erros antes da geração.

## Referências locais reunidas

- `ContextoH3.txt` — requisitos oficiais/contexto do Horizonte 3, conforme disponibilizado.
- `contexto-projeto.txt` — snapshot textual da árvore e dos artefatos históricos do H1/H2.
- `mini-mips.pdf` — artigo de referência da MiniMIPS.
- `docs/projeto/README.md` do snapshot — documentação da linguagem RNN.
- `CONTRIBUTING.md` do snapshot — convenções de contribuição.
- `docs/h3/isa-minimips.md` — especificação draft implementada no pacote H3.
- `docs/h3/mapeamento-ast-assembly.md` — mapeamento draft utilizado pelo gerador.
- `docs/h3/validacao-local.md` — registro de execução local dos testes.

---

**Fim do documento.**
