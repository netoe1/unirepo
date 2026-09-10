public class Truck extends Vehicle {
    private float cargoCap;     // Atributo a mais que precisa

    public Truck(int _passengers, float _fuelCap, float _mpg,float _cargoCap){
        // Usando super para herdar de da superclasse Vehicle
        super(_passengers,_fuelCap,_mpg);
        this.cargoCap = _cargoCap;  
    }

    public float getCargoCap(){return this.cargoCap;}
    public void setCargoCap(float cc){this.cargoCap = cc;}

    @Override 
    public String toString(){

        return String.format("%s\n cargoCap: %f",super.toString(),this.cargoCap);
    }
}
