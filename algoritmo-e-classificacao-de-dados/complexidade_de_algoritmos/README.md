# Análise Assintótica, Notação bigO, Complexidade de Algoritmos e Algoritmos de Ordenação


# Primeira Impressão

# O que é uma Ordenação de um conjunto de dados?
- Precisamos ter noção de maior, menor ou igual a.
- Podemos ordernar por diferentes campos, letras do alfabeto,enum e enfim;
  

# O que é um algoritmo:
- É um procedimento computacional, para receber uma entrada, processar e devolver uma saída;
- O tempo é finito, a sequência de passos é finita;

# Problema clássico dos algoritmos:
**- Ordernação:**
  - Entrada: uma sequência de n números:
  - Saída: vai ser um permutação, um tipo de ordenação específica
  - Existe uma relação de ordem entre os termos, podem ser estruturados de várias formas;

- O algoritmo é correto se resolve o problema computacional

# Eficiência de Algoritmo:
- O computador não é infinitamente rápido e nem memória infinita;
- Precisamos levar em contexto aspectos de recursos de tempo e espaço;


# Tipos de ordenação:
- **Ordernação por Inserção:** c*n²
- **Ordernação por Intercalação:** c*nlog(n)
- Constantes são c1 < c2;
- Fator de multiplicação: c X n² > c X nlog(n)

---

> Algoritmos de inserção tendem a ser mais simples de implementar do que de Intercalação;

# Usos:
Se a variável n for pequeno, inserção é meçhor que intercalação, mas haverá algum ponto que isso será invertido.


# Algoritmos de ordenação:
  - Cada elemento têm uma chave; chave é um critério de ordenação.
  - Em geral, podemos ordenar por outros dados que se chamam **dados satélites**, que podem embasar melhor a ordenação.

# Insertion-Sort
- Ordenação local 

# Piso (Floor)
Arredondamento de um número para baixo;
Ex: Floor(3.5) = 3; Floor(3.1) = 3;
