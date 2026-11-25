# Anotações

- **Classe Abstrata:**Um modelo incompleto para outras classes herdarem. Você não pode criar um objeto direto dela com o new.
- **Método Abstrato:** Uma regra/comando sem código dentro. Ela obriga as classes filhas a escreverem a sua própria versão desse método.
- **getClass():** O comando que descobre qual é a classe exata de um objeto enquanto o programa está rodando.
- **getName():** O comando que pega essa classe descoberta pelo getClass() e devolve o nome dela escrito em texto (com o pacote junto).

# Interfaces:
- Funcionalidades comuns a classes não relacionadas;
- Especifica quais operações são permitidas, mas sem ser realizadas, pois serão implementadas na classe;
- Uma interface só fornece as operações;
- Uma interface só pode ter constantes e métodos abstratos;

# Exemplos de Interfaces Padrões:

- Comparable: Compara objetos
- Serializable: Marcação
- Runnable + run()
- Interfaces Ouvintes de eventos GUI(): Action Listener actionPerformed(ActionEvent e);
- SwingConstants: Determinar posição em uma interface gráfica;

## Exemplos de uso:
classe implemenets interface

> Toda a classe é obrigada a implementar

# SOLID:
iNVERSÃO DA DEPENDÊNCIA:
Depender da abstração do que da implementação;

# Interpretar classes de estrutura de dados com base no que foi visto:
