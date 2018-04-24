package persistence;

import models.Lancamento;
import models.Subcategoria;
import org.skife.jdbi.v2.BeanMapper;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import play.db.Database;

import javax.inject.Inject;
import java.util.List;

public class SubcategoriaPersistence {

    Database db;

    @Inject
    public SubcategoriaPersistence(Database db) {
        this.db = db;
    }


    public List<Subcategoria> listaSubcategorias() {

        DBI dbi = new DBI(db.getDataSource());
        try (Handle handle = dbi.open()) {

            List<Subcategoria> listaSubcategorias = handle.createQuery("SELECT " +
                    "idSubcategoria \"id\", descricao, idCategoria, cdtipo " +
                    "FROM subcategoria " +
                    "order by descricao")
                    .map(new BeanMapper<>(Subcategoria.class))
                    .list();

            return listaSubcategorias;
        } catch (Exception ex) {
            throw ex;
        }
    }

}
