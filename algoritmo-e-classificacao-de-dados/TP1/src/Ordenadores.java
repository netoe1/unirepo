
public class Ordenadores {
    
    // Tipos não lineares:
    Ordenadores(TipoAlgoritmo tipoAlgoritmo){

        long inicio=0;
        long fim=0;
        long tempo=0;


        inicio = System.nanoTime();
        try{
            switch (tipoAlgoritmo) {
            case BUBBLE:
            
                break;

            case INSERTION:
                
                break;

            case SELECTION:
                
                break;

            case MERGE:
                
                break;

            case HEAP:
                
                break;

            case QUICK:
                
                break;
        
            default:
                 
                break;
        }
        }
        catch(Exception e){
            throw e;
        }
        

        fim = System.nanoTime();
        tempo = fim - inicio;

    }

    Ordenadores(){

    }
}