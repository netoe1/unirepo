### Trabalho Prático 2 \- TP2 \- Algoritmos e Classificação de Dados (ACD)

TP2: Projeto Prático de Programação, Experimentação, Análise e Apresentação de resultados de desempenho de algoritmos: realizado em duplas (excepcionalmente, trios) e com peso de 18% na média. Este trabalho envolve a implementação, planejamento e execução de experimento e apresentação de resultados referentes às diferentes formas da representação computacional de GRAFOS e o emprego de seus principais ALGORITMOS, conforme estudados no componente.  
O trabalho, grosso modo, envolve a implementação das diferentes formas de representação de grafos e o emprego de alguns algoritmos estudados em aula, como algoritmos de busca (profundidade e largura), de árvore geradora mínima, de caminho mínimo e de fluxo máximo. O trabalho também envolve o planejamento e a execução de experimentos que, neste caso, tem como objetivo avaliar o desempenho das duas formas de representação de grafos (e.g., listas de adjacência e matriz de adjacência) estudadas em aula, comparando os tempos de execução e ocupação de memória dos algoritmos estudados em diferentes contextos \- diferenciados por grafos de entrada com diferentes tamanhos e densidades (relação entre o número de arestas e de vértices). Faz parte do trabalho a escrita de um artigo relatando o experimento realizado e seus resultados, seguidos da análise dos resultados obtidos e respectivas conclusões. O trabalho TP2 será computado como atividade EaD, com carga horária vinculada de 5 horas. O trabalho deverá ser apresentado pelo(s) discente(s) para o docente e, preferencialmente, também para a turma (conforme cronograma do componente).

**Aspectos importantes do trabalho:**

