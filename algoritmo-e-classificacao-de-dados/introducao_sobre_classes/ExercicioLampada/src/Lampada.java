package ExercicioLampada.src;


public class Lampada{

    private boolean acesa;
    private Bateria bateria;

    public Lampada(boolean _estadoInicial,Bateria _bateria){
        this.acesa = _estadoInicial;  
        this.bateria = _bateria;
    }

    public void atualizarEstado(boolean ligado){
        this.acesa = ligado;
    }
    
};
