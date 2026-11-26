package grafo;

public class Vertice {
    private final int id;
    private String rotulo;

    public Vertice(int id) {
        this.id = id;
        this.rotulo = String.valueOf(id);
    }

    public Vertice(int id, String rotulo) {
        this.id = id;
        this.rotulo = rotulo;
    }

    public int getId() {
        return id;
    }

    public String getRotulo() {
        return rotulo;
    }

    public void setRotulo(String rotulo) {
        this.rotulo = rotulo;
    }

    @Override
    public String toString() {
        if (rotulo != null && !rotulo.isEmpty() && !rotulo.equals(String.valueOf(id))) {
            return id + " (" + rotulo + ")";
        }
        return String.valueOf(id);
    }
}