* Os algoritmos de grafos estudados em aula, especificamente a **busca em largura**, a **busca em profundidade**, a **ordenação topológica**, a **árvore geradora mínima** (algoritmos de Kruskal e de Prim)**,** o **caminho mínimo de origem única para todos os vértices** (algoritmos de Bellman-Ford e de Dijkstra), o **caminho mínimo entre todos os pares de vértices** (algoritmo de Floyd-Warshall) e o **fluxo máximo em redes** (algoritmo de Ford-Fulkerson) deverão ser implementados e disponíveis para execução e testes por meio de um conjunto de arquivos de classes Java. Esta exigência garante que cada algoritmo estudado tenha a sua devida implementação realizada por cada grupo. Observa-se que, conforme nossos estudos, há certas restrições para o uso de cada algoritmo. Por exemplo, alguns algoritmos podem ser executados sobre grafos não-dirigidos (não-orientados) e não-ponderados (como a busca em largura), mas outros necessitam que o grafo seja ponderado (como no caso de árvore geradora mínima), enquanto outros pressupõem que o grafo é dirigido e ponderado (e.g., algoritmo de caminhos mínimos de fonte única). Assim, para cada grafo deve-se ter disponível para execução pelo menos um exemplo de grafo para “rodar” a implementação de cada algoritmo. Para cada caso deve-se ter à disposição alguma forma de visualização da saída (resultado) de cada algoritmo, assim como uma forma de visualizar o grafo de entrada (algum tipo de impressão que permita entender a estrutura do grafo).  
* A implementação dos grafos deve ser realizada na Linguagem Java por meio da construção de classes adequadas para representar os conceitos de grafo (grafo não-orientado) e dígrafo (grafo dirigido ou orientado), ambos podendo ser ponderados (arestas com peso) ou não (arestas sem peso vinculado). Chama-se atenção para o fato de que, para cada algoritmo listado, a representação do grafo (**lista de adjacência** ou **matriz de adjacência**) a ser seguida será de acordo com aquela utilizada em aula. Na implementação das classes Java deste trabalho, considerar o modelo Orientado a Objetos que estudamos em nossas aulas, conforme link a seguir \- [modelo de classes UML](https://drive.google.com/file/d/1YxvwmZl6aiivvctyxUZ_6YzI_YcMK3db/view?usp=sharing). Os algoritmos de grafos estudados em nossas aulas poderão ser implementados, cada um, por classe Java específica para este fim \- implementar um algoritmo (e.g.: classe **Dijkstra** para tratar da implementação correspondente ao algoritmo de menor caminho de fonte única deste autor \- Dijkstra)  
* Para cada algoritmo, e considerando as classes de grafo implementadas, preparar um grafo de entrada a ser submetido para execução, com o registro (e apresentação) do respectivo tempo de execução. Preparar o menu de um programa para demonstrar a execução de cada algoritmo e os seus tempos de execução. Na escolha do referido item de menu, o grafo de entrada e a identificação do algoritmo em execução deverão ser devidamente apresentados, seguidos dos respectivos tempos de execução.

**Sobre a parte de experimento do trabalho, tem-se o seguinte:**

* Segundo Cormen et al. (2024), a representação por lista de adjacência é mais apropriada para representar grafos *esparsos*, em que o número de arestas \|E\| é bem menor que o número de vértices \|V\| ao quadrado (Grafo esparso: \|E\| ⋘ \|V\|²). Já a representação por matriz de adjacência é mais adequada para grafos densos, onde \|E\| é aproximadamente o valor de \|V\|² (Grafo Denso: \|E\| ≈ \|V\|²). Para definir o que significa **denso** e **esparso**, pode ser utilizado o conceito de densidade *D* de um grafo, dado pela relação entre o número de arestas e vértices de um grafo dirigido, conforme apresentado em Wikipedia (2024). Grafos densos possuem um valor D mais próximo de 1.0 (um), enquanto para grafos esparsos o valor D é mais próximo de 0.0 (Wikipedia, Dense Graph, 2024).

  $D\ =\ \frac{|E|}{|V|(|V|-1)}$   (Wikipedia, Dense Graph, 2024\)


* Especialmente para o caso dos Algoritmos de Menor Caminho de Única Fonte \- algoritmos de **Bellman-Ford** e de **Dijkstra** \- deverá ser planejado e executado um **experimento** que deverá refutar ou não a seguinte Hipótese:   
  Na implementação de estruturas de dados para Grafos, as representações de um grafo G por *Lista de Adjacência* (LA) e por *Matriz de Adjacência* (MA) proporcionam desempenhos semelhantes (em relação às médias de tempo de execução) para processamento de grafos densos (Ɗ) ou esparsos (Ɛ), especificamente na execução de implementações dos *algoritmos de menor caminho de fonte única* de Bellman-Ford e de Dijkstra?  
    
  Podemos dizer a forma de representação do grafo representa o **Fator A** do experimento:  
  A1: LA: representação do Grafo *G* por Lista de Adjacência  
  A2: MA: representação do Grafo *G* por Matriz de Adjacência  
    
  E o tipo de grafo (*denso* ou *esparso*) pode ser tratado como o **Fator B** do experimento:  
  B1: \- Grafo Esparso \- Ɛ  
  B2: \- Grafo Denso \- Ɗ  
    
  Com as definições acima, podemos definir o seguinte:  
    
  **Fator A (Repres. Grafo)**  
  Hipótese Nula → H0’: (μ Ɛ (LA) ≈ μ Ɛ (MA)) ∧ (μ Ɗ (LA) ≈ μ Ɗ (MA))   
  Hipótese Alternativa → HA’: (μ Ɛ (LA) ≠ μ Ɛ (MA)) ∨ (μ Ɗ (LA) ≠ μ Ɗ (MA))   
    
  onde μ representa a média de tempos de execução da implementação do algoritmo de menor caminho de uma só fonte em um grafo (Ɛ ou Ɗ) em sua respectiva forma de representação (LA ou MA); e  
    
  **Fator B (Tipo de Grafo)**  
  H0’’: (μ Ɛ (LA) ≈ μ Ɗ (LA)) ∧ (μ Ɛ (MA) ≈ μ Ɗ (MA))   
  HA’’: (μ Ɛ (LA) ≠ μ Ɗ (LA)) ∨ (μ Ɛ (MA) ≠ μ Ɗ (MA))   
    
  onde μ representa a média de tempos de execução da implementação do algoritmo de menor caminho de uma só fonte em um grafo (Ɛ ou Ɗ) em sua respectiva forma de representação (LA ou MA).  
    
  Conforme apresentado acima, no caso dos Algoritmos de Menor Caminho de Única Fonte, deverão estar disponíveis para a execução do experimento implementações que considerem ambas formas de representação de grafos, isto é, **Lista de Adjacência** e **Matriz de Adjacência**.  
  Embora não seja obrigatório, experimentos adicionais semelhantes ao descrito acima poderiam ser planejados, mas considerando outros algoritmos estudados, como árvore geradora mínima (algoritmos de Kruskal e de Prim), o caminho mínimo entre todos os pares de vértices (algoritmo de Floyd-Warshall) e o fluxo máximo em redes (algoritmo de Ford-Fulkerson). Fica a critério dos grupos elaborar (ou não) os experimentos adicionais aventados neste parágrafo.  
    
* Para lidar com a questão de grafos densos e esparsos e definir grafos de entrada para execução dos algoritmos, foram gerados, a partir do programa **GTgraph-random** (GTgraph, 2024), 15 grafos com diferentes *tamanhos* (de acordo com o número de vértices) e *densidades* (conforme o número de vértices, o número de arestas foi definido), proporcionando 15 combinações diferentes entre o número de vértices (\|V\|) e arestas (\|E\|), conforme tabela a seguir:

|  | Densidade →  | 0,2 | 0,4 | 0,6 | 0,8 | 1 |
| :---: | :---: | :---: | :---: | :---: | :---: | :---: |
| \# de vértices | **100** | 1980 | 3960 | 5940 | 7920 | 9900 |
|  | **200** | 7960 | 15920 | 23880 | 31840 | 39800 |
|  | **500** | 49900 | 99800 | 149700 | 199600 | 249500 |
|  |  | \# de arestas |  |  |  |  |

   

	Os grafos estão disponíveis como arquivos de texto, com formato adequado para leitura a partir de um programa Java. Abaixo está apresentado um exemplo de arquivo contendo a descrição de um grafo com 5 vértices e 8 arestas. O conjunto de arquivos conforme as combinações indicadas na tabela anterior estão disponíveis como um arquivo compactado neste [LINK](https://drive.google.com/file/d/1YGWcfeA1OQ7-xjl4IpVUNOLZ8J_KNWx-/view?usp=sharing).

c FILE			: sample.gr  
c No. of vertices	: 5  
c No. of directed edges	: 8  
c Max. weight		: 100  
c Min. weight		: 0  
c A directed arc from u to v of weight w  
c is represented below as ' a u v w '  
p sp 5 8  
a 4 3 66  
a 3 4 55  
a 5 4 87  
a 5 3 8  
a 3 2 78  
a 2 5 12  
a 4 2 65  
a 5 1 4

	  
Sobre o número de execuções em cada combinação (\|V\| e \|E\|) de grafo de entrada, define-se que  cada configuração seja executada **pelo menos 10 vezes**.

	Espera-se que um documento em formato de artigo seja gerado para descrever e apresentar os resultados do experimento. Sugere-se que seja utilizado Latex para a construção do artigo (e.g.: usando o ambiente do [Overleaf](https://www.google.com/url?sa=t&source=web&rct=j&opi=89978449&url=https://pt.overleaf.com/&ved=2ahUKEwiVn5SAtOmJAxUMJbkGHe7WJU8QFnoECBwQAQ&usg=AOvVaw0AvnrXOUWTPaNioszaKmms)). Especificamente para a apresentação dos tempos de execução de cada algoritmo de menor caminho (Bellman-Ford e Dijkstra) tendo como entrada cada combinação (\# de vértice e \# de arestas) do grafo de entrada apresentado na tabela anterior, sugere-se a utilização de [diagramas de caixa](https://pt.wikipedia.org/wiki/Diagrama_de_caixa) ([Boxplot](https://www.atlassian.com/data/charts/box-plot-complete-guide)). Diagramas boxplot podem ser gerados por versões mais atuais do Excel, no [Python](https://www.datacamp.com/tutorial/python-boxplots), no [R](https://www.datamentor.io/r-programming/box-plot), ou mesmo [online](https://makeboxplot.com/). Outros tipos de gráficos podem ser utilizados, mas os *boxplots* são esperados neste trabalho.

	Sobre o escopo do experimento, o mesmo pode ser descrito, resumidamente, usando o seguinte template (WOHLIN et al., 2012, TRAVASSOS et al., 2002):  
Analisar **\<as formas de representação de grafos (LA e MA) e tipos de grafos (Ɛ e Ɗ)\>**  
para o propósito de **\<avaliação\>**  
com respeito ao **\<tempo de execução de implementações em Java dos algoritmos de menor caminho de fonte única de Bellman-Ford e de Dijkstra\>**  
do ponto de vista do(a) **\<programador\>**  
no contexto de **\<estudos sobre Grafos no componente de Algoritmos e Classificação de Dados\>**.

*Observações gerais:*  
O docente da disciplina fica à disposição dos grupos para discutir dúvidas sobre o trabalho e sobre a metodologia para a realização dos experimentos. Procurem consultar o professor para discutir seu trabalho. 

**Referências**:

* CORMEN, Thomas H et al. Algoritmos: teoria e prática. 4\. Rio de Janeiro: GEN LTC, 2024\. ISBN 9788595159914.(Disponível na biblioteca digital da UNIPAMPA)  
* GTgraph. GTgraph: A suite of synthetic random graph generators. 2024\. Disponível em [https\://github.com/Bader-Research/GTgraph](https://github.com/Bader-Research/GTgraph).  
* Wikipedia. Dense Graph. 2024\. Disponível em: [https\://en.wikipedia.org/wiki/Dense\_graph](https://en.wikipedia.org/wiki/Dense_graph)  
* WOHLIN, Claes et al. **Experimentation in software engineering**. Berlin: Springer, 2012\.   
* TRAVASSOS, Guilherme Horta; GUROV, Dmytro; AMARAL, Edgar Augusto Gurgel do. Introdução à Engenharia de Software Experimental. RELATÓRIO TÉCNICO RT–ES–590/02. Programa de Engenharia de Sistemas e Computação \- COPPE/UFRJ. Rio de Janeiro, 2002\. Disponível em: [https\://www\.cin.ufpe.br/\~scbs/experimental/IntroducaoExperimentacao.pdf](https://www.cin.ufpe.br/~scbs/experimental/IntroducaoExperimentacao.pdf)