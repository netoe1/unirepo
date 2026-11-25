public class App {
    public static void main(String[] args) throws Exception {

        // Criando um produto:
        Produto p1 = new Produto(Descricao.FARINHA, 10, 5);
        Produto p2 = new Produto(Descricao.LEITE,6,10);
        Produto p3 = new Produto(Descricao.ARROZ,4,10);
        Produto p4 = new Produto(Descricao.FEIJAO,12,10);

        System.out.println(String.format("p1:%s",p1.toString()));
        System.out.println(String.format("p2:%s",p2.toString()));
        System.out.println(String.format("p3:%s",p3.toString()));
        System.out.println(String.format("p4:%s",p4.toString()));

        Pedido pedido = new Pedido(TipoPagamento.DINHEIRO,
                        new Cliente("Ely Torres Neto","000-000-000-00"));


        System.out.println(String.format("Pedido Antes:\n%s",pedido.toString()));
        
        pedido.addItem(new Item(p1, 1));
        pedido.addItem(new Item(p2, 2));
        pedido.addItem(new Item(p3, 3));
        pedido.addItem(new Item(p4, 4));
            
        System.out.println(String.format("Pedido Depois:\n%s",pedido.toString()));

        pedido.fecharPedido();

        System.out.println(String.format("Pedido fechado:%s\n",pedido.toString()));
    }
}
