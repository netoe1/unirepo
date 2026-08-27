package ExercicioLampada.src;

public class Bateria {
    private int carga;


    public Bateria(int __cargaInicial){
        this.carga = __cargaInicial;
    }

    public boolean temEnergia(){
        if(this.carga > 0 ) return true;
        return false; 
    }

    public void consumir(){
        if (carga > 0) {
            this.carga--;
        } else {
            System.out.println("A bateria está vazia! Recarregue!");
        }
    }

}
