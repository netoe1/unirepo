
# unirepo

Repositório de estudos e trabalhos das disciplinas de graduação. As anotações são material pessoal de apoio e podem estar incompletas; para trabalhos avaliativos, consulte também os enunciados oficiais e confirme os requisitos com o docente.

## Trabalho Prático 1 — Algoritmos e Classificação de Dados (ACD)

O projeto do TP1 está na pasta [`TP1/`](TP1/). Ele reúne as implementações em Java de dez algoritmos de ordenação, os testes de corretude e o programa CLI usado para medir desempenho.

### Começar

```bash
cd TP1/src
chmod +x testes_tp1.sh
./testes_tp1.sh corretude
```

Quando os testes de corretude passarem, execute um teste pequeno de desempenho antes de iniciar a bateria completa:

```bash
java -cp . Main --repeticoes 10 --caso MEDIO \
  --algoritmo MergeSort --disposicao ALEATORIO --n 1000 \
  --saida resultados/verificacao --semente 2026 --debug
```

Para executar a bateria experimental configurada:

```bash
./testes_tp1.sh desempenho
```

Os CSVs são gravados em `TP1/src/resultados/`. A aplicação acrescenta medições aos CSVs existentes com cabeçalho compatível; mantenha cópias dos dados originais e cuidado para não misturar sessões que respondem a experimentos diferentes.

Consulte [`TP1/README.md`](TP1/README.md) para os passos, parâmetros e cuidados metodológicos.
