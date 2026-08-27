# poo-basica

## Conceitos Gerais de Programação Orientada a Objetos

- **Encapsulamento:**
    - Agrupamento de ideias em uma unidade só. Porém, o estado só é acessível pela interface, sem ter acesso direto;
    - Também, é um pacote inseparável de operações e atributos;
> Exemplo Prático: Criar uma classe;

- **Ocultação**
    - Restringe a visiblidade externa de detalhes de implementação. Isso localiza decisões de desenho;
    - Podemos chamar isso também de uma abstração;
> Exemplo: Motor e acelerador (motor é privado e acelerador é interface pública) 

- **Retenção de Estado**
  - A habilidade de reter informações; Diferente de uma sub-rotina que esquece tudo ao terminar, um objeto é ciente do seu passado durante um período de tempo

- **Identidade de Objetos**
  - Propriedade pela qual cada objeto é tratado como uma entidade distinda e única, mesmo que seues estados sejam 100% idênticos;
  
- **Mensagens**
  - Para enviar uma mensagem, precisamos do id, numero da operação argumentos; 
  - Interrogativa (Presente) (getVelocidade())
  - Imperativa (Futuro) (frear())
  - Informativa (Passado) (atualizarGPS)

- **Classes**
  - É o molde estrutural, que gera o objeto;
  
- **Herança**
  - Construção Incremental, é a especialização da classe nova, herdando de uma classe antiga
  - Para exemplo simples, podemos pensar na árvore genética;

- **Polimorfismo**
  - Múltiplas formas de agir
    ```java
        // A variável v1 é do tipo veículo, mas o objeto é 
        Veiculo v1 = new Carro();
        Veiculo v2 = new Bicicleta();

        // Só conseguimos fazer isso porque Carro recebe a Herança de Veículo

    ```
- **Generalização**
  - Classes ou containers pronto

  ```c++
    int main(){

        // Estruturas genéricas para ganhar tempo...
        std::vector<int> vetor_de_inteiros = {1,2,3,4}
    }
  ```

