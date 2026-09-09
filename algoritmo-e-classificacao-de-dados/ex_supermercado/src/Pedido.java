import java.util.ArrayList;
import java.util.List;
public class Pedido {
    private TipoPagamento pagamento;
    private List<Item> itens;

    public Pedido(TipoPagamento pagamento){
        this.pagamento = pagamento;
        this.itens = new ArrayList<>();
    }

    public TipoPagamento getTipoPagamento(){return this.pagamento;}
    public void setTipoPagamento(TipoPagamento p){this.pagamento = p;}

    @Override 
    public String toString(){
        return String.format("Classe:%s\nTipoPagamento %s",
            this.getClass().getName(),
            this.pagamento.toString());
    }

}
