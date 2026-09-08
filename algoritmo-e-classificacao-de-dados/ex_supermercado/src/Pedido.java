public class Pedido {
    private TipoPagamento pagamento;


    public Pedido(TipoPagamento pagamento){
        this.pagamento = pagamento;
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
