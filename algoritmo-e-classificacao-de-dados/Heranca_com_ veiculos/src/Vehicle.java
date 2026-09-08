public class Vehicle {
    private int passengers;     // Passageiros dentro do carro;
    private float fuel_cap;     // Capacidade de abastecimento;
    private float mpg;          // Milhas por galão

    // Construtor da classe:
    // Gosto de usar underline antes para simbolizar que é um parâmetro
    public Vehicle(int _passengers,float _fuel_cap, float _mpg){
        this.passengers = _passengers;
        this.fuel_cap = _fuel_cap;
        this.mpg = _mpg;

    }

    // Criando os Getters e Setters;

    // Getters
    public int getPassengers(){return this.passengers;}
    public float getFuelCap(){return this.fuel_cap;}
    public float getMpg(){return this.mpg;}

    //SETTERS
    public void setPassengers(int p){this.passengers = p;}
    public void setFuelCap(float fc){this.fuel_cap = fc;}
    public void setMpg(float mpg){this.mpg = mpg;    }

    @Override 

    public String toString(){
        return String.format("\nClasse:%s\nClasse Pai:%s\nPassengers:%d\nFuelCapacity:%.2f\nMiles per Gallon:%.2f",this.getClass().getName(),this.getClass().getSuperclass().getName(),this.passengers,this.fuel_cap,this.mpg);
    }
}
