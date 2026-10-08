import java.util.Vector;

public abstract class Ordenador<T extends Comparable<T>> 
{
    protected long tempoTotal;
    protected long tempoInicial;
    protected long tempoFinal;
    protected long vezes_executado;
    protected Vector<T> vetor;

    Ordenador(){
        this.tempoFinal = this.tempoInicial = this.tempoTotal = 0;
        this.vetor = new Vector<>();
        this.vetor.clear();
    }   

    public abstract void sort(Vector<T> vetor_com_dados);

    
    public long getTempoTotal(){
        this.tempoTotal = this.tempoFinal - this.tempoInicial;
        return this.tempoTotal;
    }

    @Override
    public String toString() {
        if (this.vetor == null || this.vetor.isEmpty()) {
            return "[]";
        }
        
        StringBuilder aux = new StringBuilder("[");
        int limite = this.vetor.size(); 
        
        for (int i = 0; i < limite; i++) {
            aux.append(this.vetor.get(i));
            if (i < limite - 1) {
                aux.append(", ");
            }
        }
        aux.append("]");
        
        return aux.toString();
    }
}




