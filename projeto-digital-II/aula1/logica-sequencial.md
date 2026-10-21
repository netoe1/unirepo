# logica sequencial

**Processos: O que são processos em VHDL?**
São blocos de código que executa funções internas de forma sequencial

**Sinais de Sensibilidade:**
Os sinais entre parênteses são os que ativam o process;
Se tiver mais de um processes, eles irão acontecer de forma sequencial;

# Exemplo
```
// Bloco combinacional deve ter seus sinais sempre descritos dentro do process, todos os que vão ser utilizados;
// Certo
process(a,b,c):
    y <= a and b and c;
end process;

// Errado
process(a):
    y <= a and b and c;
end process;
```
# Obs:
A op. Assinalamento é feitos com sinais e op. atribuição é feita com váriáveis;

# Atribuição em Variáveis:
A atribuição acontece de forma instantenea, já os sinais (fio), não são assinalados de primeira, pois no hardware real, isso não acontece.

# IF
Sempre enxergar o IF como um mux;