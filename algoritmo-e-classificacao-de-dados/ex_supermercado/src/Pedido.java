import java.util.ArrayList;
import java.util.List;
public class Pedido {
    private TipoPagamento pagamento;
    private List<Item> itens;
    private Cliente cliente;

    public Pedido(TipoPagamento pagamento,Cliente cliente){
        this.pagamento = pagamento;
        this.itens = new ArrayList<>();
        this.cliente = cliente;
    }

    public TipoPagamento getTipoPagamento(){return this.pagamento;}
    public void setTipoPagamento(TipoPagamento p){this.pagamento = p;}
    public List<Item> getItens(){return this.itens;}
    public void setItens(List<Item> i){this.itens = i;}
    public float getValorTotalPedido(){
        float sum = 0;
        for (Item item: itens) {sum+= item.getValorTotalItem();}
        return sum;    
    }

    public void addItem(Item i){this.itens.addFirst(i);}
    public void removeItem(Item i){this.itens.remove(i);} 
    public void fecharPedido(){
        int aux0 = 0;
        int aux1 = 0;
        for (Item item : this.itens) {
            aux0 = item.getProduto().getQuantidadeEstoque();
            aux1 = item.getQuantidade();

            // Significa que o produto ainda não acabou
            if((aux0 - aux1) < 0 || (aux0 - aux1) < 0 ){
                System.out.println("O produto acabou ou foi solicitado a mais.\n Transportado apenas o que temos em estoque!");
            }

            // Atualizando o valor 
            item.setProduto(
                new Produto(item.getProduto().getDescricao(),
                            item.getProduto().getPreco(),
                            (aux0 - aux1)));
        }   
    }

    @Override 
    public String toString(){
        String cab= String.format(
            "\t\tClasse:%s\nTipoPagamento %s\nValor Total do Pedido:R$ %.2f\nCliente:%s\nCPF:%s",
            this.getClass().getName(),
            this.pagamento.toString(),
            this.getValorTotalPedido(),
            this.cliente.getNome(),
            this.cliente.getCpf()
        );

        String produtos = "";
        for (Item item : this.itens) {produtos += item.toString();produtos+="\n";}
        return (cab + "\n" + produtos);

    }

}
