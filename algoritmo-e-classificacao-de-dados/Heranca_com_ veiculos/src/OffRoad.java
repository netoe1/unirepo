public class OffRoad extends Vehicle {
    private float groundClearance;

    public OffRoad(float _groundClearance,int _passengers, float _fuel_cap,float _mpg){
        super(_passengers,_fuel_cap,_mpg);
        this.groundClearance = _groundClearance;
    }

    public float getGroundClearance(){return this.groundClearance;}
    public void setGroundClearance(float gc){this.groundClearance = gc;}
    
    @Override 
    public String toString(){
        return String.format("%s\nGroundClearance:%f",super.toString(),this.groundClearance);
    }

}
