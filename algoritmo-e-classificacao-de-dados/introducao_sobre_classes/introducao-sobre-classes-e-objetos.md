# introdução sobre classes e objetos


# Como funcionam as relações entre os objetos? 
É parecido com banco de dados, n:n,1:1, 1:n;

- De forma simples, pemos usar um objeto dentro do outro para facilitar a implementação;
  - **Composição:** é quando o todo não pode ser apagado, pois o progrante
  - **Agregação:** é agregado ao todo, porém pode ser separado.
  - **Associação:** é a relação mais fraca que pode ser utilizada, a composta indenpende dela;

  No Exemplo do slide, a fatura é composição, item é agregação, cliente é associação;

> Se a classe não tiver um construtor, o Java automaticamente cria um valor padrão para as variáveis;
> Para tipos primitivos, 0 ou false.
> Se forem tipos por referência (objeto), é colocado como NULL;

# Método de Instância vs Método de Classe

- Método de Instância: métodos que você pode usar apenas se instanciar ou criar o objeto;

- Método de Classe: Não precisa criar a classe.