# Guia de contribuição

Este repositório é um monorepo com uma branch para cada trabalho. O contexto atual é o TP2 da disciplina Algoritmos e Classificação de Dados, desenvolvido na branch `acd/tp2/main`. Faça mudanças considerando o trabalho dessa branch, sem presumir que arquivos de outros trabalhos ou branches descrevam o estado atual do TP2.

O projeto do TP2 fica em `tp2/` e implementa estruturas e algoritmos para grafos, além de experimentos de desempenho. O enunciado está em `docs/TP2 - Trabalho Prático sobre  GRAFOS.md`; consulte-o quando uma decisão afetar o escopo ou a metodologia do trabalho.

## Testes de regressão

- Ao corrigir um defeito, reproduza-o com um teste antes ou junto da correção. O teste deve falhar sem a correção e passar com ela.
- Mantenha os testes determinísticos: use entradas fixas e não dependa de tempo, ordem aleatória ou estado externo para validar resultados.
- Para algoritmos de grafos, cubra casos pequenos cujo resultado esperado possa ser conferido manualmente, além de casos de fronteira relevantes: grafo vazio, um vértice, desconexão, ciclos, arestas paralelas ou pesos especiais, conforme permitido pela implementação.
- Confira tanto o resultado quanto as condições do problema: por exemplo, distâncias e predecessores em caminhos mínimos, componentes alcançados em buscas e peso/validade da árvore geradora mínima.
- Para mudanças em geração ou leitura de grafos, inclua entradas inválidas e limites do formato, sem substituir os arquivos de amostra existentes.
- Antes de concluir uma mudança, compile o código Java do TP2. Quando houver uma suíte automatizada, execute também os testes de regressão relacionados e a suíte completa. Não trate medições de desempenho como testes de corretude.
- O repositório ainda não define framework nem comando de testes. Não invente dependências: registre no resumo quais verificações foram realmente executadas e sinalize quando um teste automatizado não existir.

## Revisão de código

Ao revisar uma alteração, priorize defeitos concretos e seus efeitos:

1. Verifique se o algoritmo corresponde ao problema e às restrições de entrada (grafo dirigido ou não dirigido, pesos, ciclos e conectividade).
2. Procure erros de limites, estados compartilhados entre execuções, mutações inesperadas do grafo e resultados incorretos em casos de fronteira.
3. Confirme que as estruturas de lista e matriz representam os mesmos dados quando usadas em comparação experimental.
4. Para medições, verifique que a geração/cópia da entrada não contamina o intervalo cronometrado, que as repetições usam condições comparáveis e que os resultados permitem rastrear algoritmo, representação e grafo.
5. Confira que a mudança não altera arquivos de amostra ou resultados experimentais sem necessidade e que documentação/comandos correspondem à implementação atual.
6. Relate primeiro os problemas encontrados, em ordem de gravidade, citando arquivo e linha e explicando como reproduzir ou qual resultado fica incorreto. Se não houver problemas, diga isso explicitamente e informe lacunas de cobertura relevantes.

## Padrão de commits

- Escreva mensagens em português, curtas e descritivas, com prefixo de escopo seguido de dois-pontos. O histórico existente usa, por exemplo, `feat-tp2: adicionando grafos` e `docs: arrumando repo`.
- Use os prefixos adequados ao conteúdo, como `feat-tp2:`, `fix-tp2:`, `test-tp2:`, `docs:` ou `refactor-tp2:`. Mantenha o escopo consistente com o histórico.
- Descreva uma única intenção por commit; prefira verbo no imperativo ou uma ação clara, por exemplo: `fix-tp2: tratar grafo desconexo no Dijkstra`.
- Não inclua mudanças sem relação, arquivos compilados, saídas temporárias ou resultados de experimento não documentados.
- Antes de registrar o commit, confira o diff e execute as verificações aplicáveis. Não afirme que testes passaram se não foram executados.
