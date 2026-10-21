public class Item {

    private Produto produto;
    private int quantidade;

    public Item(Produto produto, int quantidade){
        this.produto = produto;
        this.quantidade = quantidade;
    }

    public Produto getProduto(){return this.produto;}
    public int getQuantidade(){return this.quantidade;}
    public void setProduto(Produto p){this.produto = p;}
    public void setQuantidade(int q){this.quantidade = q;}
    public float getValorTotalItem(){ return (this.produto.getPreco() * this.quantidade);}
    
    @Override 
    public String toString(){
        return (String.format("\t\tClasse:%s\nProduto:%s\nQtd:%d\n",this.getClass().getName(),this.produto.toString(),this.quantidade));
    }
};