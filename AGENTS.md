# Guia de contribuição e uso do Copilot

## Contexto do repositório

Este repositório é um monorepo de trabalhos da disciplina **Algoritmos e Classificação de Dados (ACD)**. Os trabalhos são separados por branches. Não presuma que a estrutura ou os algoritmos de uma branch representam o estado de outra branch.

O contexto atual é o TP2, desenvolvido na branch `acd/tp2/main`. Consulte o estado da branch ativa antes de implementar qualquer mudança.

- Enunciado completo: `docs/TP2 - Trabalho Prático sobre  GRAFOS.md`
- Código Java: `tp2/src/`
- Estruturas de grafo: `tp2/src/grafo/`
- Algoritmos: `tp2/src/algoritmos/`
- Entrada e saída: `tp2/src/io/`
- Ponto interativo principal: `tp2/src/Main.java`; `tp2/src/App.java` encaminha para `Main`
- Testes Java simples: `tp2/src/test/`
- Grafos de amostra: `tp2/src/samples/`
- Visão geral: `README.md`

O TP2 implementa grafos não dirigidos e dirigidos, ponderados ou não ponderados, nas representações por lista e matriz de adjacência. As implementações disponíveis devem respeitar as restrições do enunciado:

| Algoritmo | Restrição principal |
| --- | --- |
| BFS | Grafo de qualquer tipo; origem válida |
| DFS | Grafo de qualquer tipo; origem válida |
| Ordenação topológica | Dígrafo dirigido; deve detectar ciclos |
| Kruskal | Grafo não dirigido ponderado |
| Prim | Grafo não dirigido ponderado |
| Bellman-Ford | Grafo dirigido ou não dirigido ponderado; aceita pesos negativos |
| Dijkstra | Grafo ponderado com pesos finitos e não negativos |
| Floyd-Warshall | Caminhos mínimos entre todos os pares |
| Ford-Fulkerson | Rede dirigida ponderada com capacidades não negativas |

Antes de uma decisão que altere o escopo ou a metodologia do trabalho, consulte o enunciado em `docs/TP2 - Trabalho Prático sobre  GRAFOS.md`.

## Estrutura e convenções Java

- Siga o modelo Orientado a Objetos já usado pelo projeto.
- Algoritmos devem depender de `grafo.IGrafo`, não das implementações concretas, sempre que possível.
- Preserve a semântica das duas representações: lista e matriz devem produzir resultados equivalentes para a mesma entrada.
- Índices Java começam em `0`.
- Arquivos no formato GTgraph usam índices iniciando em `1`; a conversão pertence à leitura em `io.LeituraGrafo`.
- Valide explicitamente valores nulos, índices fora dos limites, tipos incorretos e pesos incompatíveis.
- Preserve entradas quando o algoritmo não precisar modificá-las.
- Use `IllegalArgumentException` para parâmetros sem significado para o algoritmo e `IndexOutOfBoundsException` para vértices fora dos limites, seguindo as classes existentes.
- Não corrija defeitos fora do escopo da tarefa e não modifique grafos de amostra ou resultados experimentais sem necessidade.

## Política de testes automatizados de regressão

O repositório não usa JUnit nem outra dependência de testes. Cada teste é uma classe Java com `main` em `tp2/src/test/` e lança `AssertionError` em caso de falha. Os testes são compilados e executados pelos scripts `corretude_*.sh`.

### Quando corrigir um defeito

1. Reproduza o defeito com um teste.
2. Execute o teste antes da correção para confirmar que falha.
3. Faça a correção mínima na implementação.
4. Execute novamente o teste específico.
5. Execute a suíte relacionada e as suítes existentes.

Não use tabelas, randomização ou estado externo em testes determinísticos. Entradas pequenas devem poder ser conferidas manualmente.

### Cobertura mínima

Para algoritmos de grafos, inclua sempre que aplicável:

- grafo vazio;
- um vértice;
- vértices desconexos;
- componente não acessível pela origem;
- ciclos;
- laços;
- arestas paralelas permitidas pela representação;
- pesos negativos, zero, infinitos ou não finitos;
- validação de origem, tipo e parâmetros;
- execução nas representações por lista e matriz.

Para cada resultado, confira não apenas um número agregado. Valide distâncias e predecessores em caminhos mínimos, ciclo detectado, componentes alcançados em buscas, peso e arestas da árvore geradora mínima e capacidade ou fluxo onde aplicável.

