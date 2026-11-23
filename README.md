# Repositório de trabalhos — Algoritmos e Classificação de Dados

Este repositório é um monorepo para os trabalhos da disciplina **Algoritmos e Classificação de Dados (ACD)**. Cada trabalho é desenvolvido em uma branch própria. Consulte a branch ativa para identificar o conteúdo em desenvolvimento; não presuma que todos os trabalhos estarão presentes ao mesmo tempo na mesma branch.

## Trabalho atual

Na branch `acd/tp2/main`, o foco é o **TP2**, sobre representações de grafos e algoritmos de grafos em Java, com experimentos para comparar desempenho.

- Código do TP2: [`tp2/`](tp2/)
- Enunciado: [`docs/TP2 - Trabalho Prático sobre  GRAFOS.md`](<docs/TP2 - Trabalho Prático sobre  GRAFOS.md>)
- Grafos de amostra: [`samples/`](samples/)
- Instruções para contribuir e revisar: [`AGENTS.md`](AGENTS.md)

O conteúdo do TP2 ainda está em desenvolvimento. Consulte o enunciado e os arquivos do projeto para confirmar quais algoritmos e funcionalidades já estão implementados.

## Requisitos

- JDK com `java` e `javac` disponíveis no `PATH`.
- Git para obter o repositório e alternar entre branches.

## Compilar e executar o esqueleto atual

O ponto de entrada Java atual é `tp2/src/App.java`. Na raiz do repositório:

```bash
mkdir -p out
javac -d out tp2/src/App.java
java -cp out App
```

Isso compila e executa o esqueleto inicial do aplicativo. Remova a pasta `out/` quando quiser limpar os arquivos compilados.

## Testes

O repositório ainda não possui uma suíte de testes automatizada configurada. A pasta `tests/` está reservada para testes. Ao adicionar funcionalidades, inclua testes de regressão determinísticos e atualize estas instruções com o comando para executá-los.

## Licença

Este repositório está sob a licença GNU General Public License v3.0. Consulte [`LICENSE`](LICENSE) para ver o texto completo e as condições de uso, modificação e redistribuição.
