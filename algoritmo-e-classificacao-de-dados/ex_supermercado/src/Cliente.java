public class Cliente {
    private String nome;
    private String cpf;


    Cliente(String __nome, String __cpf){
        this.nome = __nome;
        this.cpf  = __cpf;
    }

    // Getters:

    public String getNome(){return this.nome;}
    public String getCpf(){return this.cpf;}

    // Setters

    public void setNome(String n){this.nome = n;}
    public void setCpf(String c){this.cpf = c; }
    

    @Override 
    public String toString(){
        return String.format("Classe:%s\nNome:%s\nCPF:%s",this.getClass().getName(),this.nome,this.cpf);
    }

}
