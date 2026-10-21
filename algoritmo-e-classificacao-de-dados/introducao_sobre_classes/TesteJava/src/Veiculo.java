public class Veiculo {
    public Roda[] rodas;

    public Veiculo(Roda roda_frontal_esq,
        Roda roda_frontal_dir,Roda roda_dianteira_esq,
        Roda roda_dianteira_dir){
        rodas = new Roda[4];
        rodas[0] = roda_frontal_esq;
        rodas[1] = roda_frontal_dir;
        rodas[2] = roda_dianteira_esq;
        rodas[3] = roda_dianteira_dir;
    }


    public void mostrarRodas(){
        int i = 0;
        for(i = 0; i < this.rodas.length;i++){
            System.out.println(String.format("Roda %d:",i));
            this.rodas[i].mostrarRoda();
        }
    }

    public static void main(String args[]){
        Veiculo veiculo = new Veiculo(  new Roda(1),
                                        new Roda(2),
                                        new Roda(3),
                                        new Roda(4));

        veiculo.mostrarRodas();
    }

}
