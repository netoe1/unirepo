
# Testes do TP1 — ACD

Coloque `testes_tp1.sh`, `TestesCorretude.java`, `TesteGenerator.java`, `Logs.java` e os demais arquivos `.java` dentro de `TP1/src`.

## Executar os testes de corretude

```
chmod +x testes_tp1.sh
./testes_tp1.sh
```

O modo padrão testa os dez algoritmos com vetores de 10, 11 e 15 elementos. Para cada tamanho, usa as disposições crescente, decrescente, aleatória e com valores repetidos. O programa verifica se o resultado está ordenado e se todos os elementos originais foram preservados.

## Executar os experimentos de desempenho

```
./testes_tp1.sh desempenho
```

O script compila uma vez, executa os testes de corretude e então roda a bateria de desempenho, com 10 repetições por configuração. Os CSVs são criados/acumulados na pasta `resultados/`.

A bateria evita os piores tamanhos para algoritmos quadráticos e limita os casos ordenados de Quick Sort a `n=1000`, pois a implementação utiliza o último elemento como pivô. Para Merge, Heap, Shell, Counting, Radix e Bucket, usa `n=1000`, `10000`, `100000` nas três disposições e `n=1000000` na disposição aleatória.

`MELHOR`, `MEDIO` e `PIOR` são rótulos experimentais independentes da disposição. Para algoritmos em que a disposição não define diretamente um caso teórico, o CSV também registra a disposição real para que a análise não confunda as duas coisas.

## Makefile (opcional)

```
make test       # apenas corretude
make benchmark  # corretude + desempenho
make clean      # remove arquivos .class
```

O script não solicita dados ao usuário e não lê entradas pelo `stdin`.
