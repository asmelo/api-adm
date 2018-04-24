package controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import models.Lancamento;
import models.PalavraChave;
import models.Subcategoria;
import persistence.LancamentoPersistence;
import persistence.PalavraChavePersistence;
import persistence.SubcategoriaPersistence;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import services.ArquivoOfx;

import javax.inject.Inject;
import java.util.List;

public class SubcategoriaController extends Controller {

    private SubcategoriaPersistence subcategoriaPersistence;

    @Inject
    public SubcategoriaController(SubcategoriaPersistence subcategoriaPersistence) {
        this.subcategoriaPersistence = subcategoriaPersistence;
    }

    public Result listaSubcategorias() {
        try {
            List<Subcategoria> listaSubcategorias = subcategoriaPersistence.listaSubcategorias();

            ObjectNode retorno = Json.newObject();
            retorno.set("subcategorias", Json.toJson(listaSubcategorias));

            return ok(retorno);
        }catch (Exception e){
            e.printStackTrace();
            return internalServerError(e.getMessage());
        }
    }

}
