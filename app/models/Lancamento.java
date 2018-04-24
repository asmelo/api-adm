package models;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Lancamento {

    private int id;
    private int idConta;
    private int cdTipo; //1- Crédito, 2- Débito
    private Date dataCompleta;//Data passada para o banco
    private String dtLancamento; //Data formatada a ser passada para o frontend
    private String descricao;
    private Double valor;
    private String subcategoria;
    private String referenciaOfx;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdConta() {
        return idConta;
    }

    public void setIdConta(int idConta) {
        this.idConta = idConta;
    }

    public int getCdTipo() {
        return cdTipo;
    }

    public void setCdTipo(int cdTipo) {
        this.cdTipo = cdTipo;
    }

    public Date getDataCompleta() {
        return dataCompleta;
    }

    public void setDataCompleta(Date dataCompleta) {
        this.dataCompleta = dataCompleta;
    }

    public void setDataCompletaFromDtLacamento() {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            String dataStr = String.valueOf(this.dtLancamento);
            Date dataCompleta = format.parse(dataStr.substring(0,10));
            setDataCompleta(dataCompleta);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public String getDtLancamento() {
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss");
        return format.format(dataCompleta);
    }

    public void setDtLancamento(String dtLancamento) {
        this.dtLancamento = dtLancamento;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Double getValor() {
        return valor;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }

    public String getSubcategoria() {
        return subcategoria;
    }

    public void setSubcategoria(String subcategoria) {
        this.subcategoria = subcategoria;
    }

    public String getReferenciaOfx() {
        return referenciaOfx;
    }

    public void setReferenciaOfx(String referenciaOfx) {
        this.referenciaOfx = referenciaOfx;
    }
}
