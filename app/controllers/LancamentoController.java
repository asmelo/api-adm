package controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import models.ChaveValor;
import models.Lancamento;
import models.PalavraChave;
import persistence.LancamentoPersistence;
import persistence.PalavraChavePersistence;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import services.ArquivoOfx;

import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class LancamentoController extends Controller {

    private ArquivoOfx ArquivoOfx;
    private PalavraChavePersistence palavraChavePersistence;
    private LancamentoPersistence lancamentoPersistence;

    @Inject
    public LancamentoController(ArquivoOfx ArquivoOfx, PalavraChavePersistence palavraChavePersistence, LancamentoPersistence lancamentoPersistence) {
        this.ArquivoOfx = ArquivoOfx;
        this.palavraChavePersistence = palavraChavePersistence;
        this.lancamentoPersistence = lancamentoPersistence;
    }

    public Result importarArquivoOfx() {
        try {
            String idConta = request().body().asFormUrlEncoded().get("idConta")[0].toString();

            List<Lancamento> lancamentos = ArquivoOfx.recuperaLancamentos("C:\\Users\\Alexandre\\Desktop\\extrato.ofx", idConta);

            lancamentos = defineCategorias(lancamentos);

            for(Lancamento lancamento : lancamentos){
                Calendar cal = Calendar.getInstance();
                cal.setTime(lancamento.getDataCompleta());
                String ano = String.valueOf(cal.get(Calendar.YEAR));
                String mes = String.valueOf(cal.get(Calendar.MONTH));
                Lancamento lancJaExistente = lancamentoPersistence.verificaReferenciaOfx(lancamento.getReferenciaOfx(), ano, mes);
                if(lancJaExistente == null) {
                    lancamento.setIdConta(Integer.parseInt(idConta));
                    lancamentoPersistence.salvarLancamento(lancamento);
                }else{
                    System.out.println("LANCAMENTO JA EXISTENTE: " + lancamento.getDataCompleta() + " - " + lancamento.getDescricao() + " - " + lancamento.getValor());
                }
            }

            return noContent();
        }catch (Exception e){
            e.printStackTrace();
            return internalServerError(e.getMessage());
        }
    }

    public Result listaLancamentos() {
        try {
            String ano = request().getQueryString("ano");
            String mes = request().getQueryString("mes");

            List<Lancamento> listaLancamentos = lancamentoPersistence.listaLancamentos(ano, mes);

            ObjectNode retorno = Json.newObject();
            retorno.set("lancamentos", Json.toJson(listaLancamentos));

            return ok(retorno);
        }catch (Exception e){
            e.printStackTrace();
            return internalServerError(e.getMessage());
        }
    }

    public Result excluiLancamento(int idLancamento) {
        try {
            lancamentoPersistence.excluirLancamento(idLancamento);
            return noContent();
        }catch (Exception e){
            e.printStackTrace();
            return internalServerError(e.getMessage());
        }
    }

    public Result consultaLancamento(int idLancamento) {
        try {
            Lancamento lancamento = lancamentoPersistence.consultaLancamento(idLancamento);

            ObjectNode retorno = Json.newObject();
            retorno.set("lancamento", Json.toJson(lancamento));

            return ok(retorno);
        }catch (Exception e){
            e.printStackTrace();
            return internalServerError(e.getMessage());
        }
    }

    public Result salvaLancamento() {
        try {
            ObjectNode requestJson = (ObjectNode) request().body().asJson();
            ObjectNode lancamentoJson = (ObjectNode) requestJson.get("lancamento");

            Lancamento lancamento = Json.fromJson(lancamentoJson, Lancamento.class);
            lancamento.setDataCompletaFromDtLacamento();

            if(lancamento.getCdTipo() == 2){
                lancamento.setValor(lancamento.getValor() * -1);
            }

            int idLancamento = lancamentoPersistence.salvarLancamento(lancamento);
            lancamento.setId(idLancamento);

            ObjectNode retorno = Json.newObject();
            retorno.set("lancamento", Json.toJson(lancamento));

            return ok(retorno);
        }catch (Exception e){
            e.printStackTrace();
            return internalServerError(e.getMessage());
        }
    }

    public Result atualizaLancamento(int idLancamento) {
        try {
            ObjectNode requestJson = (ObjectNode) request().body().asJson();
            ObjectNode lancamentoJson = (ObjectNode) requestJson.get("lancamento");

            Lancamento lancamento = Json.fromJson(lancamentoJson, Lancamento.class);
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            lancamento.setDataCompletaFromDtLacamento();

            if(lancamento.getCdTipo() == 2){
                lancamento.setValor(lancamento.getValor() * -1);
            }

            lancamento.setId(idLancamento);

            lancamentoPersistence.atualizarLancamento(idLancamento, lancamento);

            ObjectNode retorno = Json.newObject();
            retorno.set("lancamento", Json.toJson(lancamento));

            return ok(retorno);
        }catch (Exception e){
            e.printStackTrace();
            return internalServerError(e.getMessage());
        }
    }

    private List<Lancamento> defineCategorias(List<Lancamento> lancamentos) {
        List<PalavraChave> listaPalavraChaves = palavraChavePersistence.listaPalavraChave();
        for(Lancamento lancamento : lancamentos){
            for(PalavraChave palavraChave : listaPalavraChaves) {
                String descricao = lancamento.getDescricao().toLowerCase();
                String palavra = palavraChave.getPalavra().toLowerCase();
                if(descricao.contains(palavra)){
                    if(lancamento.getSubcategoria() != null){
                        System.out.println("Mais de uma palavra-chave foi indentificada para um lançamento");
                        System.out.println("Descrição do lançamento: " + descricao);
                        System.out.println("Palavra-chave: " + palavra);
                    }else {
                        lancamento.setSubcategoria(palavraChave.getIdSubcategoria());
                    }
                }
            }
        }
        return lancamentos;
    }

    public Result listaSalarios() {
        try {
            String ano = request().getQueryString("ano");
            String mes = request().getQueryString("mes");

            List<ChaveValor> listaSalarios = lancamentoPersistence.listaSalarios(ano, mes);

            ObjectNode retorno = Json.newObject();
            retorno.set("listaSalarios", Json.toJson(listaSalarios));

            return ok(retorno);
        }catch (Exception e){
            e.printStackTrace();
            return internalServerError(e.getMessage());
        }
    }

    public Result listaSubcategoriasMonitoramento() {
        try {
            String ano = request().getQueryString("ano");
            String mes = request().getQueryString("mes");

            List<ChaveValor> listaSubcategoriasMonitoramento = lancamentoPersistence.listaSubcategoriasMonitoramento(ano, mes);

            ObjectNode retorno = Json.newObject();
            retorno.set("listaSubcategoriasMonitoramento", Json.toJson(listaSubcategoriasMonitoramento));

            return ok(retorno);
        }catch (Exception e){
            e.printStackTrace();
            return internalServerError(e.getMessage());
        }
    }

    public Result listaSubcategorias() {
        try {
            String ano = request().getQueryString("ano");
            String mes = request().getQueryString("mes");

            List<ChaveValor> listaSubcategorias = lancamentoPersistence.listaSubcategorias(ano, mes);

            ObjectNode retorno = Json.newObject();
            retorno.set("listaSubcategorias", Json.toJson(listaSubcategorias));

            return ok(retorno);
        }catch (Exception e){
            e.printStackTrace();
            return internalServerError(e.getMessage());
        }
    }

    public Result listaCategorias() {
        try {
            String ano = request().getQueryString("ano");
            String mes = request().getQueryString("mes");

            List<ChaveValor> listaCategorias = lancamentoPersistence.listaCategorias(ano, mes);

            ObjectNode retorno = Json.newObject();
            retorno.set("listaCategorias", Json.toJson(listaCategorias));

            return ok(retorno);
        }catch (Exception e){
            e.printStackTrace();
            return internalServerError(e.getMessage());
        }
    }

    public Result listaEntradas() {
        try {
            String ano = request().getQueryString("ano");
            String mes = request().getQueryString("mes");

            List<ChaveValor> listaEntradas = lancamentoPersistence.listaEntradas(ano, mes);

            ObjectNode retorno = Json.newObject();
            retorno.set("listaEntradas", Json.toJson(listaEntradas));

            return ok(retorno);
        }catch (Exception e){
            e.printStackTrace();
            return internalServerError(e.getMessage());
        }
    }

    public Result listaTransferencias() {
        try {
            String ano = request().getQueryString("ano");
            String mes = request().getQueryString("mes");

            List<ChaveValor> listaTransferencias = lancamentoPersistence.listaTransferencias(ano, mes);

            ObjectNode retorno = Json.newObject();
            retorno.set("listaTransferencias", Json.toJson(listaTransferencias));

            return ok(retorno);
        }catch (Exception e){
            e.printStackTrace();
            return internalServerError(e.getMessage());
        }
    }

}
