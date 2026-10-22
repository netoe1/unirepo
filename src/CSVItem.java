public class CSVItem{
    private String nomeAlgoritmo;
    private int nElementos;
    private EnumCasos caso;
    private long tempoExecucao;

    public CSVItem(String nomeAlgoritmo, int nElementos, EnumCasos caso){
        this.nomeAlgoritmo = nomeAlgoritmo;
        this.nElementos = nElementos;
        this.caso = caso;
    }
    
    public String getNomeAlgoritmo(){
        return this.nomeAlgoritmo;
    }

    public void setNomeAlgoritmo(String novoNome){
        this.nomeAlgoritmo = novoNome;
    }

    public int getNElementos(){
        return this.nElementos;
    }

    public void setNElementos(int n){
        this.nElementos = n;
    }

    public EnumCasos getCaso(){
        return this.caso;
    }

    public void setCaso(EnumCasos caso){
        this.caso = caso;
    }

    public long getTempoExecucao(){
        return this.tempoExecucao;
    }

    public void setTempoExecucao(long n){
        this.tempoExecucao = n;
    }

    @Override 
    public String toString(){
        return String.format("Algoritmo:%s\tN:%d,\tCaso:%s",this.nomeAlgoritmo,this.nElementos,this.caso.toString().toUpperCase().trim());
    }
    
    public void print(){
        System.out.println(this.toString());
    }

}