### Comandos existentes

Execute a partir da raiz do repositório:

```bash
bash tp2/src/test/corretude_est_grafos.spec.sh
bash tp2/src/test/corretude_algoritmos_grafos.sh
bash tp2/src/test/corretude_ordenacao_topologica.sh
bash tp2/src/test/corretude_caminhos_minimos.sh
bash tp2/src/test/corretude_fluxo_maximo.sh
bash tp2/src/test/corretude_exemplos_demonstracao.sh
```

Compile todo o TP2 antes de concluir uma mudança:

```bash
mkdir -p out/tp2
find tp2/src -name '*.java' -print0 | xargs -0 javac -encoding UTF-8 -d out/tp2
```

Cada script cria uma pasta temporária, compila o projeto e remove a pasta ao terminar. Medições de desempenho não são testes de corretude. Ao adicionar uma nova suíte, atualize `tp2/src/test/README.md` e o README principal.

## Experimentos

O experimento principal exige análise de:

- representação: lista de adjacência ou matriz de adjacência;
- densidade: grafo esparso ou denso;
- algoritmos: Bellman-Ford e Dijkstra.

Mantenha comparações rastreáveis por representação, algoritmo, grafo, número de vértices, número de arestas e repetição. Use pelo menos 10 execuções por configuração, segundo o enunciado. Não cronometre leitura, geração ou preparação da entrada dentro da parte destinada ao tempo do algoritmo.

Os arquivos de `tp2/src/samples/` são entradas reais do projeto e não devem ser substituídos ou alterados por testes. Resultados temporários não devem ser adicionados ao repositório, exceto quando forem artefatos documentados de um experimento.

## Uso do Copilot

Use o Copilot como assistente de implementação, revisão e execução. As responsabilidades são:

1. Ler o enunciado e o `AGENTS.md` antes de alterar o TP2.
2. Confirmar a branch ativa e a estrutura atual da tarefa.
3. Buscar implementação ou padrão semelhante antes de criar uma nova classe.
4. Fazer mudanças cirúrgicas e relacionar todas as superfícies afetadas: código, menu, saída, teste, script e documentação.
5. Explicar decisões que possam afetar a metodologia ou o escopo do trabalho.
6. Exibir explicitamente falhas de compilação ou testes; não mascarar erros.
7. Validar com o menor comando que cobre a mudança e depois ampliar a execução conforme necessário.
8. Informar claramente quais verificações foram executadas e quais ainda não existem.

Antes de solicitar uma mudança, forneça o objetivo de maneira específica, por exemplo: “implemente o Floyd-Warshall nas duas representações e adicione testes de corretude”. Não peça ao Copilot para presumir estado de outra branch ou criar dependências desnecessárias.

Para revisões de código, o Copilot deve priorizar defeitos concretos e resultados incorretos. Relate primeiro o problema mais grave, com arquivo e linha, reprodução e efeito. Não transforme revisões em sugestões genéricas de estilo.

## Revisão de código

Ao revisar uma alteração:

1. Verifique correspondência entre o algoritmo e suas restrições de entrada.
2. Procure erros de limites, ciclos, desconexão, estado compartilhado e mutações inesperadas.
3. Confirme equivalência entre lista e matriz para os mesmos dados.
4. Confirme peso, arestas, conectividade e componentes alcançados conforme o algoritmo.
5. Verifique que exemplos, scripts e documentação correspondem ao comportamento real.
6. Examine mudanças em medição para impedir que leitura ou geração contamine o cronômetro.
7. Se não houver defeitos, diga isso explicitamente e registre lacunas relevantes de cobertura.

## Padrão de commits

- Escreva mensagens em português, curtas e descritivas.
- Use prefixo de escopo seguido de dois-pontos.
- Prefixos existentes ou apropriados:
  - `feat-tp2:`
  - `fix-tp2:`
  - `test-tp2:`
  - `refactor-tp2:`
  - `docs:`
- Exemplo: `fix-tp2: tratar grafo desconexo no Dijkstra`.
- Inclua uma única intenção por commit.
- Não inclua arquivos compilados, arquivos temporários ou saídas não documentadas.
- Confira o diff e execute as verificações aplicáveis antes do commit.
- Não afirme que testes passaram sem tê-los executado.
