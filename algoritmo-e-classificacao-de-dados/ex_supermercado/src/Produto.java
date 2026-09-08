public class Produto {
    private Descricao descricao;
    private float preco;
    private int quantidadeEstoque;


    public Produto(Descricao descricao, float preco, int quantidadeEstoque, Item item){
        this.descricao = descricao;
        this.preco = preco;
        this.quantidadeEstoque = quantidadeEstoque;
    }


    // Getters
    
    public Descricao getDescricao(){return this.descricao;}
    public float getPreco(){return this.preco;}
    public int getQuantidadeEstoque(){return this.quantidadeEstoque;}

    // Setters:

    public void setDescricao(Descricao d){this.descricao = d;}
    public void setPreco(float p){this.preco = p;}
    public void setQuantidadeEstoque(int qe){this.quantidadeEstoque = qe;}

    @Override 
    public String toString(){
        return String.format("Classe:%s\nDescrição:%s\nPreço:R$.2f\nQuantidade Estoque:%d",
            this.getClass().getName(),
            this.descricao,
            this.preco,
            this.quantidadeEstoque);
    }

}
