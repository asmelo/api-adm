package models;

public class Subcategoria {

    private int id;
    private String descricao;
    private int categoria;
    private int cdtipo;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public int getCategoria() {
        return categoria;
    }

    public void setCategoria(int categoria) {
        this.categoria = categoria;
    }

    public int getCdtipo() {
        return cdtipo;
    }

    public void setCdtipo(int cdtipo) {
        this.cdtipo = cdtipo;
    }
}
