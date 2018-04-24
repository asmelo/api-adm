package models;

public class PalavraChave {

    private int id;
    private String palavra;
    private String idSubcategoria;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPalavra() {
        return palavra;
    }

    public void setPalavra(String palavra) {
        this.palavra = palavra;
    }

    public String getIdSubcategoria() {
        return idSubcategoria;
    }

    public void setIdSubcategoria(String idSubcategoria) {
        this.idSubcategoria = idSubcategoria;
    }
}
