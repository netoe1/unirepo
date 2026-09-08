public class App {
    public static void main(String[] args) throws Exception {
        // Criando objetos:
        Vehicle veiculo_generico = new Vehicle(4, 10, 5);
        OffRoad veiculo_offroad = new OffRoad(5, 2, 30, 10);
        Truck veiculo_caminhao = new Truck(6,50,20,10);

        // Mostrando todos os objetos:

        System.out.println("Mostrando todos os objetos usando o toString() (com sobrecarga)");
        System.out.println(veiculo_generico.toString());
        System.out.println(veiculo_offroad.toString());
        System.out.println(veiculo_caminhao.toString());

    }
